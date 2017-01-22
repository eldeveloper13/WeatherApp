package eldeveloper13.weatherapp.provider;

import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import eldeveloper13.weatherapp.db.WeatherDbHelper;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherDataProvider {

    private DarkSkyService mDarkSkyService;
    private WeatherDbHelper mWeatherDbHelper;
    private static final long STALE_AGE_MILLIS = 1000 * 60 * 15; // 15 minutes;

    public WeatherDataProvider(DarkSkyService darkSkyService, WeatherDbHelper database) {
        mDarkSkyService = darkSkyService;
        mWeatherDbHelper = database;
    }

    public Observable<CurrentWeatherModel> getCurrentWeather(double lat, double lon, @DarkSkyService.Units String units) {
        return getCurrentWeather(lat, lon, units, FetchStrategy.CACHE_THEN_NETWORK);
    }

    public Observable<CurrentWeatherModel> getCurrentWeather(double lat, double lon, @DarkSkyService.Units String units, FetchStrategy strategy) {
        switch (strategy) {
            case CACHE_ONLY:
                // fetch from database
                return getForecastFromDatabase(lat, lon);
            case CACHE_THEN_NETWORK:
                // return from database
                 Observable<CurrentWeatherModel> source = Observable.concat(
                        getForecastFromDatabase(lat, lon),
                        getForecastFromNetwork(lat, lon, units)
                ).first(new Func1<CurrentWeatherModel, Boolean>() {
                    @Override
                    public Boolean call(CurrentWeatherModel currentWeatherModel) {
                        return currentWeatherModel != null && !isDataExpired(currentWeatherModel);
                    }
                });
                return source;
            case FROM_NETWORK:
            default:
                return getForecastFromNetwork(lat, lon, units);
        }
    }

    public Observable<List<CurrentWeatherModel>> getShortTermWeather(double latitude, double longitude, @DarkSkyService.Units String units) {
        return getShortTermWeather(latitude, longitude, units, FetchStrategy.CACHE_THEN_NETWORK);
    }

    public Observable<List<CurrentWeatherModel>> getShortTermWeather(double latitude, double longitude, @DarkSkyService.Units String units, FetchStrategy fetchStrategy) {
        return Observable.empty();
    }

    //region helper methods
    private boolean isDataExpired(CurrentWeatherModel currentWeatherModel) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime - currentWeatherModel.getTimestamp()) > STALE_AGE_MILLIS;
    }

    private Observable<CurrentWeatherModel> getForecastFromDatabase(final double lat, final double lon) {
        return Observable.just(mWeatherDbHelper.queryWeatherLocation(lat, lon))
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long locationId) {
                        Cursor cursor = mWeatherDbHelper.queryWeatherCurrentByLocationId(locationId);
                        try {
                            if (cursor.moveToFirst()) {
                                return cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID));
                            } else {
                                return -1L;
                            }
                        } catch (Exception e) {
                            Log.e("WeatherDataProvider", e.getMessage());
                            return -1L;
                        } finally {
                            cursor.close();
                        }
                    }
                }).map(new Func1<Long, CurrentWeatherModel>() {
            @Override
            public CurrentWeatherModel call(Long datapointId) {
                if (datapointId == -1L) {
                    return null;
                }
                Cursor cursor = mWeatherDbHelper.queryWeatherDatapoint(datapointId);
                try {
                    if (cursor.moveToFirst()) {
                        CurrentWeatherModel model = new CurrentWeatherModel(
                                cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherDatapoint.COLUMN_TIMESTAMP)),
                                cursor.getDouble(cursor.getColumnIndex(WeatherDbHelper.WeatherDatapoint.COLUMN_TEMPERATURE)),
                                cursor.getDouble(cursor.getColumnIndex(WeatherDbHelper.WeatherDatapoint.COLUMN_FEELS_LIKE)),
                                CurrentWeatherModel.PrecipType.getValue(cursor.getString(cursor.getColumnIndex(WeatherDbHelper.WeatherDatapoint.COLUMN_PRECIP_TYPE))),
                                CurrentWeatherModel.WeatherIcon.getValue(cursor.getString(cursor.getColumnIndex(WeatherDbHelper.WeatherDatapoint.COLUMN_ICON)))
                        );
                        return model;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    Log.e("WeatherDataProvider", e.getMessage());
                    return null;
                } finally {
                    cursor.close();
                }
            }
        });
    }

    private Observable<CurrentWeatherModel> getForecastFromNetwork(double lat, double lon, @DarkSkyService.Units String units) {
        Observable<CurrentWeatherModel> forecastObservable = mDarkSkyService.getForecast(Double.toString(lat), Double.toString(lon), units)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<ForecastResponse>() {
                    @Override
                    public void call(ForecastResponse response) {
                        saveResponseToDB(response);
                    }
                }).map(new Func1<ForecastResponse, CurrentWeatherModel>() {
                    @Override
                    public CurrentWeatherModel call(ForecastResponse forecastResponse) {
                        return new CurrentWeatherModel(forecastResponse.getCurrently());
                    }
                });
        return forecastObservable;
    }

    private void saveResponseToDB(ForecastResponse response) {
        long locationId = mWeatherDbHelper.queryWeatherLocation(response.getLatitude(), response.getLongitude());
        if (locationId == -1) {
            locationId = mWeatherDbHelper.insertWeatherLocation(response.getLatitude(), response.getLongitude());
        }

        ForecastResponse.DataPoint currently = response.getCurrently();

        long datapointId = -1;
        Cursor cursor = mWeatherDbHelper.queryWeatherCurrentByLocationId(locationId);
        if (cursor.moveToFirst()) {
            datapointId = cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID));
            mWeatherDbHelper.updateWeatherDatapoint(datapointId, currently.getTime(), currently.getTemperature(), currently.getApparentTemperature(), currently.getPrecipType(), currently.getIcon());
        } else {
            datapointId = mWeatherDbHelper.insertWeatherDatapoint(currently.getTime(), currently.getTemperature(), currently.getApparentTemperature(), currently.getPrecipType(), currently.getIcon());
            mWeatherDbHelper.insertWeatherCurrent(locationId, datapointId);
        }
    }
    //endregion

//    private long insertIntoWeatherDatapoint(long timestamp, double temperature, double feelsLike, String icon) {
//        ContentValues values = new ContentValues() ;
//        values.put(WeatherDbHelper.WeatherDatapoint.COLUMN_TIMESTAMP, timestamp);
//        values.put(WeatherDbHelper.WeatherDatapoint.COLUMN_TEMPERATURE, temperature);
//        values.put(WeatherDbHelper.WeatherDatapoint.COLUMN_FEELS_LIKE, feelsLike);
//        values.put(WeatherDbHelper.WeatherDatapoint.COLUMN_ICON, icon);
//
//        SQLiteDatabase db = null;
//        try {
//            db = mWeatherDbHelper.getWritableDatabase();
//            return db.insert(WeatherDbHelper.WeatherDatapoint.TABLE_NAME, null, values);
//        } finally {
//            if (db != null) {
//                db.close();
//            }
//        }
//    }

    public enum FetchStrategy {
        CACHE_ONLY, CACHE_THEN_NETWORK, FROM_NETWORK
    }
}

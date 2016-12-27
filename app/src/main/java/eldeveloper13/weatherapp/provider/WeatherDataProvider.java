package eldeveloper13.weatherapp.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

import eldeveloper13.weatherapp.db.WeatherDbHelper;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Subscriber;
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
                        return !isDataExpired(currentWeatherModel);
                    }
                });
                return source;
            case FROM_NETWORK:
            default:
                return getForecastFromNetwork(lat, lon, units);
        }
    }

    private boolean isDataExpired(CurrentWeatherModel currentWeatherModel) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime - currentWeatherModel.getTimestamp() * 1000L) > STALE_AGE_MILLIS;
    }

    private Observable<CurrentWeatherModel> getForecastFromDatabase(final double lat, final double lon) {
        return Observable.empty();
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
        long locationId = insertIntoWeatherLocation(response.getLatitude(), response.getLongitude());

        ForecastResponse.DataPoint dataPoint = response.getCurrently();
        ContentValues datapointValues = new ContentValues();
        datapointValues.put(WeatherDbHelper.WeatherDatapoint.COLUMN_TIMESTAMP, dataPoint.getTime());
        datapointValues.put(WeatherDbHelper.WeatherDatapoint.COLUMN_TEMPERATURE, dataPoint.getTemperature());
        datapointValues.put(WeatherDbHelper.WeatherDatapoint.COLUMN_FEELS_LIKE, dataPoint.getApparentTemperature());
        datapointValues.put(WeatherDbHelper.WeatherDatapoint.COLUMN_ICON, dataPoint.getIcon());

        SQLiteDatabase db = mWeatherDbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.query(WeatherDbHelper.WeatherCurrent.TABLE_NAME,
                    new String[]{WeatherDbHelper.WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID},
                    WeatherDbHelper.WeatherCurrent.COLUMN_LOCATION_ID + " = ?",
                    new String[]{Long.toString(locationId)},
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                long datapointId = cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID));
                db.update(WeatherDbHelper.WeatherDatapoint.TABLE_NAME, datapointValues,
                        WeatherDbHelper.WeatherDatapoint._ID + " = ?", new String[]{Long.toString(datapointId)});
            } else {
                long datapointId = db.insert(WeatherDbHelper.WeatherDatapoint.TABLE_NAME, null, datapointValues);
                ContentValues currentIdValues = new ContentValues();
                currentIdValues.put(WeatherDbHelper.WeatherCurrent.COLUMN_LOCATION_ID, locationId);
                currentIdValues.put(WeatherDbHelper.WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID, datapointId);

                db.insert(WeatherDbHelper.WeatherCurrent.TABLE_NAME, null, currentIdValues);
            }
            cursor.close();
        } finally {
            db.close();
        }
    }

    private long insertIntoWeatherLocation(double lat, double lon) {
        SQLiteDatabase db = null;
        try {
            db = mWeatherDbHelper.getWritableDatabase();
            Cursor cursor = db.query(WeatherDbHelper.WeatherLocation.TABLE_NAME,
                    new String[] { WeatherDbHelper.WeatherLocation._ID },
                    WeatherDbHelper.WeatherLocation.COLUMN_LATITUDE + " = ? AND " + WeatherDbHelper.WeatherLocation.COLUMN_LONGITUDE + " = ?" ,
                    new String[] { Double.toString(lat), Double.toString(lon) },
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                long locationId = cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherLocation._ID));
                cursor.close();
                return locationId;
            } else {
                cursor.close();

                ContentValues values = new ContentValues();
                values.put(WeatherDbHelper.WeatherLocation.COLUMN_LATITUDE, lat);
                values.put(WeatherDbHelper.WeatherLocation.COLUMN_LONGITUDE, lon);

                return db.insert(WeatherDbHelper.WeatherLocation.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            Log.e("WeatherDataProvider", "Error inserting weather locations: " + e.getMessage());
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    Log.e("WeatherDataProvider", "Error close database: " + e.getMessage());
                }
            }
        }
    }

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

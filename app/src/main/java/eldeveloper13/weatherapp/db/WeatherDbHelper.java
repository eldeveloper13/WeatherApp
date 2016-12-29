package eldeveloper13.weatherapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeatherApp.db";
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WeatherLocation.CREATE_TABLE_QUERY);
        db.execSQL(WeatherDatapoint.CREATE_TABLE_QUERY);
        db.execSQL(WeatherCurrent.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(WeatherCurrent.DROP_TABLE_QUERY);
        db.execSQL(WeatherDatapoint.DROP_TABLE_QUERY);
        db.execSQL(WeatherLocation.DROP_TABLE_QUERY);

        onCreate(db);
    }

    //region WeatherLocation CRUD
    public long insertWeatherLocation(double lat, double lon) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(WeatherDbHelper.WeatherLocation.COLUMN_LATITUDE, lat);
            values.put(WeatherDbHelper.WeatherLocation.COLUMN_LONGITUDE, lon);

            return db.insert(WeatherDbHelper.WeatherLocation.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    public long queryWeatherLocation(double lat, double lon) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            long locationId = -1;
            Cursor cursor = db.query(WeatherDbHelper.WeatherLocation.TABLE_NAME,
                    new String[]{WeatherDbHelper.WeatherLocation._ID},
                    WeatherDbHelper.WeatherLocation.COLUMN_LATITUDE + " = ? AND " + WeatherDbHelper.WeatherLocation.COLUMN_LONGITUDE + " = ?",
                    new String[]{Double.toString(lat), Double.toString(lon)},
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                locationId = cursor.getLong(cursor.getColumnIndex(WeatherDbHelper.WeatherLocation._ID));
            }
            cursor.close();
            return locationId;
        } finally {
            db.close();
        }
    }

    public long deleteWeatherLocation(double lat, double lon) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(WeatherLocation.TABLE_NAME,
                    WeatherLocation.COLUMN_LATITUDE + " = ? AND " + WeatherLocation.COLUMN_LONGITUDE + " = ? ",
                    new String[] { Double.toString(lat), Double.toString(lon) });
        } finally {
            db.close();
        }
    }

    public long deleteWeatherLocation(long id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(WeatherLocation.TABLE_NAME,
                    WeatherLocation._ID+ " = ?",
                    new String[] { Long.toString(id) });
        } finally {
            db.close();
        }
    }
    //endregion

    //region WeatherDatapoint CRUD
    public long insertWeatherDatapoint(long timestamp, double temperature, double feelsLike, String precipType, String icon) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues datapointValues = new ContentValues();
            datapointValues.put(WeatherDatapoint.COLUMN_TIMESTAMP, timestamp);
            datapointValues.put(WeatherDatapoint.COLUMN_TEMPERATURE, temperature);
            datapointValues.put(WeatherDatapoint.COLUMN_FEELS_LIKE, feelsLike);
            datapointValues.put(WeatherDatapoint.COLUMN_PRECIP_TYPE, precipType);
            datapointValues.put(WeatherDatapoint.COLUMN_ICON, icon);

            return db.insert(WeatherDatapoint.TABLE_NAME, null, datapointValues);
        } finally {
            db.close();
        }
    }

    public int updateWeatherDatapoint(long id, long timestamp, double temperature, double feelsLike, String precipType, String icon) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(WeatherDatapoint.COLUMN_TIMESTAMP, timestamp);
            values.put(WeatherDatapoint.COLUMN_TEMPERATURE, temperature);
            values.put(WeatherDatapoint.COLUMN_FEELS_LIKE, feelsLike);
            values.put(WeatherDatapoint.COLUMN_PRECIP_TYPE, precipType);
            values.put(WeatherDatapoint.COLUMN_ICON, icon);

            return db.update(WeatherDatapoint.TABLE_NAME, values, WeatherDatapoint._ID + " = ? ", new String[] { Long.toString(id) });
        } finally {
            db.close();
        }
    }

    public Cursor queryWeatherDatapoint(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(WeatherDbHelper.WeatherDatapoint.TABLE_NAME,
                null,
                WeatherDatapoint._ID+ " = ? ",
                new String[]{ Long.toString(id) },
                null,
                null,
                null);
        return cursor;
    }

    public long deleteWeatherDatapoint(long id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(WeatherDatapoint.TABLE_NAME,
                    WeatherDatapoint._ID+ " = ?",
                    new String[] { Long.toString(id) });
        } finally {
            db.close();
        }
    }
    //endregion

    //region WeatherCurrent CRUD
    public long insertWeatherCurrent(long locationId, long datapointId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(WeatherCurrent.COLUMN_LOCATION_ID, locationId);
            values.put(WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID, datapointId);

            return db.insert(WeatherCurrent.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    public Cursor queryWeatherCurrentByLocationId(long locationId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(WeatherCurrent.TABLE_NAME,
                new String[] { WeatherCurrent.COLUMN_LOCATION_ID, WeatherCurrent.COLUMN_CURRENT_DATAPOINT_ID } ,
                WeatherCurrent.COLUMN_LOCATION_ID + " = ? " ,
                new String[] { Long.toString(locationId) },
                null, null, null);
    }

    public int deleteWeatherCurrentByLocationId(long locationId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(WeatherCurrent.TABLE_NAME, WeatherCurrent.COLUMN_LOCATION_ID + " = ? ", new String[] { Long.toString(locationId) });
        } finally {
            db.close();
        }
    }
    //endregion

    //region Classes
    public static class WeatherLocation implements BaseColumns {
        public static final String TABLE_NAME = "weather_location";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                        COLUMN_LONGITUDE + " DOUBLE NOT NULL " +
                        ");";
        private static final String DROP_TABLE_QUERY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class WeatherDatapoint implements BaseColumns {
        public static final String TABLE_NAME = "weather_data_point";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_FEELS_LIKE = "feels_like";
        public static final String COLUMN_PRECIP_TYPE = "precip_type";
        public static final String COLUMN_ICON = "icon";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TIMESTAMP + " DOUBLE NOT NULL, " +
                        COLUMN_TEMPERATURE + " DOUBLE, " +
                        COLUMN_FEELS_LIKE + " DOUBLE, " +
                        COLUMN_PRECIP_TYPE + " VARCHAR(40), " +
                        COLUMN_ICON + " VARCHAR(20) " +
                        ");";
        private static final String DROP_TABLE_QUERY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class WeatherCurrent implements BaseColumns {
        public static final String TABLE_NAME = "weather_current";
        public static final String COLUMN_LOCATION_ID = "location";
        public static final String COLUMN_CURRENT_DATAPOINT_ID = "current";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_LOCATION_ID + " INTEGER, " +
                        COLUMN_CURRENT_DATAPOINT_ID + " INTEGER, " +
                        "FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES " + WeatherLocation.TABLE_NAME + "(" + _ID + ")," +
                        "FOREIGN KEY (" + COLUMN_CURRENT_DATAPOINT_ID + ") REFERENCES " + WeatherDatapoint.TABLE_NAME + "(" + _ID + ")" +
                        ");";
        private static final String DROP_TABLE_QUERY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class WeatherHourly implements BaseColumns {
        public static final String TABLE_NAME = "weather_hourly";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_HOURLY = "hourly";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY " +
                        COLUMN_LOCATION + " INTEGER " +
                        COLUMN_HOURLY + " INTEGER " +
                        "FOREIGN KEY (" + COLUMN_LOCATION + ") REFERENCES " + WeatherLocation.TABLE_NAME + "(" + WeatherLocation._ID + ")" +
                        "FOREIGN KEY (" + COLUMN_HOURLY + ") REFERENCES " + WeatherHourly.TABLE_NAME + "(" + WeatherHourly._ID + ")" +
                        ");";
        private static final String DROP_TABLE_QUERY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class WeatherDaily implements BaseColumns {
        public static final String TABLE_NAME = "weather_daily";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_HOURLY = "daily";
    }
    //endregion

}

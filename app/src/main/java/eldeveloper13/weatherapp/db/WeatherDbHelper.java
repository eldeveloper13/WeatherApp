package eldeveloper13.weatherapp.db;

import android.content.Context;
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

    public static class WeatherLocation implements BaseColumns {
        public static final String TABLE_NAME = "weather_location";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                        COLUMN_LONGITUDE + " DOUBLE NOT NULL " +
//                        "PRIMARY KEY (" + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE + ")" +
                        ");";
        private static final String DROP_TABLE_QUERY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class WeatherDatapoint implements BaseColumns {
        public static final String TABLE_NAME = "weather_data_point";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_FEELS_LIKE = "feels_like";
        public static final String COLUMN_ICON = "icon";

        private static final String CREATE_TABLE_QUERY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TIMESTAMP + " DOUBLE NOT NULL, " +
                        COLUMN_TEMPERATURE + " DOUBLE, " +
                        COLUMN_FEELS_LIKE + " DOUBLE, " +
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

}

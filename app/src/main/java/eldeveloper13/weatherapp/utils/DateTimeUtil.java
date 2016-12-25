package eldeveloper13.weatherapp.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeZone(TimeZone.getTimeZone("EST"));
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yy MMM dd hh:mm:ss a, z", cal).toString();
        return date;
    }
}

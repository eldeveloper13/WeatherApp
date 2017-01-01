package eldeveloper13.weatherapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eldeveloper13.weatherapp.services.darksky.DarkSkyService;

public class UpdateReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1234;

    public static Intent getIntent(Context context, double lat, double lon, @DarkSkyService.Units String unit) {
        Intent intent = new Intent(context, UpdateReceiver.class);
        intent.putExtra(Extras.LATITUDE, lat);
        intent.putExtra(Extras.LONGITUDE, lon);
        intent.putExtra(Extras.UNIT, unit);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        double lat = intent.getDoubleExtra(Extras.LATITUDE, -1);
        double lon = intent.getDoubleExtra(Extras.LONGITUDE, -1);
        @DarkSkyService.Units String unit = intent.getStringExtra(Extras.UNIT);

        Intent serviceIntent = StatusUpdateService.getIntent(context, lat, lon, unit);
        context.startService(serviceIntent);
    }

    public static class Extras {
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String UNIT = "unit";
    }
}

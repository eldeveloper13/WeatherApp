package eldeveloper13.weatherapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import eldeveloper13.weatherapp.WeatherAppApplication;
import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.utils.DateTimeUtil;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import eldeveloper13.weatherapp.weatherinfo.view.MainActivity;
import rx.Subscriber;

public class StatusUpdateService extends IntentService {

    private static final int NOTIFICATION_ID = 1000;

    public static Intent getIntent(Context context, double latitude, double longitude, @DarkSkyService.Units String units) {
        Intent intent = new Intent(context, StatusUpdateService.class);
        intent.putExtra(StatusUpdateService.Extras.LATITUDE, 43.6532);
        intent.putExtra(StatusUpdateService.Extras.LONGITUDE, -79.3832);
        intent.putExtra(StatusUpdateService.Extras.UNITS, DarkSkyService.CA);
        return intent;
    }

    @Inject
    WeatherDataProvider mWeatherDataProvider;

    public StatusUpdateService() {
        super("StatusUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((WeatherAppApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double lat = intent.getDoubleExtra(Extras.LATITUDE, -1);
        double lon = intent.getDoubleExtra(Extras.LONGITUDE, -1);
        @DarkSkyService.Units String units = intent.getStringExtra(Extras.UNITS);

        mWeatherDataProvider.getCurrentWeather(lat, lon, units, WeatherDataProvider.FetchStrategy.FROM_NETWORK)
                .subscribe(new Subscriber<CurrentWeatherModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("StatusUpdateService", e.getMessage());
                    }

                    @Override
                    public void onNext(CurrentWeatherModel currentWeatherModel) {
                        sendNotification(currentWeatherModel);
                    }
                });
    }

    private void sendNotification(CurrentWeatherModel model) {
        double temperature = model.getTemperature();
        CurrentWeatherModel.WeatherIcon weatherIcon = model.getWeatherIcon();

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        Bitmap bitmap = BitmapUtil.getBitmapFromText(getApplicationContext(), Long.toString(Math.round(model.getTemperature())));
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(weatherIcon.getIconRes())
                .setContentTitle(Double.toString(temperature))
                .setContentText(String.format("Last Update at %s", DateTimeUtil.getDate(model.getTimestamp())))
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);
    }

    public static class Extras {
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String UNITS = "units";
    }
}

package eldeveloper13.weatherapp;

import android.app.Application;

import eldeveloper13.weatherapp.dagger2.AppComponent;
import eldeveloper13.weatherapp.dagger2.AppModule;
import eldeveloper13.weatherapp.dagger2.DaggerAppComponent;

public class WeatherAppApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}

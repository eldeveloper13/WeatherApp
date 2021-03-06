package eldeveloper13.weatherapp.dagger2;

import javax.inject.Singleton;

import dagger.Component;
import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.StatusUpdateService;
import eldeveloper13.weatherapp.weatherinfo.presenter.CurrentWeatherPresenter;
import eldeveloper13.weatherapp.weatherinfo.view.CurrentWeatherFragment;
import eldeveloper13.weatherapp.weatherinfo.view.MainActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(StatusUpdateService service);
    void inject(WeatherDataProvider provider);

    void inject(CurrentWeatherPresenter presenter);
    void inject(MainActivity activity);
    void inject(CurrentWeatherFragment fragment);

}

package eldeveloper13.weatherapp.dagger2;

import javax.inject.Singleton;

import dagger.Component;
import eldeveloper13.weatherapp.weatherinfo.presenter.MainPresenter;
import eldeveloper13.weatherapp.weatherinfo.view.MainActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainPresenter presenter);
    void inject(MainActivity activity);
}

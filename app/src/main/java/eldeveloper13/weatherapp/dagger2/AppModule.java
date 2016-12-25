package eldeveloper13.weatherapp.dagger2;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.presenter.MainPresenter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    DarkSkyService provideDarkSkyService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.darksky.net")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DarkSkyService.class);
    }

    @Provides
    MainContract.Presenter provideMainPresenter(MainPresenter presenter) {
        return presenter;
    }
}

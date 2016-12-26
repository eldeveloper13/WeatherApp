package eldeveloper13.weatherapp.dagger2;

import android.app.Application;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.network.CacheRevalidationInterceptor;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.presenter.MainPresenter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
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
    OkHttpClient provideOkHttpClient() {
        File cacheDirectory = mApplication.getCacheDir();
        int cacheSize = 10 * 1024 * 1024;   // 10 MiB
        Cache cache = new Cache(cacheDirectory, cacheSize);

        return new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(new CacheRevalidationInterceptor())
                .build();
    }

    @Provides
    @Singleton
    DarkSkyService provideDarkSkyService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
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

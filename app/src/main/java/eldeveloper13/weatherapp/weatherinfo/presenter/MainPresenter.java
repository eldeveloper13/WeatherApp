package eldeveloper13.weatherapp.weatherinfo.presenter;

import android.util.Log;

import javax.inject.Inject;

import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private final WeatherDataProvider mWeatherDataProvider;
    MainContract.View mView;

    @Inject
    public MainPresenter(WeatherDataProvider weatherDataProvider) {
        mWeatherDataProvider = weatherDataProvider;
    }

    @Override
    public void attachView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getCity() {
        mView.setWeatherTabsVisible(true);
    }

    @Override
    public void getWeather() {
        mView.showSpinner();
        Observable<CurrentWeatherModel> observable = mWeatherDataProvider.getCurrentWeather(43.6532, -79.3832, DarkSkyService.CA, WeatherDataProvider.FetchStrategy.CACHE_THEN_NETWORK);
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrentWeatherModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError();
                    }

                    @Override
                    public void onNext(CurrentWeatherModel currentWeatherModel) {
                        mView.showCurrentWeather(currentWeatherModel);
                    }
                });
    }
}

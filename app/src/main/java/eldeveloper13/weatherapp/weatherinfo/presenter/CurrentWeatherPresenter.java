package eldeveloper13.weatherapp.weatherinfo.presenter;

import javax.inject.Inject;

import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.weatherinfo.CurrentWeatherContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class CurrentWeatherPresenter implements CurrentWeatherContract.Presenter {

    private final WeatherDataProvider mWeatherDataProvider;
    CurrentWeatherContract.View mView;

    @Inject
    public CurrentWeatherPresenter(WeatherDataProvider weatherDataProvider) {
        mWeatherDataProvider = weatherDataProvider;
    }

    @Override
    public void attachView(CurrentWeatherContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getWeather(double latitude, double longitude) {
        mView.showSpinner();
        Observable<CurrentWeatherModel> observable = mWeatherDataProvider.getCurrentWeather(latitude, longitude, DarkSkyService.CA, WeatherDataProvider.FetchStrategy.CACHE_THEN_NETWORK);
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrentWeatherModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSpinner();
                        mView.showError("Error loading weather: " + e.getMessage());
                    }

                    @Override
                    public void onNext(CurrentWeatherModel currentWeatherModel) {
                        mView.hideSpinner();
                        mView.showCurrentWeather(currentWeatherModel);
                    }
                });
    }
}

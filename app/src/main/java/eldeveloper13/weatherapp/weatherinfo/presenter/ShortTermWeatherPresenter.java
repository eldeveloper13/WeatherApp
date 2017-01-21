package eldeveloper13.weatherapp.weatherinfo.presenter;

import java.util.List;

import javax.inject.Inject;

import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.weatherinfo.ShortTermWeatherContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ShortTermWeatherPresenter implements ShortTermWeatherContract.Presenter {

    WeatherDataProvider mWeatherDataProvider;

    ShortTermWeatherContract.View mView;

    @Inject
    public ShortTermWeatherPresenter(WeatherDataProvider weatherDataProvider) {
        mWeatherDataProvider = weatherDataProvider;
    }

    @Override
    public void attachView(ShortTermWeatherContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void loadWeather(double latitude, double longitude) {
        mView.showSpinner();
        Observable<List<CurrentWeatherModel>> observable = mWeatherDataProvider.getShortTermWeather(latitude, longitude, DarkSkyService.CA);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CurrentWeatherModel>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSpinner();
                        mView.showError("Error loading weather: " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CurrentWeatherModel> currentWeatherModels) {
                        mView.hideSpinner();
                        mView.showWeatherData(currentWeatherModels);
                    }
                });
    }
}

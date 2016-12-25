package eldeveloper13.weatherapp.weatherinfo.presenter;

import javax.inject.Inject;

import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    DarkSkyService mDarkSkyService;

    MainContract.View mView;

    @Inject
    public MainPresenter(DarkSkyService darkSkyService) {
        mDarkSkyService = darkSkyService;
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
        Observable<ForecastResponse> observable = mDarkSkyService.getForecast("43.6532", "-79.3832", DarkSkyService.CA);
        observable.map(new Func1<ForecastResponse, CurrentWeatherModel>() {
                    @Override
                    public CurrentWeatherModel call(ForecastResponse forecastResponse) {
                        return new CurrentWeatherModel(forecastResponse.getCurrently());
                    }
                })
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

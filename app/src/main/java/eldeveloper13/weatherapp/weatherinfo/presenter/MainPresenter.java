package eldeveloper13.weatherapp.weatherinfo.presenter;

import javax.inject.Inject;

import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Call<ForecastResponse> call = mDarkSkyService.getForecast("43.6532", "-79.3832", DarkSkyService.CA);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                mView.showCurrentWeather(response.body());
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {

            }
        });

    }
}

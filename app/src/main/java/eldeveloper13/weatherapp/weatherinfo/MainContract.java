package eldeveloper13.weatherapp.weatherinfo;

import eldeveloper13.weatherapp.services.darksky.ForecastResponse;

public interface MainContract {

    interface View {
        void setWeatherTabsVisible(boolean show);
        void showCurrentWeather(ForecastResponse forecast);
    }

    interface Presenter {
        void getWeather();
        void attachView(MainContract.View view);
        void detachView();

        void getCity();
    }
}

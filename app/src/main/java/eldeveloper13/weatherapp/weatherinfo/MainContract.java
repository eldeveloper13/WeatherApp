package eldeveloper13.weatherapp.weatherinfo;

import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public interface MainContract {

    interface View {
        void setWeatherTabsVisible(boolean show);
        void showSpinner();
        void showCurrentWeather(CurrentWeatherModel model);
        void showError();
    }

    interface Presenter {
        void getCity();
        void getWeather();

        void attachView(MainContract.View view);
        void detachView();
    }
}

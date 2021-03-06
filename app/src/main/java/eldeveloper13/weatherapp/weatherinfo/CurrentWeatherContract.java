package eldeveloper13.weatherapp.weatherinfo;

import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public interface CurrentWeatherContract {

    interface View {
        void showCurrentWeather(CurrentWeatherModel model);
        void showSpinner();
        void hideSpinner();
        void showError(String error);
    }

    interface Presenter {
        void attachView(CurrentWeatherContract.View view);
        void detachView();
        void getWeather(double latitude, double longitude);
    }
}

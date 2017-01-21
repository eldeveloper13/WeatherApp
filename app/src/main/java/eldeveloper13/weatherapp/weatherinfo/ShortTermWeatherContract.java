package eldeveloper13.weatherapp.weatherinfo;

import java.util.List;

import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public interface ShortTermWeatherContract {

    interface View {
        void showSpinner();
        void hideSpinner();
        void showWeatherData(List<CurrentWeatherModel> data);
        void showError(String error);
    }

    interface Presenter {
        void attachView(ShortTermWeatherContract.View view);
        void detachView();
        void loadWeather(double latitude, double longitude);
    }
}

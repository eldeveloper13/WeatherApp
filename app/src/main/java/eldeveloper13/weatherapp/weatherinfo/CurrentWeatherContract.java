package eldeveloper13.weatherapp.weatherinfo;

import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

/**
 * Created by ericl on 12/24/2016.
 */

public interface CurrentWeatherContract {

    interface View {
        void showCurrentWeather(CurrentWeatherModel model);
    }
}

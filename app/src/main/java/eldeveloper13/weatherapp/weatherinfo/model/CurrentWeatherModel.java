package eldeveloper13.weatherapp.weatherinfo.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import lombok.Data;

@Data
public class CurrentWeatherModel {

    private long timestamp;
    private Double temperature;
    @Nullable
    private Double feelsLike;
    private PrecipType precipType;
    private WeatherIcon weatherIcon;

    public CurrentWeatherModel(ForecastResponse.DataPoint dataPoint) {
        this.timestamp = dataPoint.getTime();
        this.temperature = dataPoint.getTemperature();
        this.feelsLike = dataPoint.getApparentTemperature();
        this.weatherIcon = WeatherIcon.getValue(dataPoint.getIcon());
        this.precipType = PrecipType.getValue(dataPoint.getPrecipType());
    }
    
    //region Classes and Enums
    public enum WeatherIcon {
        ClearDay("clear-day", 0),
        ClearNight("clear-night", 0),
        Rain("rain", 0),
        Snow("snow", 0),
        Sleet("sleet", 0),
        Wind("wind", 0),
        Fog("fog", 0),
        Cloud("cloud", 0),
        PartlyCloudyDay("partly-cloudy-day", 0),
        PartlyCloudyNight("partly-cloudy-night", 0),
        None("none", 0);

        private String mName;
        private @DrawableRes int mIconRes;

        WeatherIcon(String name, @DrawableRes int iconRes){
            mName = name;
            mIconRes = iconRes;
        }

        public @DrawableRes int getIconRes() {
            return mIconRes;
        }

        public static WeatherIcon getValue(String name){
            for (WeatherIcon icon : WeatherIcon.values()) {
                if (icon.mName.equals(name)) {
                    return icon;
                }
            }
            return None;
        }
    }

    public enum PrecipType {
        Rain("rain"), Snow("snow"), Sleet("sleet"), None("none");

        private String mName;

        PrecipType(String name) {
            mName = name;
        }

        public static PrecipType getValue(String name) {
            for (PrecipType precipType : PrecipType.values()) {
                if (precipType.mName.equals(name)) {
                    return precipType;
                }
            }
            return None;
        }
    }
    //endregion
}

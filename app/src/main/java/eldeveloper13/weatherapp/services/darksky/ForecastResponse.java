package eldeveloper13.weatherapp.services.darksky;

import java.util.List;

import lombok.Data;

@Data
public class ForecastResponse {

    double latitude;
    double longitude;
    String timezone;
    DataPoint currently;
    DataBlock hourly;
    DataBlock daily;
    List<Alert> alerts;

    @Data
    public class DataPoint {
        long time;
        String summary;
        String icon;
        double dewPoint;
        double humidity;
        double cloudCover;
        int nearestStormDistance;
        int nearestStormBearing;
        double ozone;
        double precipAccumulation;
        double precipIntensity;
        double precipIntensityMax;
        long precipIntensityMaxTime;
        double precipProbability;
        String precipType;
        double pressure;
        long sunriseTime;
        long sunsetTime;
        double temperature;
        double temperatureMin;
        double temperatureMax;
        double apparentTemperature;
        int windBearing;
        double windSpeed;
        double visibility;
    }

    @Data
    public class DataBlock {
        String summary;
        String icon;
        List<DataPoint> data;
    }

    @Data
    public class Alert {
        String title;
        long time;
        long expires;
        String description;
        String uri;
    }
}

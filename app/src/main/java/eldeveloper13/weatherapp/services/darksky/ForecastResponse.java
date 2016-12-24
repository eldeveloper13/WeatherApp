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
        int nearestStormDistance;
        int nearestStormBearing;
        double precipIntensity;
        double precipProbability;
        double temperature;
        double temperatureMin;
        double temperatureMax;
        double apparentTemperature;
        double dewPoint;
        double humidity;
        int windBearing;
        double windSpeed;
        double visibility;
        double cloudCover;
        double pressure;
        double ozone;
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

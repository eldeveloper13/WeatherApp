package eldeveloper13.weatherapp.weatherinfo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import eldeveloper13.weatherapp.R;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import lombok.Data;

@Data
public class CurrentWeatherModel implements Parcelable {

    private long timestamp;
    private Double temperature;
    @Nullable
    private Double feelsLike;
    private PrecipType precipType;
    private WeatherIcon weatherIcon;

    public CurrentWeatherModel(long timestamp, Double temperature, Double feelsLike, PrecipType precipType, WeatherIcon weatherIcon) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.precipType = precipType;
        this.weatherIcon = weatherIcon;
    }

    public CurrentWeatherModel(ForecastResponse.DataPoint dataPoint) {
        this.timestamp = dataPoint.getTime();
        this.temperature = dataPoint.getTemperature();
        this.feelsLike = dataPoint.getApparentTemperature();
        this.weatherIcon = WeatherIcon.getValue(dataPoint.getIcon());
        this.precipType = PrecipType.getValue(dataPoint.getPrecipType());
    }

    //region Parcelable Code
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeValue(this.temperature);
        dest.writeValue(this.feelsLike);
        dest.writeInt(this.precipType == null ? -1 : this.precipType.ordinal());
        dest.writeInt(this.weatherIcon == null ? -1 : this.weatherIcon.ordinal());
    }

    protected CurrentWeatherModel(Parcel in) {
        this.timestamp = in.readLong();
        this.temperature = (Double) in.readValue(Double.class.getClassLoader());
        this.feelsLike = (Double) in.readValue(Double.class.getClassLoader());
        int tmpPrecipType = in.readInt();
        this.precipType = tmpPrecipType == -1 ? null : PrecipType.values()[tmpPrecipType];
        int tmpWeatherIcon = in.readInt();
        this.weatherIcon = tmpWeatherIcon == -1 ? null : WeatherIcon.values()[tmpWeatherIcon];
    }

    public static final Parcelable.Creator<CurrentWeatherModel> CREATOR = new Parcelable.Creator<CurrentWeatherModel>() {
        @Override
        public CurrentWeatherModel createFromParcel(Parcel source) {
            return new CurrentWeatherModel(source);
        }

        @Override
        public CurrentWeatherModel[] newArray(int size) {
            return new CurrentWeatherModel[size];
        }
    };
    //endregion

    //region Classes and Enums
    public enum WeatherIcon {
        ClearDay("clear-day", R.drawable.ic_clear_day),
        ClearNight("clear-night", R.drawable.ic_clear_night),
        Rain("rain", R.drawable.ic_rain),
        Snow("snow", R.drawable.ic_snow),
        Sleet("sleet", R.drawable.ic_sleet),
        Wind("wind", R.drawable.ic_wind),
        Fog("fog", R.drawable.ic_fog),
        Cloudy("cloudy", R.drawable.ic_cloud),
        PartlyCloudyDay("partly-cloudy-day", R.drawable.ic_partly_cloudy_day),
        PartlyCloudyNight("partly-cloudy-night", R.drawable.ic_partly_cloudy_night),
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

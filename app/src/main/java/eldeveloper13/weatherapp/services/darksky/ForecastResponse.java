package eldeveloper13.weatherapp.services.darksky;

public class ForecastResponse {

    String latitude;
    String longitude;
    String timezone;
    DataPointObject currently;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForecastResponse that = (ForecastResponse) o;

        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null)
            return false;
        if (timezone != null ? !timezone.equals(that.timezone) : that.timezone != null)
            return false;
        return currently != null ? currently.equals(that.currently) : that.currently == null;

    }

    @Override
    public int hashCode() {
        int result = latitude != null ? latitude.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (currently != null ? currently.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForecastResponse{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", timezone='" + timezone + '\'' +
                ", currently=" + currently +
                '}';
    }

    class DataPointObject {
        int time;
        String summary;
        String icon;
        double temperature;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataPointObject that = (DataPointObject) o;

            if (time != that.time) return false;
            if (Double.compare(that.temperature, temperature) != 0) return false;
            if (summary != null ? !summary.equals(that.summary) : that.summary != null)
                return false;
            return icon != null ? icon.equals(that.icon) : that.icon == null;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = time;
            result = 31 * result + (summary != null ? summary.hashCode() : 0);
            result = 31 * result + (icon != null ? icon.hashCode() : 0);
            temp = Double.doubleToLongBits(temperature);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "DataPointObject{" +
                    "time=" + time +
                    ", summary='" + summary + '\'' +
                    ", icon='" + icon + '\'' +
                    ", temperature=" + temperature +
                    '}';
        }
    }
}

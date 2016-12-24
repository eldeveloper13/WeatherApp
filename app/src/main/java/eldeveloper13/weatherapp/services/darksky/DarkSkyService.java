package eldeveloper13.weatherapp.services.darksky;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DarkSkyService {

    static final String KEY = "0246a28fc0ef2cb8351d4d4f24f382f3";

    @GET("forecast/"+KEY+"/{latitude},{longitude}")
    Call<ForecastResponse> getForecast(@Path("latitude") String latitude,
                                       @Path("longitude") String longitude);
}

package eldeveloper13.weatherapp.services.darksky;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface DarkSkyService {

    static final String KEY = "0246a28fc0ef2cb8351d4d4f24f382f3";

    @GET("forecast/"+KEY+"/{latitude},{longitude}")
    Observable<ForecastResponse> getForecast(@Path("latitude") String latitude,
                                                   @Path("longitude") String longitude,
                                                   @Query("units") @Units String units);

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AUTO,
            CA,
            UK2,
            US,
            SI,
    })
    public @interface Units {}
    public static final String AUTO = "auto";
    public static final String CA = "ca";
    public static final String UK2 = "uk2";
    public static final String US = "us";
    public static final String SI = "si";
}

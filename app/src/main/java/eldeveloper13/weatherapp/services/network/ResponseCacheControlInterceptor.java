package eldeveloper13.weatherapp.services.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ResponseCacheControlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        int maxStale = 60 * 5;
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().header("Cache-Control", "public, max-age=300, max-stale=" + maxStale).build();
    }

}

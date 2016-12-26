package eldeveloper13.weatherapp.services.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        int maxStale = 60 * 60 * 24 * 5;
        Request request;
        request = chain.request().newBuilder().header("Cache-Control", "max-stale=" + maxStale).build();
        return chain.proceed(request);
    }
}

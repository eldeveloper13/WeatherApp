package eldeveloper13.weatherapp.services.network;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheRevalidationInterceptor implements Interceptor {

    private static final String WARNING_RESPONSE_IS_STALE = "110";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        if (shouldSkipInterceptor(request, originalResponse)) {
            return originalResponse;
        }

        Request modifiedRequest = removeCacheHeaders(request);

        try {
            Response retriedResponse = chain.proceed(modifiedRequest);

            if (retriedResponse == null || !retriedResponse.isSuccessful()) {
                return originalResponse;
            }

            return retriedResponse;
        } catch (IOException e) {
            return originalResponse;
        }
    }

    private boolean shouldSkipInterceptor(Request request, Response response) {
        // not much we can do in this case
        if (response == null) {
            return true;
        }

        List<String> warningHeaders = response.headers("Warning");

        for (String warningHeader : warningHeaders) {
            // if we can find a warning header saying that this response is stale, we know
            // that we can't skip it.
            if (warningHeader.startsWith(WARNING_RESPONSE_IS_STALE)) {
                return false;
            }
        }

        return true;
    }

    private Request removeCacheHeaders(Request request) {
        Headers modifiedHeaders = request.headers()
                .newBuilder()
                .removeAll("Cache-Control")
                .build();

        return request.newBuilder()
                .headers(modifiedHeaders)
                .build();
    }
}


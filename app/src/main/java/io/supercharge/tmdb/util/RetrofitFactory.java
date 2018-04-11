package io.supercharge.tmdb.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aquajava on 2018. 04. 02..
 */

public class RetrofitFactory {

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            int maxAge = 60; // read from cache for 1 minute
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        }
    };

    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit createForGson(Context context) {
        return create(context, GsonConverterFactory.create());
    }

    public static Retrofit create(Context context, Converter.Factory converterFactory) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL)
                .client(cachingHttpClient(context))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        if (converterFactory != null) {
            builder.addConverterFactory(converterFactory);
        }

        return builder.build();
    }

    private static OkHttpClient cachingHttpClient(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10*1024*1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();

        return client;
    }
}

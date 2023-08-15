package com.android.bindreading.okhttp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpLoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long startTime = System.nanoTime();
        System.out.println("Sending request: " + request.url());

        Response response = chain.proceed(request);

        long endTime = System.nanoTime();
        System.out.println("Received response for " + response.request().url() + " in " + ((endTime - startTime) / 1e6) + " ms");

        return response;
    }
}
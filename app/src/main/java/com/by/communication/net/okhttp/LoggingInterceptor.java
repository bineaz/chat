package com.by.communication.net.okhttp;

import com.by.communication.util.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Produced a lot of bug on 2017/4/10.
 */

class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException
    {
        Request request = chain.request();
        Logger.d("url",request.url().toString());
        Response response = chain.proceed(request);

        return response;
    }
}

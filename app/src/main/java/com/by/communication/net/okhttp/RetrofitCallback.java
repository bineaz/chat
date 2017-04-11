package com.by.communication.net.okhttp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Produced a lot of bug on 2017/4/11.
 */

public abstract class RetrofitCallback implements Callback {

    @Override
    public void onResponse(Call call, Response response)
    {

    }

    @Override
    public void onFailure(Call call, Throwable t)
    {

    }

    public abstract void onProgress(long total, float progress);

}

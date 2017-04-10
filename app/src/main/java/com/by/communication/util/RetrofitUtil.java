package com.by.communication.util;

import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.re.GsonConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class RetrofitUtil {
    public static final int DEFAULT_TIMEOUT = 5;

    private static Retrofit mRetrofit;

    private static RetrofitUtil mInstance;

    /**
     * 私有构造方法
     */
    private RetrofitUtil()
    {
        mRetrofit = new Retrofit.Builder()
                .client(HttpUtil.getInstance().getOkHttpClient())
                .baseUrl(ConstantUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static <T> T service(Class<T> service)
    {
        return mRetrofit.create(service);
    }

    public static RetrofitUtil getInstance()
    {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                mInstance = new RetrofitUtil();
            }
        }
        return mInstance;
    }
}

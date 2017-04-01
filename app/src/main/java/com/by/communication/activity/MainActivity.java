package com.by.communication.activity;

import android.content.Intent;
import android.os.Bundle;

import com.by.communication.R;
import com.by.communication.net.PushSocketService;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.callback.StringCallback;
import com.by.communication.util.ConstantUtil;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, PushSocketService.class));

//        HttpUtil.get().url("http://www.hao123.com").build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id)
//            {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id)
//            {
//                System.out.println(response);
//            }
//        });

        HttpUtil.get().url(ConstantUtil.BASE_URL + "getChatHistory/1").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id)
            {
                System.out.println(response);
            }
        });
    }

    @Override
    public int getLayoutResId()
    {
        return R.layout.activity_main;
    }
}

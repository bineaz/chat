package com.by.communication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.permission.AndPermission;
import com.by.communication.util.Usp;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by SX3 on 2017/1/16.
 */

public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutResId()
    {
        return R.layout.splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestPermission();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

//        PackageManager pm = getPackageManager();
//        try {
//            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }


        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong)
                    {

                        if (Usp.getInstance().isLoggedIn()) {
                            App.getInstance().initUser();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }

                        finish();
                    }
                });
    }

    private void requestPermission()
    {

        AndPermission.with(this)
                .requestCode(100)
                .permission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .send();

    }
}

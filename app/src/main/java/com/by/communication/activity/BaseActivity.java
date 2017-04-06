package com.by.communication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.by.communication.App;

import butterknife.ButterKnife;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private App  app;
    private long user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        app = App.getInstance();
        user_id = app.getUser().getId();

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
    }

    public abstract int getLayoutResId();

    public App getApp()
    {
        return app;
    }

    public long getUser_id()
    {
        return user_id;
    }
}

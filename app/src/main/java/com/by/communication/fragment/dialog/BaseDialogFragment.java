package com.by.communication.fragment.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.by.communication.App;
import com.by.communication.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kwc on 2016/11/16.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    private View view;
    private App  app;

    private int  user_id;
    private long event_id;

    private LayoutInflater inflater;


    //保存activity实例，防止getActivity为null
    private Activity mParent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        this.inflater = inflater;

        mParent = getActivity();

        view = inflater.inflate(getResId(), null);
        app = App.getInstance();

        init(view);
        addListener();
        view.setClickable(true);

        //去掉标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);


        //设置透明背景和圆角
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(Util.dip2px(getActivity(), 5));

        getDialog().getWindow().setBackgroundDrawable(drawable);

        return view;
    }

    public void addListener()
    {

    }


    public Activity getActivity2()
    {
        return mParent;
    }


    public void showKeyboardDelay(final EditText editText)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Util.showKeyBoard(editText);
            }
        }, 100);
    }

    //notify others that something has done
    public void postJson(String action)
    {
        JSONObject object = new JSONObject();
        try {
            object.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(object);
    }


    public abstract int getResId();

    public abstract void init(View view);

    public View getView()
    {
        return view;
    }

    public App getApp()
    {
        return app;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public long getEvent_id()
    {
        return event_id;
    }

    public LayoutInflater getInflater()
    {
        return inflater;
    }
}

package com.by.communication.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.by.communication.util.Util;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public abstract class BaseFragment extends Fragment {

    //当前界面View
    private View view;

    private LayoutInflater inflater;

    //当前activity实例，防止getActivity返回null
    private Activity activity;

    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        this.inflater = inflater;
        activity = getActivity();

        view = inflater.inflate(getResId(), null);
        view.setClickable(true);   //防止点击到下层界面

        unbinder = ButterKnife.bind(this, view);
        init(view);

        return view;
    }

    public abstract int getResId();

    public abstract void init(View view);


    @Nullable
    @Override
    public View getView()
    {
        return view;
    }

    public LayoutInflater getInflater()
    {
        return inflater;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void toast(String message)
    {
        Util.toast(getActivity(), message);
    }
}

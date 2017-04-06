package com.by.communication.fragment;

import android.view.View;

import com.by.communication.R;

/**
 * Produced a lot of bug on 2017/4/6.
 */

public class EmptyFragment extends BaseFragment {
    @Override
    public int getResId()
    {
        return R.layout.empty_fragment;
    }

    @Override
    public void init(View view)
    {

    }
}

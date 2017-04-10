package com.by.communication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.by.communication.R;
import com.by.communication.activity.LoginActivity;
import com.by.communication.util.Usp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Produced a lot of bug on 2017/4/10.
 */

public class LogoutFragment extends BaseFragment {
    @BindView(R.id.logoutFragment_button)
    Button button;

    @Override
    public int getResId()
    {
        return R.layout.logout_fragment;
    }

    @Override
    public void init(View view)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Usp.getInstance().logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }
}

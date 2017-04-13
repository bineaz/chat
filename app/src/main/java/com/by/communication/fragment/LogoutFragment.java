package com.by.communication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.activity.LoginActivity;
import com.by.communication.util.Usp;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;

import butterknife.BindView;

/**
 * Produced a lot of bug on 2017/4/10.
 */

public class LogoutFragment extends BaseFragment {
    @BindView(R.id.logoutFragment_button)
    Button button;
    @BindView(R.id.logoutFragment_camera)
    Button camera;

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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                record();
            }
        });
    }

    private void record()
    {
        // 录制
        Intent intent = new Intent(getActivity(), MediaRecorderActivity.class);
        startActivityForResult(intent, 7001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 7001:
                    String path = data.getStringExtra("mPath");
                    toast(path);
                    break;
            }
        }
    }
}

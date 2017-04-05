package com.by.communication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.by.communication.R;
import com.by.communication.fragment.ChatRecordFragment;
import com.by.communication.fragment.FriendFragment;
import com.by.communication.net.PushSocketService;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.callback.StringCallback;
import com.by.communication.util.ConstantUtil;

import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    @BindView(R.id.mainActivity_viewPager)
    ViewPager viewPager;
    @BindView(R.id.mainActivity_tabLayout)
    TabLayout tabLayout;

    private ArrayList<Fragment> fragmentArrayList;


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


        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new ChatRecordFragment());
        fragmentArrayList.add(new FriendFragment());
        fragmentArrayList.add(new ChatRecordFragment());
        fragmentArrayList.add(new ChatRecordFragment());
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public int getLayoutResId()
    {
        return R.layout.activity_main;
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        int[] title = {R.string.chat, R.string.friend, R.string.chat, R.string.chat};

        public FragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return getString(title[position]);
        }

        @Override
        public Fragment getItem(int position)
        {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount()
        {
            return fragmentArrayList.size();
        }
    }
}

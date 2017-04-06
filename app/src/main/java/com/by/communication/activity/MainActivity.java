package com.by.communication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;
import com.by.communication.fragment.ChatRecordFragment;
import com.by.communication.fragment.EmptyFragment;
import com.by.communication.fragment.FriendFragment;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.net.PushSocketService;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.callback.StringCallback;
import com.by.communication.re.ChatService;
import com.by.communication.util.ConstantUtil;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

//        HttpUtil.get().url("getChatHistory/1").build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id)
//            {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id)
//            {
//                System.out.println(response);
//            }
//        });


        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new FriendFragment());
        fragmentArrayList.add(new ChatRecordFragment());
        fragmentArrayList.add(new EmptyFragment());
        fragmentArrayList.add(new EmptyFragment());
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.db32323d));
        tabLayout.setTabTextColors(ContextCompat.getColor(getApplicationContext(), R.color.color_b0b8b2), Color.WHITE);

        getMessageHistory();
    }

    private void getMessageHistory()
    {
        final ChatMessageDao chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();
        ChatMessage chatMessage = chatMessageDao.queryBuilder()
                .orderDesc(ChatMessageDao.Properties.Id)
                .limit(1)
                .unique();
        long max_message_id = -1;
        if (chatMessage != null) {
            max_message_id = chatMessage.getId();
        }

        RetrofitUtil.getInstance()
                .service(ChatService.class)
                .getChatMessageHistory(getUser_id(), max_message_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<List<ChatMessage>>>() {
                    @Override
                    public void onCompleted()
                    {

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response<List<ChatMessage>> response)
                    {
                        if (response.isSuccess()) {
                            chatMessageDao.insertOrReplaceInTx(response.getData());
                        } else {
                            Util.toast(getApplicationContext(), response.getInfo());
                        }
                    }
                });
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

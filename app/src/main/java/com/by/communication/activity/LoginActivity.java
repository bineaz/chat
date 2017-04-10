package com.by.communication.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.gen.UserDao;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.re.UserService;
import com.by.communication.util.Logger;
import com.by.communication.util.Usp;
import com.by.communication.util.Util;

import butterknife.BindView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.btn_login)
    TextView btn_login;

    @Override
    public int getLayoutResId()
    {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        intiView();
        initListener();
    }

    private void intiView()
    {
    }

    private void initListener()
    {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                login();
            }
        });
    }

    private void login()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://chat.tangcheng.me/index.php/Chat/")
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(HttpUtil.getInstance().getOkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        UserService userService = retrofit.create(UserService.class);

        String phone = et_mobile.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        System.out.println(phone + " hehehehehehe " + password);
        if (phone.isEmpty()) {
            phone = "15757126395";
            password = "123";
        }


        Observable<Response<User>> observable = userService.login(phone, password);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<User>>() {
                    @Override
                    public void onCompleted()
                    {
//                        Logger.e("", "complete");
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response<User> response)
                    {
                        System.out.println(response.toString());
                        if (response.getCode() == response.CODE_SUCCESS) {
                            UserDao userDao = App.getInstance().getDaoSession().getUserDao();
                            userDao.deleteAll();

                            userDao.insertOrReplace(response.getData());

                            Usp.getInstance().login(response.getData());
                            App.getInstance().setUser(response.getData());

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }


                    }
                });
    }
}

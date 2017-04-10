package com.by.communication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.by.communication.App;
import com.by.communication.entity.User;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class Usp {
    SharedPreferences        pref;
    SharedPreferences.Editor editor;

    private static final String USER_PREF = "user";

    private static Usp instance;

    public static Usp getInstance()
    {
        if (instance == null) {
            synchronized (Usp.class) {
                if (instance == null) {
                    instance = new Usp();
                }
            }
        }
        return instance;
    }

    private Usp()
    {
        pref = App.getInstance().getApplicationContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public boolean isLoggedIn()
    {
        return pref.getLong("user_id", -1) > 0;
    }

    public Long getUserId()
    {
        return pref.getLong("user_id", 0);
    }

    public void login(User user)
    {
        editor.putLong("user_id", user.getId());
        editor.commit();
    }

    public void logout()
    {
        editor.clear();
        editor.commit();
    }
}

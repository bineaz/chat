package com.by.communication;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import com.by.communication.entity.User;
import com.by.communication.gen.DaoMaster;
import com.by.communication.gen.DaoSession;
import com.by.communication.gen.UserDao;
import com.by.communication.util.Usp;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public class App extends MultiDexApplication {

    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase          db;
    private DaoMaster               mDaoMaster;
    private DaoSession              mDaoSession;

    private static App instance;

    private User user;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        setDatabase();
    }

    public static App getInstance()
    {
        return instance;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase()
    {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        helper = new DaoMaster.DevOpenHelper(this, "chat", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession()
    {
        return mDaoSession;
    }

    public SQLiteDatabase getDb()
    {
        return db;
    }

    public User getUser()
    {
        return user;
    }

    public long getUserId()
    {
        if (user == null)
            return 0;

        return user.getId();
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void initUser()
    {
        UserDao userDao = mDaoSession.getUserDao();
        user = userDao.load(Usp.getInstance().getUserId());
    }
}

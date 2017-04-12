package com.by.communication.util;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/4/12.
 */

public class ThreadUtil {

    public static void mainThreadDelay(long delay, Action1<Long> action)
    {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }
}

package com.by.communication.re;

import com.by.communication.util.Logger;

import rx.Subscriber;

/**
 * Produced a lot of bug on 2017/4/10.
 */

public abstract class SubscriberAdapter<T> extends Subscriber<T> {

    @Override
    public void onCompleted()
    {

    }

    @Override
    public void onError(Throwable e)
    {
        Logger.e("Exception", e.toString());
    }
}

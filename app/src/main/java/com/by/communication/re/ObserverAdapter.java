package com.by.communication.re;

import com.by.communication.util.Logger;

import rx.Observer;

/**
 * Produced a lot of bug on 2017/4/12.
 */

public abstract class ObserverAdapter<T> implements Observer<T> {
    @Override
    public void onCompleted()
    {

    }

    @Override
    public void onError(Throwable e)
    {
        Logger.e("ObserverAdapter", e.getMessage());
    }
}

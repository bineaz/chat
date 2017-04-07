package com.by.communication.util.an;

import android.view.View;

/**
 * Created by florentchampigny on 22/12/2015.
 */
public class AnimationListener {

    private AnimationListener(){}

    public interface OnStartListener {
        void onStart();
    }

    public interface OnStopListener {
        void onStop();
    }

    public interface Update<V extends View>{
        void update(V view, float value);
    }
}

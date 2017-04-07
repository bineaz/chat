package com.by.communication.util.an;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.by.communication.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 22/12/2015.
 * Modified by gzu-liyujiang on 24/01/2016.
 */
public class Anim {
    public static final long DEFAULT_DURATION = 300;

    private List<AnimationBuilder> animationList = new ArrayList<>();
    private long                   duration      = DEFAULT_DURATION;
    private long                   startDelay    = 0;
    private Interpolator           interpolator  = new AccelerateDecelerateInterpolator();

    private int repeatCount = 0;
    private int repeatMode  = ValueAnimator.RESTART;

    private AnimatorSet animatorSet;
    private View waitForThisViewHeight = null;

    private AnimationListener.OnStartListener onStartListenerListener;
    private AnimationListener.OnStopListener  onStopListenerListener;

    private Anim prev = null;
    private Anim next = null;


    public static void setFtAnim(FragmentTransaction ft)
    {
        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_left_out,
                R.anim.slide_right_in, R.anim.slide_right);

    }

    /**
     * The interface Repeat mode.
     */
    @IntDef(value = {ValueAnimator.RESTART, ValueAnimator.REVERSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatMode {
        //代替enum，据说枚举类极其耗费内存
    }

    public static AnimationBuilder animate(View... view)
    {
        Anim anim = new Anim();
        return anim.addAnimationBuilder(view);
    }

    public AnimationBuilder thenAnimate(View... views)
    {
        Anim nextAnim = new Anim();
        this.next = nextAnim;
        nextAnim.prev = this;
        return nextAnim.addAnimationBuilder(views);
    }

    public AnimationBuilder addAnimationBuilder(View... views)
    {
        AnimationBuilder animationBuilder = new AnimationBuilder(this, views);
        animationList.add(animationBuilder);
        return animationBuilder;
    }

    protected AnimatorSet createAnimatorSet()
    {
        final List<Animator> animators = new ArrayList<>();
        for (AnimationBuilder animationBuilder : animationList) {
            List<Animator> animatorList = animationBuilder.createAnimators();
            if (animationBuilder.getSingleInterpolator() != null) {
                for (Animator animator : animatorList) {
                    animator.setInterpolator(animationBuilder.getSingleInterpolator());
                }
            }
            animators.addAll(animatorList);
        }

        for (AnimationBuilder animationBuilder : animationList) {
            if (animationBuilder.isWaitForHeight()) {
                waitForThisViewHeight = animationBuilder.getView();
                break;
            }
        }

        for (Animator animator : animators) {
            if (animator instanceof ValueAnimator) {
                ValueAnimator valueAnimator = (ValueAnimator) animator;
                valueAnimator.setRepeatCount(repeatCount);
                valueAnimator.setRepeatMode(repeatMode);
            }
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        animatorSet.setDuration(duration);
        animatorSet.setStartDelay(startDelay);
        if (interpolator != null)
            animatorSet.setInterpolator(interpolator);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation)
            {
                if (onStartListenerListener != null) onStartListenerListener.onStart();
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (onStopListenerListener != null) onStopListenerListener.onStop();

                for (int i = 0; i < animationList.size(); i++) {
                    animationList.get(i).clearLayerType();
                }

                if (next != null) {
                    next.prev = null;
                    next.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        return animatorSet;
    }

    public Anim start()
    {
        if (prev != null) {
            prev.start();
        } else {
            animatorSet = createAnimatorSet();

            if (waitForThisViewHeight != null) {
                waitForThisViewHeight.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw()
                    {
                        animatorSet.start();
                        waitForThisViewHeight.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
            } else {
                animatorSet.start();
            }
        }
        return this;
    }

    public void cancel()
    {
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (next != null) {
            next.cancel();
            next = null;
        }
    }

    public Anim duration(long duration)
    {
        this.duration = duration;
        return this;
    }

    public Anim startDelay(long startDelay)
    {
        this.startDelay = startDelay;
        return this;
    }

    /**
     * Repeat count of animation.
     *
     * @param repeatCount the repeat count
     * @return the view animation
     */
    public Anim repeatCount(@IntRange(from = -1) int repeatCount)
    {
        this.repeatCount = repeatCount;
        return this;
    }

    /**
     * Repeat mode view animation.
     *
     * @param repeatMode the repeat mode
     * @return the view animation
     */
    public Anim repeatMode(@RepeatMode int repeatMode)
    {
        this.repeatMode = repeatMode;
        return this;
    }

    public Anim onStart(AnimationListener.OnStartListener onStartListenerListener)
    {
        this.onStartListenerListener = onStartListenerListener;
        return this;
    }

    public Anim onStop(AnimationListener.OnStopListener onStopListenerListener)
    {
        this.onStopListenerListener = onStopListenerListener;
        return this;
    }

    /**
     * Interpolator view animator.
     *
     * @param interpolator the interpolator
     * @return the view animator
     * @link https://github.com/cimi-chen/EaseInterpolator
     */
    public Anim interpolator(Interpolator interpolator)
    {
        this.interpolator = interpolator;
        return this;
    }

}

package com.by.communication.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.by.communication.R;
import com.by.communication.util.an.Anim;


/**
 * Created by admin on 2016/10/23.
 */

public class FragmentUtil {

    public static boolean addFragment(FragmentManager fm, int containerId, Fragment fragment, boolean anim)
    {

        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();

            if (anim) {
                Anim.setFtAnim(ft);
            }

            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }

    public static boolean addFragment(FragmentManager fm, int containerId, Fragment fragment, int animType)
    {

        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();

            setAnimator(ft, animType);

            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }

    public static boolean addFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment, int animType)
    {

        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();

            setAnimator(ft, animType);

            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }


    public static boolean addFragment(FragmentManager fm, int containerId, Fragment fragment)
    {
        return addFragment(fm, containerId, fragment, false);
    }

    public static boolean addFragment(FragmentManager fm, int containerId, Fragment fragment, int enter, int exit, int popEnter, int popExit)
    {
        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(enter, exit, popEnter, popExit);
            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }

    public static boolean addFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment, int enter, int exit, int popEnter, int popExit)
    {
        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(enter, exit, popEnter, popExit);
            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }


    public static void addFragmentOrReplace(FragmentManager fm, int containerId, Fragment fragment, boolean anim)
    {

        FragmentTransaction ft = fm.beginTransaction();

        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) != null) {
            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
        } else {
            ft.replace(containerId, fragment, fragment.getClass().getSimpleName());
        }

        if (anim) {
            Anim.setFtAnim(ft);
        }

        ft.add(containerId, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
        ft.commit();
    }

    public static boolean replaceFragment(FragmentManager fm, int containerId, Fragment fragment, boolean anim)
    {

        FragmentTransaction ft = fm.beginTransaction();

        if (anim) {
            Anim.setFtAnim(ft);
        }
        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) != null) {
            ft.remove(fm.findFragmentByTag(fragment.getClass().getSimpleName()));
            fm.popBackStack();
            System.out.println("removeFragment:" + fragment.getClass().getSimpleName());
        }


        ft.add(containerId, fragment, fragment.getClass().getSimpleName());

        System.out.println("addFragment:" + fragment.getClass().getSimpleName());

        ft.addToBackStack(fragment.getClass().getSimpleName());

        try {
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean replaceFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment, boolean anim)
    {

        FragmentTransaction ft = fm.beginTransaction();

        if (anim) {
            Anim.setFtAnim(ft);
        }


        ft.add(containerId, fragment, fragment.getClass().getSimpleName());

        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    public static boolean hasFragment(FragmentManager fm, Class<? extends Fragment> fragment)
    {
        if (fm.findFragmentByTag(fragment.getSimpleName()) != null) {
            System.out.println("hasFragment " + fragment.getSimpleName());
            return true;
        } else {
            System.out.println(fragment.getSimpleName() + " not found");
            return false;
        }
    }

    public static boolean addFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment)
    {
        return addFragmentNoBackStack(fm, containerId, fragment, false);
    }

    public static boolean addFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment, boolean anim)
    {

        if (fm.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            FragmentTransaction ft = fm.beginTransaction();

            if (anim) {
                Anim.setFtAnim(ft);
            }

            ft.add(containerId, fragment, fragment.getClass().getSimpleName());
            ft.commit();
        } else {
            System.out.println(fragment.getClass().getSimpleName() + " has added");
            return false;
        }
        return true;
    }

    public static void popFragment(FragmentManager fm, int count)
    {
        for (int i = 0; i < count; i++) {
            fm.popBackStack();
        }
    }

    public static final int SLIDE_DOWN_SHOW  = 0;
    public static final int BOOST            = 1;
    public static final int SLIDE_UP         = 2;
    public static final int SPREAD           = 3;
    public static final int BOOST_2_AND_SHOW = 4;
    public static final int ALPHA            = 5;

    private static void setAnimator(FragmentTransaction ft, int animatorType)
    {

        switch (animatorType) {
//            case SLIDE_DOWN_SHOW:
//                ft.setCustomAnimations(R.animator.show_slide_down, R.animator.fade_slide_up, R.animator.show_slide_down, R.animator.fade_slide_up);
//                break;
//            case BOOST:
//                ft.setCustomAnimations(R.animator.boost, R.animator.scale, R.animator.boost, R.animator.scale);
//                break;
//
            case SPREAD:
                ft.setCustomAnimations(R.anim.open, R.anim.fate, R.anim.open, R.anim.fate);
                break;
//            case BOOST_2_AND_SHOW:
//                ft.setCustomAnimations(R.animator.scale_2_and_show, R.animator.boost_2_and_fade, R.animator.scale_2_and_show, R.animator.boost_2_and_fade);
//                break;
//            case ALPHA:
//                ft.setCustomAnimations(R.animator.show, R.animator.fade, R.animator.show, R.animator.fade);
//                break;

        }
    }
}

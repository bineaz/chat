package com.by.communication.util;

import android.content.Context;
import android.widget.ImageView;

import com.by.communication.R;
import com.squareup.picasso.Picasso;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class ImageUtil {

    public static void displayAvatar(Context context, ImageView imageView, int user_id)
    {
        Picasso.with(context).load(R.mipmap.icon).into(imageView);
    }
}

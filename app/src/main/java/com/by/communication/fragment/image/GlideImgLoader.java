
package com.by.communication.fragment.image;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.by.communication.R;

import java.io.File;

public class GlideImgLoader implements ImgLoader {
    @Override
    public void onPresentImage(ImageView imageView, String imageUri, int size)
    {
        Glide.with(imageView.getContext())
                .load(new File(imageUri))
                .centerCrop()
                .placeholder(R.mipmap.default_img)
                .into(imageView);

    }

}

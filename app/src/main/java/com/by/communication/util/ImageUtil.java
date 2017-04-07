package com.by.communication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatFile;
import com.by.communication.entity.ChatMessage;
import com.by.communication.gen.ChatFileDao;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class ImageUtil {
    private static LruCache<String, Bitmap> cache = new LruCache<>(8 * 1024 * 1024);

    public static void displayAvatar(Context context, ImageView imageView, long user_id)
    {
        Picasso.with(context).load(R.mipmap.icon).into(imageView);
    }

    public static void setBitmapWithRatio(ImageView imageView, Bitmap bitmap, int width)
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = Util.dip2px(imageView.getContext(), 150);
        float ratio = bitmap.getHeight() * 1f / bitmap.getWidth();
        layoutParams.height = (int) (layoutParams.width * ratio);

        imageView.setLayoutParams(layoutParams);

        imageView.setImageBitmap(bitmap);
    }

    public static void displayImage(final ImageView imageView, final String path)
    {
        if (path == null) {
            imageView.setImageResource(R.mipmap.default_img);
            return;
        }

        //内存加载
        if (cache.get(path) != null) {
            setBitmapWithRatio(imageView, cache.get(path), 150);
            return;
        }

        //数据库加载
        final ChatFileDao chatFileDao = App.getInstance().getDaoSession().getChatFileDao();
        ChatFile chatFile = chatFileDao.queryBuilder().where(ChatFileDao.Properties.File_name.eq(path)).unique();

        if (chatFile != null) {
            Logger.e(chatFile);
            byte[] bytes = chatFile.getValue();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            cache.put(path, bitmap);
            setBitmapWithRatio(imageView, bitmap, 150);

            return;
        }

        //网络加载
        Picasso.with(imageView.getContext())
                .load(ConstantUtil.IMAGE_BASE_URL + path)
                .placeholder(R.mipmap.default_img)
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                    {
                        cache.put(path, bitmap);
                        chatFileDao.insertOrReplace(new ChatFile(
                                ChatMessage.IMAGE,
                                path,
                                Util.convertBitmapToByte(bitmap)
                        ));

                        setBitmapWithRatio(imageView, bitmap, 150);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable)
                    {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable)
                    {

                    }
                });
    }
}

package com.by.communication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatFile;
import com.by.communication.entity.ChatMessage;
import com.by.communication.gen.ChatFileDao;
import com.by.communication.re.ObserverAdapter;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class ImageUtil {
    private static LruCache<String, Bitmap> cache = new LruCache<>(8 * 1024 * 1024);

    public static void displayAvatar(Context context, ImageView imageView, long user_id)
    {
        Glide.with(context).load(R.mipmap.icon).into(imageView);
    }

    public static void setBitmapWithRatio(ImageView imageView, Bitmap bitmap, int width)
    {
//        System.out.println(bitmap.getByteCount() + "   " + bitmap.getWidth() + "  " + bitmap.getHeight());

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
        List<ChatFile> chatFileList = chatFileDao.queryBuilder().where(ChatFileDao.Properties.File_name.eq(path)).list();

        if (!chatFileList.isEmpty()) {
            final ChatFile chatFile = chatFileList.get(0);
            Logger.e(chatFile);

            Observable
                    .create(new Observable.OnSubscribe<Bitmap>() {
                        @Override
                        public void call(Subscriber<? super Bitmap> subscriber)
                        {
                            byte[] bytes = chatFile.getValue();

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                            bitmap = compressImage(bitmap);
                            subscriber.onNext(bitmap);
                            Glide.with(imageView.getContext())
                                    .fromBytes()
                                    .load(bytes)
                                    .into(imageView);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ObserverAdapter<Bitmap>() {

                        @Override
                        public void onNext(Bitmap bitmap)
                        {
                            cache.put(path, bitmap);
                            setBitmapWithRatio(imageView, bitmap, 150);
                        }
                    });


            return;
        }


        //网络加载
        Glide.with(imageView.getContext())
                .load(ConstantUtil.IMAGE_BASE_URL + path)
                .asBitmap()
                .placeholder(R.mipmap.default_img)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation glideAnimation)
                    {
                        Observable
                                .create(new Observable.OnSubscribe<Bitmap>() {
                                    @Override
                                    public void call(Subscriber<? super Bitmap> subscriber)
                                    {
                                        chatFileDao.insertOrReplace(new ChatFile(
                                                ChatMessage.IMAGE,
                                                path,
                                                Util.convertBitmapToByte(bitmap)
                                        ));
                                        Bitmap b = compressImage(bitmap);
                                        subscriber.onNext(b);
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ObserverAdapter<Bitmap>() {

                                    @Override
                                    public void onNext(Bitmap bitmap)
                                    {
                                        cache.put(path, bitmap);
                                        setBitmapWithRatio(imageView, bitmap, 150);
                                    }
                                });

                    }
                });
    }

    private static Bitmap compressImage(Bitmap bitmap)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int min = 240;

        if (w > h) {
            w = min * w / h;
            h = min;
        } else {
            h = min * h / w;
            w = min;
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

}

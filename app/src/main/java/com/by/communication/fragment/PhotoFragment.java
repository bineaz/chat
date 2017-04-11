package com.by.communication.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatFile;
import com.by.communication.gen.ChatFileDao;
import com.by.communication.util.Util;
import com.by.communication.widgit.imageView.photoView.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Produced a lot of bug on 2017/4/11.
 */

public class PhotoFragment extends BaseFragment {
    @BindView(R.id.photoFragment_photoView)
    PhotoView photoView;

    @Override
    public int getResId()
    {
        return R.layout.photo_fragment;
    }

    @Override
    public void init(View view)
    {
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().popBackStack();
            }
        });

        try {
            String path = getArguments().getString("path");

            ChatFileDao chatFileDao = App.getInstance().getDaoSession().getChatFileDao();
            ChatFile chatFile = chatFileDao.queryBuilder().where(ChatFileDao.Properties.File_name.eq(path)).unique();

            if (chatFile != null) {

                byte[] bytes = chatFile.getValue();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                photoView.setImageBitmap(bitmap);
                photoView.enable();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Util.toast(getActivity(), "path not defined");
            getFragmentManager().popBackStack();
        }
    }


}

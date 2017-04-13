/*
 *
 *  * Copyright (C) 2015 Eason.Lai (easonline7@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.by.communication.fragment.image;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by.communication.R;
import com.by.communication.fragment.BaseFragment;
import com.by.communication.permission.AndPermission;
import com.by.communication.util.Logger;
import com.by.communication.util.Util;
import com.by.communication.widgit.listView.adapter.ListHolder;
import com.by.communication.widgit.listView.adapter.MultiListAdapter;
import com.by.communication.widgit.listView.adapter.SingleListAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickImageFragment extends BaseFragment implements OnImagesLoadedListener {
    private static final String TAG = PickImageFragment.class.getSimpleName();

    public static final String KEY_PIC_PATH              = "key_pic_path";
    public static final String KEY_PIC_SELECTED_POSITION = "key_pic_selected";


    private boolean showCamera  = true;
    private int     selectLimit = 9;
    private boolean single      = true;

    public static final int REQ_CAMERA  = 1431;
    public static final int REQ_PREVIEW = 2347;

    public static final int AVATAR_ACTION     = 8;
    public static final int BACKGROUND_ACTION = 9;

    Activity mContext;

    GridView         mGridView;
    ImageGridAdapter mAdapter;
    int              imageGridSize;

    Button btnDir;//button to change ImageSet
    private View            mFooterView;
    private ListPopupWindow mFolderPopupWindow;//ImageSet PopupWindow
    private ImageSetAdapter mImageSetAdapter;
    List<ImageSet> mImageSetList;//data of all ImageSets

    ImgLoader mImagePresenter;

    private static final int ITEM_TYPE_CAMERA = 0;//the first Item may be Camera
    private static final int ITEM_TYPE_NORMAL = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public int getResId()
    {
        return R.layout.pick_image_fragment;
    }

    @Override
    public void init(View contentView)
    {
        ImageView backImageView = (ImageView) contentView.findViewById(R.id.btn_backPress);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().popBackStack();
            }
        });


        mFooterView = contentView.findViewById(R.id.footer_panel);
        imageGridSize = (mContext.getWindowManager().getDefaultDisplay().getWidth() - Util.dip2px(mContext, 2) * 2) / 3;
        btnDir = (Button) contentView.findViewById(R.id.btn_dir);
        mGridView = (GridView) contentView.findViewById(R.id.gridview);

        mImagePresenter = new GlideImgLoader();

        DataSource dataSource = new LocalDataSource(mContext);
        dataSource.provideMediaItems(this);//select all images from local database

        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList(width, height);
                }
                backgroundAlpha(0.3f);
                mImageSetAdapter.refreshData(mImageSetList);
                mFolderPopupWindow.setAdapter(mImageSetAdapter);
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mImageSetAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }

            }
        });

        mImageSetAdapter = new ImageSetAdapter(mContext, new ArrayList<ImageSet>());
        mImageSetAdapter.refreshData(mImageSetList);
    }

    /**
     * Adapter of image GridView
     */
    class ImageGridAdapter extends MultiListAdapter<ImageItem> {
        List<ImageItem> images;

        public ImageGridAdapter(Context context, List<ImageItem> dataList)
        {
            super(context, dataList);
            this.images = dataList;
        }

        @Override
        public int getViewType(ImageItem data, int position)
        {
            if (shouldShowCamera()) {
                return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
            }
            return ITEM_TYPE_NORMAL;
        }

        @Override
        public int getLayoutId(int viewType)
        {
            return viewType == ITEM_TYPE_CAMERA ? R.layout.grid_item_camera : R.layout.image_grid_item;
        }

        @Override
        public int getItemViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getCount()
        {
            return shouldShowCamera() ? images.size() + 1 : images.size();
        }

        @Override
        public ImageItem getItem(int position)
        {
            if (shouldShowCamera()) {
                if (position == 0) {
                    return null;
                }
                return images.get(position - 1);
            } else {
                return images.get(position);
            }

        }


        @Override
        public void convert(ListHolder holder, final ImageItem data, int position)
        {

            if (getItemViewType(position) == ITEM_TYPE_CAMERA) {
                TextView cameraTextView = holder.getView(R.id.cameraTextView);
                cameraTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (AndPermission.hasPermission(getActivity(), Manifest.permission.CAMERA)) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Ensure that there's a camera activity to handle the intent
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                File photoFile = createImageSaveFile(getActivity());
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    Log.i(TAG, "=====file ready to take photo:" + photoFile.getAbsolutePath());
                                }
                            }
                            startActivityForResult(takePictureIntent, REQ_CAMERA);
                        } else if (AndPermission.hasAlwaysDeniedPermission(getActivity(), Manifest.permission.CAMERA)) {
                            AndPermission.defaultSettingDialog(getActivity(), 100).show();
                        } else {
                            AndPermission.with(getActivity()).permission(Manifest.permission.CAMERA).requestCode(100).send();
                        }
                    }
                });
            } else {
                ImageView ivPic = holder.getView(R.id.iv_thumb);
                CheckBox cbSelected = holder.getView(R.id.iv_thumb_check);
                View cbPanel = holder.getView(R.id.thumb_check_panel);

                cbSelected.setVisibility(View.GONE);
                mImagePresenter.onPresentImage(ivPic, data.path, imageGridSize);

                ivPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        getFragmentManager().popBackStack();
                        onImagePickListener.onPick(data);
                    }
                });
            }


        }

        public void refreshData(List<ImageItem> items)
        {
            if (items != null && items.size() > 0) {
                images = items;
            }
            notifyDataSetChanged();
        }

    }

    private boolean shouldShowCamera()
    {
        return true;
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList)
    {

        mImageSetList = imageSetList;

        btnDir.setText(imageSetList.get(0).name);
        mAdapter = new ImageGridAdapter(mContext, imageSetList.get(0).imageItems);
        mGridView.setAdapter(mAdapter);
    }

    private String mCurrentPhotoPath;//image saving path when taking pictures

    /**
     * create a file to save photo
     *
     * @param ctx
     * @return
     */
    private File createImageSaveFile(Context ctx)
    {
        if (Util.isStorageEnable()) {
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!pic.exists()) {
                pic.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "IMG_" + timeStamp;
            File tmpFile = new File(pic, fileName + ".jpg");
            mCurrentPhotoPath = tmpFile.getAbsolutePath();
            Log.i(TAG, "=====camera path:" + mCurrentPhotoPath);
            return tmpFile;
        } else {
            //File cacheDir = ctx.getCacheDir();
            File cacheDir = Environment.getDataDirectory();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "IMG_" + timeStamp;
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            mCurrentPhotoPath = tmpFile.getAbsolutePath();
            Log.i(TAG, "=====camera path:" + mCurrentPhotoPath);
            return tmpFile;
        }


    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height)
    {
        mFolderPopupWindow = new ListPopupWindow(mContext);
        //mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mImageSetAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mFooterView);
        mFolderPopupWindow.setModal(true);

        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss()
            {
                backgroundAlpha(1f);
            }
        });

        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);

        mFolderPopupWindow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                mImageSetAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView tempAdapterView = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        mFolderPopupWindow.dismiss();

                        ImageSet imageSet = (ImageSet) tempAdapterView.getAdapter().getItem(index);
                        if (null != imageSet) {
                            mAdapter.refreshData(imageSet.imageItems);
                            btnDir.setText(imageSet.name);

                        }
                        // scroll to the top
                        mGridView.smoothScrollToPosition(0);

                    }
                }, 100);

            }
        });

    }

    // 设置屏幕透明度
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        mContext.getWindow().setAttributes(lp);
    }


    private class ImageSetAdapter extends SingleListAdapter<ImageSet> {

        private List<ImageSet> mImageSets;

        int lastSelected = 0;

        public ImageSetAdapter(Context context, List<ImageSet> dataList)
        {
            super(dataList);
            mImageSets = dataList;
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.list_item_folder;
        }

        @Override
        public void convert(ListHolder holder, ImageSet data, int position)
        {
            ImageView cover = holder.getView(R.id.broadLocationItem_imageView);
            TextView name = holder.getView(R.id.nameTextView);
            TextView size = holder.getView(R.id.sizeTextView);
            ImageView indicator = holder.getView(R.id.checkImageView);

            name.setText(data.name);
            size.setText(data.imageItems.size() + mContext.getResources().getString(R.string.piece));
            mImagePresenter.onPresentImage(cover, data.cover.path, imageGridSize);

            if (lastSelected == position) {
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.INVISIBLE);
            }
        }

        public void refreshData(List<ImageSet> folders)
        {
            if (folders != null && folders.size() > 0) {
                mImageSets.clear();
                mImageSets.addAll(folders);
            } else {
                mImageSets.clear();
            }
            notifyDataSetChanged();
        }

        public void setSelectIndex(int i)
        {
            if (lastSelected == i) {
                return;
            }
            lastSelected = i;
            notifyDataSetChanged();
        }

        public int getSelectIndex()
        {
            return lastSelected;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PickImageFragment.REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                galleryAddPic(mContext, mCurrentPhotoPath);
                //androidImagePicker.notifyPictureTaken();

                ImageItem item = new ImageItem(mCurrentPhotoPath, "", -1);

                getFragmentManager().popBackStack();
                onImagePickListener.onPick(item);


            } else {
                Logger.e(TAG, "didn't save to your path");
            }
        }

    }

    /**
     * scan the photo so that the gallery can read it
     *
     * @param ctx
     * @param path
     */
    public static void galleryAddPic(Context ctx, String path)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
        Logger.d(TAG, "=====MediaScan:" + path);
    }

    private OnImagePickListener onImagePickListener;

    public OnImagePickListener getOnImagePickListener()
    {
        return onImagePickListener;
    }

    public void setOnImagePickListener(OnImagePickListener onImagePickListener)
    {
        this.onImagePickListener = onImagePickListener;
    }

    public interface OnImagePickListener {
        void onPick(ImageItem item);
    }

}

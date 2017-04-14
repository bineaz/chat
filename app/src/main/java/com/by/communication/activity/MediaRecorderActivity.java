package com.by.communication.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.by.communication.R;
import com.by.communication.camera.MediaRecorderBase;
import com.by.communication.camera.MediaRecorderBase.OnPreparedListener;
import com.by.communication.camera.MediaRecorderNative;
import com.by.communication.camera.MediaRecorderSystem;
import com.by.communication.camera.VCamera;
import com.by.communication.entity.MediaObject;
import com.by.communication.util.DeviceUtils;
import com.by.communication.util.FileUtils;
import com.by.communication.util.Logger;
import com.by.communication.util.Util;
import com.by.communication.widgit.view.ProgressView;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * 视频录制
 *
 * @author tangjun@yixia.com
 */
public class MediaRecorderActivity extends VideoBaseActivity implements
        OnClickListener, OnPreparedListener, MediaRecorderBase.OnEncodeListener {

    //录制最长时间
    public final static  int RECORD_TIME_MAX            = 10 * 1000;
    // 录制最小时间
    public final static  int RECORD_TIME_MIN            = 2 * 1000;
    // 刷新进度条
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    // 延迟拍摄停止
    private static final int HANDLE_STOP_RECORD         = 1;
    //对焦
    private static final int HANDLE_HIDE_RECORD_FOCUS   = 2;

    // 对焦图标-带动画效果
    @BindView(R.id.recordFocusImageView)
    ImageView recordFocusImageView;

    //前后摄像头切换
    @BindView(R.id.switchCameraImageView)
    ImageView switchCameraImageView;

    // 回删按钮、延时按钮、滤镜按钮
    @BindView(R.id.recordDeleteTextView)
    CheckedTextView recordDeleteTextView;

    // 闪光灯
    @BindView(R.id.recordLedCheckBox)
    CheckBox recordLedCheckBox;

    //拍摄按钮
    @BindView(R.id.recordControllerImageView)
    ImageView recordControllerImageView;

    // 视频预览
    @BindView(R.id.recordSurfaceView)
    SurfaceView recordSurfaceView;

    //录制进度
    @BindView(R.id.recordProgressView)
    ProgressView recordProgressView;

    //取消录制
    @BindView(R.id.cancelRecordImageView)
    ImageView cancelRecordImageView;

    //对焦动画
    private Animation mFocusAnimation;

    // SDK视频录制对象
    private MediaRecorderBase mMediaRecorder;
    // 视频信息
    private MediaObject       mMediaObject;

    //on
    private          boolean mCreated;
    // 是否是点击状态
    private volatile boolean mPressedStatus;
    // 是否已经释放
    private volatile boolean mReleased;
    // 对焦图片宽度
    private          int     mFocusWidth;
    // 屏幕宽度
    private          int     mWindowWidth;

    private boolean mHasStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mCreated = false;
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        mWindowWidth = DeviceUtils.getScreenWidth(this);
        mFocusWidth = Util.dip2px(this, 64);
        initView();
        mCreated = true;
    }

    @Override
    public int getLayoutResId()
    {
        return R.layout.activity_media_recorder;
    }


    private void initView()
    {

        // ~~~ 绑定事件
        if (DeviceUtils.hasICS())
            recordSurfaceView.setOnTouchListener(mOnSurfaceViewTouchListener);

        cancelRecordImageView.setOnClickListener(this);
        recordDeleteTextView.setOnClickListener(this);
        recordControllerImageView.setOnClickListener(this);

        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
            switchCameraImageView.setOnClickListener(this);
        } else {
            switchCameraImageView.setVisibility(View.GONE);
        }

        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            recordLedCheckBox.setVisibility(View.VISIBLE);
            recordLedCheckBox.setOnClickListener(this);
        } else {
            recordLedCheckBox.setVisibility(View.GONE);
        }

        recordProgressView.setMaxDuration(RECORD_TIME_MAX);
    }


    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder()
    {
        mMediaRecorder = new MediaRecorderNative();

        mMediaRecorder.setOnEncodeListener(this);
        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                VCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(recordSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnSurfaceViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (mMediaRecorder == null || !mCreated) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    if (checkCameraFocus(event))
                        return true;
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume()
    {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            recordLedCheckBox.setChecked(false);
            mMediaRecorder.prepare();
            recordProgressView.setData(mMediaObject);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (!mHasStop) {
            stopRecord();
        }
        UtilityAdapter.freeFilterParser();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }

    /**
     * 手动对焦
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean checkCameraFocus(MotionEvent event)
    {
        recordFocusImageView.setVisibility(View.GONE);
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect touchRect = new Rect((int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));
        // The direction is relative to the sensor orientation, that is, what
        // the sensor sees. The direction is not affected by the rotation or
        // mirroring of setDisplayOrientation(int). Coordinates of the rectangle
        // range from -1000 to 1000. (-1000, -1000) is the upper left point.
        // (1000, 1000) is the lower right point. The width and height of focus
        // areas cannot be 0 or negative.
        // No matter what the zoom level is, (-1000,-1000) represents the top of
        // the currently visible camera frame
        if (touchRect.right > 1000)
            touchRect.right = 1000;
        if (touchRect.bottom > 1000)
            touchRect.bottom = 1000;
        if (touchRect.left < 0)
            touchRect.left = 0;
        if (touchRect.right < 0)
            touchRect.right = 0;

        if (touchRect.left >= touchRect.right
                || touchRect.top >= touchRect.bottom)
            return false;

        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(touchRect, 1000));
        if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera)
            {
                // if (success) {
                recordFocusImageView.setVisibility(View.GONE);
                // }
            }
        }, focusAreas)) {
            recordFocusImageView.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) recordFocusImageView.getLayoutParams();
        int left = touchRect.left - (mFocusWidth / 2);// (int) x -
        // (focusingImage.getWidth()
        // / 2);
        int top = touchRect.top - (mFocusWidth / 2);// (int) y -
        // (focusingImage.getHeight()
        // / 2);
        if (left < 0)
            left = 0;
        else if (left + mFocusWidth > mWindowWidth)
            left = mWindowWidth - mFocusWidth;
        if (top + mFocusWidth > mWindowWidth)
            top = mWindowWidth - mFocusWidth;

        lp.leftMargin = left;
        lp.topMargin = top;
        recordFocusImageView.setLayoutParams(lp);
        recordFocusImageView.setVisibility(View.VISIBLE);

        if (mFocusAnimation == null)
            mFocusAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.record_focus);

        recordFocusImageView.startAnimation(mFocusAnimation);

        mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
        return true;
    }

    /**
     * 开始录制
     */
    private void startRecord()
    {
        if (mMediaRecorder != null) {
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            // 如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
            if (mMediaRecorder instanceof MediaRecorderSystem) {
                switchCameraImageView.setVisibility(View.GONE);
            }
            recordProgressView.setData(mMediaObject);
        }

        mPressedStatus = true;

        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    RECORD_TIME_MAX - mMediaObject.getDuration());
        }
        recordDeleteTextView.setVisibility(View.GONE);
        switchCameraImageView.setEnabled(false);
        recordLedCheckBox.setEnabled(false);
    }

    @Override
    public void onBackPressed()
    {
        if (recordDeleteTextView != null && recordDeleteTextView.isChecked()) {
            cancelDelete();
            return;
        }

        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {
                                    mMediaObject.delete();
                                    finish();
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null)
                    .setCancelable(false)
                    .show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
    }

    /**
     * 停止录制
     */
    private void stopRecord()
    {
        mHasStop = true;
        mPressedStatus = false;

        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }

        //recordDeleteTextView.setVisibility(View.VISIBLE);
        switchCameraImageView.setEnabled(true);
        recordLedCheckBox.setEnabled(true);

        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    @Override
    public void onClick(View v)
    {
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        switch (v.getId()) {
            case R.id.cancelRecordImageView:
                onBackPressed();
                break;

            case R.id.recordControllerImageView:
                if (mMediaRecorder == null) {
                    return;
                }
                if (mPressedStatus) {
                    int duration = mMediaObject.getDuration();
                    if (duration < RECORD_TIME_MIN) {
                        toast("最少需要录制2秒！");
                        return;
                    }
                    recordControllerImageView.setImageResource(R.drawable.video_stop_record_bg);
                    stopRecord();
                } else {
                    recordControllerImageView.setImageResource(R.mipmap.stop_record);
                    startRecord();
                }
                break;

            case R.id.switchCameraImageView:
                if (recordLedCheckBox.isChecked()) {
                    if (mMediaRecorder != null) {
                        mMediaRecorder.toggleFlashMode();
                    }
                    recordLedCheckBox.setChecked(false);
                }

                if (mMediaRecorder != null) {
                    mMediaRecorder.switchCamera();
                }

                if (mMediaRecorder.isFrontCamera()) {
                    recordLedCheckBox.setEnabled(false);
                } else {
                    recordLedCheckBox.setEnabled(true);
                }
                break;

            case R.id.recordLedCheckBox:
                if (mMediaRecorder != null) {
                    if (mMediaRecorder.isFrontCamera()) {
                        return;
                    }
                }

                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                break;

        }
    }

    /**
     * 取消回删
     */
    private boolean cancelDelete()
    {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
                recordDeleteTextView.setChecked(false);

                if (recordProgressView != null)
                    recordProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    private void reRecord()
    {
        initMediaRecorder();
        recordProgressView.setData(mMediaObject);
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus()
    {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    switchCameraImageView.setVisibility(View.VISIBLE);
                    recordDeleteTextView.setVisibility(View.GONE);
                }
            } else {
                mMediaRecorder.startEncoding();
            }
        }
        return duration;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            stopRecord();
                        } else {

                            System.out.println("hahahaahhahahahahahhahah");
                            if (recordProgressView != null)
                                recordProgressView.invalidate();
                            // if (mPressedStatus)
                            // titleText.setText(String.format("%.1f",
                            // mMediaRecorder.getDuration() / 1000F));
                            if (mPressedStatus)
                                sendEmptyMessageDelayed(0, 30);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onEncodeStart()
    {
        showProgress("", getString(R.string.record_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(int progress)
    {
        Logger.e("[MediaRecorderActivity]onEncodeProgress..." + progress);
    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete()
    {
        hideProgress();
        Intent data = new Intent();
        data.putExtra("mPath", mMediaObject.getOutputTempVideoPath());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在
     */
    @Override
    public void onEncodeError()
    {
        hideProgress();
        toast(getString(R.string.record_video_transcoding_faild));
    }

    @Override
    public void onPrepared()
    {

    }
}

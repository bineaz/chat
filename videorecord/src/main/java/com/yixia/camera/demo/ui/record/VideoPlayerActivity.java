package com.yixia.camera.demo.ui.record;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.bobo.videorecord.R;
import com.yixia.camera.demo.ui.BaseActivity;
import com.yixia.camera.demo.ui.widget.SurfaceVideoView;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.StringUtils;

/**
 * 通用单独播放界面
 *
 * @author tangjun
 */
public class VideoPlayerActivity extends BaseActivity implements
        SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {

    /**
     * 播放控件
     */
    private SurfaceVideoView mVideoView;
    /**
     * 暂停按钮
     */
    private View mPlayerStatus;
    private View mLoading;

    /**
     * 播放路径
     */
    private String mPath;
    /**
     * 是否需要回复播放
     */
    private boolean mNeedResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPath = getIntent().getStringExtra("path");
        if (StringUtils.isEmpty(mPath)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_video_play_layout);
        mPlayerStatus = findViewById(R.id.play_status);
        mLoading = findViewById(R.id.loading);
        mVideoView = (SurfaceVideoView) findViewById(R.id.mSurfaceVideoView);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);

        mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this);

        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        mVideoView.setVideoPath(mPath);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (mVideoView.isRelease())
                mVideoView.reOpen();
            else
                mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mNeedResume = true;
                mVideoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        super.onDestroy();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
        mVideoView.start();
        // new Handler().postDelayed(new Runnable() {
        //
        // @SuppressWarnings("deprecation")
        // @Override
        // public void run() {
        // if (DeviceUtils.hasJellyBean()) {
        // mVideoView.setBackground(null);
        // } else {
        // mVideoView.setBackgroundDrawable(null);
        // }
        // }
        // }, 300);
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                mVideoView.dispatchKeyEvent(this, event);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!isFinishing()) {
            // 播放失败
        }
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mSurfaceVideoView) {
            if (mVideoView.isPlaying())
                mVideoView.pause();
            else
                mVideoView.start();

        } else if (i == R.id.cancel) {
            Intent data = new Intent();
            data.putExtra("mPath", mPath);
            setResult(RESULT_CANCELED, data);
            finish();
        } else if (i == R.id.submit) {
            Intent data = new Intent();
            data.putExtra("mPath", mPath);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!isFinishing())
            mVideoView.reOpen();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing())
                    mVideoView.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing())
                    mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (DeviceUtils.hasJellyBean()) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }

}

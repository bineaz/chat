package com.by.communication.media;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.by.communication.R;
import com.by.communication.permission.AndPermission;
import com.by.communication.util.ConstantUtil;

/**
 * 录音控件button
 */
public class RecordVoiceButton extends AppCompatButton implements View.OnClickListener {

    private Dialog    recordIndicator;
    private ImageView mVolumeIv, mIvPauseContinue, mIvComplete;
    private VoiceLineView         voiceLine;
    private TextView              mRecordHintTv;
    private TextView              cancelTextView;
    private Context               mContext;
    private OnRecordVoiceListener onRecordVoiceListener;
    private VoiceManager          voiceManager;

    public RecordVoiceButton(Context context)
    {
        super(context);
        init();
    }

    public RecordVoiceButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public RecordVoiceButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init()
    {
        voiceManager = VoiceManager.getInstance(mContext);
        setOnClickListener(this);
    }

    /**
     * 设置监听
     *
     * @param onRecordVoiceListener
     */
    public void setOnRecordVoiceListener(OnRecordVoiceListener onRecordVoiceListener)
    {
        this.onRecordVoiceListener = onRecordVoiceListener;
    }

    /**
     * 启动录音dialog
     */
    private void startRecordDialog()
    {
        recordIndicator = new Dialog(getContext(), R.style.record_voice_dialog);
        recordIndicator.setContentView(R.layout.dialog_record_voice);
        recordIndicator.setCanceledOnTouchOutside(false);
        recordIndicator.setCancelable(false);
        mVolumeIv = (ImageView) recordIndicator.findViewById(R.id.iv_voice);
        voiceLine = (VoiceLineView) recordIndicator.findViewById(R.id.voicLine);
        mRecordHintTv = (TextView) recordIndicator.findViewById(R.id.tv_length);
        mRecordHintTv.setText("00:00:00");
        mIvPauseContinue = (ImageView) recordIndicator.findViewById(R.id.iv_continue_or_pause);
        mIvComplete = (ImageView) recordIndicator.findViewById(R.id.iv_complete);
        cancelTextView = (TextView) recordIndicator.findViewById(R.id.cancelTextView);
        recordIndicator.show();

        cancelTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                voiceManager.cancelVoiceRecord();
                recordIndicator.dismiss();
            }
        });
        //暂停或继续
        mIvPauseContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (voiceManager != null) {
                    voiceManager.pauseOrStartVoiceRecord();
                }
            }
        });
        //完成
        mIvComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (voiceManager != null) {
                    voiceManager.stopVoiceRecord();
                }
                recordIndicator.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        if (AndPermission.hasPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
            startRecord();
        } else {

        }


    }

    public void startRecord()
    {
        startRecordDialog();
        voiceManager.setVoiceRecordListener(new VoiceManager.VoiceRecordCallBack() {
            @Override
            public void recDoing(long time, String strTime)
            {
                mRecordHintTv.setText(strTime);
            }

            @Override
            public void recVoiceGrade(int grade)
            {
                voiceLine.setVolume(grade);
            }

            @Override
            public void recStart(boolean init)
            {
                mIvPauseContinue.setImageResource(R.mipmap.icon_pause);
                voiceLine.setContinue();
            }

            @Override
            public void recPause(String str)
            {
                mIvPauseContinue.setImageResource(R.mipmap.icon_continue);
                voiceLine.setPause();
            }


            @Override
            public void recFinish(long length, String strLength, String path)
            {
                if (onRecordVoiceListener != null) {
                    onRecordVoiceListener.onFinishRecord(length, strLength, path);
                }
            }
        });
        voiceManager.startVoiceRecord(ConstantUtil.AUDIO_BASE_PATH);
    }

    /**
     * 结束回调监听
     */
    public interface OnRecordVoiceListener {
        void onFinishRecord(long length, String strLength, String filePath);
    }


}

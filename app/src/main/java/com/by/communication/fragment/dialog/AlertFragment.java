package com.by.communication.fragment.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.by.communication.R;
import com.by.communication.widgit.textView.DrawableTextView;

/**
 * Created by SX3 on 2017/1/17.
 */

public class AlertFragment extends BaseDialogFragment {
    private DrawableTextView cancelTextView;
    private DrawableTextView confirmTextView;
    private TextView         infoTextView;

    private OnConfirmListener onConfirmListener;

    @Override
    public int getResId()
    {
        return R.layout.alert_fragment;
    }

    @Override
    public void init(View view)
    {
        cancelTextView = (DrawableTextView) view.findViewById(R.id.cancelTextView);
        confirmTextView = (DrawableTextView) view.findViewById(R.id.confirmTextView);
        infoTextView = (TextView) view.findViewById(R.id.textView);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                dismiss();
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }

                dismiss();
            }
        });

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        String info = bundle.getString("info");
        String confirmText = bundle.getString("confirmText");
        String cancelText = bundle.getString("cancelText");

        if (info != null) {
            infoTextView.setText(info);
        }

        if (confirmText != null) {
            confirmTextView.setText(confirmText);
        }

        if (cancelText != null) {
            cancelTextView.setText(cancelText);
        }


    }

    public OnConfirmListener getOnConfirmListener()
    {
        return onConfirmListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener)
    {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}

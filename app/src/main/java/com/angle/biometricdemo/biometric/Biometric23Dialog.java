package com.angle.biometricdemo.biometric;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.angle.biometricdemo.R;

/**
 * 因为指纹在23以下是没有相应对话框的,
 * 所以这个必须自己实现相应dialog的实现
 */
public class Biometric23Dialog extends Dialog {


    /**
     * 定义以下四种状态
     */
    public static final int STATE_NORMAL = 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_SUCCEED = 4;

    private OnBiometric23DialogCallback mCallback;
    private TextView mState;

    private Context mContext;

    /**
     * 对外暴露的创建对话框的方法
     *
     * @return 相应的对话框
     */
    public static Biometric23Dialog newInstance(Context context) {
        Biometric23Dialog dialog = new Biometric23Dialog(context);
        return dialog;
    }

    public Biometric23Dialog(@NonNull Context context) {
        super(context, R.style.transparentDialog);
        setContentView(R.layout.dialog_biometric);
        mContext = context;

        setLocation();

        initView();
    }


    public void setLocation() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams WL = window.getAttributes();
            WL.height = ViewGroup.LayoutParams.MATCH_PARENT;
            WL.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(WL);
        }
    }

    private void initView() {
        TextView password = findViewById(R.id.passwordTv);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onUsePassword();
                }
            }
        });

        TextView cancel = findViewById(R.id.cancelTv);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCancel();
                }
            }
        });

        mState = findViewById(R.id.srcTv);
    }


    public void setState(int state) {
        switch (state) {
            case STATE_NORMAL:
                mState.setTextColor(ContextCompat.getColor(mContext, R.color.text_quaternary));
                mState.setText(mContext.getString(R.string.biometric_dialog_state_normal));
                break;
            case STATE_FAILED:
                mState.setTextColor(ContextCompat.getColor(mContext, R.color.text_red));
                mState.setText(mContext.getString(R.string.biometric_dialog_state_failed));
                break;
            case STATE_ERROR:
                mState.setTextColor(ContextCompat.getColor(mContext, R.color.text_red));
                mState.setText(mContext.getString(R.string.biometric_dialog_state_error));
                break;
            case STATE_SUCCEED:
                mState.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                mState.setText(mContext.getString(R.string.biometric_dialog_state_succeeded));

                mState.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 500);
                break;
        }
    }

    public void setCallback(OnBiometric23DialogCallback callback) {
        mCallback = callback;
    }
}

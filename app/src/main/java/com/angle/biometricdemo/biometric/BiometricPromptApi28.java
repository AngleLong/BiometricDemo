package com.angle.biometricdemo.biometric;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.angle.biometricdemo.App;
import com.angle.biometricdemo.R;
import com.angle.biometricdemo.utils.SharedPreferencesUtils;

import java.security.Signature;

import javax.crypto.Cipher;

@RequiresApi(Build.VERSION_CODES.P)
public class BiometricPromptApi28 extends IBiometricPromptImpl {

    /**
     * 28的指纹识别类
     */
    private BiometricPrompt mBiometricPrompt;
    private Context mContext;
    /**
     * 回调
     */
    private OnBiometricIdentifyCallback mIdentifyCallback;
    private CancellationSignal mCancellationSignal;


    public BiometricPromptApi28(Context context) {
        mContext = context;
        mBiometricPrompt = new BiometricPrompt.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setSubtitle(mContext.getResources().getString(R.string.biometric_dialog_subtitle))
                .setDescription(mContext.getResources().getString(R.string.biometric_dialog_des))
                //设置底部的按钮
                .setNegativeButton(mContext.getResources().getString(R.string.biometric_dialog_use_password), mContext.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mIdentifyCallback != null) {
                            mIdentifyCallback.onUsePassword();
                        }
                        cancel();
                    }
                })
                .build();
    }

    @Override
    public void authenticate(Cipher cipher, @NonNull CancellationSignal cancel, @NonNull OnBiometricIdentifyCallback callback) {
        mIdentifyCallback = callback;
        mCancellationSignal = cancel;

        BiometricPrompt.CryptoObject biometricPromptCryptoObject = new BiometricPrompt.CryptoObject(cipher);

        mBiometricPrompt.authenticate(biometricPromptCryptoObject, mCancellationSignal, mContext.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                mIdentifyCallback.onError(errorCode, errString.toString());
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                mIdentifyCallback.onFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Cipher cipher = result.getCryptoObject().getCipher();
                if (cipher != null) {
                    byte[] iv = cipher.getIV();
                    String ivStr = Base64.encodeToString(iv, Base64.DEFAULT);
                    SharedPreferencesUtils.setParam(App.getAPPContext(), "iv", ivStr);
                    mIdentifyCallback.onSucceeded(cipher);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                mIdentifyCallback.onFailed();
            }
        });
    }

    @Override
    public void authenticate(Signature signature,
                             @NonNull CancellationSignal cancel,
                             @NonNull OnBiometricIdentifyCallback callback) {
        mIdentifyCallback = callback;
        mCancellationSignal = cancel;

        BiometricPrompt.CryptoObject biometricPromptCryptoObject = new BiometricPrompt.CryptoObject(signature);

        mBiometricPrompt.authenticate(biometricPromptCryptoObject, mCancellationSignal, mContext.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                mIdentifyCallback.onError(errorCode, errString.toString());
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                mIdentifyCallback.onFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Signature signature = result.getCryptoObject().getSignature();
                mIdentifyCallback.onSucceeded(signature);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                mIdentifyCallback.onFailed();
            }
        });
    }


    @Override
    public void cancel() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }
}

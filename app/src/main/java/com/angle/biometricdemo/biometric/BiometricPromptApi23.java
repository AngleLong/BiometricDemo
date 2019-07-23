package com.angle.biometricdemo.biometric;

import android.content.Context;
import android.os.Build;
import android.util.Base64;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import com.angle.biometricdemo.App;
import com.angle.biometricdemo.utils.SharedPreferencesUtils;

import java.security.Signature;

import javax.crypto.Cipher;

/**
 * 指纹23之前的处理方案
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricPromptApi23 extends IBiometricPromptImpl {

    private FingerprintManagerCompat mManagerCompat;
    private Context mContext;
    /**
     * 相应的回调类
     */
    private OnBiometricIdentifyCallback mIdentifyCallback;
    private Biometric23Dialog mDialog;
    private CancellationSignal mCancellationSignal;

    public BiometricPromptApi23(Context context) {
        mContext = context;
        mManagerCompat = FingerprintManagerCompat.from(context);
    }


    /**
     * 对称加密的处理
     *
     * @param cipher   非对称加密信息
     * @param cancel   取消回调
     * @param callback 结果回调
     */
    @Override
    public void authenticate(Cipher cipher, @NonNull CancellationSignal cancel, @NonNull OnBiometricIdentifyCallback callback) {
        mIdentifyCallback = callback;
        mCancellationSignal = cancel;

        //创建一个对话框
        mDialog = Biometric23Dialog.newInstance(mContext);
        //设置相应的监听
        mDialog.setCallback(new OnBiometric23DialogCallback() {
            @Override
            public void onUsePassword() {
                mIdentifyCallback.onUsePassword();
            }

            @Override
            public void onCancel() {
                mIdentifyCallback.onCancel();
            }
        });
        mDialog.show();


        //处理取消的监听,这里应该把对话框关闭
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mDialog.dismiss();
            }
        });

        //开始指纹验证
        if (mManagerCompat == null) {
            mManagerCompat = FingerprintManagerCompat.from(mContext);
        }

        //这里应该添加相应的秘钥信息
        try {
            FingerprintManagerCompat.CryptoObject fingerprintManagerCryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
            mManagerCompat.authenticate(fingerprintManagerCryptoObject, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    super.onAuthenticationError(errMsgId, errString);
                    mDialog.setState(Biometric23Dialog.STATE_ERROR);
                    mIdentifyCallback.onError(errMsgId, errString.toString());
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    super.onAuthenticationHelp(helpMsgId, helpString);
                    mDialog.setState(Biometric23Dialog.STATE_FAILED);
                    mIdentifyCallback.onFailed();
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    mDialog.setState(Biometric23Dialog.STATE_FAILED);
                    Cipher cipher = result.getCryptoObject().getCipher();
                    if(cipher!=null) {
                        byte[] iv = cipher.getIV();
                        String ivStr = Base64.encodeToString(iv, Base64.DEFAULT);
                        SharedPreferencesUtils.setParam(App.getAPPContext(), "iv", ivStr);
                        mIdentifyCallback.onSucceeded(cipher);
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    mDialog.setState(Biometric23Dialog.STATE_SUCCEED);
                    mIdentifyCallback.onFailed();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 非对称加密的处理
     *
     * @param signature 对称加密信息
     * @param cancel    取消回调
     * @param callback  结果回调
     */
    @Override
    public void authenticate(Signature signature,
                             @NonNull androidx.core.os.CancellationSignal cancel,
                             @NonNull OnBiometricIdentifyCallback callback) {
        mIdentifyCallback = callback;
        mCancellationSignal = cancel;

        //创建一个对话框
        mDialog = Biometric23Dialog.newInstance(mContext);
        //设置相应的监听
        mDialog.setCallback(new OnBiometric23DialogCallback() {
            @Override
            public void onUsePassword() {
                mIdentifyCallback.onUsePassword();
            }

            @Override
            public void onCancel() {
                mIdentifyCallback.onCancel();
            }
        });
        mDialog.show();


        //处理取消的监听,这里应该把对话框关闭
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mDialog.dismiss();
            }
        });

        //开始指纹验证
        if (mManagerCompat == null) {
            mManagerCompat = FingerprintManagerCompat.from(mContext);
        }

        //这里应该添加相应的秘钥信息
        try {
            FingerprintManagerCompat.CryptoObject fingerprintManagerCryptoObject = new FingerprintManagerCompat.CryptoObject(signature);
            mManagerCompat.authenticate(fingerprintManagerCryptoObject, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    super.onAuthenticationError(errMsgId, errString);
                    mDialog.setState(Biometric23Dialog.STATE_ERROR);
                    mIdentifyCallback.onError(errMsgId, errString.toString());
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    super.onAuthenticationHelp(helpMsgId, helpString);
                    mDialog.setState(Biometric23Dialog.STATE_FAILED);
                    mIdentifyCallback.onFailed();
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    mDialog.setState(Biometric23Dialog.STATE_FAILED);
                    Cipher cipher = result.getCryptoObject().getCipher();
                    mIdentifyCallback.onSucceeded(cipher);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    mDialog.setState(Biometric23Dialog.STATE_SUCCEED);
                    mIdentifyCallback.onFailed();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }
}

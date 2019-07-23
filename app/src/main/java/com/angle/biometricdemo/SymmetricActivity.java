package com.angle.biometricdemo;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.angle.biometricdemo.biometric.BiometricPromptManager;
import com.angle.biometricdemo.biometric.OnBiometricIdentifyCallback;
import com.angle.biometricdemo.biometric.SymmetricHelper;

import java.security.Signature;

import javax.crypto.Cipher;

public class SymmetricActivity extends AppCompatActivity {

    /**
     * 待加密的数据
     */
    private String data = "为什么需要加密我";
    private String TAG = SymmetricActivity.class.getSimpleName();
    /**
     * 加密前
     */
    private TextView mBeforeEncryptionTv;
    /**
     * 加密
     */
    private TextView mEncryptionTv;
    /**
     * 解密
     */
    private TextView mDecryptTv;
    private BiometricPromptManager mBiometricPromptManager;
    /**
     * 加密后的字符串
     */
    private String mEncryptionStr;

    private byte[] mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeforeEncryptionTv = findViewById(R.id.beforeEncryptionTv);
        mEncryptionTv = findViewById(R.id.encryptionTv);
        mDecryptTv = findViewById(R.id.decryptTv);

        mBeforeEncryptionTv.setText(data);
    }


    public void biometricClick(View view) {
        mBiometricPromptManager = BiometricPromptManager.from(this);
        Cipher encryptCipher = SymmetricHelper.getInstance().getEncryptCipher();
        mBiometricPromptManager.authenticate(encryptCipher, new OnBiometricIdentifyCallback() {
            @Override
            public void onUsePassword() {

            }

            @Override
            public void onSucceeded(Signature result) {
                //验证非对称加密
            }

            @Override
            public void onSucceeded(Cipher result) {
                //验证对称加密
                try {
                    mIv = result.getIV();

                    byte[] sign = result.doFinal(data.getBytes());
                    mEncryptionStr = Base64.encodeToString(sign, Base64.DEFAULT);
                    Log.e(TAG, mEncryptionStr);
                    mEncryptionTv.setText(mEncryptionStr);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSucceeded: " + e.toString()
                    );
                }
            }

            @Override
            public void onFailed() {
                //这里自己看着处理
                Log.e(TAG, "onFailed: ");
            }

            @Override
            public void onError(int code, String reason) {
                //这里自己看着处理
                Log.e(TAG, "错误码为:" + code + "<----->错误信息为" + reason);
            }

            @Override
            public void onCancel() {
                //这里自己看着处理
            }
        });
    }

    @Override
    public Resources getResources() {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 750);
    }

    public void decryptBtn(View view) {
        //解密相应的数据
        if (!TextUtils.isEmpty(mEncryptionStr)) {
            mBiometricPromptManager = BiometricPromptManager.from(this);
            Cipher decryptCipher = SymmetricHelper.getInstance().getDecryptCipher(mIv);
            mBiometricPromptManager.authenticate(decryptCipher, new OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {

                }

                @Override
                public void onSucceeded(Signature result) {
                    try {
                        result.update(data.getBytes("utf-8"));
                        byte[] sign = result.sign();
                        //这里用base64加密了一下,否则你看不懂
                        mEncryptionStr = Base64.encodeToString(sign, Base64.DEFAULT);
                        mEncryptionTv.setText(mEncryptionStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSucceeded(Cipher result) {
                    try {
                        byte[] decode = Base64.decode(mEncryptionStr, Base64.DEFAULT);
                        byte[] bytes = result.doFinal(decode);
                        mDecryptTv.setText(new String(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onSucceeded: " + e.toString()
                        );
                    }
                }

                @Override
                public void onFailed() {
                    //这里自己看着处理
                }

                @Override
                public void onError(int code, String reason) {
                    //这里自己看着处理
                    Log.e(TAG, "错误码为:" + code + "<----->错误信息为" + reason);
                }

                @Override
                public void onCancel() {
                    //这里自己看着处理
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBeforeEncryptionTv != null) {
            mBiometricPromptManager.pause();
        }
    }
}

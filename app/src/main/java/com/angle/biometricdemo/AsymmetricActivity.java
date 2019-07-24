package com.angle.biometricdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.angle.biometricdemo.biometric.AsymmetricHelper;
import com.angle.biometricdemo.biometric.BiometricPromptManager;
import com.angle.biometricdemo.biometric.OnBiometricIdentifyCallback;
import com.angle.biometricdemo.utils.AdaptScreenUtils;

import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;

public class AsymmetricActivity extends AppCompatActivity {

    private BiometricPromptManager mBiometricPromptManager;
    private String TAG = AsymmetricActivity.class.getSimpleName();
    /**
     * 待加密的数据
     */
    private String data = "为什么需要加密我";
    /**
     * 加密后的信息
     */
    private String mEncryptionStr;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asymmetric);

        mBeforeEncryptionTv = findViewById(R.id.beforeEncryptionTv);
        mEncryptionTv = findViewById(R.id.encryptionTv);
        mDecryptTv = findViewById(R.id.decryptTv);

        //显示加密信息
        mBeforeEncryptionTv.setText(data);

        //初始化相应的指纹类
        mBiometricPromptManager = BiometricPromptManager.from(this);
    }

    /**
     * 调用指纹加密
     */
    public void biometricClick(View view) {
        //调用指纹对话框
        if (mBiometricPromptManager.isBiometricPromptEnable()) {
            Signature signature = AsymmetricHelper.getInstance().getPrivateSignature();
            mBiometricPromptManager.authenticate(signature, new OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {

                }

                @Override
                public void onSucceeded(Signature result) {
                    //非对称加密调用逻辑
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

                @Override
                public void onSucceeded(Cipher result) {
                    //对称加密调用逻辑
                }

                @Override
                public void onFailed() {
                    Log.e(TAG, "onFailed: ");
                }

                @Override
                public void onError(int code, String reason) {
                    Log.e(TAG, "错误码为:" + code + "错误信息为:" + reason);
                }

                @Override
                public void onCancel() {

                }
            });

        }
    }

    /**
     * 解密
     */
    public void enCodeClick(View view) {
        //这里是验签的操作
        try {
            if (mBiometricPromptManager.isBiometricPromptEnable()) {
                /**
                 * 这里是直接从本地获取相应的公钥信息进行处理的,
                 * 如果你想想获取私钥的话,这里直接使用publicKey.getEncoded()获取一个byte数组进行相应的base64加密就可以了
                 * 开始我这里面还纠结一个问题,我传到服务器之后,服务器怎么转换成公钥呢?
                 * 可以看utils中的ServerCreateKey这个类
                 */
                Signature signature = AsymmetricHelper.getInstance().getSignature();
                PublicKey publicKey = AsymmetricHelper.getInstance().getPublicKey();
                //可以把这个公钥上传到服务器,其实在一开始的时候就应该进行上传,
                //这里看看上传的地方在哪里和后端设计好就可以了!只要在验签之前给到他就好了!
                Log.e(TAG, "公钥信息为:=====>" + Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
                signature.initVerify(publicKey);
                signature.update(data.getBytes());
                boolean verify = signature.verify(Base64.decode(mEncryptionStr, Base64.DEFAULT));
                if (verify) {
                    //验签成功
                    mDecryptTv.setText("true");
                } else {
                    //验签失败
                    mDecryptTv.setText("false");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 750);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBiometricPromptManager != null) {
            mBiometricPromptManager.pause();
        }
    }
}

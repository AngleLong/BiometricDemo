package com.angle.biometricdemo.biometric;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Signature;

import javax.crypto.Cipher;

/**
 * 指纹管理类
 * 主要是提供相应的适配以及管理处理
 * 参考文章:
 * https://github.com/gaoyangcr7/BiometricPromptDemo
 */
public class BiometricPromptManager {

    private Context mContext;

    private IBiometricPromptImpl mImpl;

    public static BiometricPromptManager from(Context context) {
        return new BiometricPromptManager(context);
    }

    public BiometricPromptManager(Context context) {
        mContext = context;
        if (isAboveApi28()) {
            mImpl = new BiometricPromptApi28(context);
        } else if (isAboveApi23()) {
            mImpl = new BiometricPromptApi23(context);
        }
    }

    /**
     * 28版本的判断
     */
    private boolean isAboveApi28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 23版本的判断
     */
    private boolean isAboveApi23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 开启指纹识别
     * 这里存在一个问题,
     * CancellationSignal类在相应的版本引用不同
     * 如果想使用的话,就要把上面的抽象类变成集成的关系
     * @param callback 回调
     */
    public void authenticate(Cipher cipher, @NonNull OnBiometricIdentifyCallback callback) {
        if(isAboveApi28()){
            mImpl.authenticate(cipher,new CancellationSignal(),callback);
        }else{
            mImpl.authenticate(cipher,new androidx.core.os.CancellationSignal(),callback);
        }
    }

    public void authenticate(Signature signature, @NonNull OnBiometricIdentifyCallback callback) {
        if(isAboveApi28()){
            mImpl.authenticate(signature,new CancellationSignal(),callback);
        }else{
            mImpl.authenticate(signature,new androidx.core.os.CancellationSignal(),callback);
        }
    }

    /**
     * 是否已经注册指纹
     *
     * @return true 有 false 没有
     */
    public boolean hasEnrolledFingerprints() {
        if (isAboveApi23()) {
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(mContext);
            return managerCompat.hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    /**
     * 是否支持指纹支付
     *
     * @return true 支持 false 不支持
     */
    public boolean isHardwareDetected() {
        if (isAboveApi23()) {
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(mContext);
            return managerCompat.isHardwareDetected();
        } else {
            return false;
        }
    }

    /**
     * 是否有pin或者图形锁
     *
     * @return true 有 false 没有
     */
    public boolean isKeyguardSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager == null) {
            return false;
        }
        return keyguardManager.isKeyguardSecure();
    }

    /**
     * 设备是否支持生物识别
     *
     * @return true 支持 false 不支持
     */
    public boolean isBiometricPromptEnable() {
        return isAboveApi23()
                && isHardwareDetected()
                && hasEnrolledFingerprints()
                && isKeyguardSecure();
    }

    /**
     * 取消相应的CancellationSignal
     */
    public void pause(){
        mImpl.cancel();
    }
}

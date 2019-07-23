package com.angle.biometricdemo.biometric;


import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Signature;

import javax.crypto.Cipher;

/**
 * 相应抽离的接口
 * 主要目的是为了区分相应的版本
 * 这里存在一个问题,CancellationSignal类在相应的版本引用不同
 * 所以这里我就没有用接口去实现,因为用接口实现的话要把两个方法同时覆盖
 */
public abstract class IBiometricPromptImpl {
    /**
     * 指纹识别主要的类(23版本)
     *
     * @param cancel   取消回调
     * @param callback 结果回调
     * @param cipher   非对称加密信息
     */
    void authenticate(Cipher cipher,
                      @NonNull androidx.core.os.CancellationSignal cancel,
                      @NonNull OnBiometricIdentifyCallback callback) {

    }

    /**
     * 指纹识别主要的类(23版本)
     *
     * @param cancel    取消回调
     * @param callback  结果回调
     * @param signature 对称加密信息
     */
    void authenticate(Signature signature,
                      @NonNull androidx.core.os.CancellationSignal cancel,
                      @NonNull OnBiometricIdentifyCallback callback) {

    }

    /**
     * 指纹识别主要的类(28版本)
     *
     * @param cancel   取消回调
     * @param callback 结果回调
     * @param cipher   非对称加密信息
     */
    void authenticate(Cipher cipher,
                      @NonNull CancellationSignal cancel,
                      @NonNull OnBiometricIdentifyCallback callback) {

    }

    /**
     * 指纹识别主要的类(28版本)
     *
     * @param cancel    取消回调
     * @param callback  结果回调
     * @param signature 对称加密信息
     */
    void authenticate(Signature signature,
                      @NonNull CancellationSignal cancel,
                      @NonNull OnBiometricIdentifyCallback callback) {

    }

    /**
     * 因为api接口层面没有暴露相应的
     * CancellationSignal对象,因为这个对象引用不同,
     * 所以这里就没有暴露相应的方法
     */
    public abstract void cancel();
}

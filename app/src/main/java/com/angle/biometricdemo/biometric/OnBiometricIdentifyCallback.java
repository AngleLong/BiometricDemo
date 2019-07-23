package com.angle.biometricdemo.biometric;

import java.security.Signature;

import javax.crypto.Cipher;

/**
 * 相应生物识别的回调
 */
public interface OnBiometricIdentifyCallback {

    /**
     * 输入密码的流程
     */
    void onUsePassword();

    /**
     * 成功的回调,这里可以进行相应的加密
     * 28及以上非对称加密的处理结果
     *
     * @param result 结果
     */
    void onSucceeded(Signature result);

    /**
     * 成功的回调,这里可以进行相应的加密
     * 23以上28以下对称加密的处理结果
     *
     * @param result 结果
     */
    void onSucceeded(Cipher result);

    /**
     * 失败
     */
    void onFailed();

    /**
     * 错误信息
     *
     * @param code   错误码
     * @param reason 返回的字符串
     */
    void onError(int code, String reason);

    /**
     *
     */
    void onCancel();

}
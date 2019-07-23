package com.angle.biometricdemo.biometric;

/**
 * 指纹对话框的回调
 */
public interface OnBiometric23DialogCallback {
    /**
     * 输入密码的回调
     */
    void onUsePassword();

    /**
     * 取消的回调
     */
    void onCancel();
}
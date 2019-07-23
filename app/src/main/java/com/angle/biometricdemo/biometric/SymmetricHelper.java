package com.angle.biometricdemo.biometric;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

/**
 * 这里应该是对称加密
 * 参考项目:
 * https://github.com/googlesamples/android-FingerprintDialog
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class SymmetricHelper {
    // 秘钥的别名
    private static final String KEYSTOREALIAS = "AsymmetricKeyStore";
    private static SymmetricHelper sHelper;

    // 这些是加密所需要的
    static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING;
    private static KeyStore mKeystore = null;

    private SymmetricHelper() {
        try {
            mKeystore = KeyStore.getInstance("AndroidKeyStore");
            mKeystore.load(null);
            createKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SymmetricHelper getInstance() {
        if (sHelper == null) {
            synchronized (SymmetricHelper.class) {
                if (sHelper == null) {
                    sHelper = new SymmetricHelper();
                }
            }
        }
        return sHelper;
    }

    /**
     * 创建相应的加密秘钥
     *
     * @return 相应的秘钥
     */
    public Cipher getEncryptCipher() {
        Key key = GetKey();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("done", "这里应该让用户重新走指纹流程" );
            Log.e("异常", "createEncryptCipher: " + e.toString());
        }

        return cipher;
    }

    /**
     * 创建相应的解密秘钥
     *
     * @return 相应的秘钥
     */
    public Cipher getDecryptCipher(byte[] iv) {
        Key key = GetKey();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("异常", "createEncryptCipher: " + e.toString());
        }

        return cipher;
    }

    /**
     * 获取相应的key
     *
     * @return key
     */
    private Key GetKey() {
        Key secretKey = null;
        try {
            secretKey = mKeystore.getKey(KEYSTOREALIAS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretKey;
    }


    /**
     * 生成秘钥
     * <p>
     * 错误信息
     */
    private void createKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, "AndroidKeyStore");
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEYSTOREALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}
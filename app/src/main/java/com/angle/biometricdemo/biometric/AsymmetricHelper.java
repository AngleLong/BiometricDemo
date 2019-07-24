package com.angle.biometricdemo.biometric;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

/**
 * 非对称加密帮助类
 * 参考文章:
 * https://github.com/googlesamples/android-AsymmetricFingerprintDialog
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class AsymmetricHelper {
    /**
     * 秘钥别名
     */
    private static final String KEYSTOREALIAS = "AsymmetricKeyStore";
    private static AsymmetricHelper mHelper;
    private KeyStore mKeyStore = null;

    private AsymmetricHelper() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            getKeyPair(KEYSTOREALIAS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AsymmetricHelper getInstance() {
        if (mHelper == null) {
            synchronized (AsymmetricHelper.class) {
                if (mHelper == null) {
                    mHelper = new AsymmetricHelper();
                }
            }
        }
        return mHelper;
    }

    /**
     * 生成秘钥对的方法
     *
     * @param keyName 秘钥库别名
     * @return 返回keyPair对象
     */

    @Nullable
    private KeyPair getKeyPair(String keyName) throws Exception {
        // TODO: 2019-07-19 这里借鉴一下他的写法
//        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//        keyStore.load(null);
//        if (keyStore.containsAlias(keyName)) {
//            // Get public key
//            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
//            // Get private key
//            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
//            // Return a key pair
//            return new KeyPair(publicKey, privateKey);
//        }

        KeyPairGenerator mKeyPairGenerator;
        if (mKeyStore != null && !mKeyStore.containsAlias(keyName)) {
            mKeyPairGenerator = KeyPairGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

            mKeyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_SIGN)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            .setUserAuthenticationRequired(true)
                            .build());

            return mKeyPairGenerator.generateKeyPair();
        }
        return null;
    }

    /**
     * 获取私钥的方法
     *
     * @return 私钥
     */
    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) mKeyStore.getKey(KEYSTOREALIAS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公钥的方法
     * TODO 这里其实没有什么用,到时候直接干掉就好了
     * 因为在这之前应该是把相应的公钥传递到相应的服务器
     *
     * @return 私钥
     */
    public PublicKey getPublicKey() {
        try {
            return mKeyStore.getCertificate(KEYSTOREALIAS).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取28之后的CryptoObject
     *
     * @return 相应的对象
     */
    public Signature getPrivateSignature() {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            PrivateKey privateKey = getPrivateKey();
            signature.initSign(privateKey);
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取相应的Signature
     * @return Signature对象
     */
    public Signature getSignature() {
        try {
            return Signature.getInstance("SHA256withECDSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

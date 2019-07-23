package com.angle.biometricdemo.biometric;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SymmetricEncryptionImpl {
    private static final String TAG = SymmetricEncryptionImpl.class.getSimpleName();
    private String KEYSTORE_NAME = "AndroidKeyStore";
    private static final String KEY_NAME = "symmetricDemo";
    private final KeyStore mKeystore;

    /**
     * 创建keystore
     */
    public SymmetricEncryptionImpl() throws Exception {
        mKeystore = KeyStore.getInstance(KEYSTORE_NAME);
        mKeystore.load(null);
    }

    /**
     * 获取秘钥生成器，用于生成秘钥
     */

    public void CreateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }

    /**
     * 获取相应的Cipher对象
     */
    public Cipher createCipher() throws Exception {
        Key key = GetKey();
        //打印公钥
        Log.e("公钥", Base64.encodeToString(key.getEncoded(), Base64.DEFAULT));
        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES);
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            return null;
        }
        return cipher;
    }

    /**
     * 获取相应的秘钥
     *
     * @return 相应的秘钥
     */
    public Key GetKey() throws Exception {
        Key secretKey = mKeystore.getKey(KEY_NAME, null);

        Log.e(TAG, "GetKey: " + new String(secretKey.getEncoded()));

        return secretKey;
    }


    public  void getKeyName() {
        try {
            KeyStore mKeystore = KeyStore.getInstance("AndroidKeyStore");
            mKeystore.load(null);

            KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keyGenSpec =
                    new KeyGenParameterSpec.Builder("symmetricDemo", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setUserAuthenticationRequired(false)
                            .build();
            keyGen.init(keyGenSpec);
            keyGen.generateKey();


            mKeystore.load(null);
            Key symmetricDemo = mKeystore.getKey("symmetricDemo", null);
            System.out.println(new String(symmetricDemo.getEncoded()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
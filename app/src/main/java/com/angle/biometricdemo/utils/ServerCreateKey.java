package com.angle.biometricdemo.utils;


import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * 服务器生成相应的公钥
 * PS:服务器使用的base64工具和我们android使用的base64不一样这里注意下就好了!
 */
public class ServerCreateKey {
    private static String key = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEASZGswQqaT9JbzTr4+FhUFa5mUv1V94MQctQ1pPNSXp9JjILoxsMY0vCWvCA4MLiaKJsUsTmcEN8zmsgu5p0Wg==";

    public static void main(String[] args) {
        try{

            KeyFactory factory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT));
            System.out.println(new String(Base64.decode(key, Base64.DEFAULT)));
            PublicKey verificationKey = factory.generatePublic(spec);

            System.out.println("公钥信息为====>" + Base64.encodeToString(verificationKey.getEncoded(), Base64.DEFAULT));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}

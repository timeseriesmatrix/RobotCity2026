/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.PosLog;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESencrp {
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[]{84, 104, 101, 66, 101, 115, 116, 83, 101, 99, 114, 101, 116, 75, 101, 121};

    public static String encrypt(String Data) throws Exception {
        Key key = AESencrp.generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(1, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = AESencrp.generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(2, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public static void main(String[] args) throws Exception {
        String decrypt = AESencrp.decrypt("4T9H+1LqawVTsVvifd/TxA==");
        PosLog.info(AESencrp.class, decrypt);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 */
package com.floreantpos.util;

import com.floreantpos.PosLog;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

public class PasswordHasher {
    public static void main(String[] args) throws Exception {
        PosLog.info(PasswordHasher.class, PasswordHasher.hashPassword("123"));
    }

    public static String hashPassword(String password) {
        byte[] passwordBytes = null;
        MessageDigest md = null;
        try {
            passwordBytes = password.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            PosLog.error(PasswordHasher.class, e.getMessage());
        }
        try {
            md = MessageDigest.getInstance("SHA1");
        }
        catch (NoSuchAlgorithmException e) {
            PosLog.error(PasswordHasher.class, e.getMessage());
        }
        byte[] hashBytes = md.digest(passwordBytes);
        return Hex.encodeHexString((byte[])hashBytes);
    }
}


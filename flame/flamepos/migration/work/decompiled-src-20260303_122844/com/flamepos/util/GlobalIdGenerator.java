/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import java.util.Random;

public final class GlobalIdGenerator {
    public static String generate() {
        long currentTimeMillis = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < 3; ++i) {
            currentTimeMillis += (long)random.nextInt();
        }
        String idString = String.valueOf(currentTimeMillis);
        int length = idString.length();
        if (length == 16) {
            return idString;
        }
        if (length > 16) {
            return idString.substring(0, 16);
        }
        for (int i = 0; i < 16 - length; ++i) {
            char c = (char)(random.nextInt(26) + 97);
            idString = c + idString;
        }
        return idString;
    }
}


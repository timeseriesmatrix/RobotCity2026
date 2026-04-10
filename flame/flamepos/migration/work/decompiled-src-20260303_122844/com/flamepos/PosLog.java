/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos;

import org.apache.commons.logging.LogFactory;

public class PosLog {
    private static LogFactory factory = LogFactory.getFactory();

    public static void error(Class eClass, String errMsg) {
        factory.getInstance(eClass).error((Object)errMsg);
    }

    public static void error(Class eClass, Exception e) {
        factory.getInstance(eClass).error((Object)e);
    }

    public static void error(Class eClass, String message, Exception e) {
        factory.getInstance(eClass).error((Object)message, (Throwable)e);
    }

    public static void debug(Class eClass, String msg) {
        factory.getInstance(eClass).debug((Object)msg);
    }

    public static void info(Class eClass, String msg) {
        factory.getInstance(eClass).info((Object)msg);
    }
}


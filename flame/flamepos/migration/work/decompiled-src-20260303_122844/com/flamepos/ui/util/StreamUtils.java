/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.floreantpos.ui.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class StreamUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String toString(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        try {
            String string = IOUtils.toString((InputStream)in);
            return string;
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }
}


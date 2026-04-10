/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.PosLog;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class JarUtil {
    public static String getJarLocation(Class clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri.toString().endsWith(".jar")) {
                return new File(uri.getPath()).getParentFile().getPath() + "/";
            }
            return uri.getPath();
        }
        catch (URISyntaxException e) {
            PosLog.error(JarUtil.class, e.getMessage());
            String executableLocation = ".";
            return executableLocation + "/";
        }
    }
}


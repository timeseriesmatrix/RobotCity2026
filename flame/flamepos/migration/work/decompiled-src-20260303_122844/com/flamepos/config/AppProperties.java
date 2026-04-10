/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.configuration.PropertiesConfiguration
 */
package com.floreantpos.config;

import com.floreantpos.PosLog;
import org.apache.commons.configuration.PropertiesConfiguration;

public class AppProperties {
    private static PropertiesConfiguration properties;

    public static String getVersion() {
        return properties.getString("floreantpos.version");
    }

    static {
        try {
            properties = new PropertiesConfiguration(AppProperties.class.getResource("/app.properties"));
        }
        catch (Exception e) {
            PosLog.error(AppProperties.class, e.getMessage());
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.model.util;

import com.floreantpos.model.util.ZipCodeMap;
import com.floreantpos.ui.forms.QuickCustomerForm;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZipCodeUtil {
    private static Log logger = LogFactory.getLog(ZipCodeUtil.class);
    private static HashMap<String, ZipCodeMap> zipCodeCache = new HashMap();
    private static boolean isInitialize;

    public static String getCity(String zipCode) {
        if (!isInitialize) {
            ZipCodeUtil.initialize();
        }
        if (!ZipCodeUtil.isZipCodeMap(zipCode)) {
            return null;
        }
        String city = zipCodeCache.get(zipCode).getCity();
        return city;
    }

    public static String getState(String zipCode) {
        if (!isInitialize) {
            ZipCodeUtil.initialize();
        }
        if (!ZipCodeUtil.isZipCodeMap(zipCode)) {
            return null;
        }
        String state = zipCodeCache.get(zipCode).getState();
        return state;
    }

    private static boolean isZipCodeMap(String zipCode) {
        ZipCodeMap zipCodeMap = zipCodeCache.get(zipCode);
        return zipCodeMap != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void initialize() {
        InputStream inputStream = null;
        try {
            inputStream = QuickCustomerForm.class.getResourceAsStream("/Zipcodes/US.txt");
            List lines = IOUtils.readLines((InputStream)inputStream);
            for (String line : lines) {
                String[] str = line.split(",");
                String zipCode = str[0];
                String state = str[1];
                String city = str[2];
                ZipCodeMap zMap = new ZipCodeMap();
                zMap.setState(state);
                zMap.setCity(city);
                zipCodeCache.put(zipCode, zMap);
            }
            isInitialize = true;
        }
        catch (Exception e2) {
            logger.error((Object)e2);
        }
        finally {
            IOUtils.closeQuietly((InputStream)inputStream);
        }
    }
}


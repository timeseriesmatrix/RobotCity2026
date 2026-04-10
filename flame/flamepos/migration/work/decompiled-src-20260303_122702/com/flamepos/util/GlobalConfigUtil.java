/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.util;

import com.floreantpos.model.GlobalConfig;
import com.floreantpos.model.dao.GlobalConfigDAO;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class GlobalConfigUtil {
    private static List<GlobalConfig> globalConfigList;

    public static void populateGlobalConfig() {
        globalConfigList = new ArrayList<GlobalConfig>();
        List<GlobalConfig> list = GlobalConfigDAO.getInstance().findAll();
        if (list != null) {
            globalConfigList.addAll(list);
        }
    }

    public static GlobalConfig get(String key) {
        if (globalConfigList == null) {
            return null;
        }
        for (GlobalConfig config : globalConfigList) {
            if (!key.equals(config.getKey())) continue;
            return config;
        }
        return null;
    }

    public static String getValue(String key) {
        if (globalConfigList == null) {
            return null;
        }
        for (GlobalConfig config : globalConfigList) {
            if (!key.equals(config.getKey())) continue;
            return config.getValue();
        }
        return null;
    }

    public static String getMapApiKey() {
        String mapApiKey = GlobalConfigUtil.getValue("map api key");
        if (StringUtils.isEmpty((String)mapApiKey)) {
            return "AIzaSyDc-5LFTSC-bB9kQcZkM74LHUxwndRy_XM";
        }
        return mapApiKey;
    }
}


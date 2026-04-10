/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.DataUpdateInfo;
import com.floreantpos.model.dao.BaseDataUpdateInfoDAO;
import java.util.Date;

public class DataUpdateInfoDAO
extends BaseDataUpdateInfoDAO {
    private static DataUpdateInfo lastUpdateInfo;

    public static synchronized DataUpdateInfo getLastUpdateInfo() {
        if (lastUpdateInfo != null) {
            try {
                DataUpdateInfoDAO.getInstance().refresh(lastUpdateInfo);
            }
            catch (Exception x) {
                lastUpdateInfo = null;
            }
            return lastUpdateInfo;
        }
        lastUpdateInfo = DataUpdateInfoDAO.getInstance().get(1);
        if (lastUpdateInfo == null) {
            lastUpdateInfo = new DataUpdateInfo();
            lastUpdateInfo.setLastUpdateTime(new Date());
            DataUpdateInfoDAO.getInstance().save(lastUpdateInfo);
        }
        return lastUpdateInfo;
    }
}


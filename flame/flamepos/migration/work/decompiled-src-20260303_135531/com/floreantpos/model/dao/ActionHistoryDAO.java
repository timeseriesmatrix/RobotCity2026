/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.model.dao;

import com.floreantpos.Messages;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BaseActionHistoryDAO;
import java.util.Date;
import org.apache.commons.logging.LogFactory;

public class ActionHistoryDAO
extends BaseActionHistoryDAO {
    public void saveHistory(User performer, String actionName, String description) {
        try {
            ActionHistory history = new ActionHistory();
            history.setActionName(actionName);
            history.setDescription(description);
            history.setPerformer(performer);
            history.setActionTime(new Date());
            this.save(history);
        }
        catch (Exception e) {
            LogFactory.getLog(ActionHistoryDAO.class).error((Object)Messages.getString("ActionHistoryDAO.0"), (Throwable)e);
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.model.base.BaseActionHistory;

public class ActionHistory
extends BaseActionHistory {
    private static final long serialVersionUID = 1L;
    public static final String NEW_CHECK = Messages.getString("ActionHistory.0");
    public static final String EDIT_CHECK = Messages.getString("ActionHistory.1");
    public static final String SPLIT_CHECK = Messages.getString("ActionHistory.2");
    public static final String VOID_CHECK = Messages.getString("ActionHistory.3");
    public static final String REOPEN_CHECK = Messages.getString("ActionHistory.4");
    public static final String SETTLE_CHECK = Messages.getString("ActionHistory.5");
    public static final String PRINT_CHECK = Messages.getString("ActionHistory.6");
    public static final String PAY_CHECK = Messages.getString("ActionHistory.7");
    public static final String GROUP_SETTLE = Messages.getString("ActionHistory.8");
    public static final String PAY_OUT = Messages.getString("ActionHistory.9");
    public static final String PAY_TIPS = Messages.getString("ActionHistory.10");

    public ActionHistory() {
    }

    public ActionHistory(Integer id) {
        super(id);
    }
}


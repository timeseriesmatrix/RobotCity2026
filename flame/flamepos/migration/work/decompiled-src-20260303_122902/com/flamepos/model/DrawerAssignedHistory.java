/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.model.base.BaseDrawerAssignedHistory;

public class DrawerAssignedHistory
extends BaseDrawerAssignedHistory {
    private static final long serialVersionUID = 1L;
    public static final String ASSIGNMENT_OPERATION = Messages.getString("DrawerAssignedHistory.0");
    public static final String CLOSE_OPERATION = Messages.getString("DrawerAssignedHistory.1");

    public DrawerAssignedHistory() {
    }

    public DrawerAssignedHistory(Integer id) {
        super(id);
    }
}


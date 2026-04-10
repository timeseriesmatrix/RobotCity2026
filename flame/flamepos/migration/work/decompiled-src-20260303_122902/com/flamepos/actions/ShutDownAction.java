/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.actions.ViewChangeAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;

public class ShutDownAction
extends ViewChangeAction {
    public ShutDownAction() {
        super(POSConstants.CAPITAL_SHUTDOWN, UserPermission.SHUT_DOWN);
    }

    public ShutDownAction(boolean showText, boolean showIcon) {
        if (showText) {
            this.putValue("Name", Messages.getString("Shutdown"));
        }
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("shut_down.png"));
        }
        this.setRequiredPermission(UserPermission.SHUT_DOWN);
    }

    @Override
    public void execute() {
        Application.getInstance().shutdownPOS();
    }
}


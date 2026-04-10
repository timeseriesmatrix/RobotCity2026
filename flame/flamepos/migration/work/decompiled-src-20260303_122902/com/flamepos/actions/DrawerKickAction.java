/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.DrawerUtil;

public class DrawerKickAction
extends PosAction {
    public DrawerKickAction() {
        super("NO SALE", UserPermission.DRAWER_PULL);
        this.setEnabled(Application.getInstance().getTerminal().isHasCashDrawer());
    }

    @Override
    public void execute() {
        try {
            DrawerUtil.kickDrawer();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


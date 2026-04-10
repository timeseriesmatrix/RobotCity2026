/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.DrawerPullReportDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class DrawerPullAction
extends PosAction {
    public DrawerPullAction() {
        super(POSConstants.DRAWER_PULL_BUTTON_TEXT, UserPermission.DRAWER_PULL);
    }

    @Override
    public void execute() {
        try {
            DrawerPullReportDialog dialog = new DrawerPullReportDialog();
            dialog.setTitle(POSConstants.DRAWER_PULL_BUTTON_TEXT);
            dialog.initialize();
            dialog.setSize(PosUIManager.getSize(470, 500));
            dialog.setDefaultCloseOperation(2);
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


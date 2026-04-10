/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.UserPermission;
import com.floreantpos.util.POSUtil;

public class ShowBackofficeAction
extends PosAction {
    public ShowBackofficeAction() {
        super(POSConstants.BACK_OFFICE_BUTTON_TEXT);
        this.setRequiredPermission(UserPermission.VIEW_BACK_OFFICE);
    }

    public ShowBackofficeAction(boolean showText, boolean showIcon) {
        if (showText) {
            this.putValue("Name", UserPermission.VIEW_BACK_OFFICE);
        }
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("backoffice.png"));
        }
        this.setRequiredPermission(UserPermission.VIEW_BACK_OFFICE);
    }

    @Override
    public void execute() {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        if (window == null) {
            window = new BackOfficeWindow();
        }
        window.setVisible(true);
        window.toFront();
    }
}


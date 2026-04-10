/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.actions.PosAction;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.TicketImportPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ShowOnlineTicketManagementAction
extends PosAction {
    private TicketImportPlugin ticketImportPlugin = (TicketImportPlugin)ExtensionManager.getPlugin(TicketImportPlugin.class);

    public ShowOnlineTicketManagementAction() {
        super("ONLINE TICKET STAT", UserPermission.DRAWER_PULL);
        this.setVisible(this.ticketImportPlugin != null);
    }

    @Override
    public void execute() {
        try {
            if (this.ticketImportPlugin != null) {
                this.ticketImportPlugin.startService();
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


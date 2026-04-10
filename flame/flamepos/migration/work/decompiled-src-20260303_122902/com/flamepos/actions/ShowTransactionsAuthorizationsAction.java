/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.views.payment.AuthorizableTicketBrowser;

public class ShowTransactionsAuthorizationsAction
extends PosAction {
    public ShowTransactionsAuthorizationsAction() {
        super(POSConstants.AUTHORIZE_BUTTON_TEXT, UserPermission.AUTHORIZE_TICKETS);
    }

    @Override
    public void execute() {
        AuthorizableTicketBrowser dialog = new AuthorizableTicketBrowser(Application.getPosWindow());
        dialog.setDefaultCloseOperation(2);
        dialog.setSize(PosUIManager.getSize(800, 600));
        dialog.setLocationRelativeTo(Application.getPosWindow());
        dialog.setVisible(true);
    }
}


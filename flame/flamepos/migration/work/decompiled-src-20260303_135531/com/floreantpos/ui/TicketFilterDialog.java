/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.HeadlessException;
import java.awt.Window;

public class TicketFilterDialog
extends POSDialog {
    public TicketFilterDialog() throws HeadlessException {
        super((Window)Application.getPosWindow(), Messages.getString("TicketFilterDialog.0"), true);
    }
}


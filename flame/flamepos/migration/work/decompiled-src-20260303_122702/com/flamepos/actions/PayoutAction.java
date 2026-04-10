/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.ui.dialog.PayoutDialog;

public class PayoutAction
extends PosAction {
    public PayoutAction() {
        super(POSConstants.PAYOUT_BUTTON_TEXT);
    }

    @Override
    public void execute() {
        PayoutDialog dialog = new PayoutDialog();
        dialog.open();
    }
}


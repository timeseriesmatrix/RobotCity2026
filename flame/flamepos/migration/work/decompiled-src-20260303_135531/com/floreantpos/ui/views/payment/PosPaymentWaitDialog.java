/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.ui.dialog.POSDialog;
import javax.swing.JLabel;

public class PosPaymentWaitDialog
extends POSDialog {
    private JLabel label;

    public PosPaymentWaitDialog() {
        this.setTitle(Messages.getString("PaymentProcessWaitDialog.0"));
        this.label = new JLabel("Waiting for response from credit card Device..........");
        this.label.setHorizontalAlignment(0);
        this.label.setFont(this.label.getFont().deriveFont(1, 20.0f));
        this.add(this.label);
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
    }

    public void setMessage(String msg) {
        this.label.setText(msg);
    }
}


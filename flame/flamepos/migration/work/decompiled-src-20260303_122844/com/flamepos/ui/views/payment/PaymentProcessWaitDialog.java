/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PaymentProcessWaitDialog
extends JDialog {
    public PaymentProcessWaitDialog(JDialog parent) {
        super((Dialog)parent, false);
        this.setTitle(Messages.getString("PaymentProcessWaitDialog.0"));
        JLabel label = new JLabel(Messages.getString("PaymentProcessWaitDialog.1"));
        label.setHorizontalAlignment(0);
        label.setFont(label.getFont().deriveFont(24).deriveFont(1));
        this.add(label);
        this.setSize(500, 400);
        this.setDefaultCloseOperation(0);
        this.setLocationRelativeTo(parent);
    }

    public PaymentProcessWaitDialog(JFrame parent) {
        super((Frame)parent, false);
        this.setTitle(Messages.getString("PaymentProcessWaitDialog.2"));
        JLabel label = new JLabel(Messages.getString("PaymentProcessWaitDialog.3"));
        label.setHorizontalAlignment(0);
        label.setFont(label.getFont().deriveFont(24).deriveFont(1));
        this.add(label);
        this.setSize(500, 400);
        this.setDefaultCloseOperation(0);
        this.setLocationRelativeTo(parent);
    }
}


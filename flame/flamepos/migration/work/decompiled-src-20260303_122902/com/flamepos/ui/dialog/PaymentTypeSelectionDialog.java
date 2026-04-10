/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.model.PaymentType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class PaymentTypeSelectionDialog
extends POSDialog {
    PaymentType selectedPaymentType;
    private PaymentSelectionButton btnCash;
    private PaymentSelectionButton btnGiftCert;

    public PaymentTypeSelectionDialog() {
        this.setTitle(Messages.getString("PaymentTypeSelectionDialog.0"));
        this.initComponents();
    }

    private void initComponents() {
        JPanel content = new JPanel((LayoutManager)new MigLayout("gap 5px 20px, fill"));
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel genericPanel = new JPanel(new GridLayout(1, 0, 15, 15));
        this.btnCash = new PaymentSelectionButton(PaymentType.CASH);
        genericPanel.add((Component)this.btnCash, "grow,wrap");
        this.btnGiftCert = new PaymentSelectionButton(PaymentType.GIFT_CERTIFICATE);
        genericPanel.add(this.btnGiftCert);
        content.add((Component)genericPanel, "height 60px, wrap, growx");
        JPanel creditCardPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        creditCardPanel.add(new PaymentSelectionButton(PaymentType.CREDIT_VISA));
        creditCardPanel.add(new PaymentSelectionButton(PaymentType.CREDIT_MASTER_CARD));
        creditCardPanel.add(new PaymentSelectionButton(PaymentType.CREDIT_AMEX));
        creditCardPanel.add(new PaymentSelectionButton(PaymentType.CREDIT_DISCOVERY));
        creditCardPanel.setBorder(new CompoundBorder(new TitledBorder(Messages.getString("PaymentTypeSelectionDialog.4")), new EmptyBorder(10, 10, 10, 10)));
        content.add((Component)creditCardPanel, "wrap, height 110px, growx");
        JPanel debitCardPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        debitCardPanel.add(new PaymentSelectionButton(PaymentType.DEBIT_VISA));
        debitCardPanel.add(new PaymentSelectionButton(PaymentType.DEBIT_MASTER_CARD));
        debitCardPanel.setBorder(new CompoundBorder(new TitledBorder(Messages.getString("PaymentTypeSelectionDialog.6")), new EmptyBorder(10, 10, 10, 10)));
        content.add((Component)debitCardPanel, "wrap, height 110px, growx");
        PosButton cancel = new PosButton(Messages.getString("PaymentTypeSelectionDialog.8"));
        cancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentTypeSelectionDialog.this.setCanceled(true);
                PaymentTypeSelectionDialog.this.dispose();
            }
        });
        content.add((Component)cancel, "alignx center, gaptop 20px");
        this.add(content);
        this.pack();
    }

    public PaymentType getSelectedPaymentType() {
        return this.selectedPaymentType;
    }

    public void setCashButtonVisible(boolean visible) {
        this.btnCash.setVisible(visible);
        this.btnGiftCert.setVisible(visible);
    }

    class PaymentSelectionButton
    extends PosButton
    implements ActionListener {
        PaymentType paymentType;

        public PaymentSelectionButton(PaymentType p) {
            this.paymentType = p;
            if (p.getImageFile() != null) {
                this.setIcon(IconFactory.getIcon("/ui_icons/", "" + p.getImageFile()));
            } else {
                this.setText(p.getDisplayString());
            }
            this.addActionListener(this);
            this.setEnabled(this.paymentType.isSupported());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PaymentTypeSelectionDialog.this.selectedPaymentType = this.paymentType;
            PaymentTypeSelectionDialog.this.setCanceled(false);
            PaymentTypeSelectionDialog.this.dispose();
        }
    }
}


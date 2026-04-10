/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.model.CustomPayment;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class PaymentReferenceEntryDialog
extends POSDialog {
    private JLabel lblRef;
    private FixedLengthTextField txtRef;
    private String paymentRef;
    private CustomPayment payment;

    public PaymentReferenceEntryDialog(CustomPayment payment) {
        super((Frame)BackOfficeWindow.getInstance(), true);
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        this.payment = payment;
        this.setTitle(Messages.getString("PaymentReferenceEntryDialog.0"));
        this.createUI();
    }

    private void createUI() {
        this.lblRef = new JLabel(Messages.getString("PaymentReferenceEntryDialog.1") + this.payment.getRefNumberFieldName());
        this.txtRef = new FixedLengthTextField(120);
        this.setPreferredSize(new Dimension(900, 500));
        JPanel customTypePanel = new JPanel((LayoutManager)new MigLayout("", "grow", "grow"));
        customTypePanel.add(this.lblRef);
        customTypePanel.add(this.txtRef);
        QwertyKeyPad qwertyKeyPad = new QwertyKeyPad();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add((Component)customTypePanel, "North");
        centerPanel.add((Component)((Object)qwertyKeyPad), "Center");
        this.getContentPane().add((Component)centerPanel, "Center");
        JPanel panel_1 = new JPanel();
        this.getContentPane().add((Component)panel_1, "South");
        panel_1.setLayout(new BorderLayout(0, 0));
        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2);
        PosButton btnSubmit = new PosButton();
        panel_2.add(btnSubmit);
        btnSubmit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentReferenceEntryDialog.this.submitCard();
            }
        });
        btnSubmit.setText(Messages.getString("PaymentReferenceEntryDialog.5"));
        PosButton btnCancel = new PosButton();
        panel_2.add(btnCancel);
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentReferenceEntryDialog.this.setCanceled(true);
                PaymentReferenceEntryDialog.this.dispose();
            }
        });
        btnCancel.setText(Messages.getString("PaymentReferenceEntryDialog.2"));
        JSeparator separator = new JSeparator();
        panel_1.add((Component)separator, "North");
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("PaymentReferenceEntryDialog.6"));
        this.getContentPane().add((Component)titlePanel, "North");
    }

    protected void submitCard() {
        String ref = this.txtRef.getText();
        if (ref.equals("")) {
            POSMessageDialog.showMessage(Messages.getString("PaymentReferenceEntryDialog.8"));
            return;
        }
        this.setPaymentRef(ref);
        this.setCanceled(false);
        this.dispose();
    }

    public String getPaymentRef() {
        return this.paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }
}


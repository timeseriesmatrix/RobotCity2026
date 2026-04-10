/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.dao.CustomPaymentDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.payment.PaymentReferenceEntryDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class CustomPaymentSelectionDialog
extends POSDialog {
    private String paymentName;
    private String paymentRef;
    private String paymentFieldName;

    public CustomPaymentSelectionDialog() {
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        this.createUI();
    }

    private void createUI() {
        this.setPreferredSize(new Dimension(675, 529));
        JPanel customTypePanel = new JPanel((LayoutManager)new MigLayout("wrap 5,center", "", ""));
        PosScrollPane pane = new PosScrollPane(customTypePanel, 20, 31);
        List<CustomPayment> custompPayments = CustomPaymentDAO.getInstance().findAll();
        for (CustomPayment customPayment : custompPayments) {
            PosButton button = new PosButton(customPayment.getName());
            button.setPreferredSize(new Dimension(120, 80));
            button.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    CustomPaymentSelectionDialog.this.dispose();
                    String paymentName = e.getActionCommand();
                    CustomPaymentSelectionDialog.this.setPaymentName(paymentName);
                    CustomPayment payment = CustomPaymentDAO.getInstance().getByName(paymentName);
                    CustomPaymentSelectionDialog.this.setPaymentFieldName(payment.getRefNumberFieldName());
                    if (payment.isRequiredRefNumber().booleanValue()) {
                        PaymentReferenceEntryDialog dialog = new PaymentReferenceEntryDialog(payment);
                        dialog.pack();
                        dialog.open();
                        if (dialog.isCanceled()) {
                            return;
                        }
                        CustomPaymentSelectionDialog.this.setPaymentRef(dialog.getPaymentRef());
                    }
                    CustomPaymentSelectionDialog.this.setCanceled(false);
                }
            });
            customTypePanel.add(button);
        }
        this.getContentPane().add((Component)pane, "Center");
        JPanel panel_1 = new JPanel();
        this.getContentPane().add((Component)panel_1, "South");
        panel_1.setLayout(new BorderLayout(0, 0));
        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2);
        PosButton btnCancel = new PosButton();
        panel_2.add(btnCancel);
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                CustomPaymentSelectionDialog.this.setCanceled(true);
                CustomPaymentSelectionDialog.this.dispose();
            }
        });
        btnCancel.setText(Messages.getString("CustomPaymentSelectionDialog.3"));
        JSeparator separator = new JSeparator();
        panel_1.add((Component)separator, "North");
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("CustomPaymentSelectionDialog.4"));
        this.getContentPane().add((Component)titlePanel, "North");
    }

    public String getPaymentName() {
        return this.paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentRef() {
        return this.paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getPaymentFieldName() {
        return this.paymentFieldName;
    }

    public void setPaymentFieldName(String paymentFieldName) {
        this.paymentFieldName = paymentFieldName;
    }
}


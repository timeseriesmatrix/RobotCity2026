/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.CurrencyUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class ConfirmPayDialog
extends POSDialog {
    private JLabel lblInfo;

    public ConfirmPayDialog() {
        this.setDefaultCloseOperation(2);
        this.setSize(600, 400);
        this.setResizable(false);
        this.createUI();
    }

    private void createUI() {
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "South");
        panel.setLayout(new BorderLayout(0, 0));
        JSeparator separator = new JSeparator();
        panel.add((Component)separator, "North");
        JPanel panel_1 = new JPanel();
        panel.add((Component)panel_1, "South");
        PosButton psbtnConfirm = new PosButton();
        psbtnConfirm.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmPayDialog.this.setCanceled(false);
                ConfirmPayDialog.this.dispose();
            }
        });
        psbtnConfirm.setText(Messages.getString("ConfirmPayDialog.0"));
        panel_1.add(psbtnConfirm);
        PosButton psbtnCancel = new PosButton();
        psbtnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmPayDialog.this.setCanceled(true);
                ConfirmPayDialog.this.dispose();
            }
        });
        psbtnCancel.setText(Messages.getString("ConfirmPayDialog.1"));
        panel_1.add(psbtnCancel);
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("ConfirmPayDialog.2"));
        this.getContentPane().add((Component)titlePanel, "North");
        this.lblInfo = new JLabel("");
        this.lblInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.lblInfo.setFont(new Font("Dialog", 1, 16));
        this.getContentPane().add((Component)this.lblInfo, "Center");
    }

    public void setMessage(String message) {
        this.lblInfo.setText(message);
    }

    public void setAmount(double amount) {
        this.lblInfo.setText("<html>You are going to process <b>" + CurrencyUtil.getCurrencySymbol() + amount + "</b>.<br/><br/>If you are sure press <b>CONFIRM</b>, otherwise press <b>CANCEL</b>.<br/><br/></html>");
    }
}


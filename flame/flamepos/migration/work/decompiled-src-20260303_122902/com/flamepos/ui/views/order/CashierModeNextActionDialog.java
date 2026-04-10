/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.OpenTicketsListDialog;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CashierModeNextActionDialog
extends POSDialog
implements ActionListener {
    PosButton btnNew = new PosButton(Messages.getString("CashierModeNextActionDialog.0"));
    PosButton btnOpen = new PosButton(Messages.getString("CashierModeNextActionDialog.1"));
    PosButton btnLogout = new PosButton(Messages.getString("CashierModeNextActionDialog.2"));
    JLabel messageLabel = new JLabel("", 0);

    public CashierModeNextActionDialog(String message) {
        super((Frame)Application.getPosWindow(), true);
        this.setTitle(Messages.getString("CashierModeNextActionDialog.4"));
        JPanel contentPane = new JPanel(new BorderLayout(10, 20));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        contentPane.add((Component)this.messageLabel, "North");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        buttonPanel.add(this.btnNew);
        buttonPanel.add(this.btnOpen);
        buttonPanel.add(this.btnLogout);
        contentPane.add(buttonPanel);
        this.setContentPane(contentPane);
        this.btnNew.addActionListener(this);
        this.btnOpen.addActionListener(this);
        this.btnLogout.addActionListener(this);
        this.messageLabel.setFont(this.messageLabel.getFont().deriveFont(1, 16.0f));
        this.messageLabel.setText(message);
        this.setSize(550, 180);
        this.setResizable(false);
        Application.getPosWindow().setGlassPaneVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != this.btnNew) {
            if (e.getSource() == this.btnLogout) {
                Application.getInstance().doLogout();
            } else if (e.getSource() == this.btnOpen) {
                OpenTicketsListDialog dialog = new OpenTicketsListDialog();
                dialog.open();
            }
        }
        Application.getPosWindow().setGlassPaneVisible(false);
        this.dispose();
    }
}


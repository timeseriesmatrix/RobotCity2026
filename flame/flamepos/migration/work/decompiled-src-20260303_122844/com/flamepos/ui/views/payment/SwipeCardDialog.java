/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.config.CardConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.PaymentType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.payment.AuthorizationCodeDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import com.floreantpos.ui.views.payment.ManualCardEntryDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class SwipeCardDialog
extends POSDialog
implements CardInputProcessor {
    private CardInputListener cardInputListener;
    private JPasswordField passwordField;
    private String cardString;
    private PosButton btnEnterAuthorizationCode;
    private PosButton btnManualEntry;

    public SwipeCardDialog(CardInputListener cardInputListener) {
        this.cardInputListener = cardInputListener;
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("SwipeCardDialog.0"));
        this.getContentPane().add((Component)titlePanel, "North");
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "South");
        panel.setLayout(new BorderLayout(0, 0));
        JPanel panel_2 = new JPanel((LayoutManager)new MigLayout("", "grow", ""));
        panel.add(panel_2);
        this.btnManualEntry = new PosButton();
        panel_2.add(this.btnManualEntry);
        this.btnManualEntry.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwipeCardDialog.this.openManualEntry();
            }
        });
        this.btnManualEntry.setText(Messages.getString("SwipeCardDialog.1"));
        this.btnEnterAuthorizationCode = new PosButton();
        panel_2.add(this.btnEnterAuthorizationCode);
        this.btnEnterAuthorizationCode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwipeCardDialog.this.openAuthorizationEntryDialog();
            }
        });
        this.btnEnterAuthorizationCode.setText(Messages.getString("SwipeCardDialog.2"));
        PosButton btnSubmit = new PosButton();
        panel_2.add(btnSubmit);
        btnSubmit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwipeCardDialog.this.submitCard();
            }
        });
        btnSubmit.setText(Messages.getString("SwipeCardDialog.3"));
        PosButton psbtnCancel = new PosButton();
        panel_2.add(psbtnCancel);
        psbtnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwipeCardDialog.this.setCanceled(true);
                SwipeCardDialog.this.dispose();
            }
        });
        psbtnCancel.setText(Messages.getString("SwipeCardDialog.4"));
        JSeparator separator = new JSeparator();
        panel.add((Component)separator, "North");
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(20, 10, 20, 10));
        this.getContentPane().add((Component)panel_1, "Center");
        panel_1.setLayout(new BorderLayout(0, 0));
        this.passwordField = new JPasswordField();
        this.passwordField.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwipeCardDialog.this.submitCard();
            }
        });
        this.passwordField.setColumns(30);
        panel_1.add(this.passwordField);
        if (Application.getInstance().isDevelopmentMode()) {
            this.passwordField.setText("%B4111111111111111^SHAH/RIAR^1803101000000000020000831000000?;4111111111111111=1803101000020000831?");
        }
        if (!CardConfig.isManualEntrySupported()) {
            this.btnManualEntry.setEnabled(false);
        }
        if (!CardConfig.isExtTerminalSupported()) {
            this.btnEnterAuthorizationCode.setEnabled(false);
        }
    }

    protected void openAuthorizationEntryDialog() {
        this.setCanceled(true);
        this.dispose();
        AuthorizationCodeDialog dialog = new AuthorizationCodeDialog(this.cardInputListener);
        dialog.setLocationRelativeTo(this);
        dialog.pack();
        dialog.open();
    }

    protected void openManualEntry() {
        this.setCanceled(true);
        this.dispose();
        ManualCardEntryDialog dialog = new ManualCardEntryDialog(this.cardInputListener);
        dialog.setLocationRelativeTo(this);
        dialog.pack();
        dialog.open();
    }

    public String getCardString() {
        return this.cardString;
    }

    public void setCardString(String cardString) {
        this.cardString = cardString;
    }

    private void submitCard() {
        this.cardString = new String(this.passwordField.getPassword());
        this.setCanceled(false);
        this.dispose();
        this.cardInputListener.cardInputted(this, PaymentType.CREDIT_CARD);
    }

    public void setManualEntryVisible(boolean visible) {
        this.btnManualEntry.setVisible(visible);
    }

    public void setAuthorizationEntryVisible(boolean visible) {
        this.btnEnterAuthorizationCode.setVisible(visible);
    }
}


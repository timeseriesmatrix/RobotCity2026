/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.config.CardConfig;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.payment.AuthorizationCodeDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import com.floreantpos.ui.views.payment.SwipeCardDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class ManualCardEntryDialog
extends POSDialog
implements CardInputProcessor {
    private CardInputListener cardInputListener;
    private POSTextField tfCardNumber;
    private POSTextField tfExpMonth;
    private POSTextField tfExpYear;

    public ManualCardEntryDialog(CardInputListener cardInputListener) {
        this.cardInputListener = cardInputListener;
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        this.createUI();
    }

    private void createUI() {
        this.setPreferredSize(new Dimension(PosUIManager.getSize(900), PosUIManager.getSize(500)));
        JPanel panel = new JPanel();
        panel.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][][][][][][][grow]"));
        JLabel lblCardNumber = new JLabel(Messages.getString("ManualCardEntryDialog.3"));
        panel.add((Component)lblCardNumber, "cell 0 0,alignx trailing");
        this.tfCardNumber = new POSTextField();
        this.tfCardNumber.setColumns(20);
        panel.add((Component)this.tfCardNumber, "cell 1 0");
        JLabel lblExpieryMonth = new JLabel(Messages.getString("ManualCardEntryDialog.6"));
        panel.add((Component)lblExpieryMonth, "cell 0 1,alignx trailing");
        this.tfExpMonth = new POSTextField();
        this.tfExpMonth.setColumns(4);
        panel.add((Component)this.tfExpMonth, "cell 1 1");
        JLabel lblExpieryYear = new JLabel(Messages.getString("ManualCardEntryDialog.9"));
        panel.add((Component)lblExpieryYear, "cell 0 2,alignx trailing");
        this.tfExpYear = new POSTextField();
        this.tfExpYear.setColumns(4);
        panel.add((Component)this.tfExpYear, "cell 1 2");
        QwertyKeyPad qwertyKeyPad = new QwertyKeyPad();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 5));
        centerPanel.add((Component)panel, "North");
        centerPanel.add((Component)((Object)qwertyKeyPad), "Center");
        this.getContentPane().add((Component)centerPanel, "Center");
        JPanel panel_1 = new JPanel();
        this.getContentPane().add((Component)panel_1, "South");
        panel_1.setLayout(new BorderLayout(0, 0));
        JPanel panel_2 = new JPanel((LayoutManager)new MigLayout("al center"));
        panel_1.add(panel_2);
        PosButton btnSwipeCard = new PosButton();
        panel_2.add((Component)btnSwipeCard, "grow");
        btnSwipeCard.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ManualCardEntryDialog.this.openSwipeCardDialog();
            }
        });
        btnSwipeCard.setText(Messages.getString("ManualCardEntryDialog.13"));
        PosButton btnEnterAuthorizationCode = new PosButton();
        panel_2.add((Component)btnEnterAuthorizationCode, "grow");
        btnEnterAuthorizationCode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ManualCardEntryDialog.this.openAuthorizationCodeEntryDialog();
            }
        });
        btnEnterAuthorizationCode.setText(Messages.getString("ManualCardEntryDialog.14"));
        PosButton btnSubmit = new PosButton();
        panel_2.add((Component)btnSubmit, "grow");
        btnSubmit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ManualCardEntryDialog.this.submitCard();
            }
        });
        btnSubmit.setText(Messages.getString("ManualCardEntryDialog.15"));
        PosButton btnCancel = new PosButton();
        panel_2.add((Component)btnCancel, "grow");
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ManualCardEntryDialog.this.setCanceled(true);
                ManualCardEntryDialog.this.dispose();
            }
        });
        btnCancel.setText(Messages.getString("ManualCardEntryDialog.16"));
        JSeparator separator = new JSeparator();
        panel_1.add((Component)separator, "North");
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("ManualCardEntryDialog.17"));
        this.getContentPane().add((Component)titlePanel, "North");
        if (!CardConfig.isSwipeCardSupported()) {
            btnSwipeCard.setEnabled(false);
        }
        if (!CardConfig.isExtTerminalSupported()) {
            btnEnterAuthorizationCode.setEnabled(false);
        }
    }

    protected void openAuthorizationCodeEntryDialog() {
        this.setCanceled(true);
        this.dispose();
        AuthorizationCodeDialog dialog = new AuthorizationCodeDialog(this.cardInputListener);
        dialog.pack();
        dialog.open();
    }

    protected void submitCard() {
        this.setCanceled(false);
        this.dispose();
        this.cardInputListener.cardInputted(this, null);
    }

    private void openSwipeCardDialog() {
        this.setCanceled(true);
        this.dispose();
        SwipeCardDialog swipeCardDialog = new SwipeCardDialog(this.cardInputListener);
        swipeCardDialog.pack();
        swipeCardDialog.open();
    }

    public String getCardNumber() {
        return this.tfCardNumber.getText();
    }

    public String getExpMonth() {
        return this.tfExpMonth.getText();
    }

    public String getExpYear() {
        return this.tfExpYear.getText();
    }
}


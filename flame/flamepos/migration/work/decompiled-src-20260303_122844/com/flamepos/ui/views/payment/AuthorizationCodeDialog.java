/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.PaymentType;
import com.floreantpos.swing.FocusedTextField;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class AuthorizationCodeDialog
extends POSDialog
implements CardInputProcessor {
    private CardInputListener cardInputListener;
    private FocusedTextField tfAuthorizationCode;
    private POSToggleButton btnVisaCard;
    private POSToggleButton btnMasterCard;
    private POSToggleButton btnAmericanExpress;
    private POSToggleButton btnDiscoverCard;
    private POSToggleButton btnDebitVisaCard;
    private POSToggleButton btnDebitMasterCard;

    public AuthorizationCodeDialog(CardInputListener cardInputListener) {
        super((Frame)Application.getPosWindow(), true);
        this.cardInputListener = cardInputListener;
        this.setDefaultCloseOperation(2);
        this.setResizable(false);
        this.createUI();
        this.btnVisaCard.setSelected(true);
    }

    private void createUI() {
        this.setPreferredSize(new Dimension(PosUIManager.getSize(1000), PosUIManager.getSize(600)));
        this.btnVisaCard = new POSToggleButton();
        this.btnVisaCard.setIcon(IconFactory.getIcon("/ui_icons/", "visa_card.png"));
        this.btnMasterCard = new POSToggleButton("");
        this.btnMasterCard.setIcon(IconFactory.getIcon("/ui_icons/", "master_card.png"));
        this.btnAmericanExpress = new POSToggleButton();
        this.btnAmericanExpress.setIcon(IconFactory.getIcon("/ui_icons/", "am_ex_card.png"));
        this.btnDiscoverCard = new POSToggleButton();
        this.btnDiscoverCard.setIcon(IconFactory.getIcon("/ui_icons/", "discover_card.png"));
        ButtonGroup group = new ButtonGroup();
        group.add(this.btnVisaCard);
        group.add(this.btnMasterCard);
        group.add(this.btnAmericanExpress);
        group.add(this.btnDiscoverCard);
        JPanel creditCardPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        creditCardPanel.add(this.btnVisaCard);
        creditCardPanel.add(this.btnMasterCard);
        creditCardPanel.add(this.btnAmericanExpress);
        creditCardPanel.add(this.btnDiscoverCard);
        creditCardPanel.setBorder(new CompoundBorder(new TitledBorder(Messages.getString("PaymentTypeSelectionDialog.4")), new EmptyBorder(10, 10, 10, 10)));
        JPanel debitCardPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        this.btnDebitVisaCard = new POSToggleButton();
        this.btnDebitVisaCard.setIcon(IconFactory.getIcon("/ui_icons/", "visa_card.png"));
        this.btnDebitMasterCard = new POSToggleButton();
        this.btnDebitMasterCard.setIcon(IconFactory.getIcon("/ui_icons/", "master_card.png"));
        group.add(this.btnDebitVisaCard);
        group.add(this.btnDebitMasterCard);
        debitCardPanel.add(this.btnDebitVisaCard);
        debitCardPanel.add(this.btnDebitMasterCard);
        debitCardPanel.setBorder(new CompoundBorder(new TitledBorder(Messages.getString("PaymentTypeSelectionDialog.6")), new EmptyBorder(10, 10, 10, 10)));
        JPanel panel = new JPanel();
        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.add((Component)panel, "Center");
        JPanel cardPanel = new JPanel((LayoutManager)new MigLayout("fill, ins 2", "sg, fill", ""));
        cardPanel.add(creditCardPanel);
        cardPanel.add(debitCardPanel);
        centralPanel.add((Component)cardPanel, "North");
        this.getContentPane().add((Component)centralPanel, "Center");
        panel.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[50][grow]"));
        JLabel lblAuthorizationCode = new JLabel(Messages.getString("AuthorizationCodeDialog.3"));
        panel.add((Component)lblAuthorizationCode, "cell 0 0,alignx trailing");
        this.tfAuthorizationCode = new FocusedTextField();
        this.tfAuthorizationCode.setColumns(12);
        panel.add((Component)this.tfAuthorizationCode, "cell 1 0,growx");
        QwertyKeyPad qwertyKeyPad = new QwertyKeyPad();
        panel.add((Component)((Object)qwertyKeyPad), "cell 0 1 2 1,grow");
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
                AuthorizationCodeDialog.this.setCanceled(false);
                AuthorizationCodeDialog.this.dispose();
                AuthorizationCodeDialog.this.cardInputListener.cardInputted(AuthorizationCodeDialog.this, AuthorizationCodeDialog.this.getPaymentType());
            }
        });
        btnSubmit.setText(Messages.getString("AuthorizationCodeDialog.7"));
        PosButton btnCancel = new PosButton();
        panel_2.add(btnCancel);
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorizationCodeDialog.this.setCanceled(true);
                AuthorizationCodeDialog.this.dispose();
            }
        });
        btnCancel.setText(Messages.getString("AuthorizationCodeDialog.8"));
        JSeparator separator = new JSeparator();
        panel_1.add((Component)separator, "North");
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("AuthorizationCodeDialog.9"));
        this.getContentPane().add((Component)titlePanel, "North");
    }

    private PaymentType getPaymentType() {
        if (this.btnVisaCard.isSelected()) {
            return PaymentType.CREDIT_VISA;
        }
        if (this.btnMasterCard.isSelected()) {
            return PaymentType.CREDIT_MASTER_CARD;
        }
        if (this.btnAmericanExpress.isSelected()) {
            return PaymentType.CREDIT_AMEX;
        }
        if (this.btnDiscoverCard.isSelected()) {
            return PaymentType.CREDIT_DISCOVERY;
        }
        if (this.btnDebitMasterCard.isSelected()) {
            return PaymentType.DEBIT_MASTER_CARD;
        }
        if (this.btnDebitVisaCard.isSelected()) {
            return PaymentType.DEBIT_VISA;
        }
        return PaymentType.CREDIT_VISA;
    }

    public String getAuthorizationCode() {
        return this.tfAuthorizationCode.getText();
    }
}


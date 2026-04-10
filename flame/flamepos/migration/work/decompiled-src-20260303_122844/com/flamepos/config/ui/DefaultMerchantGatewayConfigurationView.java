/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.CardConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.model.CardReader;
import com.floreantpos.swing.POSTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import net.miginfocom.swing.MigLayout;

public class DefaultMerchantGatewayConfigurationView
extends ConfigurationView {
    private POSTextField tfMerchantAccount;
    private JComboBox cbCardReader;
    private JPasswordField tfMerchantPass;
    private JCheckBox cbSandboxMode;
    private JCheckBox chckbxAllowMagneticSwipe;
    private JCheckBox chckbxAllowCardManual;
    private JCheckBox chckbxAllowExternalTerminal;
    private JButton btnCreateNewMerchantAccount;
    private String link = "http://reseller.authorize.net/application/?resellerId=27144";

    public DefaultMerchantGatewayConfigurationView() {
        this.setLayout((LayoutManager)new MigLayout("", "[][grow]", ""));
        JLabel lblMagneticCardReader = new JLabel(Messages.getString("CardConfigurationView.9"));
        this.add((Component)lblMagneticCardReader, "cell 0 3,alignx leading");
        this.cbCardReader = new JComboBox();
        this.cbCardReader.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMerchantGatewayConfigurationView.this.updateCheckBoxes();
            }
        });
        this.add((Component)this.cbCardReader, "cell 1 3,growx");
        JLabel lblMerchantAccount = new JLabel(Messages.getString("CardConfigurationView.19"));
        this.add((Component)lblMerchantAccount, "cell 0 5,alignx leading");
        this.tfMerchantAccount = new POSTextField();
        this.add((Component)this.tfMerchantAccount, "cell 1 5,growx");
        JLabel lblSecretCode = new JLabel(Messages.getString("CardConfigurationView.22"));
        this.add((Component)lblSecretCode, "cell 0 6,alignx leading");
        this.cbCardReader.setModel(new DefaultComboBoxModel<CardReader>(CardReader.values()));
        this.tfMerchantPass = new JPasswordField();
        this.add((Component)this.tfMerchantPass, "cell 1 6,growx");
        this.chckbxAllowMagneticSwipe = new JCheckBox(Messages.getString("CardConfigurationView.3"));
        this.chckbxAllowMagneticSwipe.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMerchantGatewayConfigurationView.this.updateCardList();
            }
        });
        this.add((Component)this.chckbxAllowMagneticSwipe, "skip 1, newline");
        this.chckbxAllowCardManual = new JCheckBox(Messages.getString("CardConfigurationView.5"));
        this.chckbxAllowCardManual.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMerchantGatewayConfigurationView.this.updateCardList();
            }
        });
        this.add((Component)this.chckbxAllowCardManual, "skip 1, newline");
        this.chckbxAllowExternalTerminal = new JCheckBox(Messages.getString("CardConfigurationView.7"));
        this.chckbxAllowExternalTerminal.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMerchantGatewayConfigurationView.this.updateCardList();
            }
        });
        this.add((Component)this.chckbxAllowExternalTerminal, "skip 1, newline");
        this.cbSandboxMode = new JCheckBox(Messages.getString("CardConfigurationView.25"));
        this.add((Component)this.cbSandboxMode, "skip 1, newline");
        this.btnCreateNewMerchantAccount = new JButton(Messages.getString(Messages.getString("DefaultMerchantGatewayConfigurationView.1")));
        this.btnCreateNewMerchantAccount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultMerchantGatewayConfigurationView.this.openBrowser(DefaultMerchantGatewayConfigurationView.this.link);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        });
        this.btnCreateNewMerchantAccount.setForeground(Color.RED);
        this.btnCreateNewMerchantAccount.setFont(new Font(this.getFont().getName(), 1, 11));
        this.add((Component)this.btnCreateNewMerchantAccount, "skip 1, newline");
    }

    private void openBrowser(String link) throws Exception {
        URI uri = new URI(link);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }
    }

    @Override
    public void initialize() throws Exception {
        String merchantPass;
        this.chckbxAllowMagneticSwipe.setSelected(CardConfig.isSwipeCardSupported());
        this.chckbxAllowCardManual.setSelected(CardConfig.isManualEntrySupported());
        this.chckbxAllowExternalTerminal.setSelected(CardConfig.isExtTerminalSupported());
        CardReader card = CardConfig.getCardReader();
        this.cbCardReader.setSelectedItem((Object)card);
        String merchantAccount = CardConfig.getMerchantAccount();
        if (merchantAccount != null) {
            this.tfMerchantAccount.setText(merchantAccount);
        }
        if ((merchantPass = CardConfig.getMerchantPass()) != null) {
            this.tfMerchantPass.setText(merchantPass);
        }
        this.cbSandboxMode.setSelected(CardConfig.isSandboxMode());
        this.updateCardList();
    }

    public void setMerchantDefaultValue(String accountNo, String pass) {
        this.tfMerchantAccount.setText(accountNo);
        this.tfMerchantPass.setText(pass);
    }

    public void setVisibleLinkButton(String btnText, String link, boolean visible) {
        this.link = link;
        this.btnCreateNewMerchantAccount.setText(btnText);
        this.btnCreateNewMerchantAccount.setVisible(visible);
    }

    protected void updateCheckBoxes() {
        CardReader selectedItem = (CardReader)((Object)this.cbCardReader.getSelectedItem());
        if (selectedItem == CardReader.SWIPE) {
            this.chckbxAllowMagneticSwipe.setSelected(true);
        } else if (selectedItem == CardReader.MANUAL) {
            this.chckbxAllowCardManual.setSelected(true);
        } else if (selectedItem == CardReader.EXTERNAL_TERMINAL) {
            this.chckbxAllowExternalTerminal.setSelected(true);
        }
    }

    private DefaultComboBoxModel<CardReader> createComboBoxModel(Vector items) {
        DefaultComboBoxModel<CardReader> model = new DefaultComboBoxModel<CardReader>();
        for (Object object : items) {
            model.addElement((CardReader)((Object)object));
        }
        return model;
    }

    protected void updateCardList() {
        boolean swipeSupported = this.chckbxAllowMagneticSwipe.isSelected();
        boolean manualSupported = this.chckbxAllowCardManual.isSelected();
        boolean extSupported = this.chckbxAllowExternalTerminal.isSelected();
        CardReader currentReader = (CardReader)((Object)this.cbCardReader.getSelectedItem());
        Vector<CardReader> readers = new Vector<CardReader>(3);
        if (swipeSupported) {
            readers.add(CardReader.SWIPE);
        }
        if (manualSupported) {
            readers.add(CardReader.MANUAL);
        }
        if (extSupported) {
            readers.add(CardReader.EXTERNAL_TERMINAL);
        }
        this.cbCardReader.setModel(this.createComboBoxModel(readers));
        if (readers.contains((Object)currentReader)) {
            this.cbCardReader.setSelectedItem((Object)currentReader);
        }
        if (!(swipeSupported || manualSupported || extSupported)) {
            this.cbCardReader.setEnabled(false);
            this.tfMerchantAccount.setEnabled(false);
            this.tfMerchantPass.setEnabled(false);
            this.cbSandboxMode.setEnabled(false);
        } else {
            this.cbCardReader.setEnabled(true);
            this.tfMerchantAccount.setEnabled(true);
            this.tfMerchantPass.setEnabled(true);
            this.cbSandboxMode.setEnabled(true);
        }
        if (swipeSupported || manualSupported) {
            this.tfMerchantAccount.setEnabled(true);
            this.tfMerchantPass.setEnabled(true);
            this.cbSandboxMode.setEnabled(true);
        } else {
            this.tfMerchantAccount.setEnabled(false);
            this.tfMerchantPass.setEnabled(false);
            this.cbSandboxMode.setEnabled(false);
        }
    }

    @Override
    public boolean save() throws Exception {
        CardConfig.setSwipeCardSupported(this.chckbxAllowMagneticSwipe.isSelected());
        CardConfig.setManualEntrySupported(this.chckbxAllowCardManual.isSelected());
        CardConfig.setExtTerminalSupported(this.chckbxAllowExternalTerminal.isSelected());
        CardReader cardReader = (CardReader)((Object)this.cbCardReader.getSelectedItem());
        CardConfig.setCardReader(cardReader);
        CardConfig.setMerchantAccount(this.tfMerchantAccount.getText());
        CardConfig.setMerchantPass(new String(this.tfMerchantPass.getPassword()));
        CardConfig.setSandboxMode(this.cbSandboxMode.isSelected());
        return true;
    }

    @Override
    public String getName() {
        return "";
    }
}


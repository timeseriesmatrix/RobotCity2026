/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.POSTextField;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class TicketImportConfigurationView
extends ConfigurationView {
    public static final String CONFIG_TAB_TAX = "Ticket Import";
    private POSTextField tfURL = new POSTextField(30);
    private POSTextField tfSecretKey = new POSTextField(10);
    private IntegerTextField tfPollInterval = new IntegerTextField(6);

    public TicketImportConfigurationView() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[]", "[]"));
        contentPanel.add(new JLabel(Messages.getString("TicketImportConfigurationView.4")));
        contentPanel.add((Component)this.tfURL, "wrap");
        contentPanel.add(new JLabel(Messages.getString("TicketImportConfigurationView.6")));
        contentPanel.add((Component)this.tfPollInterval, "wrap");
        this.add(contentPanel);
    }

    @Override
    public boolean save() throws Exception {
        if (!this.isInitialized()) {
            return true;
        }
        AppConfig.put("ticket_import_url", this.tfURL.getText());
        AppConfig.put("ticket_import_secret_key", this.tfSecretKey.getText());
        AppConfig.putInt("ticket_import_poll_interval", this.tfPollInterval.getInteger());
        return true;
    }

    @Override
    public void initialize() throws Exception {
        this.tfURL.setText(AppConfig.getString("ticket_import_url", "http://cloud.floreantpos.org/webstore/"));
        this.tfSecretKey.setText(AppConfig.getString("ticket_import_secret_key", "12345"));
        this.tfPollInterval.setText(AppConfig.getString("ticket_import_poll_interval", "60"));
        this.setInitialized(true);
    }

    @Override
    public String getName() {
        return CONFIG_TAB_TAX;
    }
}


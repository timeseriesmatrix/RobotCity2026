/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.CardConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.MercuryGatewayPlugin;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class CardConfigurationView
extends ConfigurationView {
    private JComboBox cbGateway;
    private DoubleTextField tfBarTabLimit = new DoubleTextField(10);
    private DoubleTextField tfAdvanceTipsPercentage = new DoubleTextField(10);
    private JPanel pluginConfigPanel = new JPanel(new BorderLayout());

    public CardConfigurationView() {
        this.createUI();
    }

    private void createUI() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][][][][][][][]"));
        JLabel lblMerchantGateway = new JLabel(Messages.getString("CardConfigurationView.2"));
        contentPanel.add((Component)lblMerchantGateway, "cell 0 4,alignx leading");
        this.cbGateway = new JComboBox();
        this.cbGateway.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CardConfigurationView.this.updatePluginConfigUI();
                }
                catch (Exception e1) {
                    POSMessageDialog.showError(CardConfigurationView.this, e1.getMessage(), e1);
                }
            }
        });
        contentPanel.add((Component)this.cbGateway, "cell 1 4,growx");
        contentPanel.add((Component)this.pluginConfigPanel, "newline,span,wrap,growx");
        contentPanel.add((Component)new JLabel(Messages.getString("CardConfigurationView.1")), "cell 0 6");
        contentPanel.add((Component)this.tfBarTabLimit, "cell 1 6");
        contentPanel.add((Component)new JLabel(Messages.getString("CardConfigurationView.4")), "cell 0 7");
        contentPanel.add((Component)this.tfAdvanceTipsPercentage, "cell 1 7");
        contentPanel.add((Component)new JLabel(Messages.getString("CardConfigurationView.10")), "cell 1 7");
        JSeparator separator = new JSeparator(0);
        contentPanel.add((Component)separator, "newline, growx, span 10, wrap");
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    private void initialMerchantGateways() {
        DefaultComboBoxModel<PaymentGatewayPlugin> model = new DefaultComboBoxModel<PaymentGatewayPlugin>();
        List<FloreantPlugin> plugins = ExtensionManager.getPlugins(PaymentGatewayPlugin.class);
        for (FloreantPlugin plugin : plugins) {
            if (plugin instanceof MercuryGatewayPlugin) continue;
            model.addElement((PaymentGatewayPlugin)plugin);
        }
        this.cbGateway.setModel(model);
        this.cbGateway.setSelectedItem((Object)CardConfig.getPaymentGateway());
    }

    @Override
    public boolean save() throws Exception {
        PaymentGatewayPlugin plugin = (PaymentGatewayPlugin)((Object)this.cbGateway.getSelectedItem());
        plugin.getConfigurationPane().save();
        CardConfig.setPaymentGateway(plugin);
        CardConfig.setBartabLimit(this.tfBarTabLimit.getDouble());
        CardConfig.setAdvanceTipsPercentage(this.tfAdvanceTipsPercentage.getDouble());
        return true;
    }

    @Override
    public void initialize() throws Exception {
        this.initialMerchantGateways();
        this.tfBarTabLimit.setText(String.valueOf(CardConfig.getBartabLimit()));
        this.tfAdvanceTipsPercentage.setText(String.valueOf(CardConfig.getAdvanceTipsPercentage()));
        this.updatePluginConfigUI();
        this.setInitialized(true);
    }

    private void updatePluginConfigUI() throws Exception {
        PaymentGatewayPlugin plugin = (PaymentGatewayPlugin)((Object)this.cbGateway.getSelectedItem());
        this.pluginConfigPanel.removeAll();
        ConfigurationView configurationPane = plugin.getConfigurationPane();
        configurationPane.initialize();
        this.pluginConfigPanel.add(configurationPane);
        this.revalidate();
        this.repaint();
    }

    @Override
    public String getName() {
        return Messages.getString("CardConfigurationView.6");
    }
}


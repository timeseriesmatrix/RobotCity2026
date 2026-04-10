/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.config.ui.CardConfigurationView;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.config.ui.DatabaseConfigurationView;
import com.floreantpos.config.ui.PeripheralConfigurationView;
import com.floreantpos.config.ui.PrintConfigurationView;
import com.floreantpos.config.ui.RestaurantConfigurationView;
import com.floreantpos.config.ui.TaxConfigurationView;
import com.floreantpos.config.ui.TerminalConfigurationView;
import com.floreantpos.config.ui.TicketImportConfigurationView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.TicketImportPlugin;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

public class ConfigurationDialog
extends POSDialog
implements ChangeListener,
ActionListener {
    private static final String OK = POSConstants.OK;
    private static final String CANCEL = POSConstants.CANCEL;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private List<ConfigurationView> views = new ArrayList<ConfigurationView>();

    public ConfigurationDialog() {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.setTitle(Messages.getString("CONFIGURATION_WINDOW_TITLE"));
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill", "", "[fill,grow][]"));
        this.tabbedPane.addChangeListener(this);
        contentPanel.add((Component)this.tabbedPane, "span, grow");
        this.addView(new RestaurantConfigurationView());
        this.addView(new TerminalConfigurationView());
        this.addView(new PrintConfigurationView());
        this.addView(new CardConfigurationView());
        this.addView(new DatabaseConfigurationView());
        this.addView(new TaxConfigurationView());
        this.addView(new PeripheralConfigurationView());
        TicketImportPlugin ticketImportPlugin = (TicketImportPlugin)ExtensionManager.getPlugin(TicketImportPlugin.class);
        if (ticketImportPlugin != null) {
            this.addView(new TicketImportConfigurationView());
        }
        JPanel bottomPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        JButton btnOk = new JButton(CANCEL);
        btnOk.addActionListener(this);
        bottomPanel.add((Component)btnOk, "dock east, gaptop 5");
        JButton btnCancel = new JButton(OK);
        btnCancel.addActionListener(this);
        bottomPanel.add((Component)btnCancel, "dock east, gapright 5, gaptop 5");
        this.add((Component)bottomPanel, "South");
        this.setDefaultCloseOperation(2);
        for (FloreantPlugin plugin : ExtensionManager.getPlugins()) {
            plugin.initConfigurationView((JDialog)this);
        }
        this.add((Component)contentPanel, "Center");
    }

    public void addView(ConfigurationView view) {
        this.tabbedPane.addTab(view.getName(), view);
        this.views.add(view);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            this.stateChanged(null);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ConfigurationView view = (ConfigurationView)this.tabbedPane.getSelectedComponent();
        if (!view.isInitialized()) {
            try {
                view.initialize();
            }
            catch (Exception e1) {
                POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e1);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (OK.equalsIgnoreCase(e.getActionCommand())) {
            try {
                for (ConfigurationView view : this.views) {
                    if (!view.isInitialized()) continue;
                    view.save();
                }
                this.setCanceled(false);
                this.dispose();
            }
            catch (PosException x) {
                POSMessageDialog.showError(this, x.getMessage());
            }
            catch (Exception x) {
                POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, x);
            }
        } else if (CANCEL.equalsIgnoreCase(e.getActionCommand())) {
            this.setCanceled(true);
            this.dispose();
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Database;
import com.floreantpos.Messages;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.main.Main;
import com.floreantpos.swing.POSPasswordField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.DatabaseConnectionException;
import com.floreantpos.util.DatabaseUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class DatabaseConfigurationView
extends ConfigurationView
implements ActionListener {
    private static final String CONFIGURE_DB = "CD";
    private static final String SAVE = "SAVE";
    private static final String CANCEL = "cancel";
    private static final String TEST = "test";
    private POSTextField tfServerAddress;
    private POSTextField tfServerPort;
    private POSTextField tfDatabaseName;
    private POSTextField tfUserName;
    private POSPasswordField tfPassword;
    private JButton btnTestConnection;
    private JButton btnCreateDb;
    private JButton btnSave;
    private JComboBox databaseCombo;
    private JLabel lblServerAddress;
    private JLabel lblServerPort;
    private JLabel lblDbName;
    private JLabel lblUserName;
    private JLabel lblDbPassword;

    public DatabaseConfigurationView() throws HeadlessException {
        this.initUI();
        this.addUIListeners();
    }

    protected void initUI() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("fill", "[][grow,fill]", "[][][][][][][][grow,fill]"));
        this.tfServerAddress = new POSTextField();
        this.tfServerPort = new POSTextField();
        this.tfDatabaseName = new POSTextField();
        this.tfUserName = new POSTextField();
        this.tfPassword = new POSPasswordField();
        this.databaseCombo = new JComboBox<Database>(Database.values());
        String databaseProviderName = AppConfig.getDatabaseProviderName();
        if (StringUtils.isNotEmpty((String)databaseProviderName)) {
            this.databaseCombo.setSelectedItem((Object)Database.getByProviderName(databaseProviderName));
        }
        contentPanel.add(new JLabel(Messages.getString("DatabaseConfigurationDialog.8")));
        contentPanel.add((Component)this.databaseCombo, "grow, wrap");
        this.lblServerAddress = new JLabel(Messages.getString("DatabaseConfigurationDialog.10") + ":");
        contentPanel.add(this.lblServerAddress);
        contentPanel.add((Component)this.tfServerAddress, "grow, wrap");
        this.lblServerPort = new JLabel(Messages.getString("DatabaseConfigurationDialog.13") + ":");
        contentPanel.add(this.lblServerPort);
        contentPanel.add((Component)this.tfServerPort, "grow, wrap");
        this.lblDbName = new JLabel(Messages.getString("DatabaseConfigurationDialog.16") + ":");
        contentPanel.add(this.lblDbName);
        contentPanel.add((Component)this.tfDatabaseName, "grow, wrap");
        this.lblUserName = new JLabel(Messages.getString("DatabaseConfigurationDialog.19") + ":");
        contentPanel.add(this.lblUserName);
        contentPanel.add((Component)this.tfUserName, "grow, wrap");
        this.lblDbPassword = new JLabel(Messages.getString("DatabaseConfigurationDialog.22") + ":");
        contentPanel.add(this.lblDbPassword);
        contentPanel.add((Component)this.tfPassword, "grow, wrap");
        contentPanel.add((Component)new JSeparator(), "span, grow, gaptop 10");
        this.btnTestConnection = new JButton(Messages.getString("DatabaseConfigurationDialog.26"));
        this.btnTestConnection.setActionCommand(TEST);
        this.btnSave = new JButton(Messages.getString("DatabaseConfigurationDialog.27"));
        this.btnSave.setActionCommand(SAVE);
        JPanel buttonPanel = new JPanel(new FlowLayout(2));
        this.btnCreateDb = new JButton(Messages.getString("DatabaseConfigurationDialog.29"));
        this.btnCreateDb.setActionCommand(CONFIGURE_DB);
        buttonPanel.add(this.btnCreateDb);
        buttonPanel.add(this.btnTestConnection);
        buttonPanel.add(this.btnSave);
        contentPanel.add((Component)buttonPanel, "span, grow");
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    private void addUIListeners() {
        this.btnTestConnection.addActionListener(this);
        this.btnCreateDb.addActionListener(this);
        this.btnSave.addActionListener(this);
        this.databaseCombo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Database selectedDb = (Database)((Object)DatabaseConfigurationView.this.databaseCombo.getSelectedItem());
                if (selectedDb == Database.DERBY_SINGLE) {
                    DatabaseConfigurationView.this.setFieldsVisible(false);
                    return;
                }
                DatabaseConfigurationView.this.setFieldsVisible(true);
                String databasePort = AppConfig.getDatabasePort();
                if (StringUtils.isEmpty((String)databasePort)) {
                    databasePort = selectedDb.getDefaultPort();
                }
                DatabaseConfigurationView.this.tfServerPort.setText(databasePort);
            }
        });
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String command = e.getActionCommand();
            Database selectedDb = (Database)((Object)this.databaseCombo.getSelectedItem());
            String providerName = selectedDb.getProviderName();
            String databaseURL = this.tfServerAddress.getText();
            String databasePort = this.tfServerPort.getText();
            String databaseName = this.tfDatabaseName.getText();
            String user = this.tfUserName.getText();
            String pass = new String(this.tfPassword.getPassword());
            String connectionString = selectedDb.getConnectString(databaseURL, databasePort, databaseName);
            String hibernateDialect = selectedDb.getHibernateDialect();
            String driverClass = selectedDb.getHibernateConnectionDriverClass();
            if (TEST.equalsIgnoreCase(command)) {
                Application.getInstance().setSystemInitialized(false);
                this.saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
                try {
                    DatabaseUtil.checkConnection(connectionString, hibernateDialect, driverClass, user, pass);
                }
                catch (DatabaseConnectionException e1) {
                    JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.32"));
                    return;
                }
                JOptionPane.showMessageDialog(POSUtil.getBackOfficeWindow(), Messages.getString("DatabaseConfigurationDialog.31"));
                return;
            }
            if (CONFIGURE_DB.equals(command)) {
                Application.getInstance().setSystemInitialized(false);
                int i = JOptionPane.showConfirmDialog(POSUtil.getBackOfficeWindow(), Messages.getString("DatabaseConfigurationDialog.33"), Messages.getString("DatabaseConfigurationDialog.34"), 0);
                if (i != 0) {
                    return;
                }
                i = JOptionPane.showConfirmDialog(POSUtil.getBackOfficeWindow(), Messages.getString("DatabaseConfigurationView.3"), Messages.getString("DatabaseConfigurationView.4"), 0);
                boolean generateSampleData = false;
                if (i == 0) {
                    generateSampleData = true;
                }
                this.saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
                String connectionString2 = selectedDb.getCreateDbConnectString(databaseURL, databasePort, databaseName);
                this.setCursor(Cursor.getPredefinedCursor(3));
                boolean createDatabase = DatabaseUtil.createDatabase(connectionString2, hibernateDialect, driverClass, user, pass, generateSampleData);
                this.setCursor(Cursor.getDefaultCursor());
                if (createDatabase) {
                    JOptionPane.showMessageDialog(POSUtil.getBackOfficeWindow(), "Database created. Default password is 1111.\n\nThe system will now restart.");
                    Main.restart();
                    return;
                } else {
                    JOptionPane.showMessageDialog(POSUtil.getBackOfficeWindow(), Messages.getString("DatabaseConfigurationDialog.36"));
                }
                return;
            }
            if (SAVE.equalsIgnoreCase(command)) {
                Application.getInstance().setSystemInitialized(false);
                this.saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
                return;
            }
            if (!CANCEL.equalsIgnoreCase(command)) return;
        }
        catch (Exception e2) {
            POSMessageDialog.showMessage(POSUtil.getBackOfficeWindow(), e2.getMessage());
        }
    }

    private void saveConfig(Database selectedDb, String providerName, String databaseURL, String databasePort, String databaseName, String user, String pass, String connectionString, String hibernateDialect) {
        AppConfig.setDatabaseProviderName(providerName);
        AppConfig.setConnectString(connectionString);
        AppConfig.setDatabaseHost(databaseURL);
        AppConfig.setDatabasePort(databasePort);
        AppConfig.setDatabaseName(databaseName);
        AppConfig.setDatabaseUser(user);
        AppConfig.setDatabasePassword(pass);
    }

    private void setFieldsVisible(boolean visible) {
        this.lblServerAddress.setVisible(visible);
        this.tfServerAddress.setVisible(visible);
        this.lblServerPort.setVisible(visible);
        this.tfServerPort.setVisible(visible);
        this.lblDbName.setVisible(visible);
        this.tfDatabaseName.setVisible(visible);
        this.lblUserName.setVisible(visible);
        this.tfUserName.setVisible(visible);
        this.lblDbPassword.setVisible(visible);
        this.tfPassword.setVisible(visible);
    }

    @Override
    public boolean save() throws Exception {
        return false;
    }

    @Override
    public void initialize() throws Exception {
        Database selectedDb = (Database)((Object)this.databaseCombo.getSelectedItem());
        String databaseURL = AppConfig.getDatabaseHost();
        this.tfServerAddress.setText(databaseURL);
        String databasePort = AppConfig.getDatabasePort();
        if (StringUtils.isEmpty((String)databasePort)) {
            databasePort = selectedDb.getDefaultPort();
        }
        this.tfServerPort.setText(databasePort);
        this.tfDatabaseName.setText(AppConfig.getDatabaseName());
        this.tfUserName.setText(AppConfig.getDatabaseUser());
        this.tfPassword.setText(AppConfig.getDatabasePassword());
        if (selectedDb == Database.DERBY_SINGLE) {
            this.setFieldsVisible(false);
        } else {
            this.setFieldsVisible(true);
        }
        this.setInitialized(true);
    }

    @Override
    public String getName() {
        return Messages.getString("DatabaseConfigurationView.5");
    }
}


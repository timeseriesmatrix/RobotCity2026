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
import com.floreantpos.PosLog;
import com.floreantpos.config.AppConfig;
import com.floreantpos.main.Application;
import com.floreantpos.main.Main;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.POSPasswordField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.DatabaseConnectionException;
import com.floreantpos.util.DatabaseUtil;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class DatabaseConfigurationDialog
extends POSDialog
implements ActionListener {
    private static final String CREATE_DATABASE = "CD";
    private static final String UPDATE_DATABASE = "UD";
    private static final String SAVE = "SAVE";
    private static final String CANCEL = "cancel";
    private static final String TEST = "test";
    private POSTextField tfServerAddress;
    private POSTextField tfServerPort;
    private POSTextField tfDatabaseName;
    private POSTextField tfUserName;
    private POSPasswordField tfPassword;
    private PosButton btnTestConnection;
    private PosButton btnCreateDb;
    private PosButton btnUpdateDb;
    private PosButton btnExit;
    private PosButton btnSave;
    private JComboBox databaseCombo;
    private TitlePanel titlePanel;
    private JLabel lblServerAddress;
    private JLabel lblServerPort;
    private JLabel lblDbName;
    private JLabel lblUserName;
    private JLabel lblDbPassword;
    private boolean connectionSuccess;

    public DatabaseConfigurationDialog() throws HeadlessException {
        this.setFieldValues();
        this.addUIListeners();
    }

    @Override
    protected void initUI() {
        this.getContentPane().setLayout((LayoutManager)new MigLayout("fill", "[][fill, grow]", ""));
        this.titlePanel = new TitlePanel();
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
        this.getContentPane().add((Component)this.titlePanel, "span, grow, wrap");
        this.getContentPane().add(new JLabel(Messages.getString("DatabaseConfigurationDialog.8")));
        this.getContentPane().add((Component)this.databaseCombo, "grow, wrap");
        this.lblServerAddress = new JLabel(Messages.getString("DatabaseConfigurationDialog.10") + ":");
        this.getContentPane().add(this.lblServerAddress);
        this.getContentPane().add((Component)this.tfServerAddress, "grow, wrap");
        this.lblServerPort = new JLabel(Messages.getString("DatabaseConfigurationDialog.13") + ":");
        this.getContentPane().add(this.lblServerPort);
        this.getContentPane().add((Component)this.tfServerPort, "grow, wrap");
        this.lblDbName = new JLabel(Messages.getString("DatabaseConfigurationDialog.16") + ":");
        this.getContentPane().add(this.lblDbName);
        this.getContentPane().add((Component)this.tfDatabaseName, "grow, wrap");
        this.lblUserName = new JLabel(Messages.getString("DatabaseConfigurationDialog.19") + ":");
        this.getContentPane().add(this.lblUserName);
        this.getContentPane().add((Component)this.tfUserName, "grow, wrap");
        this.lblDbPassword = new JLabel(Messages.getString("DatabaseConfigurationDialog.22") + ":");
        this.getContentPane().add(this.lblDbPassword);
        this.getContentPane().add((Component)this.tfPassword, "grow, wrap");
        this.getContentPane().add((Component)new JSeparator(), "span, grow, gaptop 10");
        this.btnTestConnection = new PosButton(Messages.getString("DatabaseConfigurationDialog.26").toUpperCase());
        this.btnTestConnection.setActionCommand(TEST);
        this.btnSave = new PosButton(Messages.getString("DatabaseConfigurationDialog.27").toUpperCase());
        this.btnSave.setActionCommand(SAVE);
        this.btnExit = new PosButton(Messages.getString("DatabaseConfigurationDialog.28").toUpperCase());
        this.btnExit.setActionCommand(CANCEL);
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("inset 0, fill", "grow", ""));
        this.btnCreateDb = new PosButton(Messages.getString("DatabaseConfigurationDialog.29").toUpperCase());
        this.btnCreateDb.setActionCommand(CREATE_DATABASE);
        this.btnUpdateDb = new PosButton(Messages.getString("UPDATE_DATABASE").toUpperCase());
        this.btnUpdateDb.setActionCommand(UPDATE_DATABASE);
        buttonPanel.add(this.btnUpdateDb);
        buttonPanel.add(this.btnCreateDb);
        buttonPanel.add(this.btnTestConnection);
        buttonPanel.add(this.btnSave);
        buttonPanel.add(this.btnExit);
        this.getContentPane().add((Component)buttonPanel, "span, grow");
    }

    private void addUIListeners() {
        this.btnTestConnection.addActionListener(this);
        this.btnCreateDb.addActionListener(this);
        this.btnSave.addActionListener(this);
        this.btnExit.addActionListener(this);
        this.btnUpdateDb.addActionListener(this);
        this.databaseCombo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Database selectedDb = (Database)((Object)DatabaseConfigurationDialog.this.databaseCombo.getSelectedItem());
                if (selectedDb == Database.DERBY_SINGLE) {
                    DatabaseConfigurationDialog.this.setFieldsVisible(false);
                    return;
                }
                DatabaseConfigurationDialog.this.setFieldsVisible(true);
                String databasePort = AppConfig.getDatabasePort();
                if (StringUtils.isEmpty((String)databasePort)) {
                    databasePort = selectedDb.getDefaultPort();
                }
                DatabaseConfigurationDialog.this.tfServerPort.setText(databasePort);
            }
        });
    }

    private void setFieldValues() {
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
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        block26: {
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
                if (CANCEL.equalsIgnoreCase(command)) {
                    this.dispose();
                    return;
                }
                this.setCursor(Cursor.getPredefinedCursor(3));
                Application.getInstance().setSystemInitialized(false);
                this.saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
                if (TEST.equalsIgnoreCase(command)) {
                    try {
                        DatabaseUtil.checkConnection(connectionString, hibernateDialect, driverClass, user, pass);
                    }
                    catch (DatabaseConnectionException e1) {
                        JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.32"));
                        this.setCursor(Cursor.getDefaultCursor());
                        return;
                    }
                    this.connectionSuccess = true;
                    JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.31"));
                    break block26;
                }
                if (UPDATE_DATABASE.equals(command)) {
                    int i = JOptionPane.showConfirmDialog(this, Messages.getString("DatabaseConfigurationDialog.0"), Messages.getString("DatabaseConfigurationDialog.1"), 0);
                    if (i != 0) {
                        return;
                    }
                    this.setCursor(Cursor.getPredefinedCursor(3));
                    boolean databaseUpdated = DatabaseUtil.updateDatabase(connectionString, hibernateDialect, driverClass, user, pass);
                    if (databaseUpdated) {
                        this.connectionSuccess = true;
                        JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.2"));
                    } else {
                        JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.3"));
                    }
                } else if (CREATE_DATABASE.equals(command)) {
                    int i = JOptionPane.showConfirmDialog(this, Messages.getString("DatabaseConfigurationDialog.33"), Messages.getString("DatabaseConfigurationDialog.34"), 0);
                    if (i != 0) {
                        return;
                    }
                    i = JOptionPane.showConfirmDialog(this, Messages.getString("DatabaseConfigurationDialog.4"), Messages.getString("DatabaseConfigurationDialog.5"), 0);
                    boolean generateSampleData = false;
                    if (i == 0) {
                        generateSampleData = true;
                    }
                    this.setCursor(Cursor.getPredefinedCursor(3));
                    String createDbConnectString = selectedDb.getCreateDbConnectString(databaseURL, databasePort, databaseName);
                    boolean databaseCreated = DatabaseUtil.createDatabase(createDbConnectString, hibernateDialect, driverClass, user, pass, generateSampleData);
                    if (databaseCreated) {
                        JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.6") + Messages.getString("DatabaseConfigurationDialog.7"));
                        Main.restart();
                        this.connectionSuccess = true;
                    } else {
                        JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.36"));
                    }
                } else if (SAVE.equalsIgnoreCase(command)) {
                    if (this.connectionSuccess) {
                        Application.getInstance().initializeSystem();
                    }
                    this.dispose();
                }
            }
            catch (Exception e2) {
                PosLog.error(this.getClass(), e2);
                POSMessageDialog.showMessage(this, e2.getMessage());
            }
            finally {
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void isAuthorizedToPerformDbChange() {
        DatabaseUtil.initialize();
        UserDAO.getInstance().findAll();
        String password = JOptionPane.showInputDialog(Messages.getString("DatabaseConfigurationDialog.9"));
        User user2 = UserDAO.getInstance().findUserBySecretKey(password);
        if (user2 == null || !user2.isAdministrator()) {
            POSMessageDialog.showError(this, Messages.getString("DatabaseConfigurationDialog.11"));
            return;
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

    @Override
    public void setTitle(String title) {
        super.setTitle(Messages.getString("DatabaseConfigurationDialog.37"));
        this.titlePanel.setTitle(title);
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

    public static DatabaseConfigurationDialog show(Frame parent) {
        DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog();
        dialog.setTitle(Messages.getString("DatabaseConfigurationDialog.38"));
        dialog.pack();
        dialog.open();
        return dialog;
    }
}


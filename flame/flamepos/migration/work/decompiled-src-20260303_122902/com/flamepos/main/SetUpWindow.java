/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jgoodies.looks.plastic.PlasticTheme
 *  com.jgoodies.looks.plastic.PlasticXPLookAndFeel
 *  com.jgoodies.looks.plastic.theme.ExperienceBlue
 *  com.jidesoft.swing.JideScrollPane
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.main;

import com.floreantpos.Database;
import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.bo.actions.DataImportAction;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Main;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.POSPasswordField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.DatabaseConnectionException;
import com.floreantpos.util.DatabaseUtil;
import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jidesoft.swing.JideScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class SetUpWindow
extends JFrame
implements ActionListener {
    private static final String CREATE_DATABASE = "CD";
    private static final String CREATE_SAMPLE_DATA = "UD";
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
    private PosButton btnCreateSampleData;
    private PosButton btnExit;
    private PosButton btnSave;
    private JComboBox databaseCombo;
    private JLabel lblServerAddress;
    private JLabel lblServerPort;
    private JLabel lblDbName;
    private JLabel lblUserName;
    private JLabel lblDbPassword;
    private JLabel lblId;
    private JLabel lblConfirmSecretKey;
    private JLabel lblFirstName;
    private JLabel lblLastName;
    private JLabel lblSecretKey;
    private FixedLengthTextField tfFirstName;
    private FixedLengthTextField tfUserId;
    private FixedLengthTextField tfLastName;
    private JPasswordField tfPassword1;
    private JPasswordField tfPassword2;
    private IntegerTextField tfTerminalNumber;
    private IntegerTextField tfSecretKeyLength;
    private DoubleTextField tfScaleFactor;
    private JCheckBox chkAutoLogoff;
    private IntegerTextField tfLogoffTime = new IntegerTextField(4);
    private boolean connectionSuccess;

    public SetUpWindow() throws HeadlessException {
        this.setLookAndFeel();
        ImageIcon applicationIcon = new ImageIcon(this.getClass().getResource("/icons/icon.png"));
        this.setIconImage(applicationIcon.getImage());
        this.initUI();
        this.setFieldValues();
        this.addUIListeners();
        this.updateView();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            this.setupSizeAndLocation();
        }
    }

    private void setLookAndFeel() {
        try {
            PlasticXPLookAndFeel.setPlasticTheme((PlasticTheme)new ExperienceBlue());
            UIManager.setLookAndFeel((LookAndFeel)new PlasticXPLookAndFeel());
            this.initializeFont();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setupSizeAndLocation() {
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(0);
        this.setSize(PosUIManager.getSize(700, 420));
    }

    protected void initUI() {
        this.getContentPane().setLayout(new BorderLayout());
        JPanel databaseConfigPanel = new JPanel((LayoutManager)new MigLayout("fill,hidemode 3", "[150px][fill, grow]", ""));
        databaseConfigPanel.setBorder(new TitledBorder(Messages.getString("SetUpWindow.3")));
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
        this.btnTestConnection = new PosButton("Test");
        this.btnTestConnection.setActionCommand(TEST);
        this.btnCreateDb = new PosButton("Create New");
        this.btnCreateDb.setActionCommand(CREATE_DATABASE);
        this.btnCreateSampleData = new PosButton("Create sample data");
        this.btnCreateSampleData.setActionCommand(CREATE_SAMPLE_DATA);
        databaseConfigPanel.add(new JLabel(Messages.getString("DatabaseConfigurationDialog.8")));
        databaseConfigPanel.add((Component)this.databaseCombo, "w 200!,grow, split 4");
        databaseConfigPanel.add((Component)this.btnTestConnection, "w 50!,h 30!");
        databaseConfigPanel.add((Component)this.btnCreateDb, "w 100!,h 30!");
        databaseConfigPanel.add((Component)this.btnCreateSampleData, "h 30!,wrap");
        this.lblServerAddress = new JLabel(Messages.getString("DatabaseConfigurationDialog.10") + ":");
        databaseConfigPanel.add(this.lblServerAddress);
        databaseConfigPanel.add((Component)this.tfServerAddress, "grow, split 3");
        this.lblServerPort = new JLabel("Port:");
        databaseConfigPanel.add(this.lblServerPort);
        this.tfServerPort.setHorizontalAlignment(4);
        databaseConfigPanel.add((Component)this.tfServerPort, "w 50!,wrap");
        this.lblDbName = new JLabel(Messages.getString("DatabaseConfigurationDialog.16") + ":");
        databaseConfigPanel.add(this.lblDbName);
        databaseConfigPanel.add((Component)this.tfDatabaseName, "grow, wrap");
        this.lblUserName = new JLabel(Messages.getString("DatabaseConfigurationDialog.19") + ":");
        databaseConfigPanel.add(this.lblUserName);
        databaseConfigPanel.add((Component)this.tfUserName, "grow, split 3");
        this.lblDbPassword = new JLabel("Password:");
        databaseConfigPanel.add(this.lblDbPassword);
        databaseConfigPanel.add((Component)this.tfPassword, "grow, wrap");
        this.btnSave = new PosButton(Messages.getString("DatabaseConfigurationDialog.27").toUpperCase());
        this.btnSave.setActionCommand(SAVE);
        this.btnExit = new PosButton(Messages.getString("DatabaseConfigurationDialog.28").toUpperCase());
        this.btnExit.setActionCommand(CANCEL);
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("fillx,right"));
        buttonPanel.add((Component)this.btnSave, "h 40!,split 2,right");
        buttonPanel.add((Component)this.btnExit, "h 40!");
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fillx"));
        contentPanel.add((Component)databaseConfigPanel, "grow,wrap");
        contentPanel.add((Component)this.createTerminalConfigPanel(), "grow,wrap");
        this.getContentPane().add((Component)new JideScrollPane((Component)contentPanel), "Center");
        this.getContentPane().add((Component)buttonPanel, "South");
        this.getContentPane().setBackground(databaseConfigPanel.getBackground());
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel((LayoutManager)new MigLayout("fill,hidemode 3", "[150px][fill, grow]", ""));
        userPanel.setVisible(false);
        userPanel.setBorder(new TitledBorder(Messages.getString("SetUpWindow.16")));
        this.lblId = new JLabel();
        this.lblFirstName = new JLabel();
        this.lblLastName = new JLabel();
        this.lblSecretKey = new JLabel();
        this.lblConfirmSecretKey = new JLabel();
        this.tfPassword1 = new JPasswordField(new FixedLengthDocument(16), "", 5);
        this.tfPassword2 = new JPasswordField(new FixedLengthDocument(16), "", 5);
        this.tfUserId = new FixedLengthTextField();
        this.tfFirstName = new FixedLengthTextField();
        this.tfFirstName.setColumns(20);
        this.tfFirstName.setLength(30);
        this.tfLastName = new FixedLengthTextField();
        this.tfLastName.setLength(30);
        this.tfLastName.setColumns(20);
        this.lblId.setText("ID");
        userPanel.add((Component)this.lblId, "aligny center");
        userPanel.add((Component)this.tfUserId, "growx,aligny center,wrap");
        this.lblFirstName.setText("First Name");
        userPanel.add((Component)this.lblFirstName, "aligny center");
        userPanel.add((Component)this.tfFirstName, "growx,aligny center,split 3");
        this.lblLastName.setText("Last Name");
        userPanel.add((Component)this.lblLastName, "aligny center");
        userPanel.add((Component)this.tfLastName, "growx,aligny ,w 200!,center,wrap");
        this.lblSecretKey.setText("Secret Key");
        userPanel.add((Component)this.lblSecretKey, "aligny center");
        userPanel.add((Component)this.tfPassword1, "growx,aligny center,split 3");
        this.lblConfirmSecretKey.setText("Confirm Secret Key");
        userPanel.add((Component)this.lblConfirmSecretKey, "aligny center");
        userPanel.add((Component)this.tfPassword2, "growx,w 200!,aligny center");
        return userPanel;
    }

    private JPanel createTerminalConfigPanel() {
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill,hidemode 3", "[150px][fill, grow]", ""));
        contentPanel.setBorder(new TitledBorder(Messages.getString("SetUpWindow.20")));
        this.tfTerminalNumber = new IntegerTextField();
        this.tfTerminalNumber.setColumns(10);
        contentPanel.add(new JLabel(Messages.getString("SetUpWindow.21")));
        contentPanel.add((Component)this.tfTerminalNumber, "aligny top,wrap");
        this.tfSecretKeyLength = new IntegerTextField(3);
        contentPanel.add(new JLabel("Default password length"));
        contentPanel.add((Component)this.tfSecretKeyLength, "wrap");
        this.chkAutoLogoff = new JCheckBox(Messages.getString("SetUpWindow.22"));
        this.tfLogoffTime.setEnabled(false);
        this.chkAutoLogoff.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SetUpWindow.this.chkAutoLogoff.isSelected()) {
                    SetUpWindow.this.tfLogoffTime.setEnabled(true);
                } else {
                    SetUpWindow.this.tfLogoffTime.setEnabled(false);
                }
            }
        });
        contentPanel.add((Component)this.chkAutoLogoff, "newline");
        contentPanel.add((Component)new JLabel(Messages.getString("TerminalConfigurationView.16")), "split 2");
        contentPanel.add((Component)this.tfLogoffTime, "alignx left,grow,wrap");
        contentPanel.add(new JLabel("Screen scaling"));
        this.tfScaleFactor = new DoubleTextField(5);
        contentPanel.add(this.tfScaleFactor);
        return contentPanel;
    }

    private void addUIListeners() {
        this.btnTestConnection.addActionListener(this);
        this.btnCreateDb.addActionListener(this);
        this.btnSave.addActionListener(this);
        this.btnExit.addActionListener(this);
        this.btnCreateSampleData.addActionListener(this);
        this.databaseCombo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Database selectedDb = (Database)((Object)SetUpWindow.this.databaseCombo.getSelectedItem());
                if (selectedDb == Database.DERBY_SINGLE) {
                    SetUpWindow.this.setFieldsVisible(false);
                    return;
                }
                SetUpWindow.this.setFieldsVisible(true);
                String databasePort = AppConfig.getDatabasePort();
                if (StringUtils.isEmpty((String)databasePort)) {
                    databasePort = selectedDb.getDefaultPort();
                }
                SetUpWindow.this.tfServerPort.setText(databasePort);
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
                System.exit(1);
                return;
            }
            this.setCursor(Cursor.getPredefinedCursor(3));
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
                return;
            }
            this.saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
            if (CREATE_SAMPLE_DATA.equals(command)) {
                DataImportAction.importMenuItems(DatabaseUtil.class.getResourceAsStream("/floreantpos-menu-items.xml"));
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
                    this.connectionSuccess = true;
                } else {
                    JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.36"));
                }
            } else if (SAVE.equalsIgnoreCase(command)) {
                Integer terminalId = this.tfTerminalNumber.getInteger();
                Integer defaultPassLen = this.tfSecretKeyLength.getInteger();
                Integer autoLogOffTime = this.tfLogoffTime.getInteger();
                Boolean isLogOff = this.chkAutoLogoff.isSelected();
                Double scaleFactor = this.tfScaleFactor.getDouble();
                TerminalConfig.setTerminalId(terminalId);
                TerminalConfig.setDefaultPassLen(defaultPassLen);
                TerminalConfig.setScreenScaleFactor(scaleFactor);
                TerminalConfig.setAutoLogoffEnable(isLogOff);
                TerminalConfig.setAutoLogoffTime(autoLogOffTime <= 0 ? 10 : autoLogOffTime);
                try {
                    DatabaseUtil.initialize();
                    this.saveConfigData();
                }
                catch (Exception ex) {
                    int i = JOptionPane.showConfirmDialog(this, "Connection Failed. Do you want to save?", "Connection status!", 0);
                    if (i == 0) {
                        System.exit(1);
                    }
                }
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

    private void saveConfig(Database selectedDb, String providerName, String databaseURL, String databasePort, String databaseName, String user, String pass, String connectionString, String hibernateDialect) {
        AppConfig.setDatabaseProviderName(providerName);
        AppConfig.setConnectString(connectionString);
        AppConfig.setDatabaseHost(databaseURL);
        AppConfig.setDatabasePort(databasePort);
        AppConfig.setDatabaseName(databaseName);
        AppConfig.setDatabaseUser(user);
        AppConfig.setDatabasePassword(pass);
    }

    private void saveConfigData() {
        User user = null;
        Terminal terminal = new Terminal();
        if (!this.updateModel(user, terminal)) {
            return;
        }
        TerminalDAO.getInstance().saveOrUpdate(terminal);
        POSMessageDialog.showMessage(Messages.getString("SetUpWindow.0"));
        int i = JOptionPane.showConfirmDialog(this, "Do you want to start application?", "Message", 0);
        if (i != 0) {
            System.exit(1);
        } else {
            try {
                Main.restart();
            }
            catch (IOException iOException) {
            }
            catch (InterruptedException interruptedException) {
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
    }

    private void updateView() {
        int terminalId = TerminalConfig.getTerminalId();
        if (terminalId == -1) {
            Random random = new Random();
            terminalId = random.nextInt(10000) + 1;
        }
        this.tfTerminalNumber.setText(String.valueOf(terminalId));
        this.loadDefaultData();
    }

    private void loadDefaultData() {
        this.tfScaleFactor.setText("1");
        this.chkAutoLogoff.setSelected(false);
        this.tfSecretKeyLength.setText("4");
        this.tfLogoffTime.setText("10");
    }

    private boolean updateModel(User user, Terminal terminal) {
        Integer terminalId = this.tfTerminalNumber.getInteger();
        terminal.setId(terminalId);
        terminal.setName(String.valueOf(terminalId));
        return true;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle("Application Setup");
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

    public static SetUpWindow open() {
        SetUpWindow window = new SetUpWindow();
        window.setTitle(Messages.getString("DatabaseConfigurationDialog.38"));
        window.pack();
        window.setVisible(true);
        return window;
    }

    public static void main(String[] args) throws Exception {
        SetUpWindow.open();
    }

    private void initializeFont() {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value == null || !(value instanceof FontUIResource)) continue;
            FontUIResource f = (FontUIResource)value;
            String fontName = f.getFontName();
            Font font = new Font(fontName, f.getStyle(), PosUIManager.getDefaultFontSize());
            UIManager.put(key, new FontUIResource(font));
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.main.Application;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.DrawerUtil;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.SerialPortUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

public class PeripheralConfigurationView
extends ConfigurationView {
    public static final String CONFIG_TAB_PERIPHERAL = "Peripherals";
    private JCheckBox chkHasCashDrawer;
    private JTextField tfDrawerName = new JTextField(10);
    private JTextField tfDrawerCodes = new JTextField(15);
    private DoubleTextField tfDrawerInitialBalance = new DoubleTextField(6);
    private JCheckBox cbCustomerDisplay;
    private JTextField tfCustomerDisplayPort;
    private JTextField tfCustomerDisplayMessage;
    private JCheckBox cbScaleActive;
    private JTextField tfScalePort;
    private FixedLengthTextField tfScaleDisplayMessage;
    private JCheckBox chkCallerIdEnable;
    private JComboBox cbCallerIds;

    public PeripheralConfigurationView() {
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[grow]", "[][]"));
        JPanel drawerConfigPanel = new JPanel((LayoutManager)new MigLayout());
        drawerConfigPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PeripheralConfigurationView.4")));
        this.chkHasCashDrawer = new JCheckBox(Messages.getString("TerminalConfigurationView.15"));
        drawerConfigPanel.add((Component)this.chkHasCashDrawer, "span 5, wrap");
        drawerConfigPanel.add(new JLabel(Messages.getString("TerminalConfigurationView.25")));
        drawerConfigPanel.add((Component)this.tfDrawerName, "");
        drawerConfigPanel.add((Component)new JLabel(Messages.getString("TerminalConfigurationView.27")), "newline");
        drawerConfigPanel.add((Component)this.tfDrawerCodes, "");
        JButton btnDrawerTest = new JButton(Messages.getString("TerminalConfigurationView.11"));
        btnDrawerTest.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = PeripheralConfigurationView.this.tfDrawerCodes.getText();
                if (StringUtils.isEmpty((String)text)) {
                    text = TerminalConfig.getDefaultDrawerControlCodes();
                }
                String[] split = text.split(",");
                char[] codes = new char[split.length];
                for (int i = 0; i < split.length; ++i) {
                    try {
                        codes[i] = (char)Integer.parseInt(split[i]);
                        continue;
                    }
                    catch (Exception x) {
                        codes[i] = 48;
                    }
                }
                DrawerUtil.kickDrawer(PeripheralConfigurationView.this.tfDrawerName.getText(), codes);
            }
        });
        drawerConfigPanel.add(btnDrawerTest);
        JButton btnRestoreDrawerDefault = new JButton(Messages.getString("TerminalConfigurationView.32"));
        btnRestoreDrawerDefault.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PeripheralConfigurationView.this.tfDrawerName.setText("COM1");
                PeripheralConfigurationView.this.tfDrawerCodes.setText(TerminalConfig.getDefaultDrawerControlCodes());
            }
        });
        drawerConfigPanel.add(btnRestoreDrawerDefault);
        drawerConfigPanel.add((Component)new JLabel(Messages.getString("TerminalConfigurationView.34")), "newline");
        drawerConfigPanel.add((Component)this.tfDrawerInitialBalance, "span 4, wrap");
        contentPanel.add((Component)drawerConfigPanel, "span 3, grow, wrap");
        this.chkHasCashDrawer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PeripheralConfigurationView.this.doEnableDisableDrawerPull();
            }
        });
        JPanel customerDisplayPanel = new JPanel((LayoutManager)new MigLayout());
        customerDisplayPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PeripheralConfigurationView.5")));
        this.cbCustomerDisplay = new JCheckBox(Messages.getString("PeripheralConfigurationView.6"));
        this.tfCustomerDisplayPort = new JTextField(20);
        this.tfCustomerDisplayMessage = new FixedLengthTextField(20);
        JButton btnTest = new JButton(Messages.getString("PeripheralConfigurationView.7"));
        btnTest.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalConfig.setCustomerDisplayPort(PeripheralConfigurationView.this.tfCustomerDisplayPort.getText());
            }
        });
        JButton btnRestoreCustomerDefault = new JButton(Messages.getString("TerminalConfigurationView.32"));
        btnRestoreCustomerDefault.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PeripheralConfigurationView.this.tfCustomerDisplayPort.setText("COM2");
                PeripheralConfigurationView.this.tfCustomerDisplayMessage.setText("1234567891234567891");
            }
        });
        customerDisplayPanel.add((Component)this.cbCustomerDisplay, "wrap");
        customerDisplayPanel.add(new JLabel(Messages.getString("PeripheralConfigurationView.0")));
        customerDisplayPanel.add((Component)this.tfCustomerDisplayPort, "wrap");
        customerDisplayPanel.add(new JLabel(Messages.getString("PeripheralConfigurationView.1")));
        customerDisplayPanel.add(this.tfCustomerDisplayMessage);
        customerDisplayPanel.add(btnTest);
        customerDisplayPanel.add(btnRestoreCustomerDefault);
        contentPanel.add((Component)customerDisplayPanel, "grow,wrap");
        JPanel scaleDisplayPanel = new JPanel((LayoutManager)new MigLayout());
        scaleDisplayPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PeripheralConfigurationView.15")));
        this.cbScaleActive = new JCheckBox(Messages.getString("PeripheralConfigurationView.16"));
        this.tfScalePort = new JTextField(20);
        this.tfScaleDisplayMessage = new FixedLengthTextField(20);
        JButton btnTestScale = new JButton(Messages.getString("PeripheralConfigurationView.17"));
        btnTestScale.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PeripheralConfigurationView.this.testScaleMachine();
            }
        });
        JButton btnRestoreScaleDefault = new JButton(Messages.getString("TerminalConfigurationView.32"));
        btnRestoreScaleDefault.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PeripheralConfigurationView.this.tfScalePort.setText(Messages.getString("PeripheralConfigurationView.18"));
            }
        });
        scaleDisplayPanel.add((Component)this.cbScaleActive, "wrap");
        scaleDisplayPanel.add(new JLabel(Messages.getString("PeripheralConfigurationView.20")));
        scaleDisplayPanel.add(this.tfScalePort);
        scaleDisplayPanel.add(btnTestScale);
        scaleDisplayPanel.add(btnRestoreScaleDefault);
        if (TerminalConfig.getScaleActivationValue().equals("cas10")) {
            contentPanel.add((Component)scaleDisplayPanel, "grow,wrap");
        }
        JPanel callerIdPanel = new JPanel((LayoutManager)new MigLayout());
        callerIdPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PeripheralConfigurationView.23")));
        this.chkCallerIdEnable = new JCheckBox(Messages.getString("PeripheralConfigurationView.24"));
        Vector<String> callerIds = new Vector<String>();
        callerIds.add("NONE");
        callerIds.add("AD101");
        callerIds.add("Whozz calling");
        this.cbCallerIds = new JComboBox(callerIds);
        callerIdPanel.add((Component)this.chkCallerIdEnable, "span 2,wrap");
        callerIdPanel.add(new JLabel("Caller Id device:"));
        callerIdPanel.add(this.cbCallerIds);
        OrderServiceExtension orderServicePlugin = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        if (orderServicePlugin != null) {
            contentPanel.add((Component)callerIdPanel, "grow,wrap");
        }
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    protected void doEnableDisableDrawerPull() {
        boolean selected = this.chkHasCashDrawer.isSelected();
        this.tfDrawerName.setEnabled(selected);
        this.tfDrawerCodes.setEnabled(selected);
        this.tfDrawerInitialBalance.setEnabled(selected);
    }

    @Override
    public boolean save() throws Exception {
        TerminalConfig.setDrawerPortName(this.tfDrawerName.getText());
        TerminalConfig.setDrawerControlCodes(this.tfDrawerCodes.getText());
        TerminalConfig.setCustomerDisplay(this.cbCustomerDisplay.isSelected());
        TerminalConfig.setCustomerDisplayPort(this.tfCustomerDisplayPort.getText());
        TerminalConfig.setCustomerDisplayMessage(this.tfCustomerDisplayMessage.getText());
        TerminalConfig.setScaleDisplay(this.cbScaleActive.isSelected());
        TerminalConfig.setScalePort(this.tfScalePort.getText());
        TerminalConfig.setScaleDisplayMessage(this.tfScaleDisplayMessage.getText());
        TerminalConfig.setCallerIdDevice(this.cbCallerIds.getSelectedItem().toString());
        TerminalConfig.setEnabledCallerIdDevice(this.chkCallerIdEnable.isSelected());
        TerminalDAO terminalDAO = TerminalDAO.getInstance();
        Terminal terminal = terminalDAO.get(TerminalConfig.getTerminalId());
        if (terminal == null) {
            terminal = new Terminal();
            terminal.setId(TerminalConfig.getTerminalId());
            terminal.setCurrentBalance(this.tfDrawerInitialBalance.getDouble());
            terminal.setName(String.valueOf(TerminalConfig.getTerminalId()));
        }
        terminal.setHasCashDrawer(this.chkHasCashDrawer.isSelected());
        terminal.setOpeningBalance(this.tfDrawerInitialBalance.getDouble());
        terminalDAO.saveOrUpdate(terminal);
        return true;
    }

    @Override
    public void initialize() throws Exception {
        Terminal terminal = Application.getInstance().refreshAndGetTerminal();
        this.chkHasCashDrawer.setSelected(terminal.isHasCashDrawer());
        this.tfDrawerName.setText(TerminalConfig.getDrawerPortName());
        this.tfDrawerCodes.setText(TerminalConfig.getDrawerControlCodes());
        this.tfDrawerInitialBalance.setText("" + terminal.getOpeningBalance());
        this.cbCustomerDisplay.setSelected(TerminalConfig.isActiveCustomerDisplay());
        this.tfCustomerDisplayPort.setText(TerminalConfig.getCustomerDisplayPort());
        this.tfCustomerDisplayMessage.setText(TerminalConfig.getCustomerDisplayMessage());
        this.cbScaleActive.setSelected(TerminalConfig.isActiveScaleDisplay());
        this.tfScalePort.setText(TerminalConfig.getScalePort());
        this.tfScaleDisplayMessage.setText(TerminalConfig.getScaleDisplayMessage());
        this.cbCallerIds.setSelectedItem(TerminalConfig.getCallerIdDevice());
        this.chkCallerIdEnable.setSelected(TerminalConfig.isEanbledCallerIdDevice());
        this.doEnableDisableDrawerPull();
        this.setInitialized(true);
    }

    private void testScaleMachine() {
        try {
            String string = SerialPortUtil.readWeight(this.tfScalePort.getText());
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), string);
        }
        catch (Exception ex) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), ex.getMessage());
            LogFactory.getLog(PeripheralConfigurationView.class).error((Object)ex);
        }
    }

    @Override
    public String getName() {
        return CONFIG_TAB_PERIPHERAL;
    }
}


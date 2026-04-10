/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.main.Main;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class TerminalConfigurationView
extends ConfigurationView {
    private IntegerTextField tfTerminalNumber;
    private IntegerTextField tfSecretKeyLength;
    private JTextArea taTerminalLocation;
    private JCheckBox cbTranslatedName = new JCheckBox(Messages.getString("TerminalConfigurationView.2"));
    private JCheckBox cbFullscreenMode = new JCheckBox(Messages.getString("TerminalConfigurationView.3"));
    private JCheckBox cbUseSettlementPrompt = new JCheckBox(Messages.getString("TerminalConfigurationView.4"));
    private JCheckBox cbShowDbConfiguration = new JCheckBox(Messages.getString("TerminalConfigurationView.5"));
    private JCheckBox cbShowBarCodeOnReceipt = new JCheckBox(Messages.getString("TerminalConfigurationView.21"));
    private JCheckBox cbGroupKitchenReceiptItems = new JCheckBox("Group by Categories in kitchen Receipt");
    private JCheckBox chkEnabledMultiCurrency = new JCheckBox("Enable multi currency");
    private JCheckBox chkAllowToDelPrintedItem = new JCheckBox("Allow to delete printed ticket item");
    private JCheckBox chkAllowQuickMaintenance = new JCheckBox("Allow quick maintenance");
    private JCheckBox chkModifierCannotExceedMaxLimit = new JCheckBox("Allow adding modifier when it reaches max limit");
    private JComboBox<String> cbFonts = new JComboBox();
    private JComboBox<String> cbDefaultView;
    private IntegerTextField tfButtonHeight;
    private DoubleTextField tfScaleFactor;
    private IntegerTextField tfFontSize;
    private JCheckBox cbAutoLogoff = new JCheckBox(Messages.getString("TerminalConfigurationView.16"));
    private IntegerTextField tfLogoffTime = new IntegerTextField(4);
    private JTextField tfDrawerName = new JTextField(10);
    private JTextField tfDrawerCodes = new JTextField(15);
    DoubleTextField tfDrawerInitialBalance = new DoubleTextField(6);
    private JSlider jsResize;

    public TerminalConfigurationView() {
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("gap 5px 10px", "[][][grow]", ""));
        JLabel lblTerminalNumber = new JLabel(Messages.getString("TerminalConfigurationView.TERMINAL_NUMBER"));
        contentPanel.add((Component)lblTerminalNumber, "alignx left,aligny center");
        this.tfTerminalNumber = new IntegerTextField();
        this.tfTerminalNumber.setColumns(10);
        contentPanel.add((Component)this.tfTerminalNumber, "aligny top,wrap");
        JLabel lblTerminalLocation = new JLabel(Messages.getString("TerminalConfigurationView.24"));
        this.taTerminalLocation = new JTextArea();
        this.taTerminalLocation.setLineWrap(true);
        this.taTerminalLocation.setPreferredSize(PosUIManager.getSize(350, 40));
        JScrollPane taScrollPane = new JScrollPane(this.taTerminalLocation);
        contentPanel.add(new JLabel(Messages.getString("TerminalConfigurationView.9")));
        this.tfSecretKeyLength = new IntegerTextField(3);
        contentPanel.add((Component)this.tfSecretKeyLength, "wrap");
        contentPanel.add((Component)this.cbShowDbConfiguration, "spanx 3");
        this.cbAutoLogoff.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TerminalConfigurationView.this.cbAutoLogoff.isSelected()) {
                    TerminalConfigurationView.this.tfLogoffTime.setEnabled(true);
                } else {
                    TerminalConfigurationView.this.tfLogoffTime.setEnabled(false);
                }
            }
        });
        contentPanel.add((Component)this.cbAutoLogoff, "newline");
        contentPanel.add((Component)this.tfLogoffTime, "wrap");
        contentPanel.add((Component)this.cbTranslatedName, "span 2");
        contentPanel.add((Component)this.cbFullscreenMode, "newline, span");
        contentPanel.add((Component)this.cbUseSettlementPrompt, "newline, span");
        contentPanel.add((Component)this.cbShowBarCodeOnReceipt, "newline,span");
        contentPanel.add((Component)this.cbGroupKitchenReceiptItems, "newline,span");
        contentPanel.add((Component)this.chkEnabledMultiCurrency, "newline,span");
        contentPanel.add((Component)this.chkAllowToDelPrintedItem, "newline,span");
        contentPanel.add((Component)this.chkAllowQuickMaintenance, "newline,span");
        contentPanel.add((Component)this.chkModifierCannotExceedMaxLimit, "newline,span");
        contentPanel.add((Component)new JLabel(Messages.getString("TerminalConfigurationView.17")), "newline");
        contentPanel.add(this.cbFonts, "span 2, wrap");
        Vector<String> defaultViewList = new Vector<String>();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        if (orderTypes != null) {
            for (OrderType orderType : orderTypes) {
                defaultViewList.add(orderType.getName());
            }
        }
        defaultViewList.add("ALL FUNCTIONS");
        defaultViewList.add("KD");
        defaultViewList.add(SwitchboardView.VIEW_NAME);
        this.cbDefaultView = new JComboBox(defaultViewList);
        contentPanel.add((Component)new JLabel("Default View"), "newline");
        contentPanel.add(this.cbDefaultView, "span 2, wrap");
        contentPanel.add((Component)lblTerminalLocation, "alignx left,aligny top");
        contentPanel.add((Component)taScrollPane, "aligny top, spanx 2,wrap");
        JPanel touchConfigurationPanel = new JPanel(new FlowLayout(0, 20, 10));
        touchConfigurationPanel.setBorder(BorderFactory.createTitledBorder("-"));
        touchConfigurationPanel.add(new JLabel(Messages.getString("TerminalConfigurationView.18")));
        this.tfButtonHeight = new IntegerTextField(5);
        int FPS_MIN = 10;
        int FPS_MAX = 50;
        int FPS_INIT = 10;
        this.jsResize = new JSlider(0, FPS_MIN, FPS_MAX, FPS_INIT);
        this.jsResize.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    double fps = source.getValue();
                    TerminalConfigurationView.this.tfScaleFactor.setText(String.valueOf(fps /= 10.0));
                }
            }
        });
        touchConfigurationPanel.add(this.jsResize);
        this.tfScaleFactor = new DoubleTextField(5);
        touchConfigurationPanel.add(this.tfScaleFactor);
        this.tfFontSize = new IntegerTextField(5);
        contentPanel.add((Component)touchConfigurationPanel, "span 3, wrap");
        this.addCashDrawerConfig();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    private void addCashDrawerConfig() {
        int i;
        Integer[] hours = new Integer[24];
        Integer[] minutes = new Integer[60];
        for (i = 0; i < 24; ++i) {
            hours[i] = i;
        }
        for (i = 0; i < 60; ++i) {
            minutes[i] = i;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new TerminalConfigurationView());
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

    public boolean canSave() {
        return true;
    }

    @Override
    public boolean save() {
        int terminalNumber = 0;
        double scaleFactor = this.tfScaleFactor.getDouble();
        int fontSize = (int)(scaleFactor * 12.0);
        int menuItemButtonWidth = (int)(scaleFactor * 80.0);
        int buttonHeight = (int)(scaleFactor * 80.0);
        if (scaleFactor > 5.0) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), Messages.getString("TerminalConfigurationView.23"));
            return false;
        }
        try {
            terminalNumber = Integer.parseInt(this.tfTerminalNumber.getText());
        }
        catch (Exception x) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TerminalConfigurationView.14"));
            return false;
        }
        int defaultPassLen = this.tfSecretKeyLength.getInteger();
        if (defaultPassLen == 0) {
            defaultPassLen = 4;
        }
        TerminalConfig.setTerminalId(terminalNumber);
        TerminalConfig.setDefaultPassLen(defaultPassLen);
        TerminalConfig.setFullscreenMode(this.cbFullscreenMode.isSelected());
        TerminalConfig.setShowDbConfigureButton(this.cbShowDbConfiguration.isSelected());
        TerminalConfig.setUseTranslatedName(this.cbTranslatedName.isSelected());
        TerminalConfig.setTouchScreenButtonHeight(buttonHeight);
        TerminalConfig.setMenuItemButtonWidth(menuItemButtonWidth);
        TerminalConfig.setMenuItemButtonHeight(buttonHeight);
        TerminalConfig.setTouchScreenFontSize(fontSize);
        TerminalConfig.setScreenScaleFactor(scaleFactor);
        TerminalConfig.setAutoLogoffEnable(this.cbAutoLogoff.isSelected());
        TerminalConfig.setAutoLogoffTime(this.tfLogoffTime.getInteger() <= 0 ? 10 : this.tfLogoffTime.getInteger());
        TerminalConfig.setUseSettlementPrompt(this.cbUseSettlementPrompt.isSelected());
        TerminalConfig.setShowBarcodeOnReceipt(this.cbShowBarCodeOnReceipt.isSelected());
        TerminalConfig.setGroupKitchenReceiptItems(this.cbGroupKitchenReceiptItems.isSelected());
        TerminalConfig.setEnabledMultiCurrency(this.chkEnabledMultiCurrency.isSelected());
        TerminalConfig.setAllowToDeletePrintedTicketItem(this.chkAllowToDelPrintedItem.isSelected());
        TerminalConfig.setAllowQuickMaintenance(this.chkAllowQuickMaintenance.isSelected());
        String selectedFont = (String)this.cbFonts.getSelectedItem();
        if ("<select>".equals(selectedFont)) {
            selectedFont = null;
        }
        String selectedView = (String)this.cbDefaultView.getSelectedItem();
        TerminalConfig.setDefaultView(selectedView);
        TerminalConfig.setUiDefaultFont(selectedFont);
        TerminalConfig.setDrawerPortName(this.tfDrawerName.getText());
        TerminalConfig.setDrawerControlCodes(this.tfDrawerCodes.getText());
        TerminalDAO terminalDAO = TerminalDAO.getInstance();
        Terminal terminal = terminalDAO.get(terminalNumber);
        if (terminal == null) {
            terminal = new Terminal();
            terminal.setId(terminalNumber);
            terminal.setCurrentBalance(this.tfDrawerInitialBalance.getDouble());
            terminal.setName(String.valueOf(terminalNumber));
        }
        terminal.setLocation(this.taTerminalLocation.getText());
        terminal.setOpeningBalance(this.tfDrawerInitialBalance.getDouble());
        terminalDAO.saveOrUpdate(terminal);
        Restaurant restaurant = RestaurantDAO.getRestaurant();
        restaurant.setAllowModifierMaxExceed(this.chkModifierCannotExceedMaxLimit.isSelected());
        RestaurantDAO.getInstance().saveOrUpdate(restaurant);
        this.restartPOS();
        return true;
    }

    @Override
    public void initialize() throws Exception {
        this.tfTerminalNumber.setText(String.valueOf(TerminalConfig.getTerminalId()));
        this.tfSecretKeyLength.setText(String.valueOf(TerminalConfig.getDefaultPassLen()));
        this.cbFullscreenMode.setSelected(TerminalConfig.isFullscreenMode());
        this.cbShowDbConfiguration.setSelected(TerminalConfig.isShowDbConfigureButton());
        this.cbUseSettlementPrompt.setSelected(TerminalConfig.isUseSettlementPrompt());
        this.cbShowBarCodeOnReceipt.setSelected(TerminalConfig.isShowBarcodeOnReceipt());
        this.cbGroupKitchenReceiptItems.setSelected(TerminalConfig.isGroupKitchenReceiptItems());
        this.chkEnabledMultiCurrency.setSelected(TerminalConfig.isEnabledMultiCurrency());
        this.chkAllowToDelPrintedItem.setSelected(TerminalConfig.isAllowedToDeletePrintedTicketItem());
        this.chkAllowQuickMaintenance.setSelected(TerminalConfig.isAllowedQuickMaintenance());
        this.tfButtonHeight.setText("" + TerminalConfig.getTouchScreenButtonHeight());
        this.tfScaleFactor.setText("" + TerminalConfig.getScreenScaleFactor());
        this.tfFontSize.setText("" + TerminalConfig.getTouchScreenFontSize());
        this.jsResize.setValue((int)(TerminalConfig.getScreenScaleFactor() * 10.0));
        this.cbTranslatedName.setSelected(TerminalConfig.isUseTranslatedName());
        this.cbAutoLogoff.setSelected(TerminalConfig.isAutoLogoffEnable());
        this.tfLogoffTime.setText("" + TerminalConfig.getAutoLogoffTime());
        this.tfLogoffTime.setEnabled(this.cbAutoLogoff.isSelected());
        this.initializeFontConfig();
        this.cbDefaultView.setSelectedItem(TerminalConfig.getDefaultView());
        Terminal terminal = Application.getInstance().refreshAndGetTerminal();
        this.tfDrawerName.setText(TerminalConfig.getDrawerPortName());
        this.tfDrawerCodes.setText(TerminalConfig.getDrawerControlCodes());
        this.tfDrawerInitialBalance.setText("" + terminal.getOpeningBalance());
        this.taTerminalLocation.setText(terminal.getLocation());
        Restaurant restaurant = RestaurantDAO.getRestaurant();
        this.chkModifierCannotExceedMaxLimit.setSelected(restaurant.isAllowModifierMaxExceed());
        this.setInitialized(true);
    }

    private void initializeFontConfig() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = e.getAllFonts();
        DefaultComboBoxModel model = (DefaultComboBoxModel)this.cbFonts.getModel();
        model.addElement("<select>");
        for (Font f : fonts) {
            model.addElement(f.getFontName());
        }
        String uiDefaultFont = TerminalConfig.getUiDefaultFont();
        if (StringUtils.isNotEmpty((String)uiDefaultFont)) {
            this.cbFonts.setSelectedItem(uiDefaultFont);
        }
    }

    @Override
    public String getName() {
        return Messages.getString("TerminalConfigurationView.47");
    }

    public void restartPOS() {
        Component[] optionValues;
        JOptionPane optionPane = new JOptionPane(Messages.getString("TerminalConfigurationView.26"), 3, 2, Application.getApplicationIcon(), new String[]{Messages.getString("TerminalConfigurationView.30")});
        for (Component object : optionValues = optionPane.getComponents()) {
            Component[] components;
            if (!(object instanceof JPanel)) continue;
            JPanel panel = (JPanel)object;
            for (Component component : components = panel.getComponents()) {
                if (!(component instanceof JButton)) continue;
                component.setPreferredSize(new Dimension(100, 80));
                JButton button = (JButton)component;
                button.setPreferredSize(PosUIManager.getSize(100, 50));
            }
        }
        JDialog dialog = optionPane.createDialog(Application.getPosWindow(), Messages.getString("TerminalConfigurationView.31"));
        dialog.setIconImage(Application.getApplicationIcon().getImage());
        dialog.setLocationRelativeTo(Application.getPosWindow());
        dialog.setVisible(true);
        String selectedValue = (String)optionPane.getValue();
        if (selectedValue != null && selectedValue.equals(Messages.getString("TerminalConfigurationView.28"))) {
            try {
                Main.restart();
            }
            catch (IOException | InterruptedException | URISyntaxException exception) {
                // empty catch block
            }
        }
    }
}


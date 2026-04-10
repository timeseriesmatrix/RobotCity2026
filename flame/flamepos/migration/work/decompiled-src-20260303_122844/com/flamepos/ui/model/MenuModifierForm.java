/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao.ModifierDAO;
import com.floreantpos.model.dao.ModifierGroupDAO;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.ModifierPriceByOrderTypeDialog;
import com.floreantpos.ui.model.TaxForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class MenuModifierForm
extends BeanEditor {
    private MenuModifier modifier;
    private PriceByOrderType priceTableModel;
    private JCheckBox chkPrintToKitchen;
    private JComboBox cbModifierGroup;
    private JComboBox cbTaxes;
    private JFormattedTextField tfName;
    private FixedLengthTextField tfTranslatedName;
    private DoubleTextField tfNormalPrice;
    private DoubleTextField tfExtraPrice;
    private IntegerTextField tfSortOrder;
    private JButton btnButtonColor;
    private JButton btnTextColor;
    private JTable priceTable;
    private JTabbedPane jTabbedPane1;
    private Map<String, MultiplierPricePanel> itemMap = new HashMap<String, MultiplierPricePanel>();
    private JCheckBox chkUseFixedPrice;

    public MenuModifierForm() throws Exception {
        this(new MenuModifier());
    }

    public MenuModifierForm(MenuModifier modifier) throws Exception {
        this.modifier = modifier;
        this.checkRegularMultiplier();
        this.initComponents();
        ModifierGroupDAO modifierGroupDAO = new ModifierGroupDAO();
        List<MenuModifierGroup> groups = modifierGroupDAO.findAll();
        this.cbModifierGroup.setModel(new DefaultComboBoxModel<MenuModifierGroup>(new Vector<MenuModifierGroup>(groups)));
        this.priceTableModel = new PriceByOrderType(modifier.getProperties());
        this.priceTable.setModel(this.priceTableModel);
        TaxDAO taxDAO = new TaxDAO();
        List<Tax> taxes = taxDAO.findAll();
        this.cbTaxes.setModel(new ComboBoxModel(taxes));
        this.add(this.jTabbedPane1);
        this.setBean(modifier);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(0, 0));
        this.jTabbedPane1 = new JTabbedPane();
        this.tfName = new JFormattedTextField();
        this.tfTranslatedName = new FixedLengthTextField();
        this.cbModifierGroup = new JComboBox();
        this.tfNormalPrice = new DoubleTextField();
        this.tfExtraPrice = new DoubleTextField();
        this.tfSortOrder = new IntegerTextField();
        this.cbTaxes = new JComboBox();
        JButton btnNewTax = new JButton();
        this.chkPrintToKitchen = new JCheckBox();
        this.chkUseFixedPrice = new JCheckBox("Use fixed price");
        JButton btnNewPrice = new JButton();
        JButton btnUpdatePrice = new JButton();
        JButton btnDeletePrice = new JButton();
        JButton btnDefaultValue = new JButton();
        JButton btnDeleteAll = new JButton();
        JPanel tabPrice = new JPanel();
        JScrollPane jScrollPane3 = new JScrollPane();
        this.priceTable = new JTable();
        JLabel lblName = new JLabel(POSConstants.NAME + ":");
        JLabel lblTranslatedName = new JLabel(Messages.getString("MenuModifierForm.0"));
        JLabel lblModifierGroup = new JLabel(POSConstants.GROUP + ":");
        JLabel lblPrice = new JLabel("Price:");
        JLabel lblExtraPrice = new JLabel(POSConstants.EXTRA_PRICE + ":");
        JLabel lblSortOrder = new JLabel(Messages.getString("MenuModifierForm.15"));
        JLabel lblTaxRate = new JLabel(POSConstants.TAX_RATE + ":");
        JLabel lblPercentage = new JLabel();
        this.tfExtraPrice.setText("0");
        lblPercentage.setText("%");
        this.tfNormalPrice.setText("0");
        btnNewTax.setText("...");
        btnNewTax.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                MenuModifierForm.this.btnNewTaxActionPerformed(evt);
            }
        });
        this.chkPrintToKitchen.setText(POSConstants.PRINT_TO_KITCHEN);
        this.chkPrintToKitchen.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkPrintToKitchen.setMargin(new Insets(0, 0, 0, 0));
        JPanel generalTabPanel = new JPanel(new BorderLayout());
        this.jTabbedPane1.addTab(POSConstants.GENERAL, generalTabPanel);
        TransparentPanel lelfInputPanel = new TransparentPanel();
        lelfInputPanel.setLayout((LayoutManager)new MigLayout("wrap 2,hidemode 3", "[90px][grow]", ""));
        lelfInputPanel.add((Component)lblName, "alignx left,aligny center");
        lelfInputPanel.add((Component)this.tfName, "growx,aligny top");
        lelfInputPanel.add((Component)lblTranslatedName, "alignx left,aligny center");
        lelfInputPanel.add((Component)this.tfTranslatedName, "growx");
        lelfInputPanel.add((Component)lblModifierGroup, "alignx left,aligny center");
        lelfInputPanel.add((Component)this.cbModifierGroup, "growx,aligny top");
        JPanel rightInputPanel = new JPanel((LayoutManager)new MigLayout("wrap 2", "[86px][grow]"));
        rightInputPanel.add((Component)lblTaxRate, "alignx left,aligny center,split 2");
        rightInputPanel.add((Component)lblPercentage, "alignx left,aligny center");
        rightInputPanel.add((Component)this.cbTaxes, "growx,aligny top,split 2");
        rightInputPanel.add((Component)btnNewTax, "alignx left,aligny top");
        rightInputPanel.add((Component)lblSortOrder, "alignx left,aligny center");
        rightInputPanel.add((Component)this.tfSortOrder, "growx,aligny top");
        rightInputPanel.add((Component)this.chkPrintToKitchen, "skip 1,alignx left,aligny top");
        generalTabPanel.add(lelfInputPanel);
        generalTabPanel.add((Component)rightInputPanel, "East");
        JLabel lblButtonColor = new JLabel(Messages.getString("MenuModifierForm.1"));
        this.btnButtonColor = new JButton("");
        this.btnButtonColor.setPreferredSize(new Dimension(140, 40));
        JLabel lblTextColor = new JLabel(Messages.getString("MenuModifierForm.27"));
        this.btnTextColor = new JButton(Messages.getString("MenuModifierForm.29"));
        this.btnTextColor.setPreferredSize(new Dimension(140, 40));
        JPanel tabButtonStyle = new JPanel((LayoutManager)new MigLayout("hidemode 3,wrap 2"));
        tabButtonStyle.add(lblButtonColor);
        tabButtonStyle.add(this.btnButtonColor);
        tabButtonStyle.add(lblTextColor);
        tabButtonStyle.add(this.btnTextColor);
        this.jTabbedPane1.addTab("Button Style", tabButtonStyle);
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuModifierForm.this, Messages.getString("MenuModifierForm.39"), MenuModifierForm.this.btnButtonColor.getBackground());
                MenuModifierForm.this.btnButtonColor.setBackground(color);
                MenuModifierForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuModifierForm.this, Messages.getString("MenuModifierForm.40"), MenuModifierForm.this.btnTextColor.getForeground());
                MenuModifierForm.this.btnTextColor.setForeground(color);
            }
        });
        btnNewPrice.setText(Messages.getString("MenuModifierForm.2"));
        btnNewPrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuModifierForm.this.addNewPrice();
            }
        });
        btnUpdatePrice.setText(Messages.getString("MenuModifierForm.3"));
        btnUpdatePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuModifierForm.this.updatePrice();
            }
        });
        btnDeletePrice.setText(Messages.getString("MenuModifierForm.4"));
        btnDeletePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuModifierForm.this.deletePrice();
            }
        });
        btnDeleteAll.setText(Messages.getString("MenuModifierForm.5"));
        btnDeleteAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuModifierForm.this.deleteAll();
            }
        });
        btnDefaultValue.setText(Messages.getString("MenuModifierForm.8"));
        btnDefaultValue.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuModifierForm.this.setDefaultValue();
            }
        });
        this.priceTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        jScrollPane3.setViewportView(this.priceTable);
        tabPrice.setLayout(new BorderLayout());
        tabPrice.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabPrice.add((Component)jScrollPane3, "Center");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnNewPrice);
        buttonPanel.add(btnUpdatePrice);
        buttonPanel.add(btnDeletePrice);
        JPanel multiplierPanel = new JPanel((LayoutManager)new MigLayout("fillx,wrap 1"));
        List<Multiplier> multipliers = MultiplierDAO.getInstance().findAll();
        if (multipliers != null) {
            for (Multiplier multiplier : multipliers) {
                MultiplierPricePanel multiplierPricePanel = new MultiplierPricePanel(multiplier);
                multiplierPanel.add((Component)multiplierPricePanel, "grow");
                this.itemMap.put(multiplier.getName(), multiplierPricePanel);
            }
        }
        JScrollPane scrollPane = new JScrollPane(multiplierPanel);
        scrollPane.setBorder(new TitledBorder("Multiplier price"));
        lelfInputPanel.add((Component)scrollPane, "newline,skip 1,grow");
        tabPrice.add((Component)buttonPanel, "South");
        if (TerminalConfig.isMultipleOrderSupported()) {
            // empty if block
        }
    }

    private void checkRegularMultiplier() {
        Multiplier multiplier = MultiplierDAO.getInstance().get("Regular");
        if (multiplier != null && multiplier.isMain().booleanValue()) {
            return;
        }
        if (multiplier == null) {
            multiplier = new Multiplier("Regular");
            multiplier.setRate(0.0);
            multiplier.setSortOrder(0);
            multiplier.setTicketPrefix("");
            multiplier.setDefaultMultiplier(true);
            multiplier.setMain(true);
            MultiplierDAO.getInstance().save(multiplier);
        } else {
            multiplier.setMain(true);
            MultiplierDAO.getInstance().update(multiplier);
        }
    }

    private void btnNewTaxActionPerformed(ActionEvent evt) {
        try {
            TaxForm editor = new TaxForm();
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (!dialog.isCanceled()) {
                Tax tax = (Tax)editor.getBean();
                ComboBoxModel model = (ComboBoxModel)this.cbTaxes.getModel();
                model.addElement(tax);
                model.setSelectedItem(tax);
            }
        }
        catch (Exception x) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            MenuModifier modifier = (MenuModifier)this.getBean();
            ModifierDAO dao = new ModifierDAO();
            dao.saveOrUpdate(modifier);
        }
        catch (Exception e) {
            MessageDialog.showError(POSConstants.SAVE_ERROR, e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        List<ModifierMultiplierPrice> multiplierPriceList;
        Color color;
        MenuModifier modifier = (MenuModifier)this.getBean();
        if (modifier == null) {
            this.tfName.setText("");
            this.tfNormalPrice.setText("0");
            this.tfExtraPrice.setText("0");
            return;
        }
        this.tfName.setText(modifier.getName());
        this.tfTranslatedName.setText(modifier.getTranslatedName());
        this.tfNormalPrice.setText(String.valueOf(modifier.getPrice()));
        this.tfExtraPrice.setText(String.valueOf(modifier.getExtraPrice()));
        this.cbModifierGroup.setSelectedItem(modifier.getModifierGroup());
        this.chkPrintToKitchen.setSelected(modifier.isShouldPrintToKitchen());
        this.chkUseFixedPrice.setSelected(modifier.isFixedPrice());
        if (modifier.getSortOrder() != null) {
            this.tfSortOrder.setText(modifier.getSortOrder().toString());
        }
        if (modifier.getButtonColor() != null) {
            color = new Color(modifier.getButtonColor());
            this.btnButtonColor.setBackground(color);
            this.btnTextColor.setBackground(color);
        }
        if (modifier.getTextColor() != null) {
            color = new Color(modifier.getTextColor());
            this.btnTextColor.setForeground(color);
        }
        if (modifier.getTax() != null) {
            this.cbTaxes.setSelectedItem(modifier.getTax());
        }
        if ((multiplierPriceList = modifier.getMultiplierPriceList()) != null) {
            for (ModifierMultiplierPrice multiplierPrice : multiplierPriceList) {
                MultiplierPricePanel pricePanel = this.itemMap.get(multiplierPrice.getMultiplier().getName());
                if (pricePanel == null) continue;
                pricePanel.setModifierMultiplierPrice(multiplierPrice);
            }
        }
        this.itemMap.get((Object)"Regular").tfAditionalPrice.setText(String.valueOf(modifier.getPrice()));
    }

    @Override
    protected boolean updateModel() {
        MenuModifier modifier = (MenuModifier)this.getBean();
        String name = this.tfName.getText();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError(Messages.getString("MenuModifierForm.44"));
            return false;
        }
        modifier.setName(name);
        modifier.setExtraPrice(this.tfExtraPrice.getDouble());
        modifier.setTax((Tax)this.cbTaxes.getSelectedItem());
        modifier.setModifierGroup((MenuModifierGroup)this.cbModifierGroup.getSelectedItem());
        modifier.setShouldPrintToKitchen(this.chkPrintToKitchen.isSelected());
        modifier.setTranslatedName(this.tfTranslatedName.getText());
        modifier.setButtonColor(this.btnButtonColor.getBackground().getRGB());
        modifier.setTextColor(this.btnTextColor.getForeground().getRGB());
        modifier.setSortOrder(this.tfSortOrder.getInteger());
        modifier.setFixedPrice(this.chkUseFixedPrice.isSelected());
        ArrayList<ModifierMultiplierPrice> mulplierPriceList = new ArrayList<ModifierMultiplierPrice>();
        for (MultiplierPricePanel panel : this.itemMap.values()) {
            if (!panel.isSelected()) continue;
            ModifierMultiplierPrice multiplierPrice = panel.getMultiplierPrice();
            if (multiplierPrice == null) {
                multiplierPrice = new ModifierMultiplierPrice();
                multiplierPrice.setMultiplier(panel.getMultiplier());
                multiplierPrice.setModifier(modifier);
            }
            multiplierPrice.setPrice(panel.getPrice());
            mulplierPriceList.add(multiplierPrice);
        }
        modifier.setPrice(this.itemMap.get("Regular").getPrice());
        modifier.setMultiplierPriceList(mulplierPriceList);
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuModifier modifier = (MenuModifier)this.getBean();
        if (modifier.getId() == null) {
            return Messages.getString("MenuModifierForm.45");
        }
        return Messages.getString("MenuModifierForm.46");
    }

    private void addNewPrice() {
        ModifierPriceByOrderTypeDialog dialog = new ModifierPriceByOrderTypeDialog(this.getParentFrame(), this.modifier);
        dialog.setSize(350, 220);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.priceTableModel.add(dialog.getMenuModifier());
        }
    }

    private void deletePrice() {
        int selectedRow = this.priceTable.getSelectedRow();
        if (selectedRow == -1) {
            POSMessageDialog.showMessage(this.getParentFrame(), Messages.getString("MenuModifierForm.7"));
            return;
        }
        int option = POSMessageDialog.showYesNoQuestionDialog(this.getParentFrame(), Messages.getString("MenuModifierForm.21"), Messages.getString("MenuModifierForm.22"));
        if (option != 0) {
            return;
        }
        this.priceTableModel.remove(selectedRow);
    }

    private void deleteAll() {
        int option = POSMessageDialog.showYesNoQuestionDialog(this.getParentFrame(), Messages.getString("MenuModifierForm.23"), Messages.getString("MenuModifierForm.24"));
        if (option != 0) {
            return;
        }
        this.priceTableModel.removeAll();
    }

    private void setDefaultValue() {
        this.priceTableModel.setDefaultValue();
    }

    private void updatePrice() {
        int selectedRow = this.priceTable.getSelectedRow();
        if (selectedRow == -1) {
            POSMessageDialog.showMessage(this.getParentFrame(), Messages.getString("MenuModifierForm.25"));
            return;
        }
        this.priceTableModel.propertiesKey.get(selectedRow);
        ModifierPriceByOrderTypeDialog dialog = new ModifierPriceByOrderTypeDialog(this.getParentFrame(), this.modifier, String.valueOf(this.priceTableModel.propertiesKey.get(selectedRow)));
        dialog.setSize(350, 220);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.priceTableModel.add(dialog.getMenuModifier());
        }
    }

    protected void doCalculateMultiplierPrice() {
        MultiplierPricePanel regularPricePanel = this.itemMap.get("Regular");
        if (regularPricePanel == null) {
            return;
        }
        for (MultiplierPricePanel panel : this.itemMap.values()) {
            panel.calculatePrice(regularPricePanel.tfAditionalPrice.getDoubleOrZero());
        }
    }

    private class MultiplierPricePanel
    extends JPanel {
        ModifierMultiplierPrice multiplierPrice;
        Multiplier multiplier;
        DoubleTextField tfAditionalPrice;

        public MultiplierPricePanel(Multiplier multiplier) {
            this.multiplier = multiplier;
            this.setLayout((LayoutManager)new MigLayout("inset 0,fillx", "[100px][grow][100px]", ""));
            this.tfAditionalPrice = new DoubleTextField(multiplier.isMain() != false ? 6 : 9);
            this.tfAditionalPrice.setHorizontalAlignment(4);
            JLabel lblName = new JLabel(multiplier.getName());
            if (multiplier.isMain().booleanValue()) {
                lblName.setFont(new Font(null, 1, MenuModifierForm.this.tfName.getFont().getSize()));
            }
            this.add(lblName);
            this.add((Component)new JLabel(multiplier.isMain() != false ? "Price" : "Additional price", 11), "grow, gapright 10px");
            this.add((Component)this.tfAditionalPrice, "split 2,grow");
            if (multiplier.isMain().booleanValue()) {
                JButton btnCalculateMultilierPrice = new JButton("Calc");
                btnCalculateMultilierPrice.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MenuModifierForm.this.doCalculateMultiplierPrice();
                    }
                });
                this.add(btnCalculateMultilierPrice);
            }
        }

        public void calculatePrice(double regPrice) {
            if (this.multiplier.isMain().booleanValue()) {
                return;
            }
            this.tfAditionalPrice.setText(String.valueOf(regPrice * this.multiplier.getRate() / 100.0));
        }

        public Double getPrice() {
            return this.tfAditionalPrice.getDoubleOrZero();
        }

        public Multiplier getMultiplier() {
            return this.multiplier;
        }

        public boolean isSelected() {
            Double value = this.tfAditionalPrice.getDouble();
            return !value.isNaN();
        }

        private void update() {
            if (this.multiplierPrice == null) {
                return;
            }
            this.tfAditionalPrice.setText(String.valueOf(this.multiplierPrice.getPrice()));
        }

        public void setModifierMultiplierPrice(ModifierMultiplierPrice price) {
            this.multiplierPrice = price;
            this.update();
        }

        public ModifierMultiplierPrice getMultiplierPrice() {
            return this.multiplierPrice;
        }
    }

    class PriceByOrderType
    extends AbstractTableModel {
        List<String> propertiesKey = new ArrayList<String>();
        String[] cn = new String[]{"MODIFIER", "ORDER TYPE", "PRICE", "TAX", "EXTRA PRICE", "EXTRA TAX"};

        PriceByOrderType(Map<String, String> properties) {
            if (properties != null) {
                ArrayList<String> keys = new ArrayList<String>(properties.keySet());
                this.setPropertiesToTable(keys);
            }
        }

        private void setPropertiesToTable(List<String> keys) {
            this.propertiesKey.clear();
            for (int i = 0; i < keys.size(); ++i) {
                if (!keys.get(i).contains("_PRICE") || keys.get(i).contains("EXTRA_PRICE")) continue;
                this.propertiesKey.add(keys.get(i));
            }
        }

        public String get(int index) {
            return this.propertiesKey.get(index);
        }

        public void add(MenuModifier modifier) {
            this.setPropertiesToTable(new ArrayList<String>(modifier.getProperties().keySet()));
            this.fireTableDataChanged();
        }

        public void setDefaultValue() {
            int selectedRow = MenuModifierForm.this.priceTable.getSelectedRow();
            if (selectedRow == -1) {
                POSMessageDialog.showMessage(Messages.getString("MenuModifierForm.9"));
                return;
            }
            String modifiedKey = ((MenuModifierForm)MenuModifierForm.this).priceTableModel.propertiesKey.get(selectedRow);
            modifiedKey = modifiedKey.replaceAll("_PRICE", "");
            modifiedKey = modifiedKey.replaceAll("_", " ");
            MenuModifierForm.this.modifier.setPriceByOrderType(modifiedKey, MenuModifierForm.this.modifier.getPrice());
            if (MenuModifierForm.this.modifier.getTax() != null) {
                MenuModifierForm.this.modifier.setTaxByOrderType(modifiedKey, MenuModifierForm.this.modifier.getTax().getRate());
            } else {
                MenuModifierForm.this.modifier.setTaxByOrderType(modifiedKey, 0.0);
            }
            MenuModifierDAO.getInstance().saveOrUpdate(MenuModifierForm.this.modifier);
            this.add(MenuModifierForm.this.modifier);
            this.fireTableDataChanged();
        }

        public void remove(int index) {
            if (this.propertiesKey == null) {
                return;
            }
            String typeProperty = this.propertiesKey.get(index);
            String taxProperty = typeProperty.replaceAll("_PRICE", "_TAX");
            MenuModifierForm.this.modifier.removeProperty(typeProperty, taxProperty);
            MenuModifierDAO.getInstance().saveOrUpdate(MenuModifierForm.this.modifier);
            this.propertiesKey.remove(index);
            this.fireTableDataChanged();
        }

        public void removeAll() {
            MenuModifierForm.this.modifier.getProperties().clear();
            MenuModifierDAO.getInstance().saveOrUpdate(MenuModifierForm.this.modifier);
            this.propertiesKey.clear();
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (this.propertiesKey == null) {
                return 0;
            }
            return this.propertiesKey.size();
        }

        @Override
        public int getColumnCount() {
            return this.cn.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.cn[column];
        }

        public List<String> getProperties() {
            return this.propertiesKey;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String key = String.valueOf(this.propertiesKey.get(rowIndex));
            switch (columnIndex) {
                case 0: {
                    return MenuModifierForm.this.modifier.getName();
                }
                case 1: {
                    key = key.replaceAll("_PRICE", "");
                    key = key.replaceAll("_", " ");
                    return key;
                }
                case 2: {
                    return MenuModifierForm.this.modifier.getProperty(key);
                }
                case 3: {
                    key = key.replaceAll("_PRICE", "_TAX");
                    return MenuModifierForm.this.modifier.getProperty(key);
                }
                case 4: {
                    key = key.replaceAll("_PRICE", "_EXTRA_PRICE");
                    return MenuModifierForm.this.modifier.getProperty(key);
                }
                case 5: {
                    key = key.replaceAll("_PRICE", "_EXTRA_TAX");
                    return MenuModifierForm.this.modifier.getProperty(key);
                }
            }
            return null;
        }
    }
}


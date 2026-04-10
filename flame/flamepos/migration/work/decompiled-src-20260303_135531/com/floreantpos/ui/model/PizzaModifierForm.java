/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.model.dao.ModifierDAO;
import com.floreantpos.model.dao.ModifierGroupDAO;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.model.TaxForm;
import com.floreantpos.ui.views.order.multipart.PizzaPriceTableModel;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

public class PizzaModifierForm
extends BeanEditor {
    private MenuModifier modifier;
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
    private JXTable pizzaModifierPriceTable;
    private PizzaPriceTableModel pizzaModifierPriceTableModel;
    private JCheckBox chkUseFixedPrice;
    private JCheckBox chkSectionWisePrice;

    public PizzaModifierForm() throws Exception {
        this(new MenuModifier());
    }

    public PizzaModifierForm(MenuModifier modifier) throws Exception {
        this.modifier = modifier;
        this.checkRegularMultiplier();
        this.initComponents();
        ModifierGroupDAO modifierGroupDAO = new ModifierGroupDAO();
        List<MenuModifierGroup> groups = modifierGroupDAO.findAll();
        this.cbModifierGroup.setModel(new DefaultComboBoxModel<MenuModifierGroup>(new Vector<MenuModifierGroup>(groups)));
        TaxDAO taxDAO = new TaxDAO();
        List<Tax> taxes = taxDAO.findAll();
        this.cbTaxes.setModel(new ComboBoxModel(taxes));
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
        this.chkSectionWisePrice = new JCheckBox();
        JScrollPane jScrollPane3 = new JScrollPane();
        this.priceTable = new JTable();
        JLabel lblName = new JLabel(POSConstants.NAME + ":");
        JLabel lblTranslatedName = new JLabel(Messages.getString("MenuModifierForm.0"));
        JLabel lblModifierGroup = new JLabel(POSConstants.GROUP + ":");
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
                PizzaModifierForm.this.btnNewTaxActionPerformed(evt);
            }
        });
        this.chkPrintToKitchen.setText(POSConstants.PRINT_TO_KITCHEN);
        this.chkPrintToKitchen.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkPrintToKitchen.setMargin(new Insets(0, 0, 0, 0));
        this.chkSectionWisePrice.setText("Sectionwise price");
        this.chkSectionWisePrice.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkSectionWisePrice.setMargin(new Insets(0, 0, 0, 0));
        this.chkUseFixedPrice = new JCheckBox("Use fixed price");
        JPanel generalTabPanel = new JPanel(new BorderLayout());
        this.jTabbedPane1.addTab(POSConstants.GENERAL, generalTabPanel);
        TransparentPanel inputPanel = new TransparentPanel();
        inputPanel.setLayout((LayoutManager)new MigLayout("fill", "[60%][40%]", ""));
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
        rightInputPanel.add((Component)this.chkSectionWisePrice, "skip 1");
        inputPanel.add((Component)lelfInputPanel, "grow");
        inputPanel.add((Component)rightInputPanel, "grow");
        generalTabPanel.add((Component)inputPanel, "North");
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
                Color color = JColorChooser.showDialog(PizzaModifierForm.this, Messages.getString("MenuModifierForm.39"), PizzaModifierForm.this.btnButtonColor.getBackground());
                PizzaModifierForm.this.btnButtonColor.setBackground(color);
                PizzaModifierForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(PizzaModifierForm.this, Messages.getString("MenuModifierForm.40"), PizzaModifierForm.this.btnTextColor.getForeground());
                PizzaModifierForm.this.btnTextColor.setForeground(color);
            }
        });
        this.priceTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        jScrollPane3.setViewportView(this.priceTable);
        this.createPizzaModifierPricePanel(generalTabPanel);
        this.add(this.jTabbedPane1);
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

    private void createPizzaModifierPricePanel(JPanel generalTabPanel) {
        List<PizzaModifierPrice> pizzaModifierPriceList = this.modifier.getPizzaModifierPriceList();
        if (pizzaModifierPriceList == null || pizzaModifierPriceList.isEmpty()) {
            pizzaModifierPriceList = this.generatePossibleModifierPriceList();
        }
        this.pizzaModifierPriceTable = new JXTable();
        this.pizzaModifierPriceTable.setRowHeight(PosUIManager.getSize(this.pizzaModifierPriceTable.getRowHeight()));
        this.pizzaModifierPriceTable.setCellSelectionEnabled(true);
        this.pizzaModifierPriceTable.setSelectionMode(0);
        this.pizzaModifierPriceTable.setSurrendersFocusOnKeystroke(true);
        this.pizzaModifierPriceTableModel = new PizzaPriceTableModel(pizzaModifierPriceList, MultiplierDAO.getInstance().findAll());
        this.pizzaModifierPriceTable.setModel((TableModel)this.pizzaModifierPriceTableModel);
        JScrollPane pizzaModifierPriceTabScrollPane = new JScrollPane();
        pizzaModifierPriceTabScrollPane.setViewportView((Component)this.pizzaModifierPriceTable);
        JPanel pizzaModifierPricePanel = new JPanel();
        pizzaModifierPricePanel.setLayout(new BorderLayout());
        pizzaModifierPricePanel.setPreferredSize(PosUIManager.getSize(600, 250));
        pizzaModifierPricePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pizzaModifierPricePanel.add((Component)pizzaModifierPriceTabScrollPane, "Center");
        generalTabPanel.add(pizzaModifierPricePanel);
    }

    private List<PizzaModifierPrice> generatePossibleModifierPriceList() {
        List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
        ArrayList<PizzaModifierPrice> pizzaModifierPriceList = new ArrayList<PizzaModifierPrice>();
        for (int i = 0; i < menuItemSizeList.size(); ++i) {
            PizzaModifierPrice pizzaModifierPrice = new PizzaModifierPrice();
            pizzaModifierPrice.setSize(menuItemSizeList.get(i));
            pizzaModifierPrice.setPrice(0.0);
            pizzaModifierPrice.setExtraPrice(0.0);
            pizzaModifierPriceList.add(pizzaModifierPrice);
        }
        return pizzaModifierPriceList;
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
        this.chkSectionWisePrice.setSelected(modifier.isShouldSectionWisePrice());
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
        modifier.setPrice(this.tfNormalPrice.getDouble());
        modifier.setExtraPrice(this.tfExtraPrice.getDouble());
        modifier.setTax((Tax)this.cbTaxes.getSelectedItem());
        modifier.setModifierGroup((MenuModifierGroup)this.cbModifierGroup.getSelectedItem());
        modifier.setShouldPrintToKitchen(this.chkPrintToKitchen.isSelected());
        modifier.setShouldSectionWisePrice(this.chkSectionWisePrice.isSelected());
        modifier.setTranslatedName(this.tfTranslatedName.getText());
        modifier.setButtonColor(this.btnButtonColor.getBackground().getRGB());
        modifier.setTextColor(this.btnTextColor.getForeground().getRGB());
        modifier.setSortOrder(this.tfSortOrder.getInteger());
        modifier.setFixedPrice(this.chkUseFixedPrice.isSelected());
        modifier.setMultiplierPriceList(null);
        modifier.setPizzaModifier(true);
        List<PizzaModifierPrice> rows = this.pizzaModifierPriceTableModel.getRows(modifier);
        modifier.setPizzaModifierPriceList(rows);
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
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.io.FileUtils
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.InventoryPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.DoubleDocument;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IUpdatebleView;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.ui.model.MenuItemModifierGroupForm;
import com.floreantpos.ui.model.MenuItemPriceByOrderTypeDialog;
import com.floreantpos.ui.model.MenuItemShiftDialog;
import com.floreantpos.ui.model.TaxForm;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.jdesktop.layout.GroupLayout;

public class MenuItemForm
extends BeanEditor<MenuItem>
implements ActionListener,
ChangeListener {
    ShiftTableModel shiftTableModel;
    PriceByOrderTypeTableModel priceTableModel;
    private MenuItem menuItem;
    private JButton btnAddShift;
    private JButton btnNewPrice;
    private JButton btnUpdatePrice;
    private JButton btnDeletePrice;
    private JButton btnDeleteAll;
    private JButton btnDefaultValue;
    private JButton btnDeleteModifierGroup;
    private JButton btnDeleteShift;
    private JButton btnEditModifierGroup;
    private JButton btnNewGroup;
    private JButton btnNewModifierGroup;
    private JButton btnNewTax;
    private JComboBox cbGroup;
    private JComboBox cbTax;
    private JCheckBox chkVisible;
    private JLabel lfname;
    private JLabel lDiscountRate;
    private JLabel lblPrice;
    private JLabel lgroup;
    private JLabel lPercentage;
    private JLabel lTax;
    private JLabel lblButtonColor;
    private JPanel tabGeneral;
    private JPanel tabModifier;
    private JPanel tabShift;
    private JPanel tabPrice;
    private JPanel tabButtonStyle;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTabbedPane tabbedPane;
    private JTable shiftTable;
    private JTable priceTable;
    private JTable tableTicketItemModifierGroups;
    private DoubleTextField tfDiscountRate;
    private FixedLengthTextField tfName;
    private DoubleTextField tfPrice;
    private JTextArea tfDescription;
    private List<MenuItemModifierGroup> menuItemModifierGroups;
    private MenuItemMGListModel menuItemMGListModel;
    private JLabel lblImagePreview;
    private JButton btnClearImage;
    private JCheckBox cbShowTextWithImage;
    private DoubleTextField tfBuyPrice;
    private JLabel lblKitchenPrinter;
    private JComboBox<PrinterGroup> cbPrinterGroup;
    private JLabel lblBarcode;
    private FixedLengthTextField tfBarcode;
    private JLabel lblTextColor;
    private JLabel lblUnitName;
    private JButton btnButtonColor;
    private JButton btnTextColor;
    private JLabel lblTranslatedName;
    private FixedLengthTextField tfTranslatedName;
    private FixedLengthTextField tfUnitName;
    private IntegerTextField tfSortOrder;
    private CheckBoxList orderList;
    private JCheckBox cbFractionalUnit;
    private DoubleTextField tfStockCount;
    private JLabel lblStockCount;
    private JCheckBox cbDisableStockCount;
    private JLabel lblSortOrder;

    public MenuItemForm() throws Exception {
        this(new MenuItem());
    }

    public MenuItemForm(MenuItem menuItem) throws Exception {
        this.menuItem = menuItem;
        this.initComponents();
        MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
        List<MenuGroup> foodGroups = foodGroupDAO.findAll();
        this.cbGroup.setModel(new ComboBoxModel(foodGroups));
        TaxDAO taxDAO = new TaxDAO();
        List<Tax> taxes = taxDAO.findAll();
        this.cbTax.setModel(new ComboBoxModel(taxes));
        this.menuItemModifierGroups = menuItem.getMenuItemModiferGroups();
        this.shiftTableModel = new ShiftTableModel(menuItem.getShifts());
        this.shiftTable.setModel(this.shiftTableModel);
        this.priceTableModel = new PriceByOrderTypeTableModel(menuItem.getProperties());
        this.priceTable.setModel(this.priceTableModel);
        this.setBean(menuItem);
    }

    protected void doSelectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(0);
        int option = fileChooser.showOpenDialog(POSUtil.getBackOfficeWindow());
        if (option == 0) {
            File imageFile = fileChooser.getSelectedFile();
            try {
                byte[] itemImage = FileUtils.readFileToByteArray((File)imageFile);
                int imageSize = itemImage.length / 1024;
                if (imageSize > 20) {
                    POSMessageDialog.showMessage(Messages.getString("MenuItemForm.0"));
                    itemImage = null;
                    return;
                }
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(itemImage).getImage().getScaledInstance(80, 80, 4));
                this.lblImagePreview.setIcon(imageIcon);
                MenuItem menuItem = (MenuItem)this.getBean();
                menuItem.setImageData(itemImage);
            }
            catch (IOException e) {
                PosLog.error(this.getClass(), e);
            }
        }
    }

    protected void doClearImage() {
        MenuItem menuItem = (MenuItem)this.getBean();
        menuItem.setImageData(null);
        this.lblImagePreview.setIcon(null);
    }

    public void addRecepieExtension() {
        InventoryPlugin plugin = (InventoryPlugin)ExtensionManager.getPlugin(InventoryPlugin.class);
        if (plugin == null) {
            return;
        }
        plugin.addRecepieView(this.tabbedPane);
    }

    private void initComponents() {
        this.lblStockCount = new JLabel(Messages.getString("MenuItemForm.17"));
        this.tfStockCount = new DoubleTextField(1);
        this.cbDisableStockCount = new JCheckBox(Messages.getString("MenuItemForm.18"));
        this.lblButtonColor = new JLabel(Messages.getString("MenuItemForm.19"));
        this.tabbedPane = new JTabbedPane();
        this.tabGeneral = new JPanel();
        this.lfname = new JLabel();
        this.lfname.setHorizontalAlignment(11);
        this.tfName = new FixedLengthTextField(20);
        this.lgroup = new JLabel();
        this.lgroup.setHorizontalAlignment(11);
        this.cbGroup = new JComboBox();
        this.cbGroup.setPreferredSize(new Dimension(198, 0));
        this.btnNewGroup = new JButton();
        this.lblPrice = new JLabel();
        this.lblPrice.setHorizontalAlignment(11);
        this.tfPrice = new DoubleTextField(20);
        this.tfPrice.setHorizontalAlignment(4);
        this.tfDescription = new JTextArea(new FixedLengthDocument(255));
        this.tfUnitName = new FixedLengthTextField(20);
        this.lTax = new JLabel();
        this.lTax.setHorizontalAlignment(11);
        this.cbTax = new JComboBox();
        this.btnNewTax = new JButton();
        this.lDiscountRate = new JLabel();
        this.lDiscountRate.setHorizontalAlignment(11);
        this.lPercentage = new JLabel();
        this.tfDiscountRate = new DoubleTextField(18);
        this.tfDiscountRate.setHorizontalAlignment(11);
        this.chkVisible = new JCheckBox();
        this.tabModifier = new JPanel();
        this.btnNewModifierGroup = new JButton();
        this.btnDeleteModifierGroup = new JButton();
        this.btnEditModifierGroup = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.tableTicketItemModifierGroups = new JTable();
        this.tabShift = new JPanel();
        this.tabPrice = new JPanel();
        this.tabButtonStyle = new JPanel();
        this.btnDeleteShift = new JButton();
        this.btnAddShift = new JButton();
        this.btnNewPrice = new JButton();
        this.btnUpdatePrice = new JButton();
        this.btnDeletePrice = new JButton();
        this.btnDeleteAll = new JButton();
        this.btnDefaultValue = new JButton();
        this.jScrollPane2 = new JScrollPane();
        this.jScrollPane3 = new JScrollPane();
        this.shiftTable = new JTable();
        this.priceTable = new JTable();
        this.cbPrinterGroup = new JComboBox<PrinterGroup>(new DefaultComboBoxModel<PrinterGroup>(PrinterGroupDAO.getInstance().findAll().toArray(new PrinterGroup[0])));
        this.cbPrinterGroup.setPreferredSize(new Dimension(226, 0));
        this.tfTranslatedName = new FixedLengthTextField(20);
        this.tfTranslatedName.setLength(120);
        this.lblUnitName = new JLabel(Messages.getString("MenuItemForm.23"));
        this.lblKitchenPrinter = new JLabel(Messages.getString("MenuItemForm.27"));
        this.lgroup.setText(Messages.getString("LABEL_GROUP"));
        this.lfname.setText(Messages.getString("LABEL_NAME"));
        this.tfName.setLength(120);
        this.lblTranslatedName = new JLabel(Messages.getString("MenuItemForm.lblTranslatedName.text"));
        this.tfBarcode = new FixedLengthTextField(20);
        this.tfSortOrder = new IntegerTextField(20);
        this.lblSortOrder = new JLabel(Messages.getString("MenuItemForm.lblSortOrder.text"));
        this.tfSortOrder.setText("");
        this.lblBarcode = new JLabel(Messages.getString("MenuItemForm.lblBarcode.text"));
        this.cbTax.setPreferredSize(new Dimension(198, 0));
        this.btnButtonColor = new JButton();
        this.btnButtonColor.setPreferredSize(new Dimension(228, 40));
        this.lblTextColor = new JLabel(Messages.getString("MenuItemForm.lblTextColor.text"));
        this.btnTextColor = new JButton(Messages.getString("MenuItemForm.SAMPLE_TEXT"));
        this.cbShowTextWithImage = new JCheckBox(Messages.getString("MenuItemForm.40"));
        this.cbShowTextWithImage.setActionCommand(Messages.getString("MenuItemForm.41"));
        this.lTax.setText(Messages.getString("LABEL_TAX"));
        this.btnNewTax.setText("...");
        this.cbFractionalUnit = new JCheckBox(Messages.getString("MenuItemForm.24"));
        this.btnNewGroup.setText("...");
        this.btnNewGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                MenuItemForm.this.doCreateNewGroup(evt);
            }
        });
        if (Application.getInstance().isPriceIncludesTax()) {
            this.lblPrice.setText(Messages.getString("LABEL_SALES_PRICE_INCLUDING_TAX"));
        } else {
            this.lblPrice.setText(Messages.getString("LABEL_SALES_PRICE_EXCLUDING_TAX"));
        }
        this.tfPrice.setHorizontalAlignment(4);
        this.lTax.setText(Messages.getString("LABEL_TAX"));
        this.btnNewTax.setText("...");
        this.btnNewTax.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                MenuItemForm.this.btnNewTaxdoCreateNewTax(evt);
            }
        });
        this.lDiscountRate.setText(POSConstants.DISCOUNT_RATE + ":");
        this.lPercentage.setText("%");
        this.chkVisible.setText(POSConstants.VISIBLE);
        this.chkVisible.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkVisible.setMargin(new Insets(0, 0, 0, 0));
        this.tabbedPane.addTab(POSConstants.GENERAL, this.tabGeneral);
        this.tabbedPane.setPreferredSize(new Dimension(750, 470));
        this.btnNewModifierGroup.setText(POSConstants.ADD);
        this.btnNewModifierGroup.setActionCommand("AddModifierGroup");
        this.btnNewModifierGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                MenuItemForm.this.btnNewModifierGroupActionPerformed(evt);
            }
        });
        this.btnDeleteModifierGroup.setText(POSConstants.DELETE);
        this.btnDeleteModifierGroup.setActionCommand("DeleteModifierGroup");
        this.btnEditModifierGroup.setText(POSConstants.EDIT);
        this.btnEditModifierGroup.setActionCommand("EditModifierGroup");
        this.menuItemMGListModel = new MenuItemMGListModel();
        this.tableTicketItemModifierGroups.setModel(this.menuItemMGListModel);
        this.btnNewModifierGroup.addActionListener(this);
        this.btnEditModifierGroup.addActionListener(this);
        this.btnDeleteModifierGroup.addActionListener(this);
        this.btnAddShift.addActionListener(this);
        this.btnDeleteShift.addActionListener(this);
        this.tfDiscountRate.setDocument(new DoubleDocument());
        this.tabGeneral.setLayout((LayoutManager)new MigLayout("insets 20", "[][]20px[][]", "[][][][][][][][][][][][][]"));
        this.tabGeneral.add((Component)this.lfname, "cell 0 1 ,right");
        this.tabGeneral.add((Component)this.tfName, "cell 1 1,grow");
        this.tabGeneral.add((Component)this.lblTranslatedName, "cell 0 2,right");
        this.tabGeneral.add((Component)this.tfTranslatedName, "cell 1 2,grow");
        this.tabGeneral.add((Component)this.lblUnitName, "cell 0 3,right");
        this.tabGeneral.add((Component)this.tfUnitName, "cell 1 3,grow");
        JLabel lblBuyPrice = new JLabel(Messages.getString("LABEL_BUY_PRICE"));
        this.tabGeneral.add((Component)lblBuyPrice, "cell 0 4,alignx right");
        this.tfBuyPrice = new DoubleTextField(20);
        this.tfBuyPrice.setHorizontalAlignment(11);
        this.tabGeneral.add((Component)this.tfBuyPrice, "cell 1 4,grow");
        this.tabGeneral.add((Component)this.lblPrice, "cell 0 5,alignx right");
        this.tabGeneral.add((Component)this.tfPrice, "cell 1 5,grow");
        this.tabGeneral.add((Component)this.lgroup, "cell 0 6,alignx right");
        this.tabGeneral.add((Component)this.cbGroup, "cell 1 6");
        this.tabGeneral.add((Component)this.btnNewGroup, "cell 1 6");
        this.tabGeneral.add((Component)this.lblBarcode, "cell 0 7,alignx right");
        this.tabGeneral.add((Component)this.tfBarcode, "cell 1 7,grow");
        this.tabGeneral.add((Component)this.lblSortOrder, "cell 0 8,alignx right");
        this.tabGeneral.add((Component)this.tfSortOrder, "cell 1 8,grow");
        this.tabGeneral.add((Component)this.lblStockCount, "cell 0 9,alignx right");
        this.tabGeneral.add((Component)this.tfStockCount, "cell 1 9,grow");
        this.tabGeneral.add((Component)this.chkVisible, "cell 1 10");
        this.tabGeneral.add((Component)this.cbFractionalUnit, "cell 1 11");
        this.tabGeneral.add((Component)this.cbDisableStockCount, "cell 1 12");
        this.tabGeneral.add((Component)this.lblKitchenPrinter, "cell 2 1,right");
        this.tabGeneral.add(this.cbPrinterGroup, "cell 3 1,grow");
        this.tabGeneral.add((Component)this.lTax, "cell 2 2,right");
        this.tabGeneral.add((Component)this.cbTax, "cell 3 2");
        this.tabGeneral.add((Component)this.btnNewTax, "cell 3 2,grow");
        this.tabGeneral.add((Component)new JLabel(Messages.getString("MenuItemForm.25")), "cell 2 3,,aligny top,alignx right");
        this.orderList = new CheckBoxList();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        this.orderList.setModel(orderTypes);
        JScrollPane orderCheckBoxList = new JScrollPane(this.orderList);
        orderCheckBoxList.setPreferredSize(new Dimension(228, 100));
        this.tabGeneral.add((Component)orderCheckBoxList, "cell 3 3 3 4");
        this.tfDescription.setWrapStyleWord(true);
        this.tfDescription.setLineWrap(true);
        this.tabGeneral.add((Component)new JLabel(Messages.getString("MenuItemForm.29")), "cell 2 7,aligny top,alignx right");
        JScrollPane scrlDescription = new JScrollPane(this.tfDescription, 20, 30);
        scrlDescription.setPreferredSize(new Dimension(228, 90));
        this.tabGeneral.add((Component)scrlDescription, "cell 3 7 3 4");
        this.add(this.tabbedPane);
        this.addRecepieExtension();
        this.jScrollPane1.setViewportView(this.tableTicketItemModifierGroups);
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(this.tabModifier);
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 412, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnDeleteModifierGroup).addComponent(this.btnEditModifierGroup).addComponent(this.btnNewModifierGroup)).addContainerGap()));
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.btnNewModifierGroup).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnEditModifierGroup).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnDeleteModifierGroup)).addComponent(this.jScrollPane1, -1, 421, Short.MAX_VALUE)).addContainerGap()));
        this.tabModifier.setLayout(jPanel2Layout);
        this.tabbedPane.addTab(POSConstants.MODIFIER_GROUPS, this.tabModifier);
        this.btnDeleteShift.setText(POSConstants.DELETE_SHIFT);
        this.btnAddShift.setText(POSConstants.ADD_SHIFT);
        this.shiftTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        this.jScrollPane2.setViewportView(this.shiftTable);
        GroupLayout jPanel3Layout = new GroupLayout((Container)this.tabShift);
        this.tabShift.setLayout((LayoutManager)jPanel3Layout);
        jPanel3Layout.setHorizontalGroup((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel3Layout.createSequentialGroup().addContainerGap(76, Short.MAX_VALUE).add((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add(2, (Component)this.jScrollPane2, -2, 670, -2).add(2, (GroupLayout.Group)jPanel3Layout.createSequentialGroup().add((Component)this.btnAddShift).add(5, 5, 5).add((Component)this.btnDeleteShift))).addContainerGap()));
        jPanel3Layout.setVerticalGroup((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel3Layout.createSequentialGroup().add((Component)this.jScrollPane2, -2, 345, -2).addPreferredGap(0).add((GroupLayout.Group)jPanel3Layout.createParallelGroup(3).add((Component)this.btnAddShift).add((Component)this.btnDeleteShift)).addContainerGap(-1, Short.MAX_VALUE)));
        this.tabbedPane.addTab(POSConstants.SHIFTS, this.tabShift);
        this.btnNewPrice.setText(Messages.getString("MenuItemForm.9"));
        this.btnNewPrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.addNewPrice();
            }
        });
        this.btnUpdatePrice.setText(Messages.getString("MenuItemForm.13"));
        this.btnUpdatePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.updatePrice();
            }
        });
        this.btnDeletePrice.setText(Messages.getString("MenuItemForm.14"));
        this.btnDeletePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.deletePrice();
            }
        });
        this.btnDeleteAll.setText(Messages.getString("MenuItemForm.15"));
        this.btnDeleteAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.deleteAll();
            }
        });
        this.btnDefaultValue.setText(Messages.getString("MenuItemForm.7"));
        this.priceTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        this.jScrollPane3.setViewportView(this.priceTable);
        this.tabPrice.setLayout(new BorderLayout());
        this.tabPrice.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.tabPrice.add((Component)this.jScrollPane3, "Center");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.btnNewPrice);
        buttonPanel.add(this.btnUpdatePrice);
        buttonPanel.add(this.btnDeletePrice);
        this.tabPrice.add((Component)buttonPanel, "South");
        this.tabbedPane.addTab(Messages.getString("MenuItemForm.16"), this.tabPrice);
        this.tabbedPane.addChangeListener(this);
        this.tabButtonStyle.setLayout((LayoutManager)new MigLayout("insets 10", "[][]100[][][][]", "[][][center][][][]"));
        JLabel lblImage = new JLabel(Messages.getString("MenuItemForm.28"));
        lblImage.setHorizontalAlignment(11);
        this.tabButtonStyle.add((Component)lblImage, "cell 0 0,right");
        this.lblImagePreview = new JLabel("");
        this.lblImagePreview.setHorizontalAlignment(0);
        this.lblImagePreview.setBorder(new EtchedBorder(1, null, null));
        this.lblImagePreview.setPreferredSize(new Dimension(100, 100));
        this.tabButtonStyle.add((Component)this.lblImagePreview, "cell 1 0");
        JButton btnSelectImage = new JButton("...");
        this.btnClearImage = new JButton(Messages.getString("MenuItemForm.34"));
        this.tabButtonStyle.add((Component)this.btnClearImage, "cell  1 0");
        this.tabButtonStyle.add((Component)btnSelectImage, "cell 1 0");
        this.tabButtonStyle.add((Component)this.lblButtonColor, "cell 0 2,right");
        this.tabButtonStyle.add((Component)this.btnButtonColor, "cell 1 2,grow");
        this.tabButtonStyle.add((Component)this.lblTextColor, "cell 0 3,right");
        this.tabButtonStyle.add((Component)this.btnTextColor, "cell 1 3");
        this.tabButtonStyle.add((Component)this.cbShowTextWithImage, "cell 1 4");
        this.btnTextColor.setPreferredSize(new Dimension(228, 50));
        btnSelectImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.doSelectImageFile();
            }
        });
        this.btnClearImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemForm.this.doClearImage();
            }
        });
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuItemForm.this, Messages.getString("MenuItemForm.42"), MenuItemForm.this.btnButtonColor.getBackground());
                MenuItemForm.this.btnButtonColor.setBackground(color);
                MenuItemForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuItemForm.this, Messages.getString("MenuItemForm.43"), MenuItemForm.this.btnTextColor.getForeground());
                MenuItemForm.this.btnTextColor.setForeground(color);
            }
        });
        this.tabbedPane.addTab(Messages.getString("MenuItemForm.26"), this.tabButtonStyle);
    }

    private void btnNewTaxdoCreateNewTax(ActionEvent evt) {
        BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)new TaxForm());
        dialog.open();
    }

    private void btnNewModifierGroupActionPerformed(ActionEvent evt) {
    }

    private void doCreateNewGroup(ActionEvent evt) {
        MenuGroupForm editor = new MenuGroupForm();
        BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
        dialog.open();
        if (!dialog.isCanceled()) {
            MenuGroup foodGroup = (MenuGroup)editor.getBean();
            ComboBoxModel model = (ComboBoxModel)this.cbGroup.getModel();
            model.addElement(foodGroup);
            model.setSelectedItem(foodGroup);
        }
    }

    private void addMenuItemModifierGroup() {
        try {
            MenuItemModifierGroupForm form = new MenuItemModifierGroupForm();
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)form);
            dialog.open();
            if (!dialog.isCanceled()) {
                MenuItemModifierGroup modifier = (MenuItemModifierGroup)form.getBean();
                if (this.menuItemModifierGroups != null) {
                    for (MenuItemModifierGroup modifierGroup : this.menuItemModifierGroups) {
                        if (!modifierGroup.getModifierGroup().equals(modifier.getModifierGroup())) continue;
                        POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("MenuItemForm.48"));
                        return;
                    }
                }
                this.menuItemMGListModel.add(modifier);
            }
        }
        catch (Exception x) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    private void editMenuItemModifierGroup() {
        try {
            int index = this.tableTicketItemModifierGroups.getSelectedRow();
            if (index < 0) {
                return;
            }
            MenuItemModifierGroup menuItemModifierGroup = this.menuItemMGListModel.get(index);
            MenuItemModifierGroupForm form = new MenuItemModifierGroupForm(menuItemModifierGroup);
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)form);
            dialog.open();
            if (!dialog.isCanceled()) {
                this.menuItemMGListModel.fireTableDataChanged();
            }
        }
        catch (Exception x) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    private void deleteMenuItemModifierGroup() {
        try {
            int index = this.tableTicketItemModifierGroups.getSelectedRow();
            if (index < 0) {
                return;
            }
            if (ConfirmDeleteDialog.showMessage(this, POSConstants.CONFIRM_DELETE, POSConstants.CONFIRM) == 0) {
                this.menuItemMGListModel.remove(index);
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
            MenuItem menuItem = (MenuItem)this.getBean();
            MenuItemDAO menuItemDAO = new MenuItemDAO();
            menuItemDAO.saveOrUpdate(menuItem);
        }
        catch (Exception e) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Color buttonColor;
        MenuItem menuItem = (MenuItem)this.getBean();
        if (menuItem.getId() != null && !Hibernate.isInitialized(menuItem.getMenuItemModiferGroups())) {
            MenuItemDAO dao = new MenuItemDAO();
            Session session = dao.getSession();
            menuItem = (MenuItem)session.merge((Object)menuItem);
            Hibernate.initialize(menuItem.getMenuItemModiferGroups());
            session.close();
        }
        this.orderList.selectItems(menuItem.getOrderTypeList());
        this.tfName.setText(menuItem.getName());
        this.tfDescription.setText(menuItem.getDescription());
        this.tfTranslatedName.setText(menuItem.getTranslatedName());
        this.tfBarcode.setText(menuItem.getBarcode());
        this.tfBuyPrice.setText(String.valueOf(menuItem.getBuyPrice()));
        this.tfPrice.setText(String.valueOf(menuItem.getPrice()));
        this.tfUnitName.setText(menuItem.getUnitName());
        this.tfDiscountRate.setText(String.valueOf(menuItem.getDiscountRate()));
        this.tfStockCount.setText(String.valueOf(menuItem.getStockAmount()));
        this.chkVisible.setSelected(menuItem.isVisible());
        this.cbShowTextWithImage.setSelected(menuItem.isShowImageOnly());
        this.cbDisableStockCount.setSelected(menuItem.isDisableWhenStockAmountIsZero());
        ImageIcon menuItemImage = menuItem.getImage();
        if (menuItemImage != null) {
            this.lblImagePreview.setIcon(menuItemImage);
        }
        this.cbGroup.setSelectedItem(menuItem.getParent());
        this.cbTax.setSelectedItem(menuItem.getTax());
        this.cbPrinterGroup.setSelectedItem(menuItem.getPrinterGroup());
        if (menuItem.getSortOrder() != null) {
            this.tfSortOrder.setText(menuItem.getSortOrder().toString());
        }
        if ((buttonColor = menuItem.getButtonColor()) != null) {
            this.btnButtonColor.setBackground(buttonColor);
            this.btnTextColor.setBackground(buttonColor);
        }
        if (menuItem.getTextColor() != null) {
            this.btnTextColor.setForeground(menuItem.getTextColor());
        }
        this.cbFractionalUnit.setSelected(menuItem.isFractionalUnit());
    }

    @Override
    protected boolean updateModel() {
        String itemName = this.tfName.getText();
        if (POSUtil.isBlankOrNull(itemName)) {
            MessageDialog.showError(POSConstants.NAME_REQUIRED);
            return false;
        }
        MenuItem menuItem = (MenuItem)this.getBean();
        menuItem.setName(itemName);
        menuItem.setDescription(this.tfDescription.getText());
        menuItem.setBarcode(this.tfBarcode.getText());
        menuItem.setParent((MenuGroup)this.cbGroup.getSelectedItem());
        menuItem.setBuyPrice(this.tfBuyPrice.getDouble());
        menuItem.setPrice(Double.valueOf(this.tfPrice.getText()));
        menuItem.setUnitName(this.tfUnitName.getText());
        menuItem.setTax((Tax)this.cbTax.getSelectedItem());
        menuItem.setStockAmount(Double.parseDouble(this.tfStockCount.getText()));
        menuItem.setVisible(this.chkVisible.isSelected());
        menuItem.setShowImageOnly(this.cbShowTextWithImage.isSelected());
        menuItem.setFractionalUnit(this.cbFractionalUnit.isSelected());
        menuItem.setDisableWhenStockAmountIsZero(this.cbDisableStockCount.isSelected());
        menuItem.setTranslatedName(this.tfTranslatedName.getText());
        menuItem.setSortOrder(this.tfSortOrder.getInteger());
        menuItem.setButtonColorCode(this.btnButtonColor.getBackground().getRGB());
        menuItem.setTextColorCode(this.btnTextColor.getForeground().getRGB());
        if (this.orderList.getCheckedValues().isEmpty()) {
            menuItem.setOrderTypeList(null);
        } else {
            menuItem.setOrderTypeList(this.orderList.getCheckedValues());
        }
        try {
            menuItem.setDiscountRate(Double.parseDouble(this.tfDiscountRate.getText()));
        }
        catch (Exception exception) {
            // empty catch block
        }
        menuItem.setMenuItemModiferGroups(this.menuItemModifierGroups);
        menuItem.setShifts(this.shiftTableModel.getShifts());
        int tabCount = this.tabbedPane.getTabCount();
        for (int i = 0; i < tabCount; ++i) {
            IUpdatebleView view;
            Component componentAt = this.tabbedPane.getComponent(i);
            if (!(componentAt instanceof IUpdatebleView) || (view = (IUpdatebleView)((Object)componentAt)).updateModel(menuItem)) continue;
            return false;
        }
        menuItem.setPrinterGroup((PrinterGroup)this.cbPrinterGroup.getSelectedItem());
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuItem foodItem = (MenuItem)this.getBean();
        if (foodItem.getId() == null) {
            return POSConstants.NEW_MENU_ITEM;
        }
        return POSConstants.EDIT_MENU_ITEM;
    }

    private void addShift() {
        MenuItemShiftDialog dialog = new MenuItemShiftDialog(this.getParentFrame());
        dialog.setSize(350, 220);
        dialog.open();
        if (!dialog.isCanceled()) {
            MenuItemShift menuItemShift = dialog.getMenuItemShift();
            this.shiftTableModel.add(menuItemShift);
        }
    }

    private void addNewPrice() {
        MenuItemPriceByOrderTypeDialog dialog = new MenuItemPriceByOrderTypeDialog(this.getParentFrame(), this.menuItem);
        dialog.setSize(350, 220);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.priceTableModel.add(dialog.getMenuItem());
        }
    }

    private void deleteShift() {
        int selectedRow = this.shiftTable.getSelectedRow();
        if (selectedRow >= 0) {
            this.shiftTableModel.remove(selectedRow);
        }
    }

    private void deletePrice() {
        int selectedRow = this.priceTable.getSelectedRow();
        if (selectedRow == -1) {
            POSMessageDialog.showMessage(this.getParentFrame(), Messages.getString("MenuItemForm.32"));
            return;
        }
        int option = POSMessageDialog.showYesNoQuestionDialog(this.getParentFrame(), Messages.getString("MenuItemForm.33"), Messages.getString("MenuItemForm.35"));
        if (option != 0) {
            return;
        }
        this.priceTableModel.remove(selectedRow);
    }

    private void deleteAll() {
        int option = POSMessageDialog.showYesNoQuestionDialog(this.getParentFrame(), Messages.getString("MenuItemForm.36"), Messages.getString("MenuItemForm.37"));
        if (option != 0) {
            return;
        }
        this.priceTableModel.removeAll();
    }

    private void updatePrice() {
        int selectedRow = this.priceTable.getSelectedRow();
        if (selectedRow == -1) {
            POSMessageDialog.showMessage(this.getParentFrame(), Messages.getString("MenuItemForm.38"));
            return;
        }
        this.priceTableModel.propertiesKey.get(selectedRow);
        MenuItemPriceByOrderTypeDialog dialog = new MenuItemPriceByOrderTypeDialog(this.getParentFrame(), this.menuItem, this.priceTableModel.propertiesKey.get(selectedRow));
        dialog.setSize(350, 220);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.priceTableModel.add(dialog.getMenuItem());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("AddModifierGroup")) {
            this.addMenuItemModifierGroup();
        } else if (actionCommand.equals("EditModifierGroup")) {
            this.editMenuItemModifierGroup();
        } else if (actionCommand.equals("DeleteModifierGroup")) {
            this.deleteMenuItemModifierGroup();
        } else if (actionCommand.equals(POSConstants.ADD_SHIFT)) {
            this.addShift();
        } else if (actionCommand.equals(POSConstants.DELETE_SHIFT)) {
            this.deleteShift();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Component selectedComponent = this.tabbedPane.getSelectedComponent();
        if (!(selectedComponent instanceof IUpdatebleView)) {
            return;
        }
        IUpdatebleView view = (IUpdatebleView)((Object)selectedComponent);
        MenuItem menuItem = (MenuItem)this.getBean();
        view.initView(menuItem);
    }

    class PriceByOrderTypeTableModel
    extends AbstractTableModel {
        List<String> propertiesKey = new ArrayList<String>();
        String[] cn = new String[]{"ORDER TYPE", "PRICE", "TAX"};

        PriceByOrderTypeTableModel(Map<String, String> properties) {
            if (properties != null && !properties.isEmpty()) {
                ArrayList<String> keys = new ArrayList<String>(properties.keySet());
                this.setPropertiesToTable(keys);
            }
        }

        private void setPropertiesToTable(List<String> keys) {
            this.propertiesKey.clear();
            for (int i = 0; i < keys.size(); ++i) {
                if (!keys.get(i).contains("_PRICE")) continue;
                this.propertiesKey.add(keys.get(i));
            }
        }

        public String get(int index) {
            return this.propertiesKey.get(index);
        }

        public void add(MenuItem menuItem) {
            this.setPropertiesToTable(new ArrayList<String>(menuItem.getProperties().keySet()));
            this.fireTableDataChanged();
        }

        public void remove(int index) {
            if (this.propertiesKey == null) {
                return;
            }
            String typeProperty = this.propertiesKey.get(index);
            String taxProperty = typeProperty.replaceAll("_PRICE", "_TAX");
            MenuItemForm.this.menuItem.removeProperty(typeProperty, taxProperty);
            MenuItemDAO.getInstance().saveOrUpdate(MenuItemForm.this.menuItem);
            this.propertiesKey.remove(index);
            this.fireTableDataChanged();
        }

        public void removeAll() {
            MenuItemForm.this.menuItem.getProperties().clear();
            MenuItemDAO.getInstance().saveOrUpdate(MenuItemForm.this.menuItem);
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
                    return MenuItemForm.this.menuItem.getStringWithOutUnderScore(key, "_PRICE");
                }
                case 1: {
                    return MenuItemForm.this.menuItem.getProperty(key);
                }
                case 2: {
                    return MenuItemForm.this.menuItem.getProperty(MenuItemForm.this.menuItem.replaceString(key, "_PRICE", "_TAX"));
                }
            }
            return null;
        }
    }

    class ShiftTableModel
    extends AbstractTableModel {
        List<MenuItemShift> shifts;
        String[] cn = new String[]{POSConstants.START_TIME, POSConstants.END_TIME, POSConstants.PRICE};
        Calendar calendar = Calendar.getInstance();

        ShiftTableModel(List<MenuItemShift> shifts) {
            this.shifts = shifts == null ? new ArrayList<MenuItemShift>() : new ArrayList<MenuItemShift>(shifts);
        }

        public MenuItemShift get(int index) {
            return this.shifts.get(index);
        }

        public void add(MenuItemShift group) {
            if (this.shifts == null) {
                this.shifts = new ArrayList<MenuItemShift>();
            }
            this.shifts.add(group);
            this.fireTableDataChanged();
        }

        public void remove(int index) {
            if (this.shifts == null) {
                return;
            }
            this.shifts.remove(index);
            this.fireTableDataChanged();
        }

        public void remove(MenuItemShift group) {
            if (this.shifts == null) {
                return;
            }
            this.shifts.remove(group);
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (this.shifts == null) {
                return 0;
            }
            return this.shifts.size();
        }

        @Override
        public int getColumnCount() {
            return this.cn.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.cn[column];
        }

        public List<MenuItemShift> getShifts() {
            return this.shifts;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MenuItemShift shift = this.shifts.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return ShiftUtil.buildShiftTimeRepresentation(shift.getShift().getStartTime());
                }
                case 1: {
                    return ShiftUtil.buildShiftTimeRepresentation(shift.getShift().getEndTime());
                }
                case 2: {
                    return String.valueOf(shift.getShiftPrice());
                }
            }
            return null;
        }
    }

    class MenuItemMGListModel
    extends AbstractTableModel {
        String[] cn = new String[]{POSConstants.GROUP_NAME, POSConstants.MIN_QUANTITY, POSConstants.MAX_QUANTITY};

        MenuItemMGListModel() {
        }

        public MenuItemModifierGroup get(int index) {
            return (MenuItemModifierGroup)MenuItemForm.this.menuItemModifierGroups.get(index);
        }

        public void add(MenuItemModifierGroup group) {
            if (MenuItemForm.this.menuItemModifierGroups == null) {
                MenuItemForm.this.menuItemModifierGroups = new ArrayList();
            }
            MenuItemForm.this.menuItemModifierGroups.add(group);
            this.fireTableDataChanged();
        }

        public void remove(int index) {
            if (MenuItemForm.this.menuItemModifierGroups == null) {
                return;
            }
            MenuItemForm.this.menuItemModifierGroups.remove(index);
            this.fireTableDataChanged();
        }

        public void remove(MenuItemModifierGroup group) {
            if (MenuItemForm.this.menuItemModifierGroups == null) {
                return;
            }
            MenuItemForm.this.menuItemModifierGroups.remove(group);
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (MenuItemForm.this.menuItemModifierGroups == null) {
                return 0;
            }
            return MenuItemForm.this.menuItemModifierGroups.size();
        }

        @Override
        public int getColumnCount() {
            return this.cn.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.cn[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MenuItemModifierGroup menuItemModifierGroup = (MenuItemModifierGroup)MenuItemForm.this.menuItemModifierGroups.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return menuItemModifierGroup.getModifierGroup().getName();
                }
                case 1: {
                    return (int)menuItemModifierGroup.getMinQuantity();
                }
                case 2: {
                    return (int)menuItemModifierGroup.getMaxQuantity();
                }
            }
            return null;
        }
    }
}


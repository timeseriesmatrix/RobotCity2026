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
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.model.dao.PizzaCrustDAO;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IUpdatebleView;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.ui.model.MenuItemModifierGroupForm;
import com.floreantpos.ui.model.MenuItemShiftDialog;
import com.floreantpos.ui.model.PizzaItemPriceDialog;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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

public class PizzaItemForm
extends BeanEditor<MenuItem>
implements ActionListener,
ChangeListener {
    private JTabbedPane tabbedPane;
    private JTable shiftTable;
    private JTable priceTable;
    private JTable tableTicketItemModifierGroups;
    private FixedLengthTextField tfName;
    private FixedLengthTextField tfTranslatedName;
    private JComboBox cbGroup;
    private FixedLengthTextField tfBarcode;
    private DoubleTextField tfStockCount;
    private JCheckBox chkVisible;
    private JCheckBox cbDisableStockCount;
    private IntegerTextField tfDefaultSellPortion;
    private JComboBox<PrinterGroup> cbPrinterGroup;
    private JComboBox cbTax;
    private CheckBoxList orderList;
    private JTextArea tfDescription;
    private List<MenuItemModifierGroup> menuItemModifierGroups;
    private MenuItemMGListModel menuItemMGListModel;
    private JLabel lblImagePreview;
    private JButton btnClearImage;
    private JCheckBox cbShowTextWithImage;
    private JLabel lblKitchenPrinter;
    private JButton btnButtonColor;
    private JButton btnTextColor;
    private IntegerTextField tfSortOrder;
    private ShiftTableModel shiftTableModel;
    private BeanTableModel<PizzaPrice> priceTableModel;
    private MenuItem menuItem;

    public PizzaItemForm() throws Exception {
        this(new MenuItem());
    }

    public PizzaItemForm(MenuItem menuItem) throws Exception {
        this.menuItem = menuItem;
        this.initComponents();
        this.initData();
    }

    private void initData() {
        MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
        List<MenuGroup> foodGroups = foodGroupDAO.findAll();
        this.cbGroup.setModel(new ComboBoxModel(foodGroups));
        TaxDAO taxDAO = new TaxDAO();
        List<Tax> taxes = taxDAO.findAll();
        this.cbTax.setModel(new ComboBoxModel(taxes));
        this.menuItemModifierGroups = this.menuItem.getMenuItemModiferGroups();
        this.shiftTableModel = new ShiftTableModel(this.menuItem.getShifts());
        this.shiftTable.setModel(this.shiftTableModel);
        this.priceTableModel = new BeanTableModel<PizzaPrice>(PizzaPrice.class){

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 2;
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                if (columnIndex == 2) {
                    PizzaPrice price = (PizzaPrice)PizzaItemForm.this.priceTableModel.getRow(rowIndex);
                    price.setPrice((double)((Double)value));
                }
            }
        };
        this.priceTableModel.addColumn("SIZE", "size");
        this.priceTableModel.addColumn("CRUST", "crust");
        this.priceTableModel.addColumn("PRICE", "price");
        List<PizzaPrice> pizzaPriceList = this.menuItem.getPizzaPriceList();
        if (pizzaPriceList == null || pizzaPriceList.isEmpty()) {
            this.priceTableModel.addRows(this.generatedPossiblePizzaItemSizeAndPriceList());
        } else {
            this.priceTableModel.addRows(pizzaPriceList);
        }
        this.priceTable.setModel(this.priceTableModel);
        this.setBean(this.menuItem);
        this.priceTable.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    PizzaItemForm.this.editEvent();
                }
            }
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JLabel lblButtonColor = new JLabel(Messages.getString("MenuItemForm.19"));
        this.tabbedPane = new JTabbedPane();
        JPanel tabGeneral = new JPanel();
        JLabel lblName = new JLabel();
        lblName.setHorizontalAlignment(11);
        this.tfName = new FixedLengthTextField(20);
        this.tfDescription = new JTextArea(new FixedLengthDocument(120));
        JLabel lTax = new JLabel();
        lTax.setHorizontalAlignment(11);
        this.cbTax = new JComboBox();
        JButton btnNewTax = new JButton();
        JPanel tabShift = new JPanel();
        JPanel tabPrice = new JPanel();
        JPanel tabButtonStyle = new JPanel();
        JButton btnDeleteShift = new JButton();
        JButton btnAddShift = new JButton();
        JButton btnNewPrice = new JButton();
        JButton btnUpdatePrice = new JButton();
        JButton btnDeletePrice = new JButton();
        JButton btnDeleteAll = new JButton();
        JButton btnDefaultValue = new JButton();
        JButton btnAutoGenerate = new JButton();
        JScrollPane jScrollPane2 = new JScrollPane();
        JScrollPane priceTabScrollPane = new JScrollPane();
        this.shiftTable = new JTable();
        this.priceTable = new JTable();
        this.priceTable.setRowHeight(PosUIManager.getSize(this.priceTable.getRowHeight()));
        this.priceTable.setCellSelectionEnabled(true);
        this.priceTable.setSelectionMode(0);
        this.priceTable.setSurrendersFocusOnKeystroke(true);
        this.cbPrinterGroup = new JComboBox<PrinterGroup>(new DefaultComboBoxModel<PrinterGroup>(PrinterGroupDAO.getInstance().findAll().toArray(new PrinterGroup[0])));
        this.cbPrinterGroup.setPreferredSize(new Dimension(226, 0));
        this.tfDefaultSellPortion = new IntegerTextField(10);
        this.tfTranslatedName = new FixedLengthTextField(20);
        this.tfTranslatedName.setLength(120);
        this.lblKitchenPrinter = new JLabel(Messages.getString("MenuItemForm.27"));
        lblName.setText(Messages.getString("LABEL_NAME"));
        this.tfName.setLength(120);
        JLabel lblTranslatedName = new JLabel(Messages.getString("MenuItemForm.lblTranslatedName.text"));
        this.tfSortOrder = new IntegerTextField(20);
        this.tfSortOrder.setText("");
        this.cbTax.setPreferredSize(new Dimension(198, 0));
        this.btnButtonColor = new JButton();
        this.btnButtonColor.setPreferredSize(new Dimension(228, 40));
        JLabel lblTextColor = new JLabel(Messages.getString("MenuItemForm.lblTextColor.text"));
        this.btnTextColor = new JButton(Messages.getString("MenuItemForm.SAMPLE_TEXT"));
        this.cbShowTextWithImage = new JCheckBox(Messages.getString("MenuItemForm.40"));
        this.cbShowTextWithImage.setActionCommand(Messages.getString("MenuItemForm.41"));
        lTax.setText(Messages.getString("LABEL_TAX"));
        btnNewTax.setText("...");
        lTax.setText(Messages.getString("LABEL_TAX"));
        btnNewTax.setText("...");
        btnNewTax.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PizzaItemForm.this.btnNewTaxdoCreateNewTax(evt);
            }
        });
        this.tabbedPane.addTab(POSConstants.GENERAL, tabGeneral);
        this.tabbedPane.setPreferredSize(new Dimension(750, 470));
        this.tabbedPane.addTab(POSConstants.MODIFIER_GROUPS, this.getModifierGroupTab());
        btnAddShift.addActionListener(this);
        btnDeleteShift.addActionListener(this);
        tabGeneral.setLayout((LayoutManager)new MigLayout("insets 20", "[][]20px[][]", "[][][][][][][][][][][][][]"));
        tabGeneral.add((Component)lblName, "cell 0 1 ,right");
        tabGeneral.add((Component)this.tfName, "cell 1 1,grow");
        tabGeneral.add((Component)lblTranslatedName, "cell 0 2,right");
        tabGeneral.add((Component)this.tfTranslatedName, "cell 1 2,grow");
        JLabel lgroup = new JLabel();
        lgroup.setHorizontalAlignment(11);
        lgroup.setText(Messages.getString("LABEL_GROUP"));
        tabGeneral.add((Component)lgroup, "cell 0 3,alignx right");
        JLabel lblBarcode = new JLabel(Messages.getString("MenuItemForm.lblBarcode.text"));
        tabGeneral.add((Component)lblBarcode, "cell 0 4,alignx right");
        this.tfBarcode = new FixedLengthTextField(20);
        tabGeneral.add((Component)this.tfBarcode, "cell 1 4,grow");
        JLabel lblStockCount = new JLabel(Messages.getString("MenuItemForm.17"));
        tabGeneral.add((Component)lblStockCount, "cell 0 5,alignx right");
        this.tfStockCount = new DoubleTextField(1);
        tabGeneral.add((Component)this.tfStockCount, "cell 1 5,grow");
        this.chkVisible = new JCheckBox();
        tabGeneral.add((Component)new JLabel("Default sell portion (%)"), "cell 0 6");
        tabGeneral.add((Component)this.tfDefaultSellPortion, "cell 1 6,grow");
        this.chkVisible.setText(POSConstants.VISIBLE);
        this.chkVisible.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkVisible.setMargin(new Insets(0, 0, 0, 0));
        tabGeneral.add((Component)this.chkVisible, "cell 1 7");
        tabGeneral.add((Component)this.lblKitchenPrinter, "cell 2 1,right");
        tabGeneral.add(this.cbPrinterGroup, "cell 3 1,grow");
        tabGeneral.add((Component)lTax, "cell 2 2,right");
        tabGeneral.add((Component)this.cbTax, "cell 3 2");
        tabGeneral.add((Component)btnNewTax, "cell 3 2,grow");
        this.cbGroup = new JComboBox();
        this.cbGroup.setPreferredSize(new Dimension(198, 0));
        tabGeneral.add((Component)this.cbGroup, "flowx,cell 1 3");
        JButton btnNewGroup = new JButton();
        btnNewGroup.setText("...");
        btnNewGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PizzaItemForm.this.doCreateNewGroup(evt);
            }
        });
        tabGeneral.add((Component)btnNewGroup, "cell 1 3");
        tabGeneral.add((Component)new JLabel(Messages.getString("MenuItemForm.25")), "cell 2 3,right");
        this.orderList = new CheckBoxList();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        this.orderList.setModel(orderTypes);
        JScrollPane orderCheckBoxList = new JScrollPane(this.orderList);
        orderCheckBoxList.setPreferredSize(new Dimension(228, 100));
        tabGeneral.add((Component)orderCheckBoxList, "cell 3 3 3 3");
        this.cbDisableStockCount = new JCheckBox(Messages.getString("MenuItemForm.18"));
        tabGeneral.add((Component)this.cbDisableStockCount, "cell 1 8");
        tabGeneral.add((Component)new JLabel(Messages.getString("MenuItemForm.29")), "cell 2 6,alignx right");
        JScrollPane scrlDescription = new JScrollPane(this.tfDescription, 20, 30);
        scrlDescription.setPreferredSize(new Dimension(228, 70));
        this.tfDescription.setLineWrap(true);
        tabGeneral.add((Component)scrlDescription, "cell 3 6 3 3");
        this.add(this.tabbedPane);
        this.addRecepieExtension();
        btnDeleteShift.setText(POSConstants.DELETE_SHIFT);
        btnAddShift.setText(POSConstants.ADD_SHIFT);
        this.shiftTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        jScrollPane2.setViewportView(this.shiftTable);
        GroupLayout jPanel3Layout = new GroupLayout((Container)tabShift);
        tabShift.setLayout((LayoutManager)jPanel3Layout);
        jPanel3Layout.setHorizontalGroup((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel3Layout.createSequentialGroup().addContainerGap(76, Short.MAX_VALUE).add((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add(2, (Component)jScrollPane2, -2, 670, -2).add(2, (GroupLayout.Group)jPanel3Layout.createSequentialGroup().add((Component)btnAddShift).add(5, 5, 5).add((Component)btnDeleteShift))).addContainerGap()));
        jPanel3Layout.setVerticalGroup((GroupLayout.Group)jPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel3Layout.createSequentialGroup().add((Component)jScrollPane2, -2, 345, -2).addPreferredGap(0).add((GroupLayout.Group)jPanel3Layout.createParallelGroup(3).add((Component)btnAddShift).add((Component)btnDeleteShift)).addContainerGap(-1, Short.MAX_VALUE)));
        this.tabbedPane.addTab(POSConstants.SHIFTS, tabShift);
        btnNewPrice.setText(Messages.getString("MenuItemForm.9"));
        btnNewPrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.addNewPrice();
            }
        });
        btnUpdatePrice.setText(Messages.getString("MenuItemForm.13"));
        btnUpdatePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.updatePrice();
            }
        });
        btnDeletePrice.setText(Messages.getString("MenuItemForm.14"));
        btnDeletePrice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.deletePrice();
            }
        });
        btnAutoGenerate.setText("Auto Generate");
        btnAutoGenerate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.autoGeneratePizzaItemSizeAndPrice();
            }
        });
        btnDeleteAll.setText(Messages.getString("MenuItemForm.15"));
        btnDeleteAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.deleteAll();
            }
        });
        btnDefaultValue.setText(Messages.getString("MenuItemForm.7"));
        priceTabScrollPane.setViewportView(this.priceTable);
        tabPrice.setLayout(new BorderLayout());
        tabPrice.add((Component)priceTabScrollPane, "Center");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnNewPrice);
        buttonPanel.add(btnUpdatePrice);
        buttonPanel.add(btnDeletePrice);
        buttonPanel.add(btnAutoGenerate);
        tabPrice.add((Component)buttonPanel, "South");
        tabGeneral.add((Component)tabPrice, "cell 0 10,grow,span");
        this.tabbedPane.addChangeListener(this);
        tabButtonStyle.setLayout((LayoutManager)new MigLayout("insets 10", "[][]100[][][][]", "[][][center][][][]"));
        JLabel lblImage = new JLabel(Messages.getString("MenuItemForm.28"));
        lblImage.setHorizontalAlignment(11);
        tabButtonStyle.add((Component)lblImage, "cell 0 0,right");
        this.lblImagePreview = new JLabel("");
        this.lblImagePreview.setHorizontalAlignment(0);
        this.lblImagePreview.setBorder(new EtchedBorder(1, null, null));
        this.lblImagePreview.setPreferredSize(new Dimension(100, 100));
        tabButtonStyle.add((Component)this.lblImagePreview, "cell 1 0");
        JButton btnSelectImage = new JButton("...");
        this.btnClearImage = new JButton(Messages.getString("MenuItemForm.34"));
        tabButtonStyle.add((Component)this.btnClearImage, "cell  1 0");
        tabButtonStyle.add((Component)btnSelectImage, "cell 1 0");
        tabButtonStyle.add((Component)lblButtonColor, "cell 0 2,right");
        tabButtonStyle.add((Component)this.btnButtonColor, "cell 1 2,grow");
        tabButtonStyle.add((Component)lblTextColor, "cell 0 3,right");
        tabButtonStyle.add((Component)this.btnTextColor, "cell 1 3");
        tabButtonStyle.add((Component)this.cbShowTextWithImage, "cell 1 4");
        this.btnTextColor.setPreferredSize(new Dimension(228, 50));
        btnSelectImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.doSelectImageFile();
            }
        });
        this.btnClearImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemForm.this.doClearImage();
            }
        });
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(POSUtil.getBackOfficeWindow(), Messages.getString("MenuItemForm.42"), PizzaItemForm.this.btnButtonColor.getBackground());
                PizzaItemForm.this.btnButtonColor.setBackground(color);
                PizzaItemForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(POSUtil.getBackOfficeWindow(), Messages.getString("MenuItemForm.43"), PizzaItemForm.this.btnTextColor.getForeground());
                PizzaItemForm.this.btnTextColor.setForeground(color);
            }
        });
        this.tabbedPane.addTab(Messages.getString("MenuItemForm.26"), tabButtonStyle);
    }

    private void autoGeneratePizzaItemSizeAndPrice() {
        List<PizzaPrice> pizzaPriceList = this.generatedPossiblePizzaItemSizeAndPriceList();
        this.filterDuplicateItemSizesAndPrices(pizzaPriceList);
        this.priceTableModel.addRows(pizzaPriceList);
        this.priceTable.repaint();
    }

    private JPanel getModifierGroupTab() {
        JPanel tabModifierGroup = new JPanel((LayoutManager)new MigLayout("fill"));
        JButton btnNewModifierGroup = new JButton();
        JButton btnEditModifierGroup = new JButton();
        JButton btnDeleteModifierGroup = new JButton();
        this.tableTicketItemModifierGroups = new JTable();
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(this.tableTicketItemModifierGroups);
        tabModifierGroup.add((Component)jScrollPane1, "span,grow");
        tabModifierGroup.add((Component)btnNewModifierGroup, "left,split 3");
        tabModifierGroup.add(btnEditModifierGroup);
        tabModifierGroup.add(btnDeleteModifierGroup);
        btnNewModifierGroup.setText(POSConstants.ADD);
        btnNewModifierGroup.setActionCommand("AddModifierGroup");
        btnNewModifierGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PizzaItemForm.this.btnNewModifierGroupActionPerformed(evt);
            }
        });
        btnDeleteModifierGroup.setText(POSConstants.DELETE);
        btnDeleteModifierGroup.setActionCommand("DeleteModifierGroup");
        btnEditModifierGroup.setText(POSConstants.EDIT);
        btnEditModifierGroup.setActionCommand("EditModifierGroup");
        this.menuItemMGListModel = new MenuItemMGListModel();
        this.tableTicketItemModifierGroups.setModel(this.menuItemMGListModel);
        btnNewModifierGroup.addActionListener(this);
        btnEditModifierGroup.addActionListener(this);
        btnDeleteModifierGroup.addActionListener(this);
        return tabModifierGroup;
    }

    private void doSelectImageFile() {
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
        this.tfStockCount.setText(String.valueOf(menuItem.getStockAmount()));
        this.chkVisible.setSelected(menuItem.isVisible());
        this.cbShowTextWithImage.setSelected(menuItem.isShowImageOnly());
        this.cbDisableStockCount.setSelected(menuItem.isDisableWhenStockAmountIsZero());
        ImageIcon menuItemImage = menuItem.getImage();
        if (menuItemImage != null) {
            this.lblImagePreview.setIcon(menuItemImage);
        }
        if (menuItem.getId() == null) {
            this.tfDefaultSellPortion.setText(String.valueOf(100));
        } else {
            this.tfDefaultSellPortion.setText(String.valueOf(menuItem.getDefaultSellPortion()));
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
    }

    @Override
    protected boolean updateModel() {
        String itemName = this.tfName.getText();
        if (POSUtil.isBlankOrNull(itemName)) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.NAME_REQUIRED);
            return false;
        }
        MenuItem menuItem = (MenuItem)this.getBean();
        menuItem.setName(itemName);
        menuItem.setDescription(this.tfDescription.getText());
        menuItem.setBarcode(this.tfBarcode.getText());
        menuItem.setParent((MenuGroup)this.cbGroup.getSelectedItem());
        menuItem.setTax((Tax)this.cbTax.getSelectedItem());
        menuItem.setStockAmount(Double.parseDouble(this.tfStockCount.getText()));
        menuItem.setVisible(this.chkVisible.isSelected());
        menuItem.setShowImageOnly(this.cbShowTextWithImage.isSelected());
        menuItem.setDisableWhenStockAmountIsZero(this.cbDisableStockCount.isSelected());
        menuItem.setDefaultSellPortion(this.tfDefaultSellPortion.getInteger());
        menuItem.setTranslatedName(this.tfTranslatedName.getText());
        menuItem.setSortOrder(this.tfSortOrder.getInteger());
        menuItem.setButtonColorCode(this.btnButtonColor.getBackground().getRGB());
        menuItem.setTextColorCode(this.btnTextColor.getForeground().getRGB());
        if (this.orderList.getCheckedValues().isEmpty()) {
            menuItem.setOrderTypeList(null);
        } else {
            menuItem.setOrderTypeList(this.orderList.getCheckedValues());
        }
        menuItem.setMenuItemModiferGroups(this.menuItemModifierGroups);
        menuItem.setShifts(this.shiftTableModel.getShifts());
        List<PizzaPrice> pizzaPriceList = this.priceTableModel.getRows();
        if (menuItem.getPizzaPriceList() != null) {
            menuItem.getPizzaPriceList().clear();
        }
        for (PizzaPrice pizzaPrice : pizzaPriceList) {
            menuItem.addTopizzaPriceList(pizzaPrice);
        }
        int tabCount = this.tabbedPane.getTabCount();
        for (int i = 0; i < tabCount; ++i) {
            IUpdatebleView view;
            Component componentAt = this.tabbedPane.getComponent(i);
            if (!(componentAt instanceof IUpdatebleView) || (view = (IUpdatebleView)((Object)componentAt)).updateModel(menuItem)) continue;
            return false;
        }
        menuItem.setPrinterGroup((PrinterGroup)this.cbPrinterGroup.getSelectedItem());
        menuItem.setPizzaType(true);
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuItem foodItem = (MenuItem)this.getBean();
        if (foodItem.getId() == null) {
            return "New pizza item";
        }
        return "Edit pizza item";
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
        List<PizzaPrice> pizzaPriceList = this.priceTableModel.getRows();
        PizzaItemPriceDialog dialog = new PizzaItemPriceDialog(this.getParentFrame(), null, pizzaPriceList);
        dialog.setTitle("Add New Price");
        dialog.setSize(PosUIManager.getSize(350, 220));
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        PizzaPrice pizzaPrice = dialog.getPizzaPrice();
        this.priceTableModel.addRow(pizzaPrice);
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
        this.priceTableModel.removeRow(selectedRow);
    }

    private void deleteAll() {
        int option = POSMessageDialog.showYesNoQuestionDialog(this.getParentFrame(), Messages.getString("MenuItemForm.36"), Messages.getString("MenuItemForm.37"));
        if (option != 0) {
            return;
        }
        this.priceTableModel.removeAll();
    }

    private void updatePrice() {
        this.editEvent();
    }

    private void editEvent() {
        List<PizzaPrice> pizzaPriceList = this.priceTableModel.getRows();
        int selectedRow = this.priceTable.getSelectedRow();
        if (selectedRow == -1) {
            POSMessageDialog.showMessage(this.getParentFrame(), Messages.getString("MenuItemForm.38"));
            return;
        }
        PizzaPrice pizzaPrice = this.priceTableModel.getRow(selectedRow);
        PizzaItemPriceDialog pizzaItemPriceDialog = new PizzaItemPriceDialog(this.getParentFrame(), pizzaPrice, pizzaPriceList);
        pizzaItemPriceDialog.setTitle("Edit Pizza Price");
        pizzaItemPriceDialog.setSize(PosUIManager.getSize(350, 220));
        pizzaItemPriceDialog.open();
        if (pizzaItemPriceDialog.isCanceled()) {
            return;
        }
        this.priceTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
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

    private void filterDuplicateItemSizesAndPrices(List<PizzaPrice> pizzaPriceList) {
        List<PizzaPrice> existedPizzaPriceValueList = this.priceTableModel.getRows();
        if (existedPizzaPriceValueList != null) {
            for (PizzaPrice existingPizzaPrice : existedPizzaPriceValueList) {
                Iterator<PizzaPrice> iterator2 = pizzaPriceList.iterator();
                while (iterator2.hasNext()) {
                    PizzaPrice pizzaPrice = iterator2.next();
                    if (!existingPizzaPrice.getSize().equals(pizzaPrice.getSize()) || !existingPizzaPrice.getCrust().equals(pizzaPrice.getCrust())) continue;
                    iterator2.remove();
                }
            }
        }
    }

    private List<PizzaPrice> generatedPossiblePizzaItemSizeAndPriceList() {
        List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
        List<PizzaCrust> crustList = PizzaCrustDAO.getInstance().findAll();
        ArrayList<PizzaPrice> pizzaPriceList = new ArrayList<PizzaPrice>();
        for (int i = 0; i < menuItemSizeList.size(); ++i) {
            for (int j = 0; j < crustList.size(); ++j) {
                PizzaPrice pizzaPrice = new PizzaPrice();
                pizzaPrice.setSize(menuItemSizeList.get(i));
                pizzaPrice.setCrust(crustList.get(j));
                pizzaPrice.setPrice(0.0);
                pizzaPriceList.add(pizzaPrice);
            }
        }
        return pizzaPriceList;
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
            PizzaItemForm.this.menuItem.removeProperty(typeProperty, taxProperty);
            MenuItemDAO.getInstance().saveOrUpdate(PizzaItemForm.this.menuItem);
            this.propertiesKey.remove(index);
            this.fireTableDataChanged();
        }

        public void removeAll() {
            PizzaItemForm.this.menuItem.getProperties().clear();
            MenuItemDAO.getInstance().saveOrUpdate(PizzaItemForm.this.menuItem);
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
                    return PizzaItemForm.this.menuItem.getStringWithOutUnderScore(key, "_PRICE");
                }
                case 1: {
                    return PizzaItemForm.this.menuItem.getProperty(key);
                }
                case 2: {
                    return PizzaItemForm.this.menuItem.getProperty(PizzaItemForm.this.menuItem.replaceString(key, "_PRICE", "_TAX"));
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
            return (MenuItemModifierGroup)PizzaItemForm.this.menuItemModifierGroups.get(index);
        }

        public void add(MenuItemModifierGroup group) {
            if (PizzaItemForm.this.menuItemModifierGroups == null) {
                PizzaItemForm.this.menuItemModifierGroups = new ArrayList();
            }
            PizzaItemForm.this.menuItemModifierGroups.add(group);
            this.fireTableDataChanged();
        }

        public void remove(int index) {
            if (PizzaItemForm.this.menuItemModifierGroups == null) {
                return;
            }
            PizzaItemForm.this.menuItemModifierGroups.remove(index);
            this.fireTableDataChanged();
        }

        public void remove(MenuItemModifierGroup group) {
            if (PizzaItemForm.this.menuItemModifierGroups == null) {
                return;
            }
            PizzaItemForm.this.menuItemModifierGroups.remove(group);
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (PizzaItemForm.this.menuItemModifierGroups == null) {
                return 0;
            }
            return PizzaItemForm.this.menuItemModifierGroups.size();
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
            MenuItemModifierGroup menuItemModifierGroup = (MenuItemModifierGroup)PizzaItemForm.this.menuItemModifierGroups.get(rowIndex);
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


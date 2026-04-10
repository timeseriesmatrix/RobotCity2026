/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ComboItemSelectionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.ui.model.MenuItemForm;
import com.floreantpos.ui.model.OrderTypeForm;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

public class MenuItemExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<MenuItem> tableModel = new BeanTableModel(MenuItem.class);
    private JComboBox cbGroup;
    private JComboBox cbOrderTypes;
    private JTextField tfName;

    public MenuItemExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn(POSConstants.TRANSLATED_NAME.toUpperCase(), "translatedName");
        this.tableModel.addColumn(POSConstants.PRICE.toUpperCase() + " (" + CurrencyUtil.getCurrencySymbol() + ")", "price");
        this.tableModel.addColumn(Messages.getString("MenuItemExplorer.16"), "stockAmount");
        this.tableModel.addColumn(POSConstants.VISIBLE.toUpperCase(), "visible");
        this.tableModel.addColumn(POSConstants.FOOD_GROUP.toUpperCase(), "parent");
        this.tableModel.addColumn(POSConstants.TAX.toUpperCase(), "tax");
        this.tableModel.addColumn(Messages.getString("MenuItemExplorer.21"), "sortOrder");
        this.tableModel.addColumn(Messages.getString("MenuItemExplorer.23"), "buttonColor");
        this.tableModel.addColumn(Messages.getString("MenuItemExplorer.25"), "textColor");
        this.tableModel.addColumn(POSConstants.IMAGE.toUpperCase(), "imageData");
        List<MenuItem> findAll = MenuItemDAO.getInstance().getMenuItems();
        this.tableModel.addRows(findAll);
        this.table = new JXTable(this.tableModel);
        this.table.setAutoResizeMode(0);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new CustomCellRenderer());
        this.table.setRowHeight(60);
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        this.add((Component)this.createButtonPanel(), "South");
        this.add((Component)this.buildSearchForm(), "North");
        this.resizeColumnWidth((JTable)this.table);
        this.table.addMouseListener((MouseListener)new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    MenuItemExplorer.this.doEditSelectedMenuItem();
                }
            }
        });
    }

    private JPanel buildSearchForm() {
        JPanel panel = new JPanel();
        panel.setLayout((LayoutManager)new MigLayout("", "[][]15[][]15[][]15[]", "[]5[]"));
        JLabel lblOrderType = new JLabel(Messages.getString("MenuItemExplorer.4"));
        this.cbOrderTypes = new JComboBox();
        this.cbOrderTypes.addItem(Messages.getString("MenuItemExplorer.5"));
        this.cbOrderTypes.addItem("<None>");
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        for (OrderType orderType : orderTypes) {
            this.cbOrderTypes.addItem(orderType);
        }
        JLabel lblName = new JLabel(Messages.getString("MenuItemExplorer.0"));
        JLabel lblGroup = new JLabel(Messages.getString("MenuItemExplorer.1"));
        this.tfName = new JTextField(15);
        try {
            List<MenuGroup> menuGroupList = MenuGroupDAO.getInstance().findAll();
            this.cbGroup = new JComboBox();
            this.cbGroup.addItem(Messages.getString("MenuItemExplorer.2"));
            this.cbGroup.addItem("<None>");
            for (MenuGroup s : menuGroupList) {
                this.cbGroup.addItem(s);
            }
            JButton searchBttn = new JButton(Messages.getString("MenuItemExplorer.3"));
            panel.add((Component)lblName, "align label");
            panel.add(this.tfName);
            panel.add(lblGroup);
            panel.add(this.cbGroup);
            panel.add(lblOrderType);
            panel.add(this.cbOrderTypes);
            panel.add(searchBttn);
            Border loweredetched = BorderFactory.createEtchedBorder(1);
            TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Search");
            title.setTitleJustification(1);
            panel.setBorder(title);
            searchBttn.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    MenuItemExplorer.this.searchItem();
                }
            });
            this.tfName.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    MenuItemExplorer.this.searchItem();
                }
            });
        }
        catch (Throwable x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
        return panel;
    }

    private void searchItem() {
        Object selectedType = this.cbOrderTypes.getSelectedItem();
        String txName = this.tfName.getText();
        Object selectedGroup = this.cbGroup.getSelectedItem();
        if (!(selectedGroup instanceof MenuGroup) && selectedGroup.equals(Messages.getString("MenuItemExplorer.2"))) {
            selectedGroup = null;
        }
        if (!(selectedType instanceof OrderType) && selectedType.equals(Messages.getString("MenuItemExplorer.5"))) {
            selectedType = null;
        }
        List<MenuItem> similarItem = MenuItemDAO.getInstance().getMenuItems(txName, selectedGroup, selectedType);
        this.tableModel.removeAll();
        this.tableModel.addRows(similarItem);
    }

    private TransparentPanel createButtonPanel() {
        ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();
        JButton editButton = explorerButton.getEditButton();
        JButton addButton = explorerButton.getAddButton();
        JButton deleteButton = explorerButton.getDeleteButton();
        JButton duplicateButton = new JButton(POSConstants.DUPLICATE);
        JButton updateStockAmount = new JButton(Messages.getString("MenuItemExplorer.6"));
        JButton btnChangeMenuGroup = new JButton("Change Menu Group");
        JButton btnChangeOrderType = new JButton("Change Order Type");
        updateStockAmount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        POSMessageDialog.showMessage(MenuItemExplorer.this, Messages.getString("MenuItemExplorer.7"));
                        return;
                    }
                    MenuItem menuItem = (MenuItem)MenuItemExplorer.this.tableModel.getRow(index);
                    String amountString = JOptionPane.showInputDialog(MenuItemExplorer.this, Messages.getString("MenuItemExplorer.8"), menuItem.getStockAmount());
                    if (amountString == null || amountString.equals("")) {
                        return;
                    }
                    double stockAmount = Double.parseDouble(amountString);
                    if (stockAmount < 0.0) {
                        POSMessageDialog.showError(MenuItemExplorer.this, Messages.getString("MenuItemExplorer.10"));
                        return;
                    }
                    menuItem.setStockAmount(stockAmount);
                    MenuItemDAO.getInstance().saveOrUpdate(menuItem);
                    MenuItemExplorer.this.table.repaint();
                }
                catch (NumberFormatException e1) {
                    POSMessageDialog.showError(MenuItemExplorer.this, Messages.getString("MenuItemExplorer.11"));
                    return;
                }
                catch (Exception e2) {
                    BOMessageDialog.showError(MenuItemExplorer.this, POSConstants.ERROR_MESSAGE, e2);
                    return;
                }
            }
        });
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemExplorer.this.doEditSelectedMenuItem();
            }
        });
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Object selectedType;
                    MenuItem menuItem = new MenuItem();
                    Object group = MenuItemExplorer.this.cbGroup.getSelectedItem();
                    if (group instanceof MenuGroup) {
                        menuItem.setParent((MenuGroup)group);
                    }
                    if ((selectedType = MenuItemExplorer.this.cbOrderTypes.getSelectedItem()) instanceof OrderType) {
                        ArrayList<OrderType> types = new ArrayList<OrderType>();
                        types.add((OrderType)selectedType);
                        menuItem.setOrderTypeList(types);
                    }
                    MenuItemForm editor = new MenuItemForm(menuItem);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuItem foodItem = (MenuItem)editor.getBean();
                    MenuItemExplorer.this.tableModel.addRow(foodItem);
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        duplicateButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuItemExplorer.this.table.convertRowIndexToModel(index);
                    MenuItem existingItem = (MenuItem)MenuItemExplorer.this.tableModel.getRow(index);
                    existingItem = MenuItemDAO.getInstance().initialize(existingItem);
                    MenuItem newMenuItem = new MenuItem();
                    PropertyUtils.copyProperties((Object)newMenuItem, (Object)existingItem);
                    newMenuItem.setId(null);
                    String newName = MenuItemExplorer.this.doDuplicateName(existingItem);
                    newMenuItem.setName(newName);
                    newMenuItem.setFractionalUnit(existingItem.isFractionalUnit());
                    newMenuItem.setDisableWhenStockAmountIsZero(existingItem.isDisableWhenStockAmountIsZero());
                    newMenuItem.setShowImageOnly(existingItem.isShowImageOnly());
                    MenuItemForm editor = new MenuItemForm(newMenuItem);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuItem foodItem = (MenuItem)editor.getBean();
                    MenuItemExplorer.this.tableModel.addRow(foodItem);
                    MenuItemExplorer.this.table.getSelectionModel().addSelectionInterval(MenuItemExplorer.this.tableModel.getRowCount() - 1, MenuItemExplorer.this.tableModel.getRowCount() - 1);
                    MenuItemExplorer.this.table.scrollRowToVisible(MenuItemExplorer.this.tableModel.getRowCount() - 1);
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        btnChangeMenuGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int[] rows = MenuItemExplorer.this.table.getSelectedRows();
                    if (rows.length < 1) {
                        return;
                    }
                    MenuGroup group = MenuItemExplorer.this.getSelectedMenuGroup(null);
                    if (group == null) {
                        return;
                    }
                    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
                    for (int i = 0; i < rows.length; ++i) {
                        int index = MenuItemExplorer.this.table.convertRowIndexToModel(rows[i]);
                        MenuItem menuItem = (MenuItem)MenuItemExplorer.this.tableModel.getRow(index);
                        menuItem.setParent(group);
                        menuItems.add(menuItem);
                    }
                    MenuItemDAO.getInstance().saveAll(menuItems);
                    MenuItemExplorer.this.searchItem();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        btnChangeOrderType.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int[] rows = MenuItemExplorer.this.table.getSelectedRows();
                    if (rows.length < 1) {
                        return;
                    }
                    List<OrderType> orderTypes = MenuItemExplorer.this.getSelectedOrderTypes(new ArrayList());
                    if (orderTypes == null) {
                        return;
                    }
                    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
                    for (int i = 0; i < rows.length; ++i) {
                        int index = MenuItemExplorer.this.table.convertRowIndexToModel(rows[i]);
                        MenuItem menuItem = (MenuItem)MenuItemExplorer.this.tableModel.getRow(index);
                        menuItem.setOrderTypeList(orderTypes);
                        menuItems.add(menuItem);
                    }
                    MenuItemDAO.getInstance().saveAll(menuItems);
                    MenuItemExplorer.this.searchItem();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuItemExplorer.this.table.convertRowIndexToModel(index);
                    if (POSMessageDialog.showYesNoQuestionDialog(MenuItemExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    MenuItem item = (MenuItem)MenuItemExplorer.this.tableModel.getRow(index);
                    MenuItemDAO foodItemDAO = new MenuItemDAO();
                    if (item.getDiscounts() != null && item.getDiscounts().size() > 0) {
                        foodItemDAO.releaseParentAndDelete(item);
                    } else {
                        foodItemDAO.delete(item);
                    }
                    MenuItemExplorer.this.tableModel.removeRow(index);
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(updateStockAmount);
        panel.add(deleteButton);
        panel.add(duplicateButton);
        panel.add(btnChangeMenuGroup);
        panel.add(btnChangeOrderType);
        return panel;
    }

    private void doEditSelectedMenuItem() {
        try {
            int index = this.table.getSelectedRow();
            if (index < 0) {
                return;
            }
            index = this.table.convertRowIndexToModel(index);
            MenuItem menuItem = this.tableModel.getRow(index);
            menuItem = MenuItemDAO.getInstance().initialize(menuItem);
            this.tableModel.setRow(index, menuItem);
            MenuItemForm editor = new MenuItemForm(menuItem);
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            this.table.repaint();
        }
        catch (Throwable x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    protected MenuGroup getSelectedMenuGroup(MenuGroup defaultValue) {
        List<MenuGroup> menuGroups = MenuGroupDAO.getInstance().findAll();
        ComboItemSelectionDialog dialog = new ComboItemSelectionDialog("SELECT GROUP", "Menu Group", menuGroups, false);
        dialog.setSelectedItem(defaultValue);
        dialog.setVisibleNewButton(true);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return null;
        }
        if (dialog.isNewItem()) {
            MenuGroup foodGroup = new MenuGroup();
            MenuGroupForm editor = new MenuGroupForm(foodGroup);
            BeanEditorDialog editorDialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            editorDialog.open();
            if (editorDialog.isCanceled()) {
                return null;
            }
            return this.getSelectedMenuGroup(foodGroup);
        }
        return (MenuGroup)dialog.getSelectedItem();
    }

    protected List<OrderType> getSelectedOrderTypes(List defaultValues) {
        List<OrderType> orderTypes = OrderTypeDAO.getInstance().findAll();
        ComboItemSelectionDialog dialog = new ComboItemSelectionDialog("SELECT ORDER TYPES", "Order Type", orderTypes, true);
        dialog.setSelectedItems(defaultValues);
        dialog.setVisibleNewButton(true);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return null;
        }
        if (dialog.isNewItem()) {
            OrderType orderType = new OrderType();
            try {
                OrderTypeForm editor = new OrderTypeForm();
                editor.setBean(orderType);
                BeanEditorDialog editorDialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                editorDialog.open();
                if (editorDialog.isCanceled()) {
                    return null;
                }
                defaultValues.add(orderType);
            }
            catch (Exception exception) {
                // empty catch block
            }
            return this.getSelectedOrderTypes(defaultValues);
        }
        return dialog.getSelectedItems();
    }

    public void resizeColumnWidth(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); ++column) {
            columnModel.getColumn(column).setPreferredWidth((Integer)this.getColumnWidth().get(column));
        }
    }

    private List getColumnWidth() {
        ArrayList<Integer> columnWidth = new ArrayList<Integer>();
        columnWidth.add(50);
        columnWidth.add(200);
        columnWidth.add(200);
        columnWidth.add(70);
        columnWidth.add(50);
        columnWidth.add(50);
        columnWidth.add(140);
        columnWidth.add(70);
        columnWidth.add(70);
        columnWidth.add(100);
        columnWidth.add(100);
        columnWidth.add(200);
        return columnWidth;
    }

    private String doDuplicateName(MenuItem existingItem) {
        String existingName = existingItem.getName();
        String newName = new String();
        int lastIndexOf = existingName.lastIndexOf(" ");
        if (lastIndexOf == -1) {
            newName = existingName + " 1";
        } else {
            String processName = existingName.substring(lastIndexOf + 1, existingName.length());
            if (StringUtils.isNumeric((CharSequence)processName)) {
                Integer count = Integer.valueOf(processName);
                count = count + 1;
                newName = existingName.replace(processName, String.valueOf(count));
            } else {
                newName = existingName + " 1";
            }
        }
        return newName;
    }
}


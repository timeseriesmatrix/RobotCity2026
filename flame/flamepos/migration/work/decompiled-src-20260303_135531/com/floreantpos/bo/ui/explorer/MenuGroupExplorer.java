/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ComboItemSelectionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuCategoryForm;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class MenuGroupExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<MenuGroup> tableModel = new BeanTableModel(MenuGroup.class);

    public MenuGroupExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn(POSConstants.TRANSLATED_NAME.toUpperCase(), "translatedName");
        this.tableModel.addColumn(POSConstants.VISIBLE.toUpperCase(), "visible");
        this.tableModel.addColumn(POSConstants.MENU_CATEGORY.toUpperCase(), "parent");
        this.tableModel.addColumn(POSConstants.SORT_ORDER.toUpperCase(), "sortOrder");
        this.tableModel.addColumn(POSConstants.BUTTON_COLOR.toUpperCase(), "buttonColor");
        this.tableModel.addColumn(POSConstants.TEXT_COLOR.toUpperCase(), "textColor");
        this.tableModel.addRows(MenuGroupDAO.getInstance().findAll());
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new CustomCellRenderer());
        this.table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Color) {
                    JLabel lblColor = new JLabel(Messages.getString("MenuGroupExplorer.1"), 0);
                    lblColor.setForeground((Color)value);
                    return lblColor;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        this.createButtonPanel();
    }

    private void createButtonPanel() {
        ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();
        JButton editButton = explorerButton.getEditButton();
        JButton addButton = explorerButton.getAddButton();
        JButton deleteButton = explorerButton.getDeleteButton();
        JButton btnChangeCategory = new JButton("Change Menu Category");
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuGroupExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuGroupExplorer.this.table.convertRowIndexToModel(index);
                    MenuGroup menuGroup = (MenuGroup)MenuGroupExplorer.this.tableModel.getRow(index);
                    MenuGroupForm editor = new MenuGroupForm(menuGroup);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuGroupExplorer.this.table.repaint();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MenuGroupForm editor = new MenuGroupForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuGroup foodGroup = (MenuGroup)editor.getBean();
                    MenuGroupExplorer.this.tableModel.addRow(foodGroup);
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuGroupExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuGroupExplorer.this.table.convertRowIndexToModel(index);
                    MenuGroup group = (MenuGroup)MenuGroupExplorer.this.tableModel.getRow(index);
                    if (POSMessageDialog.showYesNoQuestionDialog(MenuGroupExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    MenuItemDAO menuItemDao = new MenuItemDAO();
                    List<MenuItem> menuItems = menuItemDao.findByParent(null, group, true);
                    if (menuItems.size() > 0) {
                        if (POSMessageDialog.showYesNoQuestionDialog(MenuGroupExplorer.this, Messages.getString("MenuGroupExplorer.0"), POSConstants.DELETE) != 0) {
                            return;
                        }
                        menuItemDao.releaseParent(menuItems);
                    }
                    MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
                    foodGroupDAO.delete(group);
                    MenuGroupExplorer.this.tableModel.removeRow(index);
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        btnChangeCategory.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int[] rows = MenuGroupExplorer.this.table.getSelectedRows();
                    if (rows.length < 1) {
                        return;
                    }
                    MenuCategory category = MenuGroupExplorer.this.getSelectedMenuCategory(null);
                    if (category == null) {
                        return;
                    }
                    ArrayList<MenuGroup> menuGroups = new ArrayList<MenuGroup>();
                    for (int i = 0; i < rows.length; ++i) {
                        int index = MenuGroupExplorer.this.table.convertRowIndexToModel(rows[i]);
                        MenuGroup menuGroup = (MenuGroup)MenuGroupExplorer.this.tableModel.getRow(index);
                        menuGroup.setParent(category);
                        menuGroups.add(menuGroup);
                    }
                    MenuGroupDAO.getInstance().saveAll(menuGroups);
                    MenuGroupExplorer.this.tableModel.fireTableDataChanged();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(btnChangeCategory);
        this.add((Component)panel, "South");
    }

    protected MenuCategory getSelectedMenuCategory(MenuCategory defaultValue) {
        List<MenuCategory> menuCategorys = MenuCategoryDAO.getInstance().findAll();
        ComboItemSelectionDialog dialog = new ComboItemSelectionDialog("SELECT GROUP", "Menu Category", menuCategorys, false);
        dialog.setSelectedItem(defaultValue);
        dialog.setVisibleNewButton(true);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return null;
        }
        if (dialog.isNewItem()) {
            MenuCategory foodCategory = new MenuCategory();
            try {
                MenuCategoryForm editor = new MenuCategoryForm(foodCategory);
                BeanEditorDialog editorDialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                editorDialog.open();
                if (editorDialog.isCanceled()) {
                    return null;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return this.getSelectedMenuCategory(foodCategory);
        }
        return (MenuCategory)dialog.getSelectedItem();
    }
}


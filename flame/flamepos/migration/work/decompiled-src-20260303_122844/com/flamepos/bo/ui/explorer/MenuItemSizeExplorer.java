/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.MenuItemSizeForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class MenuItemSizeExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<MenuItemSize> tableModel = new BeanTableModel(MenuItemSize.class);
    private List<MenuItemSize> menuItemSizeList;

    public MenuItemSizeExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn("TRANSLATED NAME", "translatedName");
        this.tableModel.addColumn("DESCRIPTION", "description");
        this.tableModel.addColumn("SIZE (in Inch)", "sizeInInch");
        this.tableModel.addColumn("SORT", "sortOrder");
        this.tableModel.addColumn("DEFAULT", "defaultSize");
        this.menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
        this.tableModel.addRows(this.menuItemSizeList);
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MenuItemSizeForm editor = new MenuItemSizeForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuItemSize foodCategory = (MenuItemSize)editor.getBean();
                    MenuItemSizeExplorer.this.tableModel.addRow(foodCategory);
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton editButton = new JButton(POSConstants.EDIT);
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemSizeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuItemSizeExplorer.this.table.convertRowIndexToModel(index);
                    MenuItemSize menuItemSize = (MenuItemSize)MenuItemSizeExplorer.this.tableModel.getRow(index);
                    MenuItemSizeForm editor = new MenuItemSizeForm(menuItemSize);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuItemSizeExplorer.this.table.repaint();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton deleteButton = new JButton(POSConstants.DELETE);
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemSizeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(MenuItemSizeExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        MenuItemSize menuItemSize = (MenuItemSize)MenuItemSizeExplorer.this.tableModel.getRow(index);
                        MenuItemSizeDAO dao = new MenuItemSizeDAO();
                        dao.delete(menuItemSize);
                        MenuItemSizeExplorer.this.tableModel.removeRow(index);
                    }
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton defaultButton = new JButton("Set Default");
        defaultButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MenuItemSizeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuItemSizeExplorer.this.table.convertRowIndexToModel(index);
                    MenuItemSize menuItemSize = (MenuItemSize)MenuItemSizeExplorer.this.tableModel.getRow(index);
                    for (MenuItemSize item : MenuItemSizeExplorer.this.menuItemSizeList) {
                        if (menuItemSize.getId() == item.getId()) {
                            item.setDefaultSize(true);
                            continue;
                        }
                        item.setDefaultSize(false);
                    }
                    MenuItemSizeDAO dao = new MenuItemSizeDAO();
                    dao.setDefault(MenuItemSizeExplorer.this.menuItemSizeList);
                    MenuItemSizeExplorer.this.tableModel.fireTableDataChanged();
                    MenuItemSizeExplorer.this.table.revalidate();
                    MenuItemSizeExplorer.this.table.repaint();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(defaultButton);
        this.add((Component)panel, "South");
    }
}


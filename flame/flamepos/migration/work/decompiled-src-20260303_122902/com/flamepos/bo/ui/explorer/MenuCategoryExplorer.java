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
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuCategoryForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class MenuCategoryExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<MenuCategory> tableModel = new BeanTableModel(MenuCategory.class);

    public MenuCategoryExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn(POSConstants.TRANSLATED_NAME.toUpperCase(), "translatedName");
        this.tableModel.addColumn(POSConstants.BEVERAGE.toUpperCase(), "beverage");
        this.tableModel.addColumn(POSConstants.VISIBLE.toUpperCase(), "visible");
        this.tableModel.addColumn(POSConstants.SORT_ORDER.toUpperCase(), "sortOrder");
        this.tableModel.addColumn(POSConstants.BUTTON_COLOR.toUpperCase(), "buttonColor");
        this.tableModel.addColumn(POSConstants.TEXT_COLOR.toUpperCase(), "textColor");
        this.tableModel.addRows(MenuCategoryDAO.getInstance().findAll());
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new CustomCellRenderer());
        this.table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Color) {
                    JLabel lblColor = new JLabel(Messages.getString("MenuCategoryExplorer.1"), 0);
                    lblColor.setForeground((Color)value);
                    return lblColor;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        this.addButtonPanel();
    }

    private void addButtonPanel() {
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MenuCategoryForm editor = new MenuCategoryForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuCategory foodCategory = (MenuCategory)editor.getBean();
                    MenuCategoryExplorer.this.tableModel.addRow(foodCategory);
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
                    int index = MenuCategoryExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuCategoryExplorer.this.table.convertRowIndexToModel(index);
                    MenuCategory category = (MenuCategory)MenuCategoryExplorer.this.tableModel.getRow(index);
                    MenuCategoryForm editor = new MenuCategoryForm(category);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuCategoryExplorer.this.table.repaint();
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
                    int index = MenuCategoryExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = MenuCategoryExplorer.this.table.convertRowIndexToModel(index);
                    MenuCategory category = (MenuCategory)MenuCategoryExplorer.this.tableModel.getRow(index);
                    if (POSMessageDialog.showYesNoQuestionDialog(MenuCategoryExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    MenuGroupDAO menuGroupDao = new MenuGroupDAO();
                    List<MenuGroup> menuGroups = menuGroupDao.findByParent(category);
                    if (menuGroups.size() > 0) {
                        if (POSMessageDialog.showYesNoQuestionDialog(MenuCategoryExplorer.this, Messages.getString("MenuCategoryExplorer.0"), POSConstants.DELETE) != 0) {
                            return;
                        }
                        menuGroupDao.releaseParent(menuGroups);
                    }
                    MenuCategoryDAO dao = new MenuCategoryDAO();
                    dao.delete(category);
                    MenuCategoryExplorer.this.tableModel.removeRow(index);
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
        this.add((Component)panel, "South");
    }
}


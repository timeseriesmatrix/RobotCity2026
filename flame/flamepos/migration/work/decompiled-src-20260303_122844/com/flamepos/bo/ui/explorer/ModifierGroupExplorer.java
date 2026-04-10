/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.ModifierGroupDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.MenuModifierGroupForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;

public class ModifierGroupExplorer
extends TransparentPanel {
    private List<MenuModifierGroup> mGroupList;
    private JXTable table;
    private ModifierGroupExplorerTableModel tableModel;

    public ModifierGroupExplorer() {
        ModifierGroupDAO dao = new ModifierGroupDAO();
        this.mGroupList = dao.findAll();
        this.tableModel = new ModifierGroupExplorerTableModel();
        this.table = new JXTable((TableModel)this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        TransparentPanel panel = new TransparentPanel();
        ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();
        JButton editButton = explorerButton.getEditButton();
        JButton addButton = explorerButton.getAddButton();
        JButton deleteButton = explorerButton.getDeleteButton();
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = ModifierGroupExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = ModifierGroupExplorer.this.table.convertRowIndexToModel(index);
                    MenuModifierGroup category = (MenuModifierGroup)ModifierGroupExplorer.this.mGroupList.get(index);
                    MenuModifierGroupForm editor = new MenuModifierGroupForm(category);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    ModifierGroupExplorer.this.table.repaint();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MenuModifierGroupForm editor = new MenuModifierGroupForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuModifierGroup modifierGroup = (MenuModifierGroup)editor.getBean();
                    ModifierGroupExplorer.this.tableModel.addModifierGroup(modifierGroup);
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
                    int index = ModifierGroupExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = ModifierGroupExplorer.this.table.convertRowIndexToModel(index);
                    if (ConfirmDeleteDialog.showMessage(ModifierGroupExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 1) {
                        MenuModifierGroup category = (MenuModifierGroup)ModifierGroupExplorer.this.mGroupList.get(index);
                        ModifierGroupDAO modifierCategoryDAO = new ModifierGroupDAO();
                        modifierCategoryDAO.delete(category);
                        ModifierGroupExplorer.this.tableModel.deleteModifierGroup(category, index);
                    }
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        this.add((Component)panel, "South");
    }

    class ModifierGroupExplorerTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.ID, POSConstants.NAME, POSConstants.TRANSLATED_NAME};

        ModifierGroupExplorerTableModel() {
        }

        @Override
        public int getRowCount() {
            if (ModifierGroupExplorer.this.mGroupList == null) {
                return 0;
            }
            return ModifierGroupExplorer.this.mGroupList.size();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (ModifierGroupExplorer.this.mGroupList == null) {
                return "";
            }
            MenuModifierGroup mgroup = (MenuModifierGroup)ModifierGroupExplorer.this.mGroupList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(mgroup.getId());
                }
                case 1: {
                    return mgroup.getName();
                }
                case 2: {
                    return mgroup.getTranslatedName();
                }
            }
            return null;
        }

        public void addModifierGroup(MenuModifierGroup category) {
            int size = ModifierGroupExplorer.this.mGroupList.size();
            ModifierGroupExplorer.this.mGroupList.add(category);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteModifierGroup(MenuModifierGroup category, int index) {
            ModifierGroupExplorer.this.mGroupList.remove(category);
            this.fireTableRowsDeleted(index, index);
        }
    }
}


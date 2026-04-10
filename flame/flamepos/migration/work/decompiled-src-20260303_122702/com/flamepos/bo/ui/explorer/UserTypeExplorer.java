/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.forms.UserTypeForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class UserTypeExplorer
extends TransparentPanel {
    private List<UserType> typeList;
    private JTable table;
    private UserTypeExplorerTableModel tableModel;

    public UserTypeExplorer() {
        UserTypeDAO dao = new UserTypeDAO();
        this.typeList = dao.findAll();
        this.tableModel = new UserTypeExplorerTableModel();
        this.table = new JTable(this.tableModel);
        this.table.setRowHeight(PosUIManager.getSize(20));
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UserTypeForm editor = new UserTypeForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.setPreferredSize(PosUIManager.getSize(500, 450));
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    UserType type = (UserType)editor.getBean();
                    UserTypeExplorer.this.tableModel.addType(type);
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
                    int index = UserTypeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    UserType type = (UserType)UserTypeExplorer.this.typeList.get(index);
                    UserTypeForm editor = new UserTypeForm();
                    editor.setUserType(type);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.setPreferredSize(PosUIManager.getSize(500, 450));
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    UserTypeExplorer.this.table.repaint();
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
                    int index = UserTypeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(UserTypeExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        UserType category = (UserType)UserTypeExplorer.this.typeList.get(index);
                        UserTypeDAO dao = new UserTypeDAO();
                        dao.delete(category);
                        UserTypeExplorer.this.tableModel.deleteCategory(category, index);
                    }
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

    class UserTypeExplorerTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.TYPE_NAME, POSConstants.PERMISSIONS};

        UserTypeExplorerTableModel() {
        }

        @Override
        public int getRowCount() {
            if (UserTypeExplorer.this.typeList == null) {
                return 0;
            }
            return UserTypeExplorer.this.typeList.size();
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
            if (UserTypeExplorer.this.typeList == null) {
                return "";
            }
            UserType userType = (UserType)UserTypeExplorer.this.typeList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return userType.getName();
                }
                case 1: {
                    return userType.getPermissions();
                }
            }
            return null;
        }

        public void addType(UserType type) {
            int size = UserTypeExplorer.this.typeList.size();
            UserTypeExplorer.this.typeList.add(type);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteCategory(UserType type, int index) {
            UserTypeExplorer.this.typeList.remove(type);
            this.fireTableRowsDeleted(index, index);
        }
    }
}


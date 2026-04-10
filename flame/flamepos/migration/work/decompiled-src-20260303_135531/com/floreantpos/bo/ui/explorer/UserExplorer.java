/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.exception.ConstraintViolationException
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.forms.UserForm;
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
import org.hibernate.exception.ConstraintViolationException;

public class UserExplorer
extends TransparentPanel {
    private JTable table;
    private UserTableModel tableModel;

    public UserExplorer() {
        List<User> users = UserDAO.getInstance().findAll();
        this.tableModel = new UserTableModel(users);
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer userWithMaxId = UserDAO.getInstance().findUserWithMaxId();
                    UserForm editor = new UserForm();
                    if (userWithMaxId != null) {
                        editor.setId(new Integer(userWithMaxId + 1));
                    }
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    User user = (User)editor.getBean();
                    UserExplorer.this.tableModel.addItem(user);
                }
                catch (Exception x) {
                    PosLog.error(this.getClass(), x);
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton copyButton = new JButton(POSConstants.COPY);
        copyButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = UserExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    User user = (User)UserExplorer.this.tableModel.getRowData(index);
                    User user2 = new User();
                    user2.setUserId(user.getUserId());
                    user2.setType(user.getType());
                    user2.setFirstName(user.getFirstName());
                    user2.setLastName(user.getLastName());
                    user2.setPassword(user.getPassword());
                    user2.setSsn(user.getSsn());
                    UserForm editor = new UserForm();
                    editor.setEditMode(false);
                    editor.setBean(user2);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    User newUser = (User)editor.getBean();
                    UserExplorer.this.tableModel.addItem(newUser);
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
                    int index = UserExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    User user = (User)UserExplorer.this.tableModel.getRowData(index);
                    UserForm editor = new UserForm();
                    editor.setEditMode(true);
                    editor.setBean(user);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    UserExplorer.this.tableModel.updateItem(index);
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
                int index = UserExplorer.this.table.getSelectedRow();
                if (index < 0) {
                    return;
                }
                User user = (User)UserExplorer.this.tableModel.getRowData(index);
                if (user == null) {
                    return;
                }
                try {
                    if (ConfirmDeleteDialog.showMessage(POSUtil.getBackOfficeWindow(), POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        UserDAO.getInstance().delete(user);
                        UserExplorer.this.tableModel.deleteItem(index);
                    }
                }
                catch (ConstraintViolationException x) {
                    String message = POSConstants.USER + " " + user.getFirstName() + " " + user.getLastName() + " (" + user.getType() + ") " + POSConstants.ERROR_MESSAGE;
                    BOMessageDialog.showError(message, x);
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(copyButton);
        panel.add(editButton);
        panel.add(deleteButton);
        this.add((Component)panel, "South");
    }

    class UserTableModel
    extends ListTableModel {
        UserTableModel(List list) {
            super(new String[]{POSConstants.ID, POSConstants.FIRST_NAME, POSConstants.LAST_NAME, POSConstants.TYPE}, list);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User user = (User)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(user.getUserId());
                }
                case 1: {
                    return user.getFirstName();
                }
                case 2: {
                    return user.getLastName();
                }
                case 3: {
                    return user.getType();
                }
            }
            return null;
        }
    }
}


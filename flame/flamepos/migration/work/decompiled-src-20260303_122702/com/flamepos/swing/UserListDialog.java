/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class UserListDialog
extends OkCancelOptionDialog {
    BeanTableModel<User> tableModel;
    JTable userListTable;
    private User selectedUser;

    public UserListDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.setTitle(Messages.getString("UserListDialog.0"));
        this.setTitlePaneText(Messages.getString("UserListDialog.0"));
        JPanel contentPane = this.getContentPanel();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.tableModel = new BeanTableModel(User.class);
        this.tableModel.addColumn("Name", "fullName");
        this.userListTable = new JTable(this.tableModel);
        this.userListTable.setRowHeight(PosUIManager.getSize(60));
        this.userListTable.getSelectionModel().setSelectionMode(0);
        contentPane.add(new JScrollPane(this.userListTable));
        List<User> userList = UserDAO.getInstance().findAll();
        this.tableModel.addRows(userList);
        if (userList != null && !userList.isEmpty()) {
            this.userListTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    public User getSelectedUser() {
        return this.selectedUser;
    }

    @Override
    public void doOk() {
        User user = this.tableModel.getRows().get(this.userListTable.getSelectedRow());
        if (user == null) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("UserListDialog.4"));
            return;
        }
        if (!user.isClockedIn().booleanValue()) {
            POSMessageDialog.showError("Can't assign drawer. Selected user is not clocked in.");
            return;
        }
        this.selectedUser = user;
        this.setCanceled(false);
        this.dispose();
    }
}


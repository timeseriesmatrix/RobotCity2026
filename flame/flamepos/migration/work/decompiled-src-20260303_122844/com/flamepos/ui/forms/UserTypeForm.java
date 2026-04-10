/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.forms;

import com.floreantpos.POSConstants;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.apache.commons.lang.StringUtils;

public class UserTypeForm
extends BeanEditor {
    private UserType userType;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private CheckBoxList<UserPermission> listPermissions;
    private JTextField tfTypeName;

    public UserTypeForm() {
        this((UserType)null);
    }

    public UserTypeForm(UserType type) {
        this.userType = type;
        this.initComponents();
        this.listPermissions.setModel((UserPermission[])UserPermission.permissions);
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.tfTypeName = new JTextField();
        this.jLabel2 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.listPermissions = new CheckBoxList();
        this.jLabel1.setText(POSConstants.TYPE_NAME + ":");
        this.jLabel2.setText(POSConstants.PERMISSIONS + ":");
        this.jScrollPane1.setViewportView(this.listPermissions);
        this.jScrollPane1.getVerticalScrollBar().setValue(10);
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.headerPanel = new JPanel(new BorderLayout(5, 5));
        this.headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.headerPanel.add((Component)this.jLabel1, "West");
        this.headerPanel.add((Component)this.tfTypeName, "Center");
        this.centerPanel = new JPanel(new BorderLayout(5, 5));
        this.centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.centerPanel.add((Component)this.jLabel2, "West");
        this.centerPanel.add((Component)this.jScrollPane1, "Center");
        this.add((Component)this.headerPanel, "North");
        this.add((Component)this.centerPanel, "Center");
    }

    @Override
    public String getDisplayText() {
        if (this.userType == null) {
            return "New user type";
        }
        return "Edit user type";
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
        }
        catch (IllegalModelStateException e) {
            POSMessageDialog.showError(e.getMessage());
            return false;
        }
        UserTypeDAO dao = new UserTypeDAO();
        dao.saveOrUpdate(this.userType);
        return true;
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        String name;
        if (this.userType == null) {
            this.userType = new UserType();
        }
        if (StringUtils.isEmpty((String)(name = this.tfTypeName.getText()))) {
            throw new IllegalModelStateException(POSConstants.TYPE_NAME_CANNOT_BE_EMPTY);
        }
        this.userType.setName(name);
        this.userType.clearPermissions();
        List<UserPermission> checkedValues = this.listPermissions.getCheckedValues();
        for (int i = 0; i < checkedValues.size(); ++i) {
            this.userType.addTopermissions(checkedValues.get(i));
        }
        this.setBean(this.userType);
        return true;
    }

    @Override
    protected void updateView() {
        if (this.userType == null) {
            this.listPermissions.clearSelection();
            return;
        }
        this.tfTypeName.setText(this.userType.getName());
        Set<UserPermission> permissions = this.userType.getPermissions();
        if (permissions == null) {
            this.listPermissions.clearSelection();
            return;
        }
        CheckBoxList.CheckBoxListModel model = (CheckBoxList.CheckBoxListModel)this.listPermissions.getModel();
        for (UserPermission permission : permissions) {
            for (int i = 0; i < model.getItems().size(); ++i) {
                CheckBoxList.Entry entry = model.getItems().get(i);
                if (!entry.getValue().equals(permission)) continue;
                entry.setChecked(true);
            }
        }
        model.fireTableRowsUpdated(0, model.getRowCount());
    }

    public UserType getUserType() {
        return this.userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
        this.setBean(userType);
        this.updateView();
    }
}


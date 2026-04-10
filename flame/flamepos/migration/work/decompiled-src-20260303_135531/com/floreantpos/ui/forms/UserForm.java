/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.forms;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import net.miginfocom.swing.MigLayout;

public class UserForm
extends BeanEditor {
    private JComboBox cbUserType;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel9;
    private DoubleTextField tfCostPerHour;
    private FixedLengthTextField tfFirstName;
    private FixedLengthTextField tfId;
    private FixedLengthTextField tfLastName;
    private JPasswordField tfPassword1;
    private JPasswordField tfPassword2;
    private FixedLengthTextField tfSsn;
    private boolean editMode;
    private JLabel lblPhone;
    private FixedLengthTextField tfPhone;
    private JCheckBox chkDriver;

    public UserForm() {
        this.initComponents();
        UserTypeDAO dao = new UserTypeDAO();
        List<UserType> userTypes = dao.findAll();
        this.cbUserType.setModel(new DefaultComboBoxModel<Object>(userTypes.toArray()));
        this.chkDriver = new JCheckBox(Messages.getString("UserForm.0"));
        this.add((Component)this.chkDriver, "cell 1 9");
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel9 = new JLabel();
        this.jLabel10 = new JLabel();
        this.tfPassword1 = new JPasswordField(new FixedLengthDocument(16), "", 10);
        this.tfPassword2 = new JPasswordField(new FixedLengthDocument(16), "", 10);
        this.tfId = new FixedLengthTextField();
        this.tfSsn = new FixedLengthTextField();
        this.tfSsn.setLength(30);
        this.tfSsn.setColumns(30);
        this.tfFirstName = new FixedLengthTextField();
        this.tfFirstName.setColumns(30);
        this.tfFirstName.setLength(30);
        this.tfLastName = new FixedLengthTextField();
        this.tfLastName.setLength(30);
        this.tfLastName.setColumns(30);
        this.jLabel5 = new JLabel();
        this.tfCostPerHour = new DoubleTextField();
        this.jLabel6 = new JLabel();
        this.cbUserType = new JComboBox();
        this.setLayout((LayoutManager)new MigLayout("", "[134px][204px,grow]", "[19px][][19px][19px][19px][19px][19px][19px][24px][]"));
        this.jLabel1.setText(Messages.getString("UserForm.7"));
        this.add((Component)this.jLabel1, "cell 0 0,alignx trailing,aligny center");
        this.lblPhone = new JLabel(Messages.getString("UserForm.9"));
        this.add((Component)this.lblPhone, "cell 0 1,alignx trailing");
        this.tfPhone = new FixedLengthTextField();
        this.tfPhone.setLength(20);
        this.tfPhone.setColumns(20);
        this.add((Component)this.tfPhone, "cell 1 1,growx");
        this.jLabel2.setText("SSN");
        this.add((Component)this.jLabel2, "cell 0 2,alignx trailing,aligny center");
        this.jLabel3.setText(Messages.getString("UserForm.14"));
        this.add((Component)this.jLabel3, "cell 0 3,alignx trailing,aligny center");
        this.jLabel4.setText(Messages.getString("UserForm.16"));
        this.add((Component)this.jLabel4, "cell 0 4,alignx trailing,aligny center");
        this.jLabel9.setText(Messages.getString("UserForm.18"));
        this.add((Component)this.jLabel9, "cell 0 5,alignx trailing,aligny center");
        this.jLabel10.setText(Messages.getString("UserForm.20"));
        this.add((Component)this.jLabel10, "cell 0 6,alignx trailing,aligny center");
        this.add((Component)this.tfPassword1, "cell 1 5,growx,aligny center");
        this.add((Component)this.tfPassword2, "cell 1 6,growx,aligny center");
        this.add((Component)this.tfId, "cell 1 0,growx,aligny center");
        this.add((Component)this.tfSsn, "cell 1 2,aligny center");
        this.add((Component)this.tfFirstName, "cell 1 3,growx,aligny center");
        this.add((Component)this.tfLastName, "cell 1 4,growx,aligny center");
        this.jLabel5.setText(Messages.getString("UserForm.28"));
        this.add((Component)this.jLabel5, "cell 0 7,alignx trailing,aligny center");
        this.add((Component)this.tfCostPerHour, "cell 1 7,growx,aligny center");
        this.jLabel6.setText(Messages.getString("UserForm.31"));
        this.add((Component)this.jLabel6, "cell 0 8,alignx trailing,aligny center");
        this.cbUserType.setModel(new DefaultComboBoxModel<String>(new String[]{Messages.getString("UserForm.33"), Messages.getString("UserForm.34"), Messages.getString("UserForm.35")}));
        this.add((Component)this.cbUserType, "cell 1 8,growx,aligny center");
    }

    @Override
    public String getDisplayText() {
        if (this.isEditMode()) {
            return Messages.getString("UserForm.37");
        }
        return Messages.getString("UserForm.38");
    }

    @Override
    public boolean save() {
        try {
            this.updateModel();
        }
        catch (IllegalModelStateException e) {
            POSMessageDialog.showError(this, e.getMessage());
            return false;
        }
        User user = (User)this.getBean();
        UserDAO userDAO = UserDAO.getInstance();
        if (!this.editMode && userDAO.isUserExist(user.getUserId())) {
            POSMessageDialog.showError(this, Messages.getString("UserForm.39") + user.getUserId() + " " + Messages.getString("UserForm.1"));
            return false;
        }
        try {
            userDAO.saveOrUpdate(user, this.editMode);
        }
        catch (PosException x) {
            POSMessageDialog.showError(this, x.getMessage(), x);
            PosLog.error(this.getClass(), x);
            return false;
        }
        catch (Exception x) {
            POSMessageDialog.showError(this, Messages.getString("UserForm.41"), x);
            PosLog.error(this.getClass(), x);
            return false;
        }
        return true;
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        User userBySecretKey;
        User user = null;
        user = !(this.getBean() instanceof User) ? new User() : (User)this.getBean();
        int id = 1000;
        try {
            id = Integer.parseInt(this.tfId.getText());
        }
        catch (Exception x) {
            throw new IllegalModelStateException(Messages.getString("UserForm.42"));
        }
        String ssn = this.tfSsn.getText();
        String firstName = this.tfFirstName.getText();
        String lastName = this.tfLastName.getText();
        String secretKey1 = new String(this.tfPassword1.getPassword());
        String secretKey2 = new String(this.tfPassword2.getPassword());
        if (POSUtil.isBlankOrNull(firstName)) {
            throw new IllegalModelStateException(Messages.getString("UserForm.43"));
        }
        if (POSUtil.isBlankOrNull(lastName)) {
            throw new IllegalModelStateException(Messages.getString("UserForm.44"));
        }
        if (POSUtil.isBlankOrNull(secretKey1)) {
            throw new IllegalModelStateException(Messages.getString("UserForm.45"));
        }
        if (POSUtil.isBlankOrNull(secretKey2)) {
            throw new IllegalModelStateException(Messages.getString("UserForm.46"));
        }
        if (!secretKey1.equals(secretKey2)) {
            throw new IllegalModelStateException(Messages.getString("UserForm.47"));
        }
        if (!this.isEditMode() && (userBySecretKey = UserDAO.getInstance().findUserBySecretKey(secretKey1)) != null) {
            throw new IllegalModelStateException(Messages.getString("UserForm.48"));
        }
        double cost = 0.0;
        try {
            cost = Double.parseDouble(this.tfCostPerHour.getText());
        }
        catch (Exception x) {
            throw new IllegalModelStateException(Messages.getString("UserForm.49") + firstName + " " + lastName + " " + Messages.getString("UserForm.2"));
        }
        user.setType((UserType)this.cbUserType.getSelectedItem());
        user.setCostPerHour(cost);
        user.setSsn(ssn);
        user.setUserId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNo(this.tfPhone.getText());
        user.setPassword(secretKey1);
        user.setDriver(this.chkDriver.isSelected());
        this.setBean(user);
        return true;
    }

    @Override
    protected void updateView() {
        if (!(this.getBean() instanceof User)) {
            return;
        }
        User user = (User)this.getBean();
        this.setData(user);
    }

    private void setData(User data) {
        if (data.getUserId() != null) {
            this.tfId.setText(String.valueOf(data.getUserId()));
        } else {
            this.tfId.setText("");
        }
        if (data.getSsn() != null) {
            this.tfSsn.setText(data.getSsn());
        } else {
            this.tfSsn.setText("");
        }
        this.tfFirstName.setText(data.getFirstName());
        this.tfLastName.setText(data.getLastName());
        this.tfPassword1.setText(data.getPassword());
        this.tfPassword2.setText(data.getPassword());
        this.tfPhone.setText(data.getPhoneNo());
        this.cbUserType.setSelectedItem(data.getType());
        Double costPerHour = data.getCostPerHour();
        if (costPerHour == null) {
            costPerHour = 0.0;
        }
        this.tfCostPerHour.setText(String.valueOf(costPerHour));
        this.chkDriver.setSelected(data.isDriver());
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode) {
            this.tfId.setEditable(false);
        } else {
            this.tfId.setEditable(true);
        }
    }

    public void setId(Integer id) {
        if (id != null) {
            this.tfId.setText(String.valueOf(id));
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 *  org.hibernate.StaleObjectStateException
 */
package com.floreantpos.ui.forms;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Customer;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.model.util.ZipCodeUtil;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;

public class QuickCustomerForm
extends BeanEditor<Customer> {
    static MyOwnFocusTraversalPolicy newPolicy;
    private JTextArea tfAddress;
    private FixedLengthTextField tfCity;
    private FixedLengthTextField tfZip;
    private FixedLengthTextField tfFirstName;
    private FixedLengthTextField tfLastName;
    private FixedLengthTextField tfName;
    private JTextField tfState;
    private JTextField tfCellPhone;
    private QwertyKeyPad qwertyKeyPad;
    public boolean isKeypad;

    public QuickCustomerForm() {
        this.createCustomerForm();
    }

    public QuickCustomerForm(boolean enable) {
        this.isKeypad = enable;
        this.createCustomerForm();
    }

    private void createCustomerForm() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setOpaque(true);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout((LayoutManager)new MigLayout("insets 10 10 10 10", "[][][][]", "[][][][][]"));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Customer Information"));
        JLabel lblAddress = new JLabel(Messages.getString("CustomerForm.18"));
        this.tfAddress = new JTextArea(new FixedLengthDocument(220));
        JScrollPane scrlDescription = new JScrollPane(this.tfAddress);
        scrlDescription.setPreferredSize(PosUIManager.getSize(338, 52));
        JLabel lblZip = new JLabel(Messages.getString("CustomerForm.21"));
        this.tfZip = new FixedLengthTextField(30);
        JLabel lblCitytown = new JLabel(Messages.getString("CustomerForm.24"));
        this.tfCity = new FixedLengthTextField();
        JLabel lblState = new JLabel(Messages.getString("QuickCustomerForm.0"));
        this.tfState = new JTextField(30);
        JLabel lblCellPhone = new JLabel(Messages.getString("CustomerForm.32"));
        inputPanel.add((Component)lblCellPhone, "cell 0 1,alignx right");
        this.tfCellPhone = new JTextField(30);
        inputPanel.add((Component)this.tfCellPhone, "cell 1 1");
        JLabel lblFirstName = new JLabel(Messages.getString("CustomerForm.3"));
        this.tfFirstName = new FixedLengthTextField();
        JLabel lblLastName = new JLabel(Messages.getString("CustomerForm.11"));
        this.tfLastName = new FixedLengthTextField();
        JLabel lblName = new JLabel("Name");
        inputPanel.add((Component)lblName, "cell 0 3,alignx right");
        this.tfName = new FixedLengthTextField();
        this.tfName.setLength(120);
        inputPanel.add((Component)this.tfName, "cell 1 3");
        inputPanel.add((Component)lblZip, "cell 0 4,right");
        inputPanel.add((Component)this.tfZip, "cell 1 4");
        inputPanel.add((Component)lblCitytown, "cell 0 5,right");
        inputPanel.add((Component)this.tfCity, "cell 1 5");
        inputPanel.add((Component)lblState, "cell 0 6,right");
        inputPanel.add((Component)this.tfState, "cell 1 6");
        inputPanel.add((Component)lblAddress, "cell 2 1 1 6,right");
        inputPanel.add((Component)scrlDescription, "grow, cell 3 1 1 6");
        this.qwertyKeyPad = new QwertyKeyPad();
        this.add((Component)inputPanel, "Center");
        if (this.isKeypad) {
            this.add((Component)((Object)this.qwertyKeyPad), "South");
        }
        this.tfZip.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                QuickCustomerForm.this.getStateAndCityByZipCode();
            }
        });
        this.tfZip.addFocusListener(new FocusListener(){

            @Override
            public void focusLost(FocusEvent e) {
                QuickCustomerForm.this.getStateAndCityByZipCode();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        this.enableCustomerFields(false);
        this.callOrderController();
    }

    public void callOrderController() {
        Vector<Component> order = new Vector<Component>();
        order.add(this.tfCellPhone);
        order.add(this.tfName);
        order.add(this.tfZip);
        order.add(this.tfCity);
        order.add(this.tfState);
        order.add(this.tfAddress);
        newPolicy = new MyOwnFocusTraversalPolicy(order);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(newPolicy);
    }

    public void enableCustomerFields(boolean enable) {
        this.tfName.setEnabled(enable);
        this.tfLastName.setEnabled(enable);
        this.tfFirstName.setEnabled(enable);
        this.tfAddress.setEnabled(enable);
        this.tfCity.setEnabled(enable);
        this.tfZip.setEnabled(enable);
        this.tfCellPhone.setEnabled(enable);
    }

    @Override
    public void setFieldsEnable(boolean enable) {
        this.tfName.setEnabled(enable);
        this.tfFirstName.setEnabled(enable);
        this.tfLastName.setEnabled(enable);
        this.tfAddress.setEnabled(enable);
        this.tfCity.setEnabled(enable);
        this.tfZip.setEnabled(enable);
        this.tfCellPhone.setEnabled(enable);
    }

    public void setFieldsEditable(boolean editable) {
        this.tfName.setEditable(editable);
        this.tfFirstName.setEditable(editable);
        this.tfLastName.setEditable(editable);
        this.tfAddress.setEditable(editable);
        this.tfCity.setEditable(editable);
        this.tfZip.setEditable(editable);
        this.tfCellPhone.setEditable(editable);
    }

    @Override
    public void createNew() {
        this.setBean(new Customer());
        this.tfName.setText("");
        this.tfFirstName.setText("");
        this.tfLastName.setText("");
        this.tfAddress.setText("");
        this.tfCity.setText("");
        this.tfZip.setText("");
        this.tfCellPhone.setText("");
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            Customer customer = (Customer)this.getBean();
            CustomerDAO.getInstance().saveOrUpdate(customer);
            this.updateView();
            return true;
        }
        catch (IllegalModelStateException customer) {
        }
        catch (StaleObjectStateException e) {
            BOMessageDialog.showError(this, Messages.getString("CustomerForm.47"));
        }
        return false;
    }

    @Override
    protected void updateView() {
        Customer customer = (Customer)this.getBean();
        if (customer == null) {
            return;
        }
        this.tfName.setText(customer.getName());
        this.tfFirstName.setText(customer.getFirstName());
        this.tfLastName.setText(customer.getLastName());
        this.tfCity.setText(customer.getCity());
        this.tfState.setText(customer.getState());
        this.tfZip.setText(customer.getZipCode());
        this.tfAddress.setText(customer.getAddress());
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        String mobile = this.tfCellPhone.getText();
        String name = this.tfName.getText();
        String[] fullName = name.split(" ");
        String fname = fullName[0];
        String lastName = name.substring(fname.length(), name.length());
        if (StringUtils.isEmpty((String)mobile) && StringUtils.isEmpty((String)name)) {
            POSMessageDialog.showError(null, Messages.getString("QuickCustomerForm.1"));
            return false;
        }
        Customer customer = (Customer)this.getBean();
        if (customer == null) {
            customer = new Customer();
            this.setBean(customer, false);
        }
        customer.setName(name);
        customer.setFirstName(fname);
        customer.setLastName(lastName);
        customer.setAddress(this.tfAddress.getText());
        customer.setCity(this.tfCity.getText());
        customer.setState(this.tfState.getText());
        customer.setZipCode(this.tfZip.getText());
        customer.setMobileNo(this.tfCellPhone.getText());
        return true;
    }

    @Override
    public boolean delete() {
        try {
            Customer bean2 = (Customer)this.getBean();
            if (bean2 == null) {
                return false;
            }
            int option = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getBackOfficeWindow(), "Are you sure to delete selected table?", "Confirm");
            if (option != 0) {
                return false;
            }
            CustomerDAO.getInstance().delete(bean2);
            return true;
        }
        catch (Exception e) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getDisplayText() {
        return Messages.getString("CustomerForm.54");
    }

    private void getStateAndCityByZipCode() {
        String zipCode = this.tfZip.getText();
        if (zipCode == null || zipCode.isEmpty()) {
            this.tfState.setText("");
            this.tfCity.setText("");
            return;
        }
        String city = ZipCodeUtil.getCity(zipCode);
        String state = ZipCodeUtil.getState(zipCode);
        this.tfState.setText(state);
        this.tfCity.setText(city);
    }

    public void setPhoneNo(String phoneNo) {
        this.tfCellPhone.setText(phoneNo);
    }

    public static class MyOwnFocusTraversalPolicy
    extends FocusTraversalPolicy {
        Vector<Component> order;

        public MyOwnFocusTraversalPolicy(Vector<Component> order) {
            this.order = new Vector(order.size());
            this.order.addAll(order);
        }

        @Override
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int idx = (this.order.indexOf(aComponent) + 1) % this.order.size();
            return this.order.get(idx);
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int idx = this.order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = this.order.size() - 1;
            }
            return this.order.get(idx);
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return this.order.get(0);
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return this.order.lastElement();
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return this.order.get(0);
        }
    }
}


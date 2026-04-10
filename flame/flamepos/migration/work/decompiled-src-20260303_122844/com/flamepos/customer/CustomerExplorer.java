/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Customer;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.forms.CustomerForm;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.PosGuiUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class CustomerExplorer
extends TransparentPanel {
    private List<Customer> customerList;
    private JTable table;
    private BeanTableModel<Customer> tableModel;

    public CustomerExplorer() {
        CustomerDAO dao = new CustomerDAO();
        this.customerList = dao.findAll();
        this.tableModel = new BeanTableModel(Customer.class);
        this.tableModel.addColumn("ID", "autoId");
        this.tableModel.addColumn("NAME", "name");
        this.tableModel.addColumn("LOYALTY", "loyaltyNo");
        this.tableModel.addColumn("TELEPHONE", "telephoneNo");
        this.tableModel.addColumn("EMAIL", "email");
        this.tableModel.addColumn("DOB", "dob");
        this.tableModel.addColumn("SSN", "ssn");
        this.tableModel.addColumn("ADDRESS", "address");
        this.tableModel.addColumn("CITY", "city");
        this.tableModel.addColumn("STATE", "state");
        this.tableModel.addColumn("ZIP", "zipCode");
        this.tableModel.addColumn("COUNTRY", "country");
        this.tableModel.addColumn("CREDIT LIMIT", "creditLimit");
        this.tableModel.addColumn("CREDIT SPENT", "creditSpent");
        this.tableModel.addColumn("NOTE", "note");
        this.tableModel.addRows(this.customerList);
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        PosGuiUtil.setColumnWidth(this.table, 0, 40);
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean setKeyPad = true;
                    CustomerForm editor = new CustomerForm(setKeyPad);
                    editor.enableCustomerFields(true);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    Customer customer = (Customer)editor.getBean();
                    CustomerExplorer.this.tableModel.addRow(customer);
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
                    int index = CustomerExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Customer customer = (Customer)CustomerExplorer.this.customerList.get(index);
                    boolean setKeyPad = true;
                    CustomerForm editor = new CustomerForm();
                    editor.enableCustomerFields(true);
                    editor.setBean(customer);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    CustomerExplorer.this.table.repaint();
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
                    int index = CustomerExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(CustomerExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        Customer customer = (Customer)CustomerExplorer.this.customerList.get(index);
                        CustomerDAO dao = new CustomerDAO();
                        dao.delete(customer);
                        CustomerExplorer.this.tableModel.removeRow(customer);
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
}


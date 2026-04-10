/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.bo.ui;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class RestaurantPropertyDialog
extends POSDialog {
    RestaurantDAO dao;
    Restaurant restaurant;
    private JButton btnCancel;
    private JButton btnOk;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JSeparator jSeparator1;
    private JTextField tfAddrLine1;
    private JTextField tfAddrLine2;
    private JTextField tfCapacity;
    private JTextField tfName;
    private JTextField tfTables;

    public RestaurantPropertyDialog() {
        this.setTitle(POSConstants.CONFIGURE);
        this.initComponents();
        this.dao = new RestaurantDAO();
        this.restaurant = this.dao.get(1);
        this.tfName.setText(this.restaurant.getName());
        this.tfAddrLine1.setText(this.restaurant.getAddressLine1());
        this.tfAddrLine2.setText(this.restaurant.getAddressLine2());
        this.tfCapacity.setText(String.valueOf(this.restaurant.getCapacity()));
        this.tfTables.setText(String.valueOf(this.restaurant.getTables()));
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel5 = new JLabel();
        this.tfName = new JTextField();
        this.tfAddrLine1 = new JTextField();
        this.tfAddrLine2 = new JTextField();
        this.tfCapacity = new JTextField();
        this.tfTables = new JTextField();
        this.btnCancel = new JButton();
        this.btnOk = new JButton();
        this.jSeparator1 = new JSeparator();
        this.setDefaultCloseOperation(2);
        this.jLabel1.setText(POSConstants.RESTAURANT_NAME + ":");
        this.jLabel2.setText(POSConstants.ADDRESS_LINE1 + ":");
        this.jLabel3.setText(POSConstants.ADDRESS_LINE2 + ":");
        this.jLabel4.setText(POSConstants.CAPACITY + ":");
        this.jLabel5.setText(POSConstants.TABLES + ":");
        this.btnCancel.setText(POSConstants.CLOSE);
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                RestaurantPropertyDialog.this.doClose(evt);
            }
        });
        this.btnOk.setText(POSConstants.SAVE);
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                RestaurantPropertyDialog.this.doSave(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add(2, (GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jSeparator1, -1, 380, Short.MAX_VALUE).add(1, (GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel1).add((Component)this.jLabel2).add((Component)this.jLabel3).add((Component)this.jLabel4).add((Component)this.jLabel5)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.tfName, -1, 288, Short.MAX_VALUE).add((Component)this.tfAddrLine1, -1, 288, Short.MAX_VALUE).add((Component)this.tfAddrLine2, -1, 288, Short.MAX_VALUE).add((GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.tfTables).add(1, (Component)this.tfCapacity, -1, 110, Short.MAX_VALUE)))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.btnOk).addPreferredGap(0).add((Component)this.btnCancel))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.tfName, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel2).add((Component)this.tfAddrLine1, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel3).add((Component)this.tfAddrLine2, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel4).add((Component)this.tfCapacity, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel5).add((Component)this.tfTables, -2, -1, -2)).addPreferredGap(0, 115, Short.MAX_VALUE).add((Component)this.jSeparator1, -2, 10, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.btnCancel).add((Component)this.btnOk)).addContainerGap()));
        this.pack();
    }

    private void doClose(ActionEvent evt) {
        this.setCanceled(true);
        this.dispose();
    }

    private void doSave(ActionEvent evt) {
        try {
            String name = null;
            String addr1 = null;
            String addr2 = null;
            int capacity = 299;
            int tables = 74;
            name = this.tfName.getText();
            addr1 = this.tfAddrLine1.getText();
            addr2 = this.tfAddrLine2.getText();
            try {
                capacity = Integer.parseInt(this.tfCapacity.getText());
            }
            catch (Exception e) {
                POSMessageDialog.showError(this, POSConstants.CAPACITY_IS_NOT_VALID_);
                return;
            }
            try {
                tables = Integer.parseInt(this.tfTables.getText());
            }
            catch (Exception e) {
                POSMessageDialog.showError(this, POSConstants.NUMBER_OF_TABLES_IS_NOT_VALID);
                return;
            }
            this.restaurant.setName(name);
            this.restaurant.setAddressLine1(addr1);
            this.restaurant.setAddressLine2(addr2);
            this.restaurant.setCapacity(capacity);
            this.restaurant.setTables(tables);
            this.dao.saveOrUpdate(this.restaurant);
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }
}


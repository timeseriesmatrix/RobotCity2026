/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import net.miginfocom.swing.MigLayout;

public class MenuItemPriceByOrderTypeDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton btnOK;
    private JButton btnCancel;
    private JComboBox cbOrderTypes;
    private JComboBox cbTax;
    private JTextField tfPrice;
    private String key;
    private MenuItem menuItem;

    public MenuItemPriceByOrderTypeDialog(Frame owner, MenuItem item) {
        super(owner, true);
        this.menuItem = item;
        this.init();
    }

    public MenuItemPriceByOrderTypeDialog(Frame owner, MenuItem item, String key) {
        super(owner, true);
        this.menuItem = item;
        this.key = key;
        this.init();
    }

    private void init() {
        this.createView();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        if (orderTypes != null) {
            for (OrderType orderType : orderTypes) {
                this.cbOrderTypes.addItem(orderType.getName());
            }
            this.cbOrderTypes.addItem("FOR HERE");
            this.cbOrderTypes.addItem("TO GO");
        }
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.btnOK);
        this.btnOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemPriceByOrderTypeDialog.this.onOK();
            }
        });
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemPriceByOrderTypeDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                MenuItemPriceByOrderTypeDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemPriceByOrderTypeDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.setMenuItem(this.menuItem);
    }

    private void onOK() {
        if (!this.updateModel()) {
            return;
        }
        try {
            this.setCanceled(false);
            this.dispose();
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void onCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    private void updateView() {
        if (this.menuItem == null) {
            return;
        }
        if (this.key != null) {
            this.cbOrderTypes.setSelectedItem(this.menuItem.getStringWithOutUnderScore(this.key, "_PRICE"));
            Tax newtax = TaxDAO.getInstance().findByTaxRate(Double.parseDouble(this.menuItem.getProperty(this.menuItem.replaceString(this.key, "_PRICE", "_TAX"))));
            this.cbTax.setSelectedItem(newtax);
            this.tfPrice.setText(String.valueOf(this.menuItem.getProperties().get(this.key)));
        }
    }

    public boolean updateModel() {
        double price = 0.0;
        try {
            price = Double.parseDouble(this.tfPrice.getText());
        }
        catch (Exception x) {
            POSMessageDialog.showError(this, POSConstants.PRICE_IS_NOT_VALID_);
            return false;
        }
        Tax tax = (Tax)this.cbTax.getSelectedItem();
        if (this.cbOrderTypes.getSelectedItem() == null) {
            return false;
        }
        this.menuItem.setTaxByOrderType(this.cbOrderTypes.getSelectedItem().toString(), tax.getRate());
        this.menuItem.setPriceByOrderType(this.cbOrderTypes.getSelectedItem().toString(), price);
        return true;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.updateView();
    }

    private void createView() {
        this.contentPane = new JPanel(new BorderLayout());
        JLabel label1 = new JLabel();
        label1.setText(Messages.getString("MenuItemPriceByOrderTypeDialog.6"));
        this.cbOrderTypes = new JComboBox();
        JLabel label3 = new JLabel();
        label3.setText(Messages.getString("MenuItemPriceByOrderTypeDialog.7"));
        this.cbTax = new JComboBox<Object>(new DefaultComboBoxModel<Object>(TaxDAO.getInstance().findAll().toArray()));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.PRICE + ":");
        this.tfPrice = new JTextField();
        JPanel panel = new JPanel((LayoutManager)new MigLayout("", "grow", ""));
        panel.add((Component)label1, "right");
        panel.add((Component)this.cbOrderTypes, "grow,wrap");
        panel.add((Component)label2, "right");
        panel.add((Component)this.tfPrice, "grow,wrap");
        panel.add((Component)label3, "right");
        panel.add((Component)this.cbTax, "grow");
        this.contentPane.add((Component)panel, "Center");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("al center center", "sg", ""));
        this.btnOK = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.0"));
        this.btnCancel = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.21"));
        buttonPanel.add((Component)this.btnOK, "grow");
        buttonPanel.add((Component)this.btnCancel, "grow");
        this.contentPane.add((Component)buttonPanel, "South");
        this.add(this.contentPane);
    }
}


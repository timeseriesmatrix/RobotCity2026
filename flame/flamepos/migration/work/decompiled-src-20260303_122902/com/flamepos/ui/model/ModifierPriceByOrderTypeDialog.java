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
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.MenuModifierDAO;
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

public class ModifierPriceByOrderTypeDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton btnOK;
    private JButton btnCancel;
    private JComboBox cbOrderTypes;
    private JComboBox cbTax;
    private JComboBox cbExtraTax;
    private JTextField tfPrice;
    private JTextField tfExtraPrice;
    private String key;
    private MenuModifier modifier;

    public ModifierPriceByOrderTypeDialog(Frame owner, MenuModifier modifier) {
        super(owner, true);
        this.modifier = modifier;
        this.init();
    }

    public ModifierPriceByOrderTypeDialog(Frame owner, MenuModifier modifier, String key) {
        super(owner, true);
        this.modifier = modifier;
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
                ModifierPriceByOrderTypeDialog.this.onOK();
            }
        });
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ModifierPriceByOrderTypeDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                ModifierPriceByOrderTypeDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ModifierPriceByOrderTypeDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.setMenuModifier(this.modifier);
    }

    private void onOK() {
        if (!this.updateModel()) {
            return;
        }
        try {
            MenuModifierDAO dao = new MenuModifierDAO();
            dao.saveOrUpdate(this.modifier);
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
        if (this.modifier == null) {
            return;
        }
        String modifiedKey = this.key;
        if (modifiedKey != null) {
            Tax extraTax;
            Tax newtax;
            modifiedKey = modifiedKey.replaceAll("_PRICE", "");
            modifiedKey = modifiedKey.replaceAll("_", " ");
            this.cbOrderTypes.setSelectedItem(modifiedKey);
            String taxKey = this.key;
            taxKey = taxKey.replaceAll("_PRICE", "_TAX");
            if (this.modifier.getProperty(taxKey) != null && (newtax = TaxDAO.getInstance().findByTaxRate(Double.parseDouble(this.modifier.getProperty(taxKey)))) != null) {
                this.cbTax.setSelectedItem(newtax);
            }
            String extraTaxKey = this.key;
            if (this.modifier.getProperty(extraTaxKey = extraTaxKey.replaceAll("_PRICE", "_EXTRA_TAX")) != null && (extraTax = TaxDAO.getInstance().findByTaxRate(Double.parseDouble(this.modifier.getProperty(extraTaxKey)))) != null) {
                this.cbExtraTax.setSelectedItem(extraTax);
            }
            String extraPrice = this.key;
            extraPrice = extraPrice.replaceAll("_PRICE", "_EXTRA_PRICE");
            this.tfExtraPrice.setText(this.modifier.getProperty(extraPrice));
            this.tfPrice.setText(String.valueOf(this.modifier.getProperties().get(this.key)));
        }
    }

    public boolean updateModel() {
        double price = 0.0;
        double extraPrice = 0.0;
        try {
            price = Double.parseDouble(this.tfPrice.getText());
            extraPrice = Double.parseDouble(this.tfExtraPrice.getText());
        }
        catch (Exception x) {
            POSMessageDialog.showError(this, POSConstants.PRICE_IS_NOT_VALID_);
            return false;
        }
        if (this.cbOrderTypes.getSelectedItem() == null) {
            return false;
        }
        Tax tax = (Tax)this.cbTax.getSelectedItem();
        this.modifier.setTaxByOrderType(this.cbOrderTypes.getSelectedItem().toString(), tax.getRate());
        this.modifier.setPriceByOrderType(this.cbOrderTypes.getSelectedItem().toString(), price);
        Tax extraTax = (Tax)this.cbExtraTax.getSelectedItem();
        this.modifier.setExtraTaxByOrderType(this.cbOrderTypes.getSelectedItem().toString(), extraTax.getRate());
        this.modifier.setExtraPriceByOrderType(this.cbOrderTypes.getSelectedItem().toString(), extraPrice);
        return true;
    }

    public MenuModifier getMenuModifier() {
        return this.modifier;
    }

    public void setMenuModifier(MenuModifier modifier) {
        this.modifier = modifier;
        this.updateView();
    }

    private void createView() {
        this.contentPane = new JPanel(new BorderLayout());
        JLabel label1 = new JLabel();
        label1.setText("Order type:");
        this.cbOrderTypes = new JComboBox();
        JLabel label3 = new JLabel();
        label3.setText("Tax:");
        this.cbTax = new JComboBox<Object>(new DefaultComboBoxModel<Object>(TaxDAO.getInstance().findAll().toArray()));
        JLabel lblExtraTax = new JLabel();
        lblExtraTax.setText("Extra tax:");
        this.cbExtraTax = new JComboBox<Object>(new DefaultComboBoxModel<Object>(TaxDAO.getInstance().findAll().toArray()));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.PRICE + ":");
        this.tfPrice = new JTextField();
        JLabel lblExtraPrice = new JLabel();
        lblExtraPrice.setText("Extra price");
        this.tfExtraPrice = new JTextField();
        JPanel panel = new JPanel((LayoutManager)new MigLayout("", "grow", ""));
        panel.add((Component)label1, "right");
        panel.add((Component)this.cbOrderTypes, "grow,wrap");
        panel.add((Component)label2, "right");
        panel.add((Component)this.tfPrice, "grow,wrap");
        panel.add((Component)lblExtraPrice, "right");
        panel.add((Component)this.tfExtraPrice, "grow,wrap");
        panel.add((Component)label3, "right");
        panel.add((Component)this.cbTax, "grow,wrap");
        panel.add((Component)lblExtraTax, "right");
        panel.add((Component)this.cbExtraTax, "grow");
        this.contentPane.add((Component)panel, "Center");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("al center center", "sg", ""));
        this.btnOK = new JButton(Messages.getString("ModifierPriceByOrderTypeDialog.0"));
        this.btnCancel = new JButton(Messages.getString("ModifierPriceByOrderTypeDialog.19"));
        buttonPanel.add((Component)this.btnOK, "grow");
        buttonPanel.add((Component)this.btnCancel, "grow");
        this.contentPane.add((Component)buttonPanel, "South");
        this.add(this.contentPane);
    }
}


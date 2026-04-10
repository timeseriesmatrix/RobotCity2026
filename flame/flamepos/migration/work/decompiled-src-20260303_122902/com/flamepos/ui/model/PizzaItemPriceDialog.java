/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.combobox.ListComboBoxModel
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.model.dao.PizzaCrustDAO;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class PizzaItemPriceDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton btnOK;
    private JButton btnCancel;
    private JComboBox cbCrust;
    private JComboBox cbSize;
    private JTextField tfPrice;
    private PizzaPrice pizzaPrice;
    List<PizzaPrice> pizzaPriceList;

    public PizzaItemPriceDialog(Frame owner, PizzaPrice pizzaPrice, List<PizzaPrice> pizzaPriceList) {
        super(owner, true);
        this.pizzaPrice = pizzaPrice;
        this.pizzaPriceList = pizzaPriceList;
        this.init();
        this.updateView();
    }

    private void init() {
        this.createView();
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.btnOK);
        this.btnOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemPriceDialog.this.onOK();
            }
        });
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemPriceDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                PizzaItemPriceDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaItemPriceDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
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
        if (this.pizzaPrice == null) {
            return;
        }
        this.cbSize.setSelectedItem(this.pizzaPrice.getSize());
        this.cbCrust.setSelectedItem(this.pizzaPrice.getCrust());
        this.tfPrice.setText(String.valueOf(this.pizzaPrice.getPrice()));
    }

    public boolean updateModel() {
        if (this.pizzaPrice == null) {
            this.pizzaPrice = new PizzaPrice();
        }
        if (this.cbSize.getSelectedItem() == null) {
            POSMessageDialog.showError(this, "Please Select Size!");
            return false;
        }
        if (this.cbCrust.getSelectedItem() == null) {
            POSMessageDialog.showError(this, "Please Select Crust!");
            return false;
        }
        if (this.tfPrice.getText() == null) {
            POSMessageDialog.showError(this, "Price Cannot Be Empty!");
            return false;
        }
        double price = 0.0;
        try {
            price = Double.parseDouble(this.tfPrice.getText());
        }
        catch (Exception x) {
            POSMessageDialog.showError(this, POSConstants.PRICE_IS_NOT_VALID_);
            return false;
        }
        if (this.pizzaPriceList != null) {
            for (PizzaPrice pc : this.pizzaPriceList) {
                if (!pc.getSize().equals(this.cbSize.getSelectedItem()) || !pc.getCrust().equals(this.cbCrust.getSelectedItem()) || pc == this.pizzaPrice) continue;
                POSMessageDialog.showMessage(this, "Duplicate item cannot be entered");
                return false;
            }
        }
        this.pizzaPrice.setSize((MenuItemSize)this.cbSize.getSelectedItem());
        this.pizzaPrice.setCrust((PizzaCrust)this.cbCrust.getSelectedItem());
        this.pizzaPrice.setPrice(price);
        return true;
    }

    private void createView() {
        this.contentPane = new JPanel(new BorderLayout());
        List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
        List<PizzaCrust> crustList = PizzaCrustDAO.getInstance().findAll();
        List<OrderType> orderTypeList = OrderTypeDAO.getInstance().findAll();
        orderTypeList.add(0, null);
        JLabel sizeLabel = new JLabel();
        sizeLabel.setText("Size");
        this.cbSize = new JComboBox(new ListComboBoxModel(menuItemSizeList));
        JLabel crustLabel = new JLabel();
        crustLabel.setText("Crust");
        this.cbCrust = new JComboBox(new ListComboBoxModel(crustList));
        JLabel priceLabel = new JLabel();
        priceLabel.setText(POSConstants.PRICE + ":");
        this.tfPrice = new JTextField();
        JPanel panel = new JPanel((LayoutManager)new MigLayout("", "grow", ""));
        panel.add((Component)sizeLabel, "right");
        panel.add((Component)this.cbSize, "grow,wrap");
        panel.add((Component)crustLabel, "right");
        panel.add((Component)this.cbCrust, "grow,wrap");
        panel.add((Component)priceLabel, "right");
        panel.add((Component)this.tfPrice, "grow");
        this.contentPane.add((Component)panel, "Center");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("al center center", "sg", ""));
        this.btnOK = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.0"));
        this.btnCancel = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.21"));
        buttonPanel.add((Component)this.btnOK, "grow");
        buttonPanel.add((Component)this.btnCancel, "grow");
        this.contentPane.add((Component)buttonPanel, "South");
        this.add(this.contentPane);
    }

    public PizzaPrice getPizzaPrice() {
        return this.pizzaPrice;
    }
}


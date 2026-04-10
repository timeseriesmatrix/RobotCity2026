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
import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.swing.DoubleTextField;
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
import javax.swing.KeyStroke;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class PizzaModifierPriceDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton btnOK;
    private JButton btnCancel;
    private JComboBox cbSize;
    private DoubleTextField tfPrice;
    private DoubleTextField tfExtraPrice;
    private PizzaModifierPrice modifierPrice;
    private final List<PizzaModifierPrice> existingPriceList;

    public PizzaModifierPriceDialog(Frame owner, PizzaModifierPrice modifierPrice, List<PizzaModifierPrice> existingPriceList) {
        super(owner, true);
        this.modifierPrice = modifierPrice;
        this.existingPriceList = existingPriceList;
        this.init();
        this.updateView();
    }

    private void init() {
        this.createView();
        this.btnOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaModifierPriceDialog.this.onOK();
            }
        });
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaModifierPriceDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                PizzaModifierPriceDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaModifierPriceDialog.this.onCancel();
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
        if (this.modifierPrice == null) {
            return;
        }
        this.cbSize.setSelectedItem(this.modifierPrice.getSize());
        this.tfPrice.setText(String.valueOf(this.modifierPrice.getPrice()));
        this.tfExtraPrice.setText(String.valueOf(this.modifierPrice.getExtraPrice()));
        this.tfPrice.requestFocus();
        this.tfPrice.grabFocus();
    }

    public boolean updateModel() {
        double price = this.tfPrice.getDoubleOrZero();
        double extraPrice = this.tfExtraPrice.getDoubleOrZero();
        MenuItemSize selectedSize = (MenuItemSize)this.cbSize.getSelectedItem();
        if (selectedSize == null) {
            POSMessageDialog.showError(this, "Please Select Size");
            return false;
        }
        if (this.existingPriceList != null) {
            for (PizzaModifierPrice mp : this.existingPriceList) {
                if (!selectedSize.equals(mp.getSize()) || mp == this.modifierPrice) continue;
                POSMessageDialog.showError(this, "Duplicate size!");
                return false;
            }
        }
        if (this.modifierPrice == null) {
            this.modifierPrice = new PizzaModifierPrice();
        }
        this.modifierPrice.setSize(selectedSize);
        this.modifierPrice.setPrice(price);
        this.modifierPrice.setExtraPrice(extraPrice);
        return true;
    }

    public PizzaModifierPrice getModifierPrice() {
        return this.modifierPrice;
    }

    private void createView() {
        this.contentPane = new JPanel(new BorderLayout());
        List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
        JLabel label3 = new JLabel();
        label3.setText("Size:");
        this.cbSize = new JComboBox(new ListComboBoxModel(menuItemSizeList));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.PRICE + ":");
        this.tfPrice = new DoubleTextField();
        JLabel lblExtraPrice = new JLabel();
        lblExtraPrice.setText("Extra Price:");
        this.tfExtraPrice = new DoubleTextField();
        JPanel panel = new JPanel((LayoutManager)new MigLayout("", "[][fill, grow]", ""));
        panel.add((Component)label3, "left");
        panel.add((Component)this.cbSize, "grow,wrap");
        panel.add((Component)label2, "left");
        panel.add((Component)this.tfPrice, "wrap");
        panel.add((Component)lblExtraPrice, "left");
        panel.add((Component)this.tfExtraPrice, "grow,wrap");
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


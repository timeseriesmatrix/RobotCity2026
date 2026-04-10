/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.intellij.uiDesigner.core.GridConstraints
 *  com.intellij.uiDesigner.core.GridLayoutManager
 *  com.intellij.uiDesigner.core.Spacer
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class MenuItemShiftDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox cbShifts;
    private JTextField tfPrice;
    private MenuItemShift menuItemShift;

    public MenuItemShiftDialog(Frame owner) {
        super(owner, true);
        this.$$$setupUI$$$();
        this.setContentPane(this.contentPane);
        ShiftDAO dao = new ShiftDAO();
        List<Shift> shifts = dao.findAll();
        this.cbShifts.setModel(new ListComboBoxModel(shifts));
        if (shifts.size() == 0) {
            this.buttonOK.setEnabled(false);
        }
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemShiftDialog.this.onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemShiftDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                MenuItemShiftDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MenuItemShiftDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.setMenuItemShift(this.menuItemShift);
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
        if (this.menuItemShift == null) {
            return;
        }
        this.cbShifts.setSelectedItem(this.menuItemShift.getShift());
        this.tfPrice.setText(String.valueOf(this.menuItemShift.getShiftPrice()));
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
        if (this.menuItemShift == null) {
            this.menuItemShift = new MenuItemShift();
        }
        this.menuItemShift.setShift((Shift)this.cbShifts.getSelectedItem());
        this.menuItemShift.setShiftPrice(price);
        return true;
    }

    public MenuItemShift getMenuItemShift() {
        return this.menuItemShift;
    }

    public void setMenuItemShift(MenuItemShift menuItemShift) {
        this.menuItemShift = menuItemShift;
        this.updateView();
    }

    private void $$$setupUI$$$() {
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        JPanel panel1 = new JPanel();
        panel1.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel1, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 1, null, null, null, 0, false));
        Spacer spacer1 = new Spacer();
        panel1.add((Component)spacer1, new GridConstraints(0, 0, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
        JPanel panel2 = new JPanel();
        panel2.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add((Component)panel2, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
        this.buttonOK = new JButton();
        this.buttonOK.setText(POSConstants.OK);
        panel2.add((Component)this.buttonOK, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
        this.buttonCancel = new JButton();
        this.buttonCancel.setText(POSConstants.CANCEL);
        panel2.add((Component)this.buttonCancel, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
        JPanel panel3 = new JPanel();
        panel3.setLayout((LayoutManager)new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
        JLabel label1 = new JLabel();
        label1.setText(POSConstants.SELECT_SHIFT + ":");
        panel3.add((Component)label1, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.cbShifts = new JComboBox();
        panel3.add((Component)this.cbShifts, new GridConstraints(0, 1, 1, 1, 8, 1, 2, 0, null, null, null, 0, false));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.PRICE + ":");
        panel3.add((Component)label2, new GridConstraints(1, 0, 1, 1, 4, 0, 0, 0, null, null, null, 0, false));
        this.tfPrice = new JTextField();
        panel3.add((Component)this.tfPrice, new GridConstraints(1, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
        Spacer spacer2 = new Spacer();
        this.contentPane.add((Component)spacer2, new GridConstraints(1, 0, 1, 1, 0, 2, 1, 4, null, new Dimension(-1, 15), null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.NumericKeypad;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class NumberSelectionDialog2
extends OkCancelOptionDialog {
    private int defaultValue;
    private JTextField tfNumber;
    private boolean floatingPoint;

    public NumberSelectionDialog2() {
        super((Window)Application.getPosWindow());
        this.init();
    }

    private void init() {
        JPanel contentPane = this.getContentPanel();
        contentPane.setLayout((LayoutManager)new MigLayout("inset 0", "[grow,fill]", "[grow,fill]"));
        this.tfNumber = new JTextField();
        this.tfNumber.setFont(this.tfNumber.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        this.tfNumber.setFocusable(true);
        this.tfNumber.requestFocus();
        this.tfNumber.setBackground(Color.WHITE);
        contentPane.add((Component)this.tfNumber, "cell 0 0,alignx left,height 40px,aligny top");
        NumericKeypad numericKeypad = new NumericKeypad();
        contentPane.add((Component)numericKeypad, "cell 0 1");
    }

    @Override
    public void doOk() {
        if (!this.validate(this.tfNumber.getText())) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }

    private boolean validate(String str) {
        if (this.isFloatingPoint()) {
            try {
                Double.parseDouble(str);
            }
            catch (Exception x) {
                return false;
            }
        }
        try {
            Integer.parseInt(str);
        }
        catch (Exception x) {
            return false;
        }
        return true;
    }

    @Override
    public void setTitle(String title) {
        super.setTitlePaneText(title);
        super.setTitle(title);
    }

    public void setDialogTitle(String title) {
        super.setTitle(title);
    }

    public double getValue() {
        return Double.parseDouble(this.tfNumber.getText());
    }

    public void setValue(double value) {
        if (value == 0.0) {
            this.tfNumber.setText("");
        } else if (this.isFloatingPoint()) {
            this.tfNumber.setText(String.valueOf(value));
        } else {
            this.tfNumber.setText(String.valueOf((int)value));
        }
    }

    public boolean isFloatingPoint() {
        return this.floatingPoint;
    }

    public void setFloatingPoint(boolean decimalAllowed) {
        this.floatingPoint = decimalAllowed;
    }

    public static void main(String[] args) {
        NumberSelectionDialog2 dialog2 = new NumberSelectionDialog2();
        dialog2.pack();
        dialog2.setVisible(true);
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
        this.tfNumber.setText(String.valueOf(defaultValue));
    }

    public static int takeIntInput(String title) {
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setTitle(title);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1;
        }
        return (int)dialog.getValue();
    }

    public static double takeDoubleInput(String title, String dialogTitle, double initialAmount) {
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setFloatingPoint(true);
        dialog.setValue(initialAmount);
        dialog.setTitle(title);
        dialog.setDialogTitle(dialogTitle);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return Double.NaN;
        }
        return dialog.getValue();
    }

    public static double takeDoubleInput(String title, double initialAmount) {
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setFloatingPoint(true);
        dialog.setTitle(title);
        dialog.setValue(initialAmount);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1.0;
        }
        return dialog.getValue();
    }

    public static double show(Component parent, String title, double initialAmount) {
        NumberSelectionDialog2 dialog2 = new NumberSelectionDialog2();
        dialog2.setFloatingPoint(true);
        dialog2.setTitle(title);
        dialog2.pack();
        dialog2.setLocationRelativeTo(parent);
        dialog2.setValue(initialAmount);
        dialog2.setVisible(true);
        if (dialog2.isCanceled()) {
            return Double.NaN;
        }
        return dialog2.getValue();
    }
}


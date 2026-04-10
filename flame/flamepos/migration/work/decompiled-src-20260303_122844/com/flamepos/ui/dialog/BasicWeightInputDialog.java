/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class BasicWeightInputDialog
extends OkCancelOptionDialog
implements ActionListener {
    private int defaultValue;
    private JTextField tfNumber;
    private boolean floatingPoint;
    private PosButton btnCancel;
    private boolean clearPreviousNumber = true;

    public BasicWeightInputDialog() {
        this.init();
    }

    private void init() {
        JPanel contentPane = this.getContentPanel();
        Dimension size = PosUIManager.getSize_w100_h70();
        MigLayout layout = new MigLayout("", "sg", "");
        contentPane.setLayout((LayoutManager)layout);
        this.tfNumber = new JTextField();
        this.tfNumber.setText(String.valueOf(this.defaultValue));
        this.tfNumber.setFont(this.tfNumber.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        this.tfNumber.setFocusable(true);
        this.tfNumber.requestFocus();
        this.tfNumber.setBackground(Color.WHITE);
        contentPane.add((Component)this.tfNumber, "span, grow");
        String[][] numbers = new String[][]{{"7", "8", "9"}, {"4", "5", "6"}, {"1", "2", "3"}, {".", "0", "CLEAR ALL"}};
        String[][] iconNames = new String[][]{{"7.png", "8.png", "9.png"}, {"4.png", "5.png", "6.png"}, {"1.png", "2.png", "3.png"}, {"dot.png", "0.png", ""}};
        int height = PosUIManager.getSize(55);
        for (int i = 0; i < numbers.length; ++i) {
            for (int j = 0; j < numbers[i].length; ++j) {
                PosButton posButton = new PosButton();
                posButton.setFocusable(false);
                ImageIcon icon = IconFactory.getIcon("/ui_icons/", iconNames[i][j]);
                String buttonText = String.valueOf(numbers[i][j]);
                if (icon == null) {
                    posButton.setText(buttonText);
                } else {
                    posButton.setIcon(icon);
                    if (POSConstants.CLEAR_ALL.equals(buttonText)) {
                        posButton.setText("CLEAR ALL");
                    }
                }
                posButton.setActionCommand(buttonText);
                posButton.addActionListener(this);
                String constraints = "grow,w " + size.width + "!,h " + size.height + "!";
                if (j == numbers[i].length - 1) {
                    constraints = constraints + ",wrap";
                }
                contentPane.add((Component)posButton, constraints);
            }
        }
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

    private void doClearAll() {
        this.tfNumber.setText(String.valueOf(this.defaultValue));
    }

    private void doInsertNumber(String number) {
        if (this.clearPreviousNumber) {
            this.tfNumber.setText("0");
            this.clearPreviousNumber = false;
        }
        String s = this.tfNumber.getText();
        double d = 0.0;
        try {
            d = Double.parseDouble(s);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (d == 0.0 && !s.contains(".")) {
            this.tfNumber.setText(number);
            return;
        }
        if (!this.validate(s = s + number)) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        this.tfNumber.setText(s);
    }

    private void doInsertDot() {
        String string = this.tfNumber.getText() + ".";
        if (!this.validate(string)) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        this.tfNumber.setText(string);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
            this.doCancel();
        } else if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
            this.doOk();
        } else if (actionCommand.equals(POSConstants.CLEAR_ALL)) {
            this.doClearAll();
        } else if (actionCommand.equals(".")) {
            this.doInsertDot();
        } else {
            this.doInsertNumber(actionCommand);
        }
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
        super.setTitle(title);
        super.setTitlePaneText(title);
    }

    public void setDialogTitle(String title) {
        super.setTitle(title);
    }

    public double getValue() {
        return Double.parseDouble(this.tfNumber.getText());
    }

    public void setValue(double value) {
        if (value == 0.0) {
            this.tfNumber.setText("0");
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
        BasicWeightInputDialog dialog2 = new BasicWeightInputDialog();
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
        BasicWeightInputDialog dialog = new BasicWeightInputDialog();
        dialog.setTitle(title);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1;
        }
        return (int)dialog.getValue();
    }

    public static double takeDoubleInput(String title, String dialogTitle, double initialAmount) {
        BasicWeightInputDialog dialog = new BasicWeightInputDialog();
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
        BasicWeightInputDialog dialog = new BasicWeightInputDialog();
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
        BasicWeightInputDialog dialog2 = new BasicWeightInputDialog();
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


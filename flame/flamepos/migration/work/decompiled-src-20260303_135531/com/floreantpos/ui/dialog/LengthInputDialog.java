/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.model.DeliveryConfiguration;
import com.floreantpos.model.dao.DeliveryConfigurationDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class LengthInputDialog
extends OkCancelOptionDialog
implements ActionListener {
    private double defaultValue;
    private static DoubleTextField tfNumber;
    private boolean clearPreviousNumber = true;
    private static final String UNIT_KM = "KM";
    private static final String UNIT_MILE = "MILE";
    private JComboBox tfUnitC;

    public LengthInputDialog(String title) {
        super(title);
        this.init();
        DeliveryConfiguration deliveryConfig = DeliveryConfigurationDAO.getInstance().get(1);
        if (deliveryConfig != null) {
            this.tfUnitC.setSelectedItem(deliveryConfig.getUnitName());
        }
    }

    private void init() {
        this.setResizable(false);
        JPanel leftPanel = new JPanel();
        MigLayout layout = new MigLayout("fill,inset 0", "sg, fill", "");
        leftPanel.setLayout((LayoutManager)layout);
        Dimension size = PosUIManager.getSize_w100_h70();
        tfNumber = new DoubleTextField();
        tfNumber.setText(String.valueOf(this.defaultValue));
        tfNumber.setFont(tfNumber.getFont().deriveFont(1, 24.0f));
        tfNumber.setFocusable(true);
        tfNumber.requestFocus();
        tfNumber.setBackground(Color.WHITE);
        Vector<String> units = new Vector<String>();
        units.add(UNIT_KM);
        units.add(UNIT_MILE);
        this.tfUnitC = new JComboBox(units);
        this.tfUnitC.setFont(tfNumber.getFont().deriveFont(1, 24.0f));
        this.tfUnitC.setEnabled(false);
        leftPanel.add((Component)tfNumber, "span 2, grow");
        leftPanel.add((Component)this.tfUnitC, "span, grow");
        String[][] numbers = new String[][]{{"7", "8", "9"}, {"4", "5", "6"}, {"1", "2", "3"}, {".", "0", "CLEAR ALL"}};
        String[][] iconNames = new String[][]{{"7.png", "8.png", "9.png"}, {"4.png", "5.png", "6.png"}, {"1.png", "2.png", "3.png"}, {"dot.png", "0.png", ""}};
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
                String constraints = "w " + size.width + "!,h " + size.height + "!,grow";
                if (j == numbers[i].length - 1) {
                    constraints = constraints + ",wrap";
                }
                leftPanel.add((Component)posButton, constraints);
            }
        }
        this.getContentPanel().add((Component)leftPanel, "Center");
    }

    private double convert(String unitTo, double inputValue) {
        String unitFrom = this.tfUnitC.getSelectedItem().toString();
        if (unitFrom.equals(UNIT_KM)) {
            if (unitTo.equals(UNIT_MILE)) {
                return NumberUtil.roundToTwoDigit(0.621371 * inputValue);
            }
        } else if (unitFrom.equals(UNIT_MILE) && unitTo.equals(UNIT_KM)) {
            return NumberUtil.roundToTwoDigit(1.609344 * inputValue);
        }
        return inputValue;
    }

    private void doClearAll() {
        tfNumber.setText(String.valueOf(0.0));
    }

    private void doInsertNumber(String number) {
        if (this.clearPreviousNumber) {
            tfNumber.setText("0");
            this.clearPreviousNumber = false;
        }
        String s = tfNumber.getText();
        double d = 0.0;
        try {
            d = Double.parseDouble(s);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (d == 0.0 && !s.contains(".")) {
            tfNumber.setText(number);
            return;
        }
        if (!this.validate(s = s + number)) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        tfNumber.setText(s);
    }

    private void doInsertDot() {
        String string = tfNumber.getText() + ".";
        if (!this.validate(string)) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        tfNumber.setText(string);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        double value = tfNumber.getDouble();
        if (actionCommand.equals(POSConstants.CLEAR_ALL)) {
            this.doClearAll();
        } else if (actionCommand.equals(".")) {
            this.doInsertDot();
        } else if (actionCommand.equals(UNIT_MILE)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_MILE, value)));
        } else if (actionCommand.equals(UNIT_KM)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_KM, value)));
        } else {
            this.doInsertNumber(actionCommand);
        }
    }

    private boolean validate(String str) {
        try {
            Double.parseDouble(str);
        }
        catch (Exception x) {
            return false;
        }
        return true;
    }

    public double getValue() {
        return Double.parseDouble(tfNumber.getText());
    }

    public double getDefaultUnitValue() {
        return this.convert(UNIT_KM, Double.parseDouble(tfNumber.getText()));
    }

    public void setValue(double value) {
        tfNumber.setText(String.valueOf(value));
    }

    public static double takeDoubleInput(String title, double initialAmount) {
        LengthInputDialog dialog = new LengthInputDialog("Please enter length.");
        dialog.setTitlePaneText(title);
        dialog.setValue(initialAmount);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1.0;
        }
        return dialog.getDefaultUnitValue();
    }

    @Override
    public void doOk() {
        if (!this.validate(tfNumber.getText())) {
            POSMessageDialog.showError(this, POSConstants.INVALID_NUMBER);
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }
}


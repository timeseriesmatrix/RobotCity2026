/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  jssc.SerialPortException
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.SerialPortUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jssc.SerialPortException;
import net.miginfocom.swing.MigLayout;

public class AutomatedWeightInputDialog
extends OkCancelOptionDialog
implements ActionListener {
    private double defaultValue;
    private static DoubleTextField tfNumber;
    private boolean clearPreviousNumber = true;
    private static final String UNIT_KG = "KG";
    private static final String UNIT_LB = "LB";
    private static final String UNIT_OZ = "OZ";
    private static final String UNIT_G = "G";
    private POSToggleButton btnLB;
    private POSToggleButton btnOZ;
    private POSToggleButton btnKG;
    private POSToggleButton btnG;
    private PosButton btnRefresh;
    private JTextField tfUnitC;
    private ButtonGroup group;

    public AutomatedWeightInputDialog(String title) {
        super(title);
        this.init();
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
        this.tfUnitC = new JTextField();
        this.tfUnitC.setText(UNIT_KG);
        this.tfUnitC.setFont(tfNumber.getFont().deriveFont(1, 24.0f));
        this.tfUnitC.setFocusable(false);
        this.tfUnitC.setEnabled(false);
        this.tfUnitC.setHorizontalAlignment(4);
        this.tfUnitC.setBackground(Color.WHITE);
        this.tfUnitC.setForeground(Color.LIGHT_GRAY);
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
        this.createRightPanel();
    }

    private void createRightPanel() {
        JPanel rightPanel = new JPanel((LayoutManager)new MigLayout("fill, inset 0 10 0 0", "sg, fill", ""));
        Dimension size = PosUIManager.getSize_w100_h70();
        this.group = new ButtonGroup();
        this.btnRefresh = new PosButton("REFRESH");
        this.btnLB = new POSToggleButton(UNIT_LB);
        this.btnOZ = new POSToggleButton(UNIT_OZ);
        this.btnKG = new POSToggleButton(UNIT_KG);
        this.btnG = new POSToggleButton(UNIT_G);
        this.btnRefresh.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AutomatedWeightInputDialog.this.readWeight();
            }
        });
        this.btnLB.addActionListener(this);
        this.btnOZ.addActionListener(this);
        this.btnKG.addActionListener(this);
        this.btnKG.setSelected(true);
        this.btnG.addActionListener(this);
        this.group.add(this.btnLB);
        this.group.add(this.btnOZ);
        this.group.add(this.btnKG);
        this.group.add(this.btnG);
        rightPanel.add((Component)this.btnRefresh, "w " + size.width + "!,h " + 40 + "!,wrap");
        rightPanel.add((Component)this.btnLB, "w " + size.width + "!, h " + size.height + "!,wrap");
        rightPanel.add((Component)this.btnOZ, "w " + size.width + "!, h " + size.height + "!,wrap");
        rightPanel.add((Component)this.btnKG, "w " + size.width + "!, h " + size.height + "!,wrap");
        rightPanel.add((Component)this.btnG, "w " + size.width + "!, h " + size.height + "!,wrap");
        this.getContentPanel().add((Component)rightPanel, "East");
    }

    public void readWeight() {
        try {
            String weightString = SerialPortUtil.readWeight(TerminalConfig.getScalePort());
            this.updateScaleView(weightString);
        }
        catch (Exception ex) {
            POSMessageDialog.showError(ex.getMessage());
        }
    }

    protected void updateScaleView(String value) throws SerialPortException {
        value = value.replaceAll("\n", "");
        String patternFormat = "(\\d*\\.?\\d*)(lb|oz|g|kg)";
        Pattern pattern = Pattern.compile(patternFormat, 2);
        Matcher m = pattern.matcher(value);
        if (m.find()) {
            tfNumber.setText(m.group(1));
            String unitName = m.group(2).toUpperCase();
            if (unitName.equals(UNIT_KG)) {
                this.btnKG.setSelected(true);
                this.tfUnitC.setText(UNIT_KG);
            } else if (unitName.equals(UNIT_LB)) {
                this.btnLB.setSelected(true);
                this.tfUnitC.setText(UNIT_LB);
            } else if (unitName.equals(UNIT_G)) {
                this.btnG.setSelected(true);
                this.tfUnitC.setText(UNIT_G);
            } else if (unitName.equals(UNIT_OZ)) {
                this.btnOZ.setSelected(true);
                this.tfUnitC.setText(UNIT_OZ);
            }
        } else {
            tfNumber.setText(String.valueOf(0));
            this.group.clearSelection();
        }
    }

    private double convert(String unitTo, double inputValue) {
        String unitFrom = this.tfUnitC.getText();
        if (unitFrom.equals(UNIT_KG)) {
            if (unitTo.equals(UNIT_LB)) {
                return NumberUtil.roundToTwoDigit(2.2 * inputValue);
            }
            if (unitTo.equals(UNIT_OZ)) {
                return NumberUtil.roundToTwoDigit(35.27 * inputValue);
            }
            if (unitTo.equals(UNIT_G)) {
                return NumberUtil.roundToTwoDigit(1000.0 * inputValue);
            }
        } else if (unitFrom.equals(UNIT_LB)) {
            if (unitTo.equals(UNIT_KG)) {
                return NumberUtil.roundToTwoDigit(0.45 * inputValue);
            }
            if (unitTo.equals(UNIT_OZ)) {
                return NumberUtil.roundToTwoDigit(16.0 * inputValue);
            }
            if (unitTo.equals(UNIT_G)) {
                return NumberUtil.roundToTwoDigit(453.59 * inputValue);
            }
        } else if (unitFrom.equals(UNIT_G)) {
            if (unitTo.equals(UNIT_KG)) {
                return NumberUtil.roundToTwoDigit(0.001 * inputValue);
            }
            if (unitTo.equals(UNIT_OZ)) {
                return NumberUtil.roundToTwoDigit(0.035 * inputValue);
            }
            if (unitTo.equals(UNIT_LB)) {
                return NumberUtil.roundToTwoDigit(0.00220462 * inputValue);
            }
        } else if (unitFrom.equals(UNIT_OZ)) {
            if (unitTo.equals(UNIT_KG)) {
                return NumberUtil.roundToTwoDigit(0.0283495 * inputValue);
            }
            if (unitTo.equals(UNIT_G)) {
                return NumberUtil.roundToTwoDigit(28.3495 * inputValue);
            }
            if (unitTo.equals(UNIT_LB)) {
                return NumberUtil.roundToTwoDigit(0.0625 * inputValue);
            }
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
        } else if (actionCommand.equals(UNIT_LB)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_LB, value)));
            this.tfUnitC.setText(UNIT_LB);
        } else if (actionCommand.equals(UNIT_OZ)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_OZ, value)));
            this.tfUnitC.setText(UNIT_OZ);
        } else if (actionCommand.equals(UNIT_G)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_G, value)));
            this.tfUnitC.setText(UNIT_G);
        } else if (actionCommand.equals(UNIT_KG)) {
            tfNumber.setText(String.valueOf(this.convert(UNIT_KG, value)));
            this.tfUnitC.setText(UNIT_KG);
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

    public void setValue(double value) {
        tfNumber.setText(String.valueOf(value));
    }

    public static double takeDoubleInput(String title, double initialAmount) {
        AutomatedWeightInputDialog dialog = new AutomatedWeightInputDialog("Please enter item weight or quantity");
        dialog.setTitlePaneText(title);
        dialog.setValue(initialAmount);
        if (TerminalConfig.isActiveScaleDisplay()) {
            dialog.readWeight();
        }
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1.0;
        }
        return dialog.getValue();
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


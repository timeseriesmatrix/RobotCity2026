/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class NumberSelectionView
extends TransparentPanel
implements ActionListener {
    private TitledBorder titledBorder;
    private boolean decimalAllowed;
    private JTextField tfNumber;

    public NumberSelectionView() {
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(5, 5));
        this.tfNumber = new JTextField();
        this.tfNumber.setText("0");
        this.tfNumber.setFont(this.tfNumber.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        this.tfNumber.setEditable(false);
        this.tfNumber.setBackground(Color.WHITE);
        this.tfNumber.setHorizontalAlignment(4);
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.add((Component)this.tfNumber, "Center");
        this.add((Component)northPanel, "North");
        String[][] numbers = new String[][]{{"7", "8", "9"}, {"4", "5", "6"}, {"1", "2", "3"}, {".", "0", POSConstants.CLEAR_ALL}};
        String[][] iconNames = new String[][]{{"7.png", "8.png", "9.png"}, {"4.png", "5.png", "6.png"}, {"1.png", "2.png", "3.png"}, {"dot.png", "0.png", "clear.png"}};
        JPanel centerPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        Dimension preferredSize = PosUIManager.getSize(100, 70);
        for (int i = 0; i < numbers.length; ++i) {
            for (int j = 0; j < numbers[i].length; ++j) {
                PosButton posButton = new PosButton();
                ImageIcon icon = IconFactory.getIcon("/ui_icons/", iconNames[i][j]);
                String buttonText = String.valueOf(numbers[i][j]);
                if (icon == null) {
                    posButton.setText(buttonText);
                } else {
                    posButton.setIcon(icon);
                    if (POSConstants.CLEAR_ALL.equals(buttonText)) {
                        posButton.setText(buttonText);
                    }
                }
                posButton.setActionCommand(buttonText);
                posButton.setPreferredSize(preferredSize);
                posButton.addActionListener(this);
                centerPanel.add(posButton);
            }
        }
        this.add((Component)centerPanel, "Center");
        this.titledBorder = new TitledBorder("");
        this.titledBorder.setTitleJustification(2);
        this.setBorder(this.titledBorder);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(POSConstants.CLEAR_ALL)) {
            this.tfNumber.setText("0");
        } else if (actionCommand.equals(POSConstants.CLEAR)) {
            String s = this.tfNumber.getText();
            s = s.length() > 1 ? s.substring(0, s.length() - 1) : "0";
            this.tfNumber.setText(s);
        } else if (actionCommand.equals(".")) {
            if (this.isDecimalAllowed() && this.tfNumber.getText().indexOf(46) < 0) {
                String string = this.tfNumber.getText() + ".";
                if (!this.validate(string)) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("NumberSelectionView.1"));
                    return;
                }
                this.tfNumber.setText(string);
            }
        } else {
            String s = this.tfNumber.getText();
            if (s.equals("0")) {
                this.tfNumber.setText(actionCommand);
                return;
            }
            if (!this.validate(s = s + actionCommand)) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("NumberSelectionView.0"));
                return;
            }
            this.tfNumber.setText(s);
        }
    }

    private boolean validate(String str) {
        if (this.isDecimalAllowed()) {
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

    public void setTitle(String title) {
        this.titledBorder.setTitle(title);
    }

    public double getValue() {
        return Double.parseDouble(this.tfNumber.getText());
    }

    public String getText() {
        return this.tfNumber.getText();
    }

    public void setValue(double value) {
        if (this.isDecimalAllowed()) {
            this.tfNumber.setText(String.valueOf(value));
        } else {
            this.tfNumber.setText(String.valueOf((int)value));
        }
    }

    public boolean isDecimalAllowed() {
        return this.decimalAllowed;
    }

    public void setDecimalAllowed(boolean decimalAllowed) {
        this.decimalAllowed = decimalAllowed;
    }
}


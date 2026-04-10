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
import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class ShiftEntryDialog
extends POSDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox startHour;
    private JComboBox startMin;
    private JComboBox startAmPm;
    private JComboBox endHour;
    private JComboBox endAmPm;
    private JComboBox endMin;
    private JTextField tfShiftName;
    private Vector<Integer> hours;
    private Vector<Integer> mins;
    private Shift shift;
    private Date shiftStart;
    private Date shiftEnd;

    public ShiftEntryDialog() {
        this((Shift)null);
    }

    public ShiftEntryDialog(Shift shift) {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        int i;
        this.$$$setupUI$$$();
        this.setTitle(POSConstants.NEW_SHIFT);
        this.setContentPane(this.contentPane);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.hours = new Vector();
        for (i = 1; i <= 12; ++i) {
            this.hours.add(i);
        }
        this.mins = new Vector();
        for (i = 0; i < 60; ++i) {
            this.mins.add(i);
        }
        this.startHour.setModel(new DefaultComboBoxModel<Integer>(this.hours));
        this.endHour.setModel(new DefaultComboBoxModel<Integer>(this.hours));
        this.startMin.setModel(new DefaultComboBoxModel<Integer>(this.mins));
        this.endMin.setModel(new DefaultComboBoxModel<Integer>(this.mins));
        this.buttonOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ShiftEntryDialog.this.onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ShiftEntryDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                ShiftEntryDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ShiftEntryDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.setSize(350, 250);
        this.setShift(shift);
    }

    private boolean calculateShifts() {
        int hour1 = (Integer)this.startHour.getSelectedItem();
        int hour2 = (Integer)this.endHour.getSelectedItem();
        int min1 = (Integer)this.startMin.getSelectedItem();
        int min2 = (Integer)this.endMin.getSelectedItem();
        int ampm1 = this.startAmPm.getSelectedIndex();
        int ampm2 = this.endAmPm.getSelectedIndex();
        this.shiftStart = ShiftUtil.buildShiftStartTime(hour1, min1, ampm1, hour2, min2, ampm2);
        this.shiftEnd = ShiftUtil.buildShiftEndTime(hour1, min1, ampm1, hour2, min2, ampm2);
        if (!this.shiftEnd.after(this.shiftStart)) {
            POSMessageDialog.showError(this, POSConstants.SHIFT_END_TIME_MUST_BE_GREATER_THAN_SHIFT_START_TIME);
            return false;
        }
        return true;
    }

    private void onOK() {
        try {
            if (!this.updateModel()) {
                return;
            }
            ShiftDAO dao = new ShiftDAO();
            if (this.shift.getId() == null && dao.exists(this.shift.getName())) {
                POSMessageDialog.showError(this, POSConstants.SHIFT_NAME_ALREADY_EXISTS);
                return;
            }
            dao.saveOrUpdate(this.shift);
            this.setCanceled(false);
            this.dispose();
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_SAVING_SHIFT_STATE, e);
        }
    }

    private void onCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    public Date getShiftStart() {
        return this.shiftStart;
    }

    public Date getShiftEnd() {
        return this.shiftEnd;
    }

    public void updateView() {
        if (this.shift == null) {
            return;
        }
        this.tfShiftName.setText(this.shift.getName());
        Date startTime = this.shift.getStartTime();
        Date endTime = this.shift.getEndTime();
        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        this.startHour.setSelectedIndex(c.get(10) - 1);
        this.startMin.setSelectedIndex(c.get(12));
        this.startAmPm.setSelectedIndex(c.get(9) == 0 ? 0 : 1);
        c.setTime(endTime);
        this.endHour.setSelectedIndex(c.get(10) - 1);
        this.endMin.setSelectedIndex(c.get(12));
        this.endAmPm.setSelectedIndex(c.get(9) == 0 ? 0 : 1);
    }

    public boolean updateModel() {
        if (!this.calculateShifts()) {
            return false;
        }
        if (this.shift == null) {
            this.shift = new Shift();
        }
        this.shift.setName(this.tfShiftName.getText());
        this.shift.setStartTime(this.shiftStart);
        this.shift.setEndTime(this.shiftEnd);
        Calendar c = Calendar.getInstance();
        c.setTime(this.shiftStart);
        long length = Math.abs(this.shiftStart.getTime() - this.shiftEnd.getTime());
        this.shift.setShiftLength(length);
        return true;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
        this.updateView();
    }

    private void $$$setupUI$$$() {
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        JPanel panel1 = new JPanel();
        panel1.setLayout((LayoutManager)new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel1, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 1, null, null, null, 0, false));
        Spacer spacer1 = new Spacer();
        panel1.add((Component)spacer1, new GridConstraints(1, 0, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
        JPanel panel2 = new JPanel();
        panel2.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add((Component)panel2, new GridConstraints(1, 1, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
        this.buttonOK = new JButton();
        this.buttonOK.setText(POSConstants.OK);
        panel2.add((Component)this.buttonOK, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
        this.buttonCancel = new JButton();
        this.buttonCancel.setText(POSConstants.CANCEL);
        panel2.add((Component)this.buttonCancel, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
        JSeparator separator1 = new JSeparator();
        panel1.add((Component)separator1, new GridConstraints(0, 0, 1, 2, 2, 1, 4, 4, null, null, null, 0, false));
        JPanel panel3 = new JPanel();
        panel3.setLayout((LayoutManager)new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
        JLabel label1 = new JLabel();
        label1.setText(POSConstants.START_TIME + ":");
        panel3.add((Component)label1, new GridConstraints(1, 0, 1, 1, 6, 0, 0, 0, null, null, null, 0, false));
        JSeparator separator2 = new JSeparator();
        panel3.add((Component)separator2, new GridConstraints(1, 1, 1, 4, 2, 1, 2, 4, null, null, null, 0, false));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.HOUR + ":");
        panel3.add((Component)label2, new GridConstraints(2, 0, 1, 1, 4, 0, 0, 0, null, null, null, 0, false));
        this.startHour = new JComboBox();
        panel3.add((Component)this.startHour, new GridConstraints(2, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(75, 22), null, 0, false));
        JLabel label3 = new JLabel();
        label3.setText(POSConstants.MIN + ":");
        panel3.add((Component)label3, new GridConstraints(2, 2, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.startMin = new JComboBox();
        panel3.add((Component)this.startMin, new GridConstraints(2, 3, 1, 1, 8, 1, 4, 0, null, null, null, 0, false));
        this.startAmPm = new JComboBox();
        DefaultComboBoxModel<String> defaultComboBoxModel1 = new DefaultComboBoxModel<String>();
        defaultComboBoxModel1.addElement(POSConstants.AM);
        defaultComboBoxModel1.addElement(POSConstants.PM);
        this.startAmPm.setModel(defaultComboBoxModel1);
        panel3.add((Component)this.startAmPm, new GridConstraints(2, 4, 1, 1, 8, 1, 2, 0, null, null, null, 0, false));
        JLabel label4 = new JLabel();
        label4.setText(POSConstants.END_TIME + ":");
        panel3.add((Component)label4, new GridConstraints(4, 0, 1, 1, 6, 0, 0, 0, null, null, null, 0, false));
        JSeparator separator3 = new JSeparator();
        panel3.add((Component)separator3, new GridConstraints(4, 1, 1, 4, 2, 1, 2, 4, null, null, new Dimension(-1, 2), 0, false));
        JLabel label5 = new JLabel();
        label5.setText(POSConstants.HOUR + ":");
        panel3.add((Component)label5, new GridConstraints(5, 0, 1, 1, 4, 0, 0, 0, null, null, null, 0, false));
        this.endHour = new JComboBox();
        panel3.add((Component)this.endHour, new GridConstraints(5, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(75, 22), null, 0, false));
        JLabel label6 = new JLabel();
        label6.setText(POSConstants.MIN + ":");
        panel3.add((Component)label6, new GridConstraints(5, 2, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.endMin = new JComboBox();
        panel3.add((Component)this.endMin, new GridConstraints(5, 3, 1, 1, 8, 1, 2, 0, null, null, null, 0, false));
        this.endAmPm = new JComboBox();
        DefaultComboBoxModel<String> defaultComboBoxModel2 = new DefaultComboBoxModel<String>();
        defaultComboBoxModel2.addElement(POSConstants.AM);
        defaultComboBoxModel2.addElement(POSConstants.PM);
        this.endAmPm.setModel(defaultComboBoxModel2);
        panel3.add((Component)this.endAmPm, new GridConstraints(5, 4, 1, 1, 8, 1, 2, 0, null, null, null, 0, false));
        Spacer spacer2 = new Spacer();
        panel3.add((Component)spacer2, new GridConstraints(3, 0, 1, 1, 0, 2, 1, 4, null, null, null, 0, false));
        JLabel label7 = new JLabel();
        label7.setText(POSConstants.SHIFT_NAME + ":");
        panel3.add((Component)label7, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.tfShiftName = new JTextField();
        panel3.add((Component)this.tfShiftName, new GridConstraints(0, 1, 1, 4, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
        Spacer spacer3 = new Spacer();
        this.contentPane.add((Component)spacer3, new GridConstraints(1, 0, 1, 1, 0, 2, 1, 4, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }
}


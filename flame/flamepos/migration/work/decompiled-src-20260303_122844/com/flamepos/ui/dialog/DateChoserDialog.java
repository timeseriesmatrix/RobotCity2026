/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class DateChoserDialog
extends POSDialog {
    private JXDatePicker tbStartDate;
    private JXDatePicker tbEndDate;
    private IntegerTextField tfStartHour;
    private IntegerTextField tfStartMin;
    private JRadioButton rbStartAm;
    private JRadioButton rbStartPm;
    private IntegerTextField tfEndHour;
    private IntegerTextField tfEndMin;
    private JRadioButton rbEndAm;
    private JRadioButton rbEndPm;
    private ButtonGroup btnGroupStartAmPm;
    private ButtonGroup btnGroupEndAmPm;
    private PosButton btnOk;
    private PosButton btnCancel;
    private AttendenceHistory attendenceHistory;
    private JComboBox cbEmployees;
    private JCheckBox chkClockOut;

    public DateChoserDialog(String title) {
        super((Window)POSUtil.getBackOfficeWindow(), title);
        this.attendenceHistory = new AttendenceHistory();
        this.initUi();
    }

    public DateChoserDialog(AttendenceHistory history, String title) {
        super((Window)POSUtil.getBackOfficeWindow(), title);
        this.attendenceHistory = history;
        this.initUi();
    }

    private void initUi() {
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.setResizable(false);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.red);
        List<User> employees = UserDAO.getInstance().findAll();
        this.cbEmployees = new JComboBox(new ComboBoxModel(employees));
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        topPanel.add(new JLabel("Select employee:"));
        topPanel.add(this.cbEmployees);
        mainPanel.add((Component)topPanel, "North");
        JPanel panel = new JPanel((LayoutManager)new MigLayout("wrap 2", " [][][][][][][][][]", "[][]"));
        panel.setBorder(new TitledBorder("-"));
        this.btnGroupStartAmPm = new ButtonGroup();
        this.rbStartAm = new JRadioButton("AM");
        this.rbStartPm = new JRadioButton("PM");
        this.btnGroupStartAmPm.add(this.rbStartAm);
        this.btnGroupStartAmPm.add(this.rbStartPm);
        this.rbStartPm.setSelected(true);
        this.btnGroupEndAmPm = new ButtonGroup();
        this.rbEndAm = new JRadioButton("AM");
        this.rbEndPm = new JRadioButton("PM");
        this.btnGroupEndAmPm.add(this.rbEndAm);
        this.btnGroupEndAmPm.add(this.rbEndPm);
        this.rbEndPm.setSelected(true);
        this.tbStartDate = UiUtil.getCurrentMonthStart();
        this.tbEndDate = UiUtil.getCurrentMonthEnd();
        Vector<Integer> hours = new Vector<Integer>();
        for (int i = 1; i <= 12; ++i) {
            hours.add(i);
        }
        DefaultComboBoxModel<Integer> stMinModel = new DefaultComboBoxModel<Integer>();
        stMinModel.addElement(0);
        stMinModel.addElement(15);
        stMinModel.addElement(30);
        stMinModel.addElement(45);
        DefaultComboBoxModel<Integer> etMinModel = new DefaultComboBoxModel<Integer>();
        etMinModel.addElement(0);
        etMinModel.addElement(15);
        etMinModel.addElement(30);
        etMinModel.addElement(45);
        this.tfStartHour = new IntegerTextField();
        this.tfStartMin = new IntegerTextField();
        this.tfEndHour = new IntegerTextField();
        this.tfEndMin = new IntegerTextField();
        this.tfStartHour.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedItem = DateChoserDialog.this.tfStartHour.getInteger();
                selectedItem = selectedItem == 12 ? Integer.valueOf(1) : Integer.valueOf(selectedItem + 1);
                DateChoserDialog.this.tfEndHour.setText(String.valueOf(selectedItem));
            }
        });
        this.chkClockOut = new JCheckBox("Clock Out: ");
        this.chkClockOut.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DateChoserDialog.this.enabledItemsForClockOut();
            }
        });
        JLabel lblClockIn = new JLabel("Clock In: ");
        panel.add((Component)lblClockIn, "cell 0 0,right");
        panel.add((Component)new JLabel("Date"), "cell 1 0");
        panel.add((Component)this.tbStartDate, "cell 2 0");
        panel.add((Component)new JLabel("Hour"), "cell 3 0");
        panel.add((Component)this.tfStartHour, "w 40!,cell 4 0");
        panel.add((Component)new JLabel("Min"), "cell 5 0");
        panel.add((Component)this.tfStartMin, "w 40!,cell 6 0");
        panel.add((Component)this.rbStartAm, "cell 7 0");
        panel.add((Component)this.rbStartPm, "cell 8 0");
        panel.add((Component)this.chkClockOut, "cell 0 1");
        panel.add((Component)new JLabel("Date"), "cell 1 1");
        panel.add((Component)this.tbEndDate, "cell 2 1");
        panel.add((Component)new JLabel("Hour"), "cell 3 1");
        panel.add((Component)this.tfEndHour, "w 40!,cell 4 1");
        panel.add((Component)new JLabel("Min"), "cell 5 1");
        panel.add((Component)this.tfEndMin, "w 40!,cell 6 1");
        panel.add((Component)this.rbEndAm, "cell 7 1");
        panel.add((Component)this.rbEndPm, "cell 8 1");
        JPanel footerPanel = new JPanel((LayoutManager)new MigLayout("al center center", "sg", ""));
        this.btnOk = new PosButton("OK");
        this.btnCancel = new PosButton("CANCEL");
        this.btnCancel.setPreferredSize(new Dimension(100, 0));
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DateChoserDialog.this.setCanceled(true);
                DateChoserDialog.this.dispose();
            }
        });
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DateChoserDialog.this.updateModel()) {
                    DateChoserDialog.this.setCanceled(false);
                    DateChoserDialog.this.dispose();
                }
            }
        });
        footerPanel.add((Component)this.btnOk, "grow");
        footerPanel.add((Component)this.btnCancel, "grow");
        mainPanel.add((Component)panel, "Center");
        mainPanel.add((Component)footerPanel, "South");
        this.getContentPane().add((Component)mainPanel, "Center");
        this.updateView();
        this.enabledItemsForClockOut();
    }

    private void enabledItemsForClockOut() {
        boolean selected = this.chkClockOut.isSelected();
        this.tbEndDate.setEnabled(selected);
        this.tfEndHour.setEnabled(selected);
        this.tfEndMin.setEnabled(selected);
    }

    private void updateView() {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        if (this.attendenceHistory.getId() == null) {
            startCalendar.setTime(new Date());
            endCalendar.setTime(new Date());
        } else {
            if (this.attendenceHistory.getClockInTime() != null) {
                startCalendar.setTime(this.attendenceHistory.getClockInTime());
                if (this.attendenceHistory.getClockOutTime() != null) {
                    endCalendar.setTime(this.attendenceHistory.getClockOutTime());
                }
            }
            this.cbEmployees.setSelectedItem(this.attendenceHistory.getUser());
            this.chkClockOut.setSelected(this.attendenceHistory.isClockedOut());
        }
        this.tbStartDate.setDate(startCalendar.getTime());
        Integer hour = startCalendar.get(10);
        if (hour.equals(0)) {
            hour = 12;
        }
        this.tfStartHour.setText(String.valueOf(hour));
        this.tfStartMin.setText(String.valueOf(startCalendar.get(12)));
        if (startCalendar.get(9) == 0) {
            this.rbStartAm.setSelected(true);
        } else {
            this.rbStartPm.setSelected(true);
        }
        this.tbEndDate.setDate(endCalendar.getTime());
        Integer endHour = endCalendar.get(10);
        if (endHour.equals(0)) {
            endHour = 12;
        }
        this.tfEndHour.setText(String.valueOf(endHour));
        this.tfEndMin.setText(String.valueOf(endCalendar.get(12)));
        if (endCalendar.get(9) == 0) {
            this.rbEndAm.setSelected(true);
        } else {
            this.rbEndPm.setSelected(true);
        }
    }

    private boolean updateModel() {
        Calendar clockInTime = this.getStartDate();
        Calendar clockOutTime = this.getEndDate();
        PosLog.info(DateChoserDialog.class, "" + clockInTime.getTime().getTime());
        PosLog.info(DateChoserDialog.class, "" + clockOutTime.getTime().getTime());
        if (clockInTime.getTime().getTime() > clockOutTime.getTime().getTime()) {
            POSMessageDialog.showMessage(POSUtil.getBackOfficeWindow(), "Clock in can not be greater than clock out");
            return false;
        }
        this.attendenceHistory.setClockInTime(clockInTime.getTime());
        this.attendenceHistory.setClockInHour((short)clockInTime.get(11));
        if (!this.chkClockOut.isSelected()) {
            this.attendenceHistory.setClockOutTime(null);
            this.attendenceHistory.setClockOutHour(null);
        } else {
            this.attendenceHistory.setClockOutTime(clockOutTime.getTime());
            this.attendenceHistory.setClockOutHour((short)clockOutTime.get(11));
        }
        User employee = (User)this.cbEmployees.getSelectedItem();
        Shift currentShift = ShiftUtil.getCurrentShift();
        this.attendenceHistory.setClockedOut(this.chkClockOut.isSelected());
        this.attendenceHistory.setUser(employee);
        this.attendenceHistory.setTerminal(Application.getInstance().getTerminal());
        this.attendenceHistory.setShift(currentShift);
        return true;
    }

    private Calendar getStartDate() {
        if (this.tbStartDate.getDate() == null) {
            return null;
        }
        Calendar clStartDate = Calendar.getInstance();
        clStartDate.setTime(this.tbStartDate.getDate());
        Integer hour = this.tfStartHour.getInteger();
        if (hour == 12) {
            hour = 0;
        }
        clStartDate.set(10, hour);
        clStartDate.set(12, this.tfStartMin.getInteger());
        if (this.rbStartAm.isSelected()) {
            clStartDate.set(9, 0);
        } else if (this.rbStartPm.isSelected()) {
            clStartDate.set(9, 1);
        }
        return clStartDate;
    }

    private Calendar getEndDate() {
        if (this.tbEndDate.getDate() == null) {
            return null;
        }
        Calendar clEndDate = Calendar.getInstance();
        clEndDate.setTime(this.tbEndDate.getDate());
        Integer hour = this.tfEndHour.getInteger();
        if (hour == 12) {
            hour = 0;
        }
        clEndDate.set(10, hour);
        clEndDate.set(12, this.tfEndMin.getInteger());
        if (this.rbEndAm.isSelected()) {
            clEndDate.set(9, 0);
        } else if (this.rbEndPm.isSelected()) {
            clEndDate.set(9, 1);
        }
        return clEndDate;
    }

    public AttendenceHistory getAttendenceHistory() {
        return this.attendenceHistory;
    }
}


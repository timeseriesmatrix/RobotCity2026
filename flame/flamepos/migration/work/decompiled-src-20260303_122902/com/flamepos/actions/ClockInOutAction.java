/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao.EmployeeInOutHistoryDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.commons.logging.LogFactory;

public class ClockInOutAction
extends PosAction {
    public ClockInOutAction() {
        super(Messages.getString("ClockInOutAction.0"));
    }

    public ClockInOutAction(boolean showText, boolean showIcon) {
        if (showText) {
            this.putValue("Name", Messages.getString("ClockInOutAction.1"));
        }
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("/ui_icons/", "clock_out.png"));
        }
    }

    @Override
    public void execute() {
        final User user = PasswordEntryDialog.getUser(Application.getPosWindow(), Messages.getString("ClockInOutAction.3"));
        if (user == null) {
            return;
        }
        final POSDialog dialog = new POSDialog((Frame)Application.getPosWindow(), true);
        dialog.setTitle(Messages.getString("ClockInOutAction.4"));
        PosButton btnClockIn = new PosButton(Messages.getString("ClockInOutAction.5"));
        btnClockIn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                ClockInOutAction.this.performClockIn(user);
            }
        });
        PosButton btnClockOut = new PosButton(Messages.getString("ClockInOutAction.6"));
        btnClockOut.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                ClockInOutAction.this.performClockOut(user);
            }
        });
        PosButton btnDriverIn = new PosButton("DRIVER IN");
        btnDriverIn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                ClockInOutAction.this.performDriverIn(user);
            }
        });
        PosButton btnDriverOut = new PosButton("DRIVER OUT");
        btnDriverOut.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                ClockInOutAction.this.performDriverOut(user);
            }
        });
        PosButton btnCancel = new PosButton(Messages.getString("ClockInOutAction.7"));
        btnCancel.setPreferredSize(new Dimension(150, 120));
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        JPanel contentPane = (JPanel)dialog.getContentPane();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridLayout(1, 0, 10, 10));
        if (user.isClockedIn().booleanValue()) {
            contentPane.add(btnClockOut);
        } else {
            contentPane.add(btnClockIn);
        }
        if (user.isDriver().booleanValue() && user.isClockedIn().booleanValue()) {
            if (user.isAvailableForDelivery().booleanValue()) {
                contentPane.add(btnDriverOut);
            } else {
                contentPane.add(btnDriverIn);
            }
        }
        contentPane.add(btnCancel);
        dialog.pack();
        dialog.open();
    }

    private void performClockOut(User user) {
        try {
            if (user == null) {
                return;
            }
            AttendenceHistoryDAO attendenceHistoryDAO = new AttendenceHistoryDAO();
            AttendenceHistory attendenceHistory = attendenceHistoryDAO.findHistoryByClockedInTime(user);
            if (attendenceHistory == null) {
                attendenceHistory = new AttendenceHistory();
                Date lastClockInTime = user.getLastClockInTime();
                Calendar c = Calendar.getInstance();
                c.setTime(lastClockInTime);
                attendenceHistory.setClockInTime(lastClockInTime);
                attendenceHistory.setClockInHour((short)c.get(10));
                attendenceHistory.setUser(user);
                attendenceHistory.setTerminal(Application.getInstance().getTerminal());
                attendenceHistory.setShift(user.getCurrentShift());
            }
            Shift shift = user.getCurrentShift();
            Calendar calendar = Calendar.getInstance();
            user.doClockOut(attendenceHistory, shift, calendar);
            POSMessageDialog.showMessage(Messages.getString("ClockInOutAction.8") + user.getFirstName() + " " + user.getLastName() + Messages.getString("ClockInOutAction.10"));
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    private void performClockIn(User user) {
        try {
            if (user == null) {
                return;
            }
            if (user.isClockedIn() != null && user.isClockedIn().booleanValue()) {
                POSMessageDialog.showMessage(Messages.getString("ClockInOutAction.11") + user.getFirstName() + " " + user.getLastName() + Messages.getString("ClockInOutAction.13"));
                return;
            }
            Shift currentShift = ShiftUtil.getCurrentShift();
            Calendar currentTime = Calendar.getInstance();
            user.doClockIn(Application.getInstance().getTerminal(), currentShift, currentTime);
            POSMessageDialog.showMessage(Messages.getString("ClockInOutAction.14") + user.getFirstName() + " " + user.getLastName() + Messages.getString("ClockInOutAction.16"));
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public void performDriverOut(User user) {
        try {
            if (user == null) {
                return;
            }
            Shift currentShift = ShiftUtil.getCurrentShift();
            Terminal terminal = Application.getInstance().getTerminal();
            Calendar currentTime = Calendar.getInstance();
            user.setAvailableForDelivery(false);
            user.setLastClockOutTime(currentTime.getTime());
            LogFactory.getLog(Application.class).info((Object)("terminal id befor saving clockIn=" + terminal.getId()));
            EmployeeInOutHistory attendenceHistory = new EmployeeInOutHistory();
            attendenceHistory.setOutTime(currentTime.getTime());
            attendenceHistory.setOutHour((short)currentTime.get(11));
            attendenceHistory.setClockOut(true);
            attendenceHistory.setUser(user);
            attendenceHistory.setTerminal(terminal);
            attendenceHistory.setShift(currentShift);
            UserDAO.getInstance().saveDriverOut(user, attendenceHistory, currentShift, currentTime);
            POSMessageDialog.showMessage("Driver " + user.getFirstName() + " " + user.getLastName() + " is out for delivery.");
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public void performDriverIn(User user) {
        try {
            if (user == null) {
                return;
            }
            if (!user.isClockedIn().booleanValue()) {
                POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("ClockInOutAction.2"));
                return;
            }
            EmployeeInOutHistoryDAO attendenceHistoryDAO = new EmployeeInOutHistoryDAO();
            EmployeeInOutHistory attendenceHistory = attendenceHistoryDAO.findDriverHistoryByClockedInTime(user);
            if (attendenceHistory == null) {
                attendenceHistory = new EmployeeInOutHistory();
                Date lastClockOutTime = user.getLastClockOutTime();
                Calendar c = Calendar.getInstance();
                c.setTime(lastClockOutTime);
                attendenceHistory.setOutTime(lastClockOutTime);
                attendenceHistory.setOutHour((short)c.get(10));
                attendenceHistory.setUser(user);
                attendenceHistory.setTerminal(Application.getInstance().getTerminal());
                attendenceHistory.setShift(user.getCurrentShift());
            }
            Shift shift = user.getCurrentShift();
            Calendar calendar = Calendar.getInstance();
            user.setAvailableForDelivery(true);
            attendenceHistory.setClockOut(false);
            attendenceHistory.setInTime(calendar.getTime());
            attendenceHistory.setInHour((short)calendar.get(11));
            UserDAO.getInstance().saveDriverIn(user, attendenceHistory, shift, calendar);
            POSMessageDialog.showMessage("Driver " + user.getFirstName() + " " + user.getLastName() + " is in after delivery");
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


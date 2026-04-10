/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.AttendanceReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class EmployeeAttendanceAction
extends AbstractAction {
    public EmployeeAttendanceAction() {
        super(Messages.getString("EmployeeAttendanceAction.0"));
    }

    public EmployeeAttendanceAction(String name) {
        super(name);
    }

    public EmployeeAttendanceAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        AttendanceReportView reportView = null;
        int index = tabbedPane.indexOfTab(Messages.getString("EmployeeAttendanceAction.1"));
        if (index == -1) {
            reportView = new AttendanceReportView();
            tabbedPane.addTab(Messages.getString("EmployeeAttendanceAction.2"), reportView);
        } else {
            reportView = (AttendanceReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


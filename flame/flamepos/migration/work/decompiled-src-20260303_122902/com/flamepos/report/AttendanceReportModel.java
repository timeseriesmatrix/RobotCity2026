/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.report.AttendanceReportData;
import com.floreantpos.swing.ListTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class AttendanceReportModel
extends ListTableModel {
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMMdd  HH:mm");
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AttendanceReportModel() {
        super(new String[]{"employeeId", "employeeName", "clockIn", "clockOut", "workTime"});
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AttendanceReportData data = (AttendanceReportData)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(data.getUser().getUserId());
            }
            case 1: {
                return data.getUser().getFirstName() + " " + data.getUser().getLastName();
            }
            case 2: {
                return this.dateFormat2.format(data.getClockIn());
            }
            case 3: {
                return this.dateFormat2.format(data.getClockOut());
            }
            case 4: {
                return this.decimalFormat.format(data.getWorkTime());
            }
        }
        return null;
    }
}


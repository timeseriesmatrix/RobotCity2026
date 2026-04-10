/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.report.HourlyLaborReportView;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.List;

public class HourlyLaborReportModel
extends ListTableModel {
    private String[] columnNames = new String[]{"period", "checks", "guests", "sales", "manHour", "labor", "salesPerMHr", "guestsPerMHr", "checksPerMHr", "laborCost"};

    public HourlyLaborReportModel() {
        this.setColumnNames(this.columnNames);
    }

    public HourlyLaborReportModel(List rows) {
        this.setColumnNames(this.columnNames);
        this.setRows(rows);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HourlyLaborReportView.LaborReportData reportData = (HourlyLaborReportView.LaborReportData)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return reportData.getPeriod();
            }
            case 1: {
                return String.valueOf(reportData.getNoOfChecks());
            }
            case 2: {
                return String.valueOf(reportData.getNoOfGuests());
            }
            case 3: {
                return NumberUtil.formatNumber(reportData.getSales());
            }
            case 4: {
                return NumberUtil.formatNumber(reportData.getManHour());
            }
            case 5: {
                return NumberUtil.formatNumber(reportData.getLabor());
            }
            case 6: {
                return NumberUtil.formatNumber(reportData.getSalesPerMHr());
            }
            case 7: {
                return NumberUtil.formatNumber(reportData.getGuestsPerMHr());
            }
            case 8: {
                return NumberUtil.formatNumber(reportData.getCheckPerMHr());
            }
            case 9: {
                return NumberUtil.formatNumber(reportData.getLaborCost());
            }
        }
        return null;
    }
}


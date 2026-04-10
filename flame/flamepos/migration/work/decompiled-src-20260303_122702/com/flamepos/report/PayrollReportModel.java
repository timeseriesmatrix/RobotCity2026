/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.report.PayrollReportData;
import com.floreantpos.swing.ListTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class PayrollReportModel
extends ListTableModel {
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public PayrollReportModel() {
        super(new String[]{"userID", "userName", "from", "to", "total", "rate", "payment", "userSSN"});
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PayrollReportData data = (PayrollReportData)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(data.getUser().getUserId());
            }
            case 1: {
                return data.getUser().getFirstName() + " " + data.getUser().getLastName();
            }
            case 2: {
                return this.dateFormat2.format(data.getFrom());
            }
            case 3: {
                return this.dateFormat2.format(data.getTo());
            }
            case 4: {
                return data.getTotalHour();
            }
            case 5: {
                return this.decimalFormat.format(data.getRate());
            }
            case 6: {
                return data.getPayment();
            }
            case 7: {
                return String.valueOf(data.getUser().getUserId());
            }
        }
        return null;
    }
}


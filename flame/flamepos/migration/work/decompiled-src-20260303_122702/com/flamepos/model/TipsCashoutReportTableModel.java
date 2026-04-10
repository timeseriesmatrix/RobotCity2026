/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.TipsCashoutReportData;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.List;

public class TipsCashoutReportTableModel
extends ListTableModel {
    public TipsCashoutReportTableModel(List<TipsCashoutReportData> datas) {
        super(new String[]{"TICKET ID", "", "TICKET TOTAL", "TIPS"}, datas);
    }

    public TipsCashoutReportTableModel(List<TipsCashoutReportData> datas, String[] columnNames) {
        super(columnNames, datas);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TipsCashoutReportData data = (TipsCashoutReportData)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return data.getTicketId();
            }
            case 1: {
                return data.getSaleType();
            }
            case 2: {
                return NumberUtil.formatNumber(data.getTicketTotal());
            }
            case 3: {
                return NumberUtil.formatNumber(data.getTips());
            }
        }
        return null;
    }
}


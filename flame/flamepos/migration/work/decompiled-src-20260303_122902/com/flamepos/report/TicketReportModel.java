/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.util.NumberUtil;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TicketReportModel
extends AbstractTableModel {
    private static DecimalFormat formatter = new DecimalFormat("#,##0.00");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
    private String[] columnNames = new String[]{"id", "date", "tableNum", "status", "total"};
    private List<Ticket> items;
    private double grandTotal;

    @Override
    public int getRowCount() {
        if (this.items == null) {
            return 0;
        }
        return this.items.size();
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ticket ticket = this.items.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(ticket.getId());
            }
            case 1: {
                return dateFormat.format(ticket.getCreateDate());
            }
            case 2: {
                if (ticket.getTableNumbers().size() > 0) {
                    return String.valueOf(ticket.getTableNumbers());
                }
                return "";
            }
            case 3: {
                if (ticket.isClosed().booleanValue()) {
                    return POSConstants.CLOSED;
                }
                return POSConstants.OPEN;
            }
            case 4: {
                return NumberUtil.formatNumber(ticket.getTotalAmount());
            }
        }
        return null;
    }

    public List<Ticket> getItems() {
        return this.items;
    }

    public void setItems(List<Ticket> items) {
        this.items = items;
    }

    public String getGrandTotalAsString() {
        return formatter.format(this.grandTotal);
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void calculateGrandTotal() {
        this.grandTotal = 0.0;
        if (this.items == null) {
            return;
        }
        for (Ticket item : this.items) {
            this.grandTotal += item.getDueAmount().doubleValue();
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.report.ReportItem;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class SalesReportModel
extends AbstractTableModel {
    private static DecimalFormat formatter = new DecimalFormat("#,##0.00");
    private String[] columnNames = new String[]{"Id", "Name", "Price", "QTY", "Total", "Dis", "Tax", "Tax Total", "Gross Total"};
    private List<ReportItem> items;
    private double grandTotal;
    private double totalQuantity;
    private double taxTotal;
    private double grossTotal;
    private double discountTotal;
    private double itemTotal;

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
        ReportItem item = this.items.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return item.getUniqueId();
            }
            case 1: {
                return item.getName();
            }
            case 2: {
                return formatter.format(item.getPrice());
            }
            case 3: {
                return item.getQuantity();
            }
            case 4: {
                return formatter.format(item.getTotal());
            }
            case 5: {
                return String.valueOf(item.getDiscount());
            }
            case 6: {
                return String.valueOf(item.getTaxRate()) + "%";
            }
            case 7: {
                return formatter.format(item.getTaxTotal());
            }
            case 8: {
                return item.getGrossTotal();
            }
        }
        return null;
    }

    public double getGrossTotal() {
        return this.grossTotal;
    }

    public List<ReportItem> getItems() {
        return this.items;
    }

    public void setItems(List<ReportItem> items) {
        this.items = items;
    }

    public double getGrandTotal() {
        return this.grandTotal;
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
        for (ReportItem item : this.items) {
            this.grandTotal += item.getTotal();
        }
    }

    public String getTaxTotalAsString() {
        return formatter.format(this.taxTotal);
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    public void calculateTaxTotal() {
        this.taxTotal = 0.0;
        if (this.items == null) {
            return;
        }
        for (ReportItem item : this.items) {
            this.taxTotal += item.getTaxTotal();
        }
    }

    public double getGrossTotalAsDouble() {
        return this.grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public void calculateGrossTotal() {
        this.grossTotal = 0.0;
        if (this.items == null) {
            return;
        }
        for (ReportItem item : this.items) {
            this.grossTotal += item.getGrossTotal();
        }
    }

    public double getTotalQuantity() {
        return this.totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void calculateTotalQuantity() {
        this.totalQuantity = 0.0;
        if (this.items == null) {
            return;
        }
        for (ReportItem item : this.items) {
            this.totalQuantity += item.getQuantity();
        }
    }

    public void calculateTotal() {
        this.itemTotal = 0.0;
        if (this.items == null) {
            return;
        }
        for (ReportItem item : this.items) {
            this.itemTotal += item.getTotal();
        }
    }

    public String getTotalAsString() {
        return formatter.format(this.itemTotal);
    }

    public String getDiscountTotalAsString() {
        return String.valueOf(this.discountTotal);
    }

    public void setDiscountTotal(int discountTotal) {
        this.discountTotal = discountTotal;
    }

    public void calculateDiscountTotal() {
        this.discountTotal = 0.0;
        if (this.items == null) {
            return;
        }
        for (ReportItem item : this.items) {
            this.discountTotal += item.getDiscount();
        }
    }
}


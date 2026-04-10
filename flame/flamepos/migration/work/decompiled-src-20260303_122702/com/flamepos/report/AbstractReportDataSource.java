/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public abstract class AbstractReportDataSource
extends AbstractTableModel {
    protected String[] columnNames;
    protected List rows;

    public AbstractReportDataSource() {
    }

    public AbstractReportDataSource(String[] columnNames, List rows) {
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public AbstractReportDataSource(List rows) {
        this.rows = rows;
    }

    public AbstractReportDataSource(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        if (this.rows == null) {
            return 0;
        }
        return this.rows.size();
    }

    @Override
    public int getColumnCount() {
        if (this.columnNames == null) {
            return 0;
        }
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List getRows() {
        return this.rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}


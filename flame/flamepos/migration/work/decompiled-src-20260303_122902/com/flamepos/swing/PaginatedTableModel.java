/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.ListTableModel;
import java.util.List;

public abstract class PaginatedTableModel
extends ListTableModel {
    private int numRows;
    private int currentRowIndex;
    private int pageSize = 100;

    public PaginatedTableModel() {
    }

    public PaginatedTableModel(String[] columnNames, List rows) {
        super(columnNames, rows);
    }

    public PaginatedTableModel(String[] columnNames) {
        super(columnNames);
    }

    public int getNumRows() {
        return this.numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getCurrentRowIndex() {
        return this.currentRowIndex;
    }

    public void setCurrentRowIndex(int currentRowIndex) {
        this.currentRowIndex = currentRowIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean hasNext() {
        return this.currentRowIndex + this.pageSize < this.numRows;
    }

    public boolean hasPrevious() {
        return this.currentRowIndex > 0;
    }

    public int getNextRowIndex() {
        if (this.numRows == 0) {
            return 0;
        }
        return this.getCurrentRowIndex() + this.getPageSize();
    }

    public int getPreviousRowIndex() {
        int i = this.getCurrentRowIndex() - this.getPageSize();
        if (i < 0) {
            i = 0;
        }
        return i;
    }
}


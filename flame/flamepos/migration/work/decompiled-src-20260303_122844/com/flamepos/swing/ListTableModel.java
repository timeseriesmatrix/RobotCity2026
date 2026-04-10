/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public abstract class ListTableModel<E>
extends AbstractTableModel {
    protected String[] columnNames;
    protected List<E> rows;

    public ListTableModel() {
    }

    public ListTableModel(String[] names) {
        this.columnNames = names;
    }

    public ListTableModel(String[] names, List<E> rows) {
        this.columnNames = names;
        this.rows = rows;
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<E> getRows() {
        return this.rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
        this.fireTableDataChanged();
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
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public E getRowData(int row) {
        return this.rows.get(row);
    }

    public void addItem(E data) {
        if (this.rows == null) {
            this.rows = new ArrayList();
        }
        int size = this.rows.size();
        this.rows.add(data);
        this.fireTableRowsInserted(size, size);
    }

    public void deleteItem(int index) {
        this.rows.remove(index);
        this.fireTableRowsDeleted(index, index);
    }

    public boolean deleteItem(Object item) {
        return this.rows.remove(item);
    }

    public void updateItem(int index) {
        this.fireTableRowsUpdated(index, index);
    }
}


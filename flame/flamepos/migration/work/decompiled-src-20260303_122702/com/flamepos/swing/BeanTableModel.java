/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.PosLog;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class BeanTableModel<M>
extends AbstractTableModel {
    private List<M> rows = new ArrayList<M>();
    private List<BeanColumn> columns = new ArrayList<BeanColumn>();
    private Class<?> beanClass;

    public BeanTableModel(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void addColumn(String columnGUIName, String beanAttribute, EditMode editable) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(beanAttribute, this.beanClass);
            this.columns.add(new BeanColumn(columnGUIName, editable, descriptor));
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    public void addColumn(String columnGUIName, String beanAttribute) {
        this.addColumn(columnGUIName, beanAttribute, EditMode.NON_EDITABLE);
    }

    public void addRow(M row) {
        this.rows.add(row);
        this.fireTableDataChanged();
    }

    public void removeRow(M row) {
        this.rows.remove(row);
        this.fireTableDataChanged();
    }

    public void removeRow(int index) {
        this.rows.remove(index);
        this.fireTableRowsDeleted(index, index);
    }

    public void removeAll() {
        this.rows.clear();
        this.fireTableDataChanged();
    }

    public void addRows(List<M> rows) {
        if (rows == null) {
            return;
        }
        for (M row : rows) {
            this.addRow(row);
        }
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return this.columns.size();
    }

    @Override
    public int getRowCount() {
        return this.rows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BeanColumn column = this.columns.get(columnIndex);
        M row = this.rows.get(rowIndex);
        Object result = null;
        try {
            result = column.descriptor.getReadMethod().invoke(row, new Object[0]);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        return result;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        M row = this.rows.get(rowIndex);
        BeanColumn column = this.columns.get(columnIndex);
        try {
            column.descriptor.getWriteMethod().invoke(row, value);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        BeanColumn column = this.columns.get(columnIndex);
        Class<?> returnType = column.descriptor.getReadMethod().getReturnType();
        return returnType;
    }

    @Override
    public String getColumnName(int column) {
        return this.columns.get(column).columnGUIName;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return this.columns.get(columnIndex).editable == EditMode.EDITABLE;
    }

    public void setRow(int index, M row) {
        this.getRows().set(index, row);
    }

    public M getRow(int index) {
        return this.getRows().get(index);
    }

    public List<M> getRows() {
        return this.rows;
    }

    private static class BeanColumn {
        private String columnGUIName;
        private EditMode editable;
        private PropertyDescriptor descriptor;

        public BeanColumn(String columnGUIName, EditMode editable, PropertyDescriptor descriptor) {
            this.columnGUIName = columnGUIName;
            this.editable = editable;
            this.descriptor = descriptor;
        }
    }

    public static enum EditMode {
        NON_EDITABLE,
        EDITABLE;

    }
}


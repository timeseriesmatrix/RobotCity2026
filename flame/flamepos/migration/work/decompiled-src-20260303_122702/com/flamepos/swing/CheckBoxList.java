/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CheckBoxList<E>
extends JTable {
    public CheckBoxList() {
    }

    public CheckBoxList(E[] items) {
        this.setModel(items);
    }

    public CheckBoxList(List<E> items) {
        this.setModel(items);
    }

    public void setModel(E[] items) {
        this.setModel(new CheckBoxListModel<E>(items));
        this.init();
    }

    public void setModel(List<E> items) {
        this.setModel(new CheckBoxListModel<E>(items));
        this.init();
    }

    public List<E> getCheckedValues() {
        ArrayList values = new ArrayList();
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            Entry entry = model.items.get(i);
            if (!entry.checked) continue;
            values.add(entry.value);
        }
        return values;
    }

    @Override
    public void selectAll() {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            Entry entry = model.items.get(i);
            entry.checked = true;
        }
        model.fireTableRowsUpdated(0, model.getRowCount());
    }

    public void selectItems(List types) {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        if (types != null) {
            block0: for (int i = 0; i < model.items.size(); ++i) {
                Entry entry = model.items.get(i);
                for (int j = 0; j < types.size(); ++j) {
                    Object type = types.get(j);
                    if (!type.equals(entry.value)) continue;
                    entry.checked = true;
                    continue block0;
                }
            }
            model.fireTableRowsUpdated(0, model.getRowCount());
        }
    }

    public void unCheckAll() {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            Entry entry = model.items.get(i);
            entry.checked = false;
        }
        model.fireTableRowsUpdated(0, model.getRowCount());
    }

    public Entry[] getValues() {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        return (Entry[])model.items.toArray();
    }

    public Object getSelectedValue() {
        int row = this.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return this.getModel().getValueAt(row, 1);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer cellRenderer = super.getCellRenderer(row, column);
        if (cellRenderer instanceof JCheckBox) {
            ((JCheckBox)((Object)cellRenderer)).setEnabled(this.isEnabled());
        }
        return cellRenderer;
    }

    public void init() {
        this.getSelectionModel().setSelectionMode(0);
        this.setShowGrid(false);
        this.setAutoResizeMode(3);
        TableColumn column = this.getColumnModel().getColumn(0);
        int checkBoxWidth = new JCheckBox().getPreferredSize().width;
        column.setPreferredWidth(checkBoxWidth);
        column.setMinWidth(checkBoxWidth);
        column.setWidth(checkBoxWidth);
        column.setMaxWidth(checkBoxWidth);
        column.setResizable(false);
        this.setTableHeader(null);
    }

    public static class CheckBoxListModel<E>
    extends AbstractTableModel {
        List<Entry<E>> items;

        protected CheckBoxListModel(List<E> _items) {
            this.items = new ArrayList<Entry<E>>(_items.size());
            for (int i = 0; i < _items.size(); ++i) {
                this.items.add(this.createEntry(_items.get(i)));
            }
        }

        CheckBoxListModel(E[] _items) {
            this.items = new ArrayList<Entry<E>>(_items.length);
            for (int i = 0; i < _items.length; ++i) {
                this.items.add(this.createEntry(_items[i]));
            }
        }

        protected Entry createEntry(E obj) {
            if (obj instanceof Entry) {
                return (Entry)obj;
            }
            return new Entry<E>(false, obj);
        }

        @Override
        public int getRowCount() {
            return this.items.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int col) {
            return null;
        }

        @Override
        public Object getValueAt(int row, int col) {
            Entry<E> entry = this.items.get(row);
            switch (col) {
                case 0: {
                    return entry.checked;
                }
                case 1: {
                    return entry.value;
                }
            }
            throw new InternalError();
        }

        public Class getColumnClass(int col) {
            switch (col) {
                case 0: {
                    return Boolean.class;
                }
                case 1: {
                    return String.class;
                }
            }
            throw new InternalError();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) {
                Entry<E> entry = this.items.get(row);
                entry.checked = value.equals(Boolean.TRUE);
                this.fireTableRowsUpdated(row, row);
            }
        }

        public List<Entry<E>> getItems() {
            return this.items;
        }

        public void setItems(List<Entry<E>> items) {
            this.items = items;
        }
    }

    public static class Entry<E> {
        public boolean checked;
        public E value;

        public Entry(boolean checked, E value) {
            this.checked = checked;
            this.value = value;
        }

        public boolean isChecked() {
            return this.checked;
        }

        public Object getValue() {
            return this.value;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setValue(E value) {
            this.value = value;
        }
    }
}


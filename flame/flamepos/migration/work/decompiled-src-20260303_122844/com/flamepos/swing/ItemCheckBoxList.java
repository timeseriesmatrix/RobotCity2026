/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.swing.CheckBoxList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ItemCheckBoxList<E>
extends CheckBoxList {
    public void setModel(List items) {
        this.setModel(new CheckBoxListModel(items));
        this.init();
    }

    public List getCheckedValues() {
        ArrayList values = new ArrayList();
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            CheckBoxList.Entry entry = model.items.get(i);
            if (!entry.checked) continue;
            values.add(entry.value);
        }
        return values;
    }

    public List getAllValues() {
        ArrayList values = new ArrayList();
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            CheckBoxList.Entry entry = model.items.get(i);
            values.add(entry.value);
        }
        return values;
    }

    @Override
    public void unCheckAll() {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        for (int i = 0; i < model.items.size(); ++i) {
            CheckBoxList.Entry entry = model.items.get(i);
            entry.checked = false;
        }
        model.fireTableRowsUpdated(0, model.getRowCount());
    }

    public void setSelected(Object type) {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        if (type != null) {
            for (int i = 0; i < model.items.size(); ++i) {
                CheckBoxList.Entry entry = model.items.get(i);
                if (!type.equals(entry.value)) continue;
                entry.checked = true;
                break;
            }
            model.fireTableRowsUpdated(0, model.getRowCount());
        }
    }

    @Override
    public void selectItems(List types) {
        CheckBoxListModel model = (CheckBoxListModel)this.getModel();
        if (types != null) {
            block0: for (int i = 0; i < model.items.size(); ++i) {
                CheckBoxList.Entry entry = model.items.get(i);
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

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 0) {
            return super.getCellRenderer(row, column);
        }
        if (column == 1) {
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(2);
            this.getColumnModel().getColumn(column).setCellRenderer(center);
            return super.getCellRenderer(row, column);
        }
        if (column == 2) {
            DefaultTableCellRenderer right = new DefaultTableCellRenderer();
            right.setHorizontalAlignment(4);
            this.getColumnModel().getColumn(column).setCellRenderer(right);
            this.getColumnModel().getColumn(column).setPreferredWidth(15);
            return super.getCellRenderer(row, column);
        }
        return super.getCellRenderer(row, column);
    }

    @Override
    public void init() {
        this.getSelectionModel().setSelectionMode(0);
        this.setShowGrid(true);
        this.setRowHeight(25);
        TableColumn column = this.getColumnModel().getColumn(0);
        int checkBoxWidth = new JCheckBox().getPreferredSize().width;
        column.setPreferredWidth(checkBoxWidth);
        column.setMinWidth(checkBoxWidth);
        column.setWidth(checkBoxWidth);
        column.setMaxWidth(checkBoxWidth);
        column.setResizable(false);
    }

    public static class CheckBoxListModel<E>
    extends CheckBoxList.CheckBoxListModel<E> {
        List<CheckBoxList.Entry<E>> items;

        CheckBoxListModel(List<E> _items) {
            super(_items);
            this.items = new ArrayList<CheckBoxList.Entry<E>>(_items.size());
            for (int i = 0; i < _items.size(); ++i) {
                this.items.add(this.createEntry(_items.get(i)));
            }
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0: {
                    return "ALL";
                }
                case 1: {
                    return "Name";
                }
                case 2: {
                    return "Price";
                }
            }
            return null;
        }

        @Override
        public Object getValueAt(int row, int col) {
            CheckBoxList.Entry<E> entry = this.items.get(row);
            switch (col) {
                case 0: {
                    return entry.checked;
                }
                case 1: {
                    if (entry.value instanceof MenuItem) {
                        return ((MenuItem)entry.value).getName();
                    }
                    if (entry.value instanceof MenuGroup) {
                        return ((MenuGroup)entry.value).getName();
                    }
                    if (entry.value instanceof MenuCategory) {
                        return ((MenuCategory)entry.value).getName();
                    }
                    return entry.value;
                }
                case 2: {
                    return ((MenuItem)entry.value).getPrice();
                }
            }
            throw new InternalError();
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) {
                CheckBoxList.Entry<E> entry = this.items.get(row);
                entry.checked = value.equals(Boolean.TRUE);
                this.fireTableRowsUpdated(row, row);
            }
        }
    }
}


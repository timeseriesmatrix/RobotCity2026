/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.multipart;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.PizzaModifierPrice;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class PizzaPriceTableModel
extends AbstractTableModel {
    private List<PizzaModifierPrice> priceList;
    private String[] columnNames;
    private Class[] columnClass;

    public PizzaPriceTableModel(List<PizzaModifierPrice> priceList, List<Multiplier> multipliers) {
        this.columnNames = new String[multipliers.size() + 1];
        this.columnClass = new Class[multipliers.size() + 1];
        this.columnNames[0] = "Size";
        this.columnClass[0] = String.class;
        int index = 1;
        for (Multiplier multiplier : multipliers) {
            this.columnNames[index] = multiplier.getName();
            this.columnClass[index] = Double.class;
            ++index;
        }
        this.priceList = priceList;
        for (PizzaModifierPrice price : priceList) {
            price.initializeSizeAndPriceList(multipliers);
        }
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.columnClass[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public int getRowCount() {
        return this.priceList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PizzaModifierPrice row = this.priceList.get(rowIndex);
        if (row == null) {
            return null;
        }
        if (0 == columnIndex) {
            return row.getSize().getName();
        }
        ModifierMultiplierPrice price = row.getMultiplier(this.columnNames[columnIndex]);
        return price.getPrice();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PizzaModifierPrice row = this.priceList.get(rowIndex);
        if (0 != columnIndex) {
            ModifierMultiplierPrice price = row.getMultiplier(this.columnNames[columnIndex]);
            price.setPrice((Double)aValue);
        }
    }

    public List<PizzaModifierPrice> getRows(MenuModifier modifier) {
        for (PizzaModifierPrice pizzaModifierPrice : this.priceList) {
            pizzaModifierPrice.populateMultiplierPriceListRowValue(modifier);
        }
        return this.priceList;
    }
}


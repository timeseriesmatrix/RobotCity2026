/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.Messages;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ModifierViewerTableModel
extends AbstractTableModel {
    protected TicketItem ticketItem;
    private final List<ITicketItem> tableRows = new ArrayList<ITicketItem>();
    private boolean priceIncludesTax = false;
    protected String[] columnNames = new String[]{Messages.getString("TicketViewerTableModel.0"), Messages.getString("TicketViewerTableModel.3")};
    private boolean forReciptPrint;
    private boolean printCookingInstructions;

    public ModifierViewerTableModel(TicketItem ticketItem) {
        this.setTicketItem(ticketItem);
    }

    public int getItemCount() {
        return this.tableRows.size();
    }

    @Override
    public int getRowCount() {
        int size = this.tableRows.size();
        return size;
    }

    public int getActualRowCount() {
        return this.tableRows.size();
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
        ITicketItem ticketItem = this.tableRows.get(rowIndex);
        if (ticketItem == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return ticketItem.getNameDisplay();
            }
            case 1: {
                return ticketItem.getSubTotalAmountWithoutModifiersDisplay();
            }
        }
        return null;
    }

    private void calculateRows() {
        this.tableRows.clear();
        this.calculateRowsForModifiers();
        this.calculateRowsForAddOns();
    }

    private void calculateRowsForAddOns() {
        List<TicketItemModifier> addOns = this.ticketItem.getAddOns();
        if (addOns == null) {
            return;
        }
        for (TicketItemModifier ticketItemModifier : addOns) {
            this.tableRows.add(ticketItemModifier);
        }
    }

    private void calculateRowsForModifiers() {
        List<TicketItemModifier> ticketItemModifiers = this.ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers == null) {
            return;
        }
        for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
            this.tableRows.add(ticketItemModifier);
        }
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifierToDelete) {
    }

    public Object delete(int index) {
        if (index < 0 || index >= this.tableRows.size()) {
            return null;
        }
        TicketItemModifier ticketItemModifier = (TicketItemModifier)this.tableRows.remove(index);
        if (ticketItemModifier.getModifierType() == 3) {
            ticketItemModifier.getTicketItem().removeAddOn(ticketItemModifier);
        } else {
            this.ticketItem.removeTicketItemModifier(ticketItemModifier);
        }
        this.fireTableRowsDeleted(index, index);
        return ticketItemModifier;
    }

    public Object get(int index) {
        if (index < 0 || index >= this.tableRows.size()) {
            return null;
        }
        return this.tableRows.get(index);
    }

    public TicketItem getTicketItem() {
        return this.ticketItem;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
        this.update();
    }

    public void update() {
        this.calculateRows();
        this.fireTableDataChanged();
    }

    public boolean isForReciptPrint() {
        return this.forReciptPrint;
    }

    public void setForReciptPrint(boolean forReciptPrint) {
        this.forReciptPrint = forReciptPrint;
    }

    public boolean isPrintCookingInstructions() {
        return this.printCookingInstructions;
    }

    public void setPrintCookingInstructions(boolean printCookingInstructions) {
        this.printCookingInstructions = printCookingInstructions;
    }

    public boolean isPriceIncludesTax() {
        return this.priceIncludesTax;
    }

    public void setPriceIncludesTax(boolean priceIncludesTax) {
        this.priceIncludesTax = priceIncludesTax;
    }
}


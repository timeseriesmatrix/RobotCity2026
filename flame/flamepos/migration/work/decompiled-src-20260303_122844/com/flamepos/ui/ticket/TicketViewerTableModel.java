/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.ticket;

import com.floreantpos.Messages;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.ui.ticket.TicketItemRowCreator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TicketViewerTableModel
extends AbstractTableModel {
    private JTable table;
    protected Ticket ticket;
    private double previousFractionalItemQuantity;
    protected final HashMap<String, ITicketItem> tableRows = new LinkedHashMap<String, ITicketItem>();
    private boolean priceIncludesTax = false;
    protected String[] columnNames = new String[]{Messages.getString("TicketViewerTableModel.2"), Messages.getString("TicketViewerTableModel.0"), Messages.getString("TicketViewerTableModel.3")};
    private boolean forReciptPrint;
    private boolean printCookingInstructions;

    public TicketViewerTableModel(JTable table) {
        this(table, null);
    }

    public TicketViewerTableModel(JTable table, Ticket ticket) {
        this.table = table;
        this.setTicket(ticket);
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
        ITicketItem ticketItem = this.tableRows.get(String.valueOf(rowIndex));
        if (ticketItem == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return ticketItem.getItemQuantityDisplay();
            }
            case 1: {
                return ticketItem.getNameDisplay();
            }
            case 2: {
                return ticketItem.getSubTotalAmountDisplay();
            }
        }
        return null;
    }

    private void calculateRows() {
        TicketItemRowCreator.calculateTicketRows(this.ticket, this.tableRows);
    }

    public int addTicketItem(TicketItem ticketItem) {
        if (ticketItem.isHasModifiers().booleanValue()) {
            return this.addTicketItemToTicket(ticketItem);
        }
        Object[] values = this.tableRows.values().toArray();
        if (values == null || values.length == 0) {
            this.previousFractionalItemQuantity = ticketItem.getItemQuantity();
            return this.addTicketItemToTicket(ticketItem);
        }
        Object object = values[values.length - 1];
        if (object instanceof TicketItem) {
            TicketItem item = (TicketItem)object;
            if (ticketItem.getItemId() == 0) {
                return this.addTicketItemToTicket(ticketItem);
            }
            if (ticketItem.getItemId().equals(item.getItemId()) && !item.isPrintedToKitchen().booleanValue() && !item.isInventoryHandled().booleanValue()) {
                if (ticketItem.isFractionalUnit().booleanValue()) {
                    item.setItemQuantity(this.previousFractionalItemQuantity + ticketItem.getItemQuantity());
                    this.previousFractionalItemQuantity = item.getItemQuantity();
                } else {
                    item.setItemCount(item.getItemCount() + 1);
                }
                return values.length - 1;
            }
        }
        this.previousFractionalItemQuantity = ticketItem.getItemQuantity();
        return this.addTicketItemToTicket(ticketItem);
    }

    private int addTicketItemToTicket(TicketItem ticketItem) {
        this.ticket.addToticketItems(ticketItem);
        this.calculateRows();
        this.fireTableDataChanged();
        return this.tableRows.size() - 1;
    }

    public void addAllTicketItem(TicketItem ticketItem) {
        if (ticketItem.isHasModifiers().booleanValue()) {
            List<TicketItem> ticketItems = this.ticket.getTicketItems();
            ticketItems.add(ticketItem);
            this.calculateRows();
            this.fireTableDataChanged();
        } else {
            List<TicketItem> ticketItems = this.ticket.getTicketItems();
            boolean exists = false;
            for (TicketItem item : ticketItems) {
                if (!item.getName().equals(ticketItem.getName())) continue;
                int itemCount = item.getItemCount();
                item.setItemCount(itemCount += ticketItem.getItemCount().intValue());
                exists = true;
                this.table.repaint();
                return;
            }
            if (!exists) {
                this.ticket.addToticketItems(ticketItem);
                this.calculateRows();
                this.fireTableDataChanged();
            }
        }
    }

    public boolean containsTicketItem(TicketItem ticketItem) {
        if (ticketItem.isHasModifiers().booleanValue()) {
            return false;
        }
        List<TicketItem> ticketItems = this.ticket.getTicketItems();
        for (TicketItem item : ticketItems) {
            if (!item.getName().equals(ticketItem.getName())) continue;
            return true;
        }
        return false;
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifierToDelete) {
        List<TicketItemModifier> ticketItemModifiers = parent.getTicketItemModifiers();
        Iterator<TicketItemModifier> iter = ticketItemModifiers.iterator();
        while (iter.hasNext()) {
            TicketItemModifier modifier = iter.next();
            if (modifier.getModifierId() != modifierToDelete.getModifierId()) continue;
            iter.remove();
            if (modifier.isPrintedToKitchen().booleanValue()) {
                this.ticket.addDeletedItems(modifier);
            }
            this.calculateRows();
            this.fireTableDataChanged();
            return;
        }
    }

    public Object delete(int index) {
        if (index < 0 || index >= this.tableRows.size()) {
            return null;
        }
        ITicketItem object = this.tableRows.get(String.valueOf(index));
        if (object instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)object;
            int rowNum = ticketItem.getTableRowNum();
            List<TicketItem> ticketItems = this.ticket.getTicketItems();
            Iterator<TicketItem> iter = ticketItems.iterator();
            while (iter.hasNext()) {
                TicketItem item = iter.next();
                if (item.getTableRowNum() != rowNum) continue;
                iter.remove();
                if (item.isPrintedToKitchen().booleanValue() || item.isInventoryHandled().booleanValue()) {
                    this.ticket.addDeletedItems(item);
                }
                break;
            }
        } else if (!(object instanceof TicketItemModifier) && object instanceof TicketItemCookingInstruction) {
            TicketItemCookingInstruction cookingInstruction = (TicketItemCookingInstruction)object;
            int tableRowNum = cookingInstruction.getTableRowNum();
            TicketItem ticketItem = null;
            while (tableRowNum > 0) {
                ITicketItem object2;
                if (!((object2 = this.tableRows.get(String.valueOf(--tableRowNum))) instanceof TicketItem)) continue;
                ticketItem = (TicketItem)object2;
                break;
            }
            if (ticketItem != null) {
                ticketItem.removeCookingInstruction(cookingInstruction);
            }
        }
        this.calculateRows();
        this.fireTableDataChanged();
        return object;
    }

    public Object get(int index) {
        if (index < 0 || index >= this.tableRows.size()) {
            return null;
        }
        return this.tableRows.get(String.valueOf(index));
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
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


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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TodoTicketViewerTableModel
extends AbstractTableModel {
    private JTable table;
    protected Ticket ticket;
    private List<ITicketItem> items = new ArrayList<ITicketItem>();
    protected String[] columnNames = new String[]{Messages.getString("TodoTicketViewerTableModel.0"), Messages.getString("TodoTicketViewerTableModel.1"), Messages.getString("TodoTicketViewerTableModel.2"), Messages.getString("TodoTicketViewerTableModel.3"), Messages.getString("TodoTicketViewerTableModel.4")};
    private boolean forReciptPrint;
    private boolean printCookingInstructions;

    public TodoTicketViewerTableModel() {
    }

    public TodoTicketViewerTableModel(Ticket ticket) {
        this.setTicket(ticket);
    }

    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getRowCount() {
        return this.items.size();
    }

    public int getActualRowCount() {
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
        ITicketItem ticketItem = this.items.get(rowIndex);
        if (ticketItem == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return ticketItem.getNameDisplay();
            }
            case 1: {
                return ticketItem.getUnitPriceDisplay();
            }
            case 2: {
                return ticketItem.getItemQuantityDisplay();
            }
            case 3: {
                return ticketItem.getTaxAmountWithoutModifiersDisplay();
            }
            case 4: {
                return ticketItem.getTotalAmountWithoutModifiersDisplay();
            }
        }
        return null;
    }

    private void calculateRows() {
        this.items.clear();
        if (this.ticket == null || this.ticket.getTicketItems() == null) {
            return;
        }
        List<TicketItem> ticketItems = this.ticket.getTicketItems();
        for (TicketItem ticketItem : ticketItems) {
            List<TicketItemCookingInstruction> cookingInstructions;
            this.items.add(ticketItem);
            List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
            if (ticketItemModifiers != null) {
                for (TicketItemModifier itemModifier : ticketItemModifiers) {
                    this.items.add(itemModifier);
                }
            }
            if ((cookingInstructions = ticketItem.getCookingInstructions()) == null) continue;
            for (TicketItemCookingInstruction ticketItemCookingInstruction : cookingInstructions) {
                this.items.add(ticketItemCookingInstruction);
            }
        }
    }

    public int addTicketItem(TicketItem ticketItem) {
        if (ticketItem.isHasModifiers().booleanValue()) {
            return this.addTicketItemToTicket(ticketItem);
        }
        for (int row = 0; row < this.items.size(); ++row) {
            ITicketItem iTicketItem = this.items.get(row);
            if (!(iTicketItem instanceof TicketItem)) continue;
            TicketItem t = (TicketItem)iTicketItem;
            if (!ticketItem.getName().equals(t.getName()) || t.isPrintedToKitchen().booleanValue()) continue;
            t.setItemCount(t.getItemCount() + 1);
            this.table.repaint();
            return row;
        }
        return this.addTicketItemToTicket(ticketItem);
    }

    private int addTicketItemToTicket(TicketItem ticketItem) {
        this.ticket.addToticketItems(ticketItem);
        this.calculateRows();
        this.fireTableDataChanged();
        return this.items.size() - 1;
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
        TicketItemModifier itemModifier;
        List<TicketItemModifier> ticketItemModifiers;
        if (index < 0 || index >= this.items.size()) {
            return null;
        }
        ITicketItem iTicketItem = this.items.get(index);
        if (iTicketItem instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)iTicketItem;
            int rowNum = ticketItem.getTableRowNum();
            List<TicketItem> ticketItems = this.ticket.getTicketItems();
            Iterator<TicketItem> iter = ticketItems.iterator();
            while (iter.hasNext()) {
                TicketItem item = iter.next();
                if (item.getTableRowNum() != rowNum) continue;
                iter.remove();
                if (!item.isPrintedToKitchen().booleanValue() && !item.isInventoryHandled().booleanValue()) break;
                this.ticket.addDeletedItems(item);
                break;
            }
        } else if (iTicketItem instanceof TicketItemModifier && (ticketItemModifiers = (itemModifier = (TicketItemModifier)iTicketItem).getTicketItem().getTicketItemModifiers()) != null) {
            Iterator<TicketItemModifier> iterator = ticketItemModifiers.iterator();
            while (iterator.hasNext()) {
                TicketItemModifier element = iterator.next();
                if (itemModifier.getTableRowNum() != element.getTableRowNum()) continue;
                iterator.remove();
                if (!element.isPrintedToKitchen().booleanValue()) continue;
                this.ticket.addDeletedItems(element);
            }
        }
        this.calculateRows();
        this.fireTableDataChanged();
        return iTicketItem;
    }

    public Object get(int index) {
        return null;
    }

    public JTable getTable() {
        return this.table;
    }

    public void setTable(JTable table) {
        this.table = table;
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
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.ticket;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.ticket.TicketViewerTableCellRenderer;
import com.floreantpos.ui.ticket.TicketViewerTableModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TicketViewerTable
extends JTable {
    private TicketViewerTableModel model = new TicketViewerTableModel(this);
    private DefaultListSelectionModel selectionModel;
    private TicketViewerTableCellRenderer cellRenderer;

    public TicketViewerTable() {
        this((Ticket)null);
    }

    public TicketViewerTable(Ticket ticket) {
        this.setModel(this.model);
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionModel.setSelectionMode(0);
        this.cellRenderer = new TicketViewerTableCellRenderer();
        this.setGridColor(Color.LIGHT_GRAY);
        this.setSelectionModel(this.selectionModel);
        this.setAutoscrolls(true);
        this.setShowGrid(true);
        this.setBorder(null);
        this.setFocusable(false);
        this.setRowHeight(50);
        this.resizeTableColumns();
        this.setTicket(ticket);
    }

    private void resizeTableColumns() {
        this.setAutoResizeMode(4);
        this.setColumnWidth(0, PosUIManager.getSize(50));
        this.setColumnWidth(2, PosUIManager.getSize(60));
    }

    private void setColumnWidth(int columnNumber, int width) {
        TableColumn column = this.getColumnModel().getColumn(columnNumber);
        column.setPreferredWidth(width);
        column.setMaxWidth(width);
        column.setMinWidth(width);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return this.cellRenderer;
    }

    public TicketViewerTableCellRenderer getRenderer() {
        return this.cellRenderer;
    }

    private boolean isTicketNull() {
        Ticket ticket = this.getTicket();
        if (ticket == null) {
            return true;
        }
        return ticket.getTicketItems() == null;
    }

    public void scrollUp() {
        int rowCount;
        if (this.isTicketNull()) {
            return;
        }
        int selectedRow = this.getSelectedRow();
        if (selectedRow > (rowCount = this.model.getItemCount()) - 1) {
            return;
        }
        if (--selectedRow < 0) {
            selectedRow = 0;
        }
        this.selectionModel.addSelectionInterval(selectedRow, selectedRow);
        Rectangle cellRect = this.getCellRect(selectedRow, 0, false);
        this.scrollRectToVisible(cellRect);
    }

    public void scrollDown() {
        if (this.isTicketNull()) {
            return;
        }
        int selectedRow = this.getSelectedRow();
        if (selectedRow >= this.model.getItemCount() - 1) {
            return;
        }
        this.selectionModel.addSelectionInterval(++selectedRow, selectedRow);
        Rectangle cellRect = this.getCellRect(selectedRow, 0, false);
        this.scrollRectToVisible(cellRect);
    }

    private boolean isModifierOrOther(int selectedRow) {
        ITicketItem selectedItem = this.get(selectedRow);
        return !(selectedItem instanceof TicketItem);
    }

    public void increaseItemAmount(TicketItem ticketItem) {
        int itemCount = ticketItem.getItemCount();
        ticketItem.setItemCount(++itemCount);
        this.repaint();
    }

    public boolean increaseFractionalUnit(double selectedQuantity) {
        int selectedRow = this.getSelectedRow();
        if (selectedRow < 0) {
            return false;
        }
        if (selectedRow >= this.model.getItemCount()) {
            return false;
        }
        Object object = this.model.get(selectedRow);
        if (object instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)object;
            ticketItem.setItemQuantity(selectedQuantity);
            return true;
        }
        return false;
    }

    public boolean increaseItemAmount() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow < 0) {
            return false;
        }
        if (selectedRow >= this.model.getItemCount()) {
            return false;
        }
        ITicketItem iTicketItem = (ITicketItem)this.model.get(selectedRow);
        if (iTicketItem.isPrintedToKitchen().booleanValue()) {
            return false;
        }
        if (iTicketItem instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)iTicketItem;
            int itemCount = ticketItem.getItemCount();
            ticketItem.setItemCount(++itemCount);
            return true;
        }
        return false;
    }

    public boolean decreaseItemAmount() {
        int selectedRow = this.getSelectedRow();
        if (selectedRow < 0) {
            return false;
        }
        if (selectedRow >= this.model.getItemCount()) {
            return false;
        }
        ITicketItem iTicketItem = (ITicketItem)this.model.get(selectedRow);
        if (iTicketItem.isPrintedToKitchen().booleanValue()) {
            return false;
        }
        if (iTicketItem instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)iTicketItem;
            int itemCount = ticketItem.getItemCount();
            if (itemCount == 1) {
                this.model.delete(selectedRow);
                return true;
            }
            ticketItem.setItemCount(--itemCount);
            return true;
        }
        return false;
    }

    public void setTicket(Ticket ticket) {
        this.model.setTicket(ticket);
    }

    public Ticket getTicket() {
        return this.model.getTicket();
    }

    public void addTicketItem(TicketItem ticketItem) {
        int addTicketItem;
        ticketItem.setTicket(this.getTicket());
        int actualRowCount = addTicketItem = this.model.addTicketItem(ticketItem);
        this.selectionModel.addSelectionInterval(actualRowCount, actualRowCount);
        Rectangle cellRect = this.getCellRect(actualRowCount, 0, false);
        this.scrollRectToVisible(cellRect);
    }

    public Object deleteSelectedItem() {
        int selectedRow = this.getSelectedRow();
        Object delete = this.model.delete(selectedRow);
        return delete;
    }

    public boolean containsTicketItem(TicketItem ticketItem) {
        return this.model.containsTicketItem(ticketItem);
    }

    public void delete(int index) {
        this.model.delete(index);
    }

    public ITicketItem get(int index) {
        return (ITicketItem)this.model.get(index);
    }

    public ITicketItem getSelected() {
        int index = this.getSelectedRow();
        return (ITicketItem)this.model.get(index);
    }

    public void addAllTicketItem(TicketItem ticketItem) {
        this.model.addAllTicketItem(ticketItem);
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifier) {
        this.model.removeModifier(parent, modifier);
    }

    public void updateView() {
        int selectedRow = this.getSelectedRow();
        this.model.update();
        try {
            this.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int getActualRowCount() {
        return this.model.getActualRowCount();
    }

    public void selectLast() {
        int actualRowCount = this.getActualRowCount() - 1;
        this.selectionModel.addSelectionInterval(actualRowCount, actualRowCount);
        Rectangle cellRect = this.getCellRect(actualRowCount, 0, false);
        this.scrollRectToVisible(cellRect);
    }

    public void selectRow(int index) {
        if (index < 0 || index >= this.getActualRowCount()) {
            index = 0;
        }
        this.selectionModel.addSelectionInterval(index, index);
        Rectangle cellRect = this.getCellRect(index, 0, false);
        this.scrollRectToVisible(cellRect);
    }

    @Override
    public TicketViewerTableModel getModel() {
        return this.model;
    }

    private List<TicketItem> getRowByValue(TicketViewerTableModel model) {
        ArrayList<TicketItem> ticketItems = new ArrayList<TicketItem>();
        for (int i = 0; i <= model.getRowCount(); ++i) {
            Object value = model.get(i);
            if (!(value instanceof TicketItem)) continue;
            TicketItem ticketItem = (TicketItem)value;
            ticketItems.add(ticketItem);
        }
        return ticketItems;
    }

    public List<TicketItem> getTicketItems() {
        return this.getRowByValue(this.model);
    }

    public TicketItem getTicketItem() {
        return (TicketItem)this.getSelected();
    }
}


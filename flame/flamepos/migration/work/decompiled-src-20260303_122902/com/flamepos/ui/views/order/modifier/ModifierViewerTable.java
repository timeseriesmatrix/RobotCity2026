/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.ui.views.order.modifier.ModifierViewerTableCellRenderer;
import com.floreantpos.ui.views.order.modifier.ModifierViewerTableModel;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ModifierViewerTable
extends JTable {
    private ModifierViewerTableModel model;
    private DefaultListSelectionModel selectionModel;
    private ModifierViewerTableCellRenderer cellRenderer;

    public ModifierViewerTable() {
        this((TicketItem)null);
    }

    public ModifierViewerTable(TicketItem ticketItem) {
        this.model = new ModifierViewerTableModel(ticketItem);
        this.setModel(this.model);
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionModel.setSelectionMode(0);
        this.cellRenderer = new ModifierViewerTableCellRenderer();
        this.setGridColor(Color.LIGHT_GRAY);
        this.setSelectionModel(this.selectionModel);
        this.setAutoscrolls(true);
        this.setShowGrid(true);
        this.setBorder(null);
        this.setFocusable(false);
        this.setRowHeight(60);
        this.resizeTableColumns();
    }

    private void resizeTableColumns() {
        this.setAutoResizeMode(4);
        this.setColumnWidth(1, 60);
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

    public ModifierViewerTableCellRenderer getRenderer() {
        return this.cellRenderer;
    }

    private boolean isTicketNull() {
        return false;
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
        this.selectionModel.addSelectionInterval(--selectedRow, selectedRow);
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

    public void increaseItemAmount(TicketItem ticketItem) {
        int itemCount = ticketItem.getItemCount();
        ticketItem.setItemCount(++itemCount);
        this.repaint();
    }

    public boolean increaseItemAmount() {
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
            int itemCount = ticketItem.getItemCount();
            ticketItem.setItemCount(++itemCount);
            this.repaint();
            return true;
        }
        if (object instanceof TicketItemModifier) {
            return false;
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
        Object object = this.model.get(selectedRow);
        if (object instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)object;
            int itemCount = ticketItem.getItemCount();
            if (itemCount == 1) {
                return false;
            }
            ticketItem.setItemCount(--itemCount);
            this.repaint();
            return true;
        }
        if (object instanceof TicketItemModifier) {
            TicketItemModifier modifier = (TicketItemModifier)object;
            int itemCount = modifier.getItemCount();
            if (itemCount == 1) {
                return false;
            }
            modifier.setItemCount(--itemCount);
            this.repaint();
            return true;
        }
        return false;
    }

    public Object deleteSelectedItem() {
        int selectedRow = this.getSelectedRow();
        Object delete = this.model.delete(selectedRow);
        return delete;
    }

    public void delete(int index) {
        this.model.delete(index);
    }

    public Object get(int index) {
        return this.model.get(index);
    }

    public Object getSelected() {
        int index = this.getSelectedRow();
        return this.model.get(index);
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifier) {
        this.model.removeModifier(parent, modifier);
    }

    public void updateView() {
        this.model.update();
        try {
            int actualRowCount = this.model.getRowCount() - 1;
            this.selectionModel.addSelectionInterval(actualRowCount, actualRowCount);
            Rectangle cellRect = this.getCellRect(actualRowCount, 0, false);
            this.scrollRectToVisible(cellRect);
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

    public boolean isAddOnMode() {
        return false;
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.ticket;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.ui.ticket.MultiLineTableCellRenderer;
import com.floreantpos.ui.ticket.TicketViewerTableModel;
import com.floreantpos.util.NumberUtil;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TicketViewerTableCellRenderer
extends DefaultTableCellRenderer {
    private boolean inTicketScreen = false;
    MultiLineTableCellRenderer multiLineTableCellRenderer = new MultiLineTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ITicketItem ticketItem;
        Component rendererComponent = null;
        TicketViewerTableModel model = (TicketViewerTableModel)table.getModel();
        Object object = model.get(row);
        if (column == 1) {
            rendererComponent = this.multiLineTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else {
            rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            if (column == 0) {
                this.setHorizontalAlignment(0);
            } else {
                this.setHorizontalAlignment(4);
            }
        }
        if (!this.inTicketScreen || isSelected) {
            return rendererComponent;
        }
        rendererComponent.setBackground(table.getBackground());
        if (object instanceof ITicketItem && (ticketItem = (ITicketItem)object).isPrintedToKitchen().booleanValue()) {
            rendererComponent.setBackground(Color.YELLOW);
        }
        return rendererComponent;
    }

    @Override
    protected void setValue(Object value) {
        if (value == null) {
            this.setText("");
            return;
        }
        String text = value.toString();
        if (value instanceof String) {
            this.setHorizontalAlignment(0);
        }
        if (value instanceof Double || value instanceof Float) {
            text = NumberUtil.formatNumber(((Number)value).doubleValue());
        }
        this.setText(text);
    }

    public boolean isInTicketScreen() {
        return this.inTicketScreen;
    }

    public void setInTicketScreen(boolean inTicketScreen) {
        this.inTicketScreen = inTicketScreen;
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.ui.views.order.modifier.ModifierViewerTableModel;
import com.floreantpos.util.NumberUtil;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ModifierViewerTableCellRenderer
extends DefaultTableCellRenderer {
    private boolean inTicketScreen = false;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ITicketItem ticketItem;
        Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        ModifierViewerTableModel model = (ModifierViewerTableModel)table.getModel();
        Object object = model.get(row);
        if (!this.inTicketScreen || isSelected) {
            return rendererComponent;
        }
        rendererComponent.setBackground(Color.WHITE);
        if (object instanceof TicketItemModifier) {
            TicketItemModifier modifier = (TicketItemModifier)object;
            if (modifier.getModifierType() == 3) {
                rendererComponent.setForeground(Color.red);
            } else {
                rendererComponent.setForeground(Color.black);
            }
        }
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
        if (value instanceof Double || value instanceof Float) {
            text = NumberUtil.formatNumber(((Number)value).doubleValue());
            this.setHorizontalAlignment(4);
        } else if (value instanceof Integer) {
            this.setHorizontalAlignment(4);
        } else {
            this.setHorizontalAlignment(2);
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


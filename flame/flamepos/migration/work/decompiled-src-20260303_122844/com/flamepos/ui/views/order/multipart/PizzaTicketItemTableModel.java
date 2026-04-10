/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.multipart;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.TicketItem;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.ArrayList;

public class PizzaTicketItemTableModel
extends ListTableModel<ITicketItem> {
    private TicketItem ticketItem;

    public PizzaTicketItemTableModel() {
        super(new String[]{"Item", "Price"});
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ITicketItem item = (ITicketItem)this.rows.get(rowIndex);
        if (item instanceof TicketItem) {
            ((TicketItem)item).calculatePrice();
        }
        switch (columnIndex) {
            case 0: {
                if (item instanceof TicketItem) {
                    return ((TicketItem)item).getName();
                }
                return " " + item.getNameDisplay();
            }
            case 1: {
                Double total = null;
                if (item instanceof TicketItem) {
                    total = item.getSubTotalAmountDisplay();
                    return NumberUtil.roundToTwoDigit(total);
                }
                return null;
            }
        }
        return null;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }

    public void updateView() {
        ArrayList<Comparable> list = new ArrayList<Comparable>();
        list.add(this.ticketItem);
        this.ticketItem.calculatePrice();
        if (this.ticketItem.getTicketItemModifiers() != null) {
            list.addAll(this.ticketItem.getTicketItemModifiers());
        }
        this.setRows(list);
        this.fireTableDataChanged();
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.report.AbstractReportDataSource;
import com.floreantpos.ui.ticket.TicketItemRowCreator;
import com.floreantpos.util.NumberUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class TicketDataSource
extends AbstractReportDataSource {
    public TicketDataSource() {
        super(new String[]{"itemName", "itemQty", "itemSubtotal"});
    }

    public TicketDataSource(Ticket ticket) {
        super(new String[]{"itemName", "itemQty", "itemSubtotal"});
        this.setTicket(ticket);
    }

    private void setTicket(Ticket ticket) {
        ArrayList<ITicketItem> rows = new ArrayList<ITicketItem>();
        LinkedHashMap<String, ITicketItem> tableRows = new LinkedHashMap<String, ITicketItem>();
        TicketItemRowCreator.calculateTicketRows(ticket, tableRows);
        Collection<ITicketItem> items = tableRows.values();
        for (ITicketItem item : items) {
            if (item instanceof TicketItem && ((TicketItem)item).isTreatAsSeat().booleanValue()) continue;
            rows.add(item);
        }
        this.setRows(rows);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ITicketItem item = (ITicketItem)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return item.getNameDisplay();
            }
            case 1: {
                return item.getItemQuantityDisplay();
            }
            case 2: {
                Double total = item.getSubTotalAmountDisplay();
                if (total == null) {
                    return null;
                }
                return NumberUtil.formatNumber(total);
            }
        }
        return null;
    }
}


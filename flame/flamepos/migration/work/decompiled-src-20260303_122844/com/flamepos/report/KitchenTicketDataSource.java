/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.report.AbstractReportDataSource;
import java.util.Collections;
import java.util.Comparator;

public class KitchenTicketDataSource
extends AbstractReportDataSource {
    public KitchenTicketDataSource() {
        super(new String[]{"groupName", "itemNo", "itemName", "itemQty"});
    }

    public KitchenTicketDataSource(KitchenTicket ticket) {
        super(new String[]{"groupId", "groupName", "itemNo", "itemName", "itemQty"});
        this.setTicket(ticket);
    }

    private void setTicket(KitchenTicket ticket) {
        if (!ticket.getType().isAllowSeatBasedOrder().booleanValue() && TerminalConfig.isGroupKitchenReceiptItems()) {
            Collections.sort(ticket.getTicketItems(), new Comparator<KitchenTicketItem>(){

                @Override
                public int compare(KitchenTicketItem o1, KitchenTicketItem o2) {
                    return o1.getMenuItemGroupId() - o2.getMenuItemGroupId();
                }
            });
            Collections.sort(ticket.getTicketItems(), new Comparator<KitchenTicketItem>(){

                @Override
                public int compare(KitchenTicketItem o1, KitchenTicketItem o2) {
                    return o1.getSortOrder() - o2.getSortOrder();
                }
            });
        }
        this.setRows(ticket.getTicketItems());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KitchenTicketItem item = (KitchenTicketItem)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(item.getMenuItemGroupId());
            }
            case 1: {
                return item.getMenuItemGroupName();
            }
            case 2: {
                return item.getMenuItemCode();
            }
            case 3: {
                return item.getMenuItemName();
            }
            case 4: {
                if (item.isFractionalUnit().booleanValue()) {
                    double itemQuantity = item.getFractionalQuantity();
                    if (itemQuantity % 1.0 == 0.0) {
                        return String.valueOf((int)itemQuantity) + item.getUnitName();
                    }
                    return String.valueOf(itemQuantity) + item.getUnitName();
                }
                if (item.getQuantity() == 0) {
                    return "";
                }
                return String.valueOf(item.getQuantity());
            }
        }
        return null;
    }
}


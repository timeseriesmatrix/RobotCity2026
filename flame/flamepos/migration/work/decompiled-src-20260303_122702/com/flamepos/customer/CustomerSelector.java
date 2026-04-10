/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customer;

import com.floreantpos.PosLog;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.model.Customer;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.util.List;
import javax.swing.JPanel;

public abstract class CustomerSelector
extends JPanel {
    protected OrderType orderType;
    protected Ticket ticket;
    private boolean createNewTicket = true;
    private Customer customer;
    private String callerId;

    public void customerSelected(OrderType orderType, List<ShopTable> selectedTables) {
        try {
            OrderServiceFactory.getOrderService().createNewTicket(orderType, selectedTables, null);
        }
        catch (TicketAlreadyExistsException e) {
            PosLog.error(this.getClass(), e);
        }
    }

    public abstract void redererCustomers();

    public abstract Customer getSelectedCustomer();

    public abstract void updateView(boolean var1);

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public boolean isCreateNewTicket() {
        return this.createNewTicket;
    }

    public void setCreateNewTicket(boolean createNewTicket) {
        this.createNewTicket = createNewTicket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallerId() {
        return this.callerId;
    }
}


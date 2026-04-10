/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customer;

import com.floreantpos.Messages;
import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.main.Application;
import com.floreantpos.main.PosWindow;
import com.floreantpos.model.Customer;
import com.floreantpos.model.Ticket;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;

public class CustomerSelectorDialog
extends POSDialog {
    private final CustomerSelector customerSelector;

    public CustomerSelectorDialog(CustomerSelector customerSelector) throws HeadlessException {
        super((Frame)Application.getPosWindow(), true);
        this.customerSelector = customerSelector;
        TitlePanel titlePane = new TitlePanel();
        titlePane.setTitle(Messages.getString("CustomerSelectorDialog.0"));
        this.getContentPane().add((Component)titlePane, "North");
        this.getContentPane().add(customerSelector);
        PosWindow window = Application.getPosWindow();
        this.setSize(window.getSize());
        this.setLocation(window.getLocation());
    }

    public void setCreateNewTicket(boolean createNewTicket) {
        this.customerSelector.setCreateNewTicket(createNewTicket);
    }

    public void updateView(boolean update) {
        this.customerSelector.updateView(update);
    }

    public Customer getSelectedCustomer() {
        return this.customerSelector.getSelectedCustomer();
    }

    public void setTicket(Ticket thisTicket) {
        this.customerSelector.setTicket(thisTicket);
    }

    public void setCustomer(Customer customer) {
        this.customerSelector.setCustomer(customer);
    }

    public void setCallerId(String callerId) {
        this.customerSelector.setCallerId(callerId);
    }
}


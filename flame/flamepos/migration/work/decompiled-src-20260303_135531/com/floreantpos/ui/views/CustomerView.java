/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.customer.DefaultCustomerListView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.model.OrderType;
import com.floreantpos.ui.views.order.ViewPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.Locale;

public class CustomerView
extends ViewPanel {
    public static final String VIEW_NAME = "CUSTOMER_ACTIVITY";
    private CustomerSelector customerSelector = null;
    private static CustomerView instance;

    private CustomerView(OrderType orderType) {
        this.setLayout(new BorderLayout());
        OrderServiceExtension orderServicePlugin = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        this.customerSelector = orderServicePlugin == null ? new DefaultCustomerListView() : orderServicePlugin.createCustomerSelectorView();
        this.customerSelector.setCreateNewTicket(true);
        this.customerSelector.updateView(false);
        this.add((Component)this.customerSelector, "Center");
        this.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void updateView() {
        this.customerSelector.redererCustomers();
    }

    public static CustomerView getInstance(OrderType orderType) {
        if (instance == null) {
            instance = new CustomerView(orderType);
        }
        CustomerView.instance.customerSelector.setOrderType(orderType);
        return instance;
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }
}


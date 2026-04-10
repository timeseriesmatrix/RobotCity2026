/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customer;

import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.customer.CustomerSelectorDialog;
import com.floreantpos.customer.DefaultCustomerListView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.model.OrderType;

public class CustomerSelectorFactory {
    private static CustomerSelector customerSelector;

    public static CustomerSelectorDialog createCustomerSelectorDialog(OrderType orderType) {
        OrderServiceExtension orderServicePlugin = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        if (customerSelector == null) {
            customerSelector = orderServicePlugin == null ? new DefaultCustomerListView() : orderServicePlugin.createNewCustomerSelector();
        }
        customerSelector.setOrderType(orderType);
        customerSelector.redererCustomers();
        return new CustomerSelectorDialog(customerSelector);
    }
}


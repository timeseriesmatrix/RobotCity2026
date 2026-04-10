/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.AbstractFloreantPlugin
 */
package com.floreantpos.extension;

import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.extension.AbstractFloreantPlugin;
import com.floreantpos.model.Customer;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.ui.views.IView;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.util.List;
import javax.swing.JMenu;

public abstract class OrderServiceExtension
extends AbstractFloreantPlugin {
    public abstract String getProductName();

    public abstract String getDescription();

    public abstract void initUI();

    public abstract void createNewTicket(OrderType var1, List<ShopTable> var2, Customer var3) throws TicketAlreadyExistsException;

    public abstract void setCustomerToTicket(int var1);

    public abstract void setDeliveryDate(int var1);

    public abstract void assignDriver(int var1);

    public abstract boolean finishOrder(int var1);

    public abstract void createCustomerMenu(JMenu var1);

    public abstract CustomerSelector createNewCustomerSelector();

    public abstract CustomerSelector createCustomerSelectorView();

    public abstract IView getDeliveryDispatchView(OrderType var1);

    public abstract IView getDriverView();

    public abstract void openDeliveryDispatchDialog(OrderType var1);

    public abstract void showDeliveryInfo(OrderType var1, Customer var2);
}


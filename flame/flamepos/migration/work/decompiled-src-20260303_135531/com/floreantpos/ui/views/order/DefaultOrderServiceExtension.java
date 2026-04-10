/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.Messages;
import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.main.Application;
import com.floreantpos.model.Customer;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.IView;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.PosGuiUtil;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.awt.Component;
import java.util.Calendar;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

public class DefaultOrderServiceExtension
extends OrderServiceExtension {
    @Override
    public String getProductName() {
        return Messages.getString("DefaultOrderServiceExtension.0");
    }

    @Override
    public String getDescription() {
        return Messages.getString("DefaultOrderServiceExtension.1");
    }

    @Override
    public void initUI() {
    }

    @Override
    public void createNewTicket(OrderType ticketType, List<ShopTable> selectedTables, Customer customer) throws TicketAlreadyExistsException {
        int numberOfGuests = 0;
        if (ticketType.isShowGuestSelection().booleanValue()) {
            numberOfGuests = PosGuiUtil.captureGuestNumber();
        }
        if (ticketType.isRequiredCustomerData().booleanValue() && customer == null && (customer = PosGuiUtil.captureCustomer(ticketType)) == null) {
            return;
        }
        Application application = Application.getInstance();
        Ticket ticket = new Ticket();
        ticket.setPriceIncludesTax(application.isPriceIncludesTax());
        ticket.setOrderType(ticketType);
        ticket.setNumberOfGuests(numberOfGuests);
        ticket.setCustomer(customer);
        ticket.setTerminal(application.getTerminal());
        ticket.setOwner(Application.getCurrentUser());
        ticket.setShift(application.getCurrentShift());
        if (selectedTables != null) {
            for (ShopTable shopTable : selectedTables) {
                shopTable.setServing(true);
                ticket.addTable(shopTable.getTableNumber());
            }
        }
        Calendar currentTime = Calendar.getInstance();
        ticket.setCreateDate(currentTime.getTime());
        ticket.setCreationHour(currentTime.get(11));
        OrderView.getInstance().setCurrentTicket(ticket);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
    }

    @Override
    public void setCustomerToTicket(int ticketId) {
    }

    @Override
    public void setDeliveryDate(int ticketId) {
    }

    @Override
    public void assignDriver(int ticketId) {
    }

    @Override
    public boolean finishOrder(int ticketId) {
        Ticket ticket = TicketDAO.getInstance().get(ticketId);
        int due = (int)POSUtil.getDouble(ticket.getDueAmount());
        if (due != 0) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("DefaultOrderServiceExtension.2"));
            return false;
        }
        int option = JOptionPane.showOptionDialog(Application.getPosWindow(), Messages.getString("DefaultOrderServiceExtension.3") + ticket.getId() + Messages.getString("DefaultOrderServiceExtension.4"), Messages.getString("DefaultOrderServiceExtension.5"), 2, 1, null, null, null);
        if (option != 0) {
            return false;
        }
        OrderController.closeOrder(ticket);
        return true;
    }

    @Override
    public void createCustomerMenu(JMenu menu) {
    }

    public void initBackoffice() {
    }

    public void initConfigurationView(JDialog dialog) {
    }

    public String getId() {
        return String.valueOf("DefaultOrderServiceExtension".hashCode());
    }

    @Override
    public IView getDeliveryDispatchView(OrderType orderType) {
        return null;
    }

    @Override
    public CustomerSelector createNewCustomerSelector() {
        return null;
    }

    @Override
    public CustomerSelector createCustomerSelectorView() {
        return null;
    }

    @Override
    public void openDeliveryDispatchDialog(OrderType orderType) {
    }

    @Override
    public IView getDriverView() {
        return null;
    }

    public List<AbstractAction> getSpecialFunctionActions() {
        return null;
    }

    public String getProductVersion() {
        return null;
    }

    public Component getParent() {
        return null;
    }

    public boolean requireLicense() {
        return false;
    }

    @Override
    public void showDeliveryInfo(OrderType orderType, Customer customer) {
    }
}


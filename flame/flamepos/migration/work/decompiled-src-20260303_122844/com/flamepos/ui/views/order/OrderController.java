/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.SettleTicketAction;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.ui.dialog.AutomatedWeightInputDialog;
import com.floreantpos.ui.dialog.BasicWeightInputDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.GroupView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.actions.CategorySelectionListener;
import com.floreantpos.ui.views.order.actions.GroupSelectionListener;
import com.floreantpos.ui.views.order.actions.ItemSelectionListener;
import com.floreantpos.ui.views.order.actions.OrderListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionDialog;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.floreantpos.ui.views.order.multipart.PizzaModifierSelectionDialog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OrderController
implements OrderListener,
CategorySelectionListener,
GroupSelectionListener,
ItemSelectionListener {
    private OrderView orderView;

    public OrderController(OrderView orderView) {
        this.orderView = orderView;
        orderView.getCategoryView().addCategorySelectionListener(this);
        orderView.getGroupView().addGroupSelectionListener(this);
        orderView.getItemView().addItemSelectionListener(this);
        orderView.getTicketView().addOrderListener(this);
    }

    @Override
    public void categorySelected(MenuCategory foodCategory) {
        this.orderView.showView("GROUP_VIEW");
        this.orderView.getGroupView().setMenuCategory(foodCategory);
        this.orderView.getTicketView().getTxtSearchItem().requestFocus();
    }

    @Override
    public void groupSelected(MenuGroup foodGroup) {
        this.orderView.showView("ITEM_VIEW");
        this.orderView.getItemView().setMenuGroup(foodGroup);
        this.orderView.getTicketView().getTxtSearchItem().requestFocus();
    }

    @Override
    public void itemSelected(MenuItem menuItem) {
        MenuItemDAO dao = new MenuItemDAO();
        menuItem = dao.initialize(menuItem);
        if (this.orderView.isVisible() && RootView.getInstance().isMaintenanceMode()) {
            return;
        }
        double itemQuantity = 0.0;
        if (menuItem.isFractionalUnit().booleanValue()) {
            itemQuantity = TerminalConfig.getScaleActivationValue().equals("cas10") ? AutomatedWeightInputDialog.takeDoubleInput(menuItem.getName(), 1.0) : BasicWeightInputDialog.takeDoubleInput("Please enter item weight or quantity.", 1.0);
            if (itemQuantity <= -1.0) {
                return;
            }
            if (itemQuantity == 0.0) {
                POSMessageDialog.showError("Unit can not be zero");
                return;
            }
        }
        TicketItem ticketItem = menuItem.convertToTicketItem(this.orderView.getTicketView().getTicket().getOrderType(), itemQuantity);
        if (!this.orderView.getTicketView().isStockAvailable(menuItem, ticketItem, -1.0)) {
            POSMessageDialog.showError("Items are not available in stock");
            this.orderView.getItemView().disableItemButton(menuItem);
            return;
        }
        ticketItem.setTicket(this.orderView.getTicketView().getTicket());
        ticketItem.setSeatNumber(this.orderView.getSelectedSeatNumber());
        if (menuItem.isPizzaType().booleanValue()) {
            PizzaModifierSelectionDialog dialog = new PizzaModifierSelectionDialog(ticketItem, menuItem, false);
            dialog.openFullScreen();
            if (dialog.isCanceled()) {
                return;
            }
        } else if (menuItem.hasMandatoryModifiers()) {
            ModifierSelectionDialog dialog = new ModifierSelectionDialog(new ModifierSelectionModel(ticketItem, menuItem));
            dialog.open();
            if (!dialog.isCanceled()) {
                this.orderView.getTicketView().addTicketItem(ticketItem);
            }
        } else {
            this.orderView.getTicketView().addTicketItem(ticketItem);
        }
    }

    @Override
    public void itemSelectionFinished(MenuGroup parent) {
        GroupView groupView;
        MenuCategory menuCategory = parent.getParent();
        if (!menuCategory.equals((groupView = this.orderView.getGroupView()).getMenuCategory())) {
            groupView.setMenuCategory(menuCategory);
        }
        this.orderView.showView("GROUP_VIEW");
    }

    @Override
    public void payOrderSelected(Ticket ticket) {
        if (!new SettleTicketAction(ticket.getId()).execute()) {
            return;
        }
        RootView.getInstance().showDefaultView();
    }

    public static void openModifierDialog(ITicketItem ticketItemObject) {
        try {
            POSDialog dialog;
            TicketItemModifier ticketItemModifier;
            TicketItem ticketItem = null;
            if (ticketItemObject instanceof TicketItem) {
                ticketItem = (TicketItem)ticketItemObject;
            } else if (ticketItemObject instanceof TicketItemModifier && (ticketItem = (ticketItemModifier = (TicketItemModifier)ticketItemObject).getTicketItem()) == null) {
                ticketItem = ticketItemModifier.getTicketItem();
            }
            MenuItem menuItem = ticketItem.getMenuItem();
            List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
            if (ticketItemModifiers == null) {
                ticketItemModifiers = new ArrayList<TicketItemModifier>();
            }
            TicketItem cloneTicketItem = ticketItem.clone(ticketItem);
            boolean pizzaType = ticketItem.isPizzaType();
            if (pizzaType) {
                dialog = new PizzaModifierSelectionDialog(cloneTicketItem, menuItem, true);
                dialog.openFullScreen();
                if (dialog.isCanceled()) {
                    return;
                }
                TicketItemModifier sizeModifier = cloneTicketItem.getSizeModifier();
                sizeModifier.setTicketItem(ticketItem);
                ticketItem.setSizeModifier(sizeModifier);
                ticketItem.setItemCount(cloneTicketItem.getItemCount());
                ticketItem.setUnitPrice(cloneTicketItem.getUnitPrice());
            } else {
                dialog = new ModifierSelectionDialog(new ModifierSelectionModel(cloneTicketItem, menuItem));
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
            }
            List<TicketItemModifier> addedTicketItemModifiers = cloneTicketItem.getTicketItemModifiers();
            if (addedTicketItemModifiers == null) {
                addedTicketItemModifiers = new ArrayList<TicketItemModifier>();
            }
            ticketItemModifiers.clear();
            for (TicketItemModifier ticketItemModifier2 : addedTicketItemModifiers) {
                ticketItemModifier2.setTicketItem(ticketItem);
                ticketItem.addToticketItemModifiers(ticketItemModifier2);
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public static synchronized void saveOrder(Ticket ticket) {
        if (ticket == null) {
            return;
        }
        boolean newTicket = ticket.getId() == null;
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.saveOrUpdate(ticket);
        ActionHistoryDAO actionHistoryDAO = ActionHistoryDAO.getInstance();
        User user = Application.getCurrentUser();
        if (newTicket) {
            ShopTableDAO.getInstance().occupyTables(ticket);
            actionHistoryDAO.saveHistory(user, ActionHistory.NEW_CHECK, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + ticket.getId());
        } else {
            actionHistoryDAO.saveHistory(user, ActionHistory.EDIT_CHECK, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + ticket.getId());
        }
    }

    public static synchronized void closeOrder(Ticket ticket) {
        if (ticket.getOrderType().isCloseOnPaid().booleanValue() || ticket.isPaid().booleanValue()) {
            ticket.setClosed(true);
            ticket.setClosingDate(new Date());
        }
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.saveOrUpdate(ticket);
        User driver = ticket.getAssignedDriver();
        if (driver != null) {
            driver.setAvailableForDelivery(true);
            UserDAO.getInstance().saveOrUpdate(driver);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized void closeDeliveryOrders(List<Ticket> tickets) {
        Transaction transaction = null;
        try (Session session = TicketDAO.getInstance().createNewSession();){
            transaction = session.beginTransaction();
            for (Ticket ticket : tickets) {
                ticket.setClosed(true);
                ticket.setClosingDate(new Date());
                session.saveOrUpdate((Object)ticket);
            }
            transaction.commit();
        }
    }
}


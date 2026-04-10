/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.OrderInfoView;
import com.floreantpos.ui.views.UserTransferDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JPanel;

public class OrderInfoDialog
extends POSDialog {
    OrderInfoView view;
    private boolean reorder = false;
    private PosButton btnReOrder;
    private PosButton btnTransferUser;
    private PosButton btnPrint;
    private PosButton btnPrintDriverCopy;

    public OrderInfoDialog(OrderInfoView view) {
        this.view = view;
        this.setTitle(Messages.getString("OrderInfoDialog.0"));
        this.createUI();
    }

    public void createUI() {
        this.add(this.view);
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "South");
        this.btnReOrder = new PosButton("Reorder");
        this.btnReOrder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Ticket ticket : OrderInfoDialog.this.view.getTickets()) {
                    OrderInfoDialog.this.createReOrder(ticket);
                    OrderInfoDialog.this.setCanceled(true);
                    OrderInfoDialog.this.dispose();
                }
            }
        });
        panel.add(this.btnReOrder);
        this.btnTransferUser = new PosButton();
        this.btnTransferUser.setText(Messages.getString("OrderInfoDialog.3"));
        this.btnTransferUser.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                User currentUser = Application.getCurrentUser();
                for (Ticket ticket : OrderInfoDialog.this.view.getTickets()) {
                    if (currentUser.equals(ticket.getOwner()) || currentUser.hasPermission(UserPermission.TRANSFER_TICKET)) continue;
                    POSMessageDialog.showError(OrderInfoDialog.this.getParent(), Messages.getString("OrderInfoDialog.4") + ticket.getId());
                    return;
                }
                UserTransferDialog dialog = new UserTransferDialog(OrderInfoDialog.this.view);
                dialog.setSize(PosUIManager.getSize(360, 555));
                dialog.setDefaultCloseOperation(2);
                dialog.setLocationRelativeTo(Application.getPosWindow());
                dialog.setVisible(true);
            }
        });
        panel.add(this.btnTransferUser);
        this.btnPrint = new PosButton();
        this.btnPrint.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderInfoDialog.this.doPrint();
            }
        });
        this.btnPrint.setText(Messages.getString("OrderInfoDialog.1"));
        panel.add(this.btnPrint);
        this.btnPrintDriverCopy = new PosButton();
        this.btnPrintDriverCopy.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderInfoDialog.this.doPrintDriverCopy();
            }
        });
        this.btnPrintDriverCopy.setText("Print (Driver Copy)");
        this.btnPrintDriverCopy.setVisible(false);
        panel.add(this.btnPrintDriverCopy);
        PosButton btnClose = new PosButton();
        btnClose.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderInfoDialog.this.dispose();
            }
        });
        btnClose.setText(Messages.getString("OrderInfoDialog.2"));
        panel.add(btnClose);
    }

    private void doPrintDriverCopy() {
        try {
            this.view.printCopy("Driver Copy");
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage());
        }
    }

    public void updateView() {
        this.btnTransferUser.setVisible(false);
        this.btnReOrder.setVisible(false);
        this.btnPrintDriverCopy.setVisible(true);
        this.btnPrint.setText("Print (Customer Copy)");
    }

    protected void doPrint() {
        try {
            this.view.printCopy("Customer Copy");
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage());
        }
    }

    private void createReOrder(Ticket oldticket) {
        Ticket ticket = new Ticket();
        ticket.setPriceIncludesTax(oldticket.isPriceIncludesTax());
        ticket.setOrderType(oldticket.getOrderType());
        ticket.setProperties(oldticket.getProperties());
        ticket.setTerminal(Application.getInstance().getTerminal());
        ticket.setOwner(Application.getCurrentUser());
        ticket.setShift(Application.getInstance().getCurrentShift());
        ticket.setNumberOfGuests(oldticket.getNumberOfGuests());
        Calendar currentTime = Calendar.getInstance();
        ticket.setCreateDate(currentTime.getTime());
        ticket.setCreationHour(currentTime.get(11));
        ArrayList<TicketItem> newTicketItems = new ArrayList<TicketItem>();
        for (TicketItem oldTicketItem : oldticket.getTicketItems()) {
            List<TicketItemModifier> addOnsList;
            List<TicketItemModifier> ticketItemModifiers;
            TicketItem newTicketItem = new TicketItem();
            newTicketItem.setItemCount(oldTicketItem.getItemCount());
            newTicketItem.setItemQuantity(oldTicketItem.getItemQuantity());
            newTicketItem.setItemId(oldTicketItem.getItemId());
            newTicketItem.setHasModifiers(oldTicketItem.isHasModifiers());
            newTicketItem.setName(oldTicketItem.getName());
            newTicketItem.setGroupName(oldTicketItem.getGroupName());
            newTicketItem.setCategoryName(oldTicketItem.getCategoryName());
            newTicketItem.setUnitPrice(oldTicketItem.getUnitPrice());
            newTicketItem.setFractionalUnit(oldTicketItem.isFractionalUnit());
            newTicketItem.setItemUnitName(oldTicketItem.getItemUnitName());
            List<TicketItemDiscount> discounts = oldTicketItem.getDiscounts();
            if (discounts != null) {
                ArrayList<TicketItemDiscount> newDiscounts = new ArrayList<TicketItemDiscount>();
                for (TicketItemDiscount ticketItemDiscount : discounts) {
                    TicketItemDiscount newDiscount = new TicketItemDiscount(ticketItemDiscount);
                    newDiscount.setTicketItem(newTicketItem);
                    newDiscounts.add(newDiscount);
                }
                newTicketItem.setDiscounts(newDiscounts);
            }
            if ((ticketItemModifiers = oldTicketItem.getTicketItemModifiers()) != null) {
                for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                    TicketItemModifier newModifier = new TicketItemModifier();
                    newModifier.setModifierId(ticketItemModifier.getModifierId());
                    newModifier.setMenuItemModifierGroupId(ticketItemModifier.getMenuItemModifierGroupId());
                    newModifier.setItemCount(ticketItemModifier.getItemCount());
                    newModifier.setName(ticketItemModifier.getName());
                    newModifier.setUnitPrice(ticketItemModifier.getUnitPrice());
                    newModifier.setTaxRate(ticketItemModifier.getTaxRate());
                    newModifier.setModifierType(ticketItemModifier.getModifierType());
                    newModifier.setPrintedToKitchen(false);
                    newModifier.setShouldPrintToKitchen(ticketItemModifier.isShouldPrintToKitchen());
                    newModifier.setTicketItem(newTicketItem);
                    newTicketItem.addToticketItemModifiers(newModifier);
                }
            }
            if ((addOnsList = oldTicketItem.getAddOns()) != null) {
                for (TicketItemModifier addOns : oldTicketItem.getAddOns()) {
                    TicketItemModifier newAddOns = new TicketItemModifier();
                    newAddOns.setModifierId(addOns.getModifierId());
                    newAddOns.setMenuItemModifierGroupId(addOns.getMenuItemModifierGroupId());
                    newAddOns.setItemCount(addOns.getItemCount());
                    newAddOns.setName(addOns.getName());
                    newAddOns.setUnitPrice(addOns.getUnitPrice());
                    newAddOns.setTaxRate(addOns.getTaxRate());
                    newAddOns.setModifierType(addOns.getModifierType());
                    newAddOns.setPrintedToKitchen(false);
                    newAddOns.setShouldPrintToKitchen(addOns.isShouldPrintToKitchen());
                    newTicketItem.addToaddOns(newAddOns);
                }
            }
            newTicketItem.setTaxRate(oldTicketItem.getTaxRate());
            newTicketItem.setBeverage(oldTicketItem.isBeverage());
            newTicketItem.setShouldPrintToKitchen(oldTicketItem.isShouldPrintToKitchen());
            newTicketItem.setPrinterGroup(oldTicketItem.getPrinterGroup());
            newTicketItem.setPrintedToKitchen(false);
            newTicketItem.setTicket(ticket);
            newTicketItems.add(newTicketItem);
        }
        ticket.getTicketItems().addAll(newTicketItems);
        OrderView.getInstance().setCurrentTicket(ticket);
        RootView.getInstance().showView("ORDER_VIEW");
        this.reorder = true;
    }

    public boolean isReorder() {
        return this.reorder;
    }
}


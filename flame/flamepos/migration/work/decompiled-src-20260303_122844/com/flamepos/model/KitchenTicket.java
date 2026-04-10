/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.SerializationUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.Printer;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.base.BaseKitchenTicket;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.util.GlobalIdGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

public class KitchenTicket
extends BaseKitchenTicket {
    private static final long serialVersionUID = 1L;
    private String customerName;
    private Printer printer;

    public KitchenTicket() {
    }

    public KitchenTicket(Integer id) {
        super(id);
    }

    public OrderType getType() {
        String type = this.getTicketType();
        return OrderTypeDAO.getInstance().findByName(type);
    }

    public void setType(OrderType type) {
        this.setTicketType(type.name());
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public Printer getPrinter() {
        return this.printer;
    }

    public List<Printer> getPrinters() {
        ArrayList<Printer> printers = new ArrayList<Printer>();
        PosPrinters posPrinters = PosPrinters.load();
        PrinterGroup virtualPrinter = this.getPrinterGroup();
        if (virtualPrinter == null) {
            printers.add(posPrinters.getDefaultKitchenPrinter());
            return printers;
        }
        List<String> printerNames = virtualPrinter.getPrinterNames();
        List<Printer> kitchenPrinters = posPrinters.getKitchenPrinters();
        for (Printer printer : kitchenPrinters) {
            if (!printerNames.contains(printer.getVirtualPrinter().getName())) continue;
            printers.add(printer);
        }
        if (printers.size() == 0) {
            printers.add(posPrinters.getDefaultKitchenPrinter());
        }
        return printers;
    }

    private static void setPrintedToKitchen(TicketItem ticketItem) {
        List<TicketItemCookingInstruction> cookingInstructions;
        List<TicketItemModifier> addOns;
        ticketItem.setPrintedToKitchen(true);
        List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            for (TicketItemModifier itemModifier : ticketItemModifiers) {
                if (!itemModifier.isShouldPrintToKitchen().booleanValue()) continue;
                itemModifier.setPrintedToKitchen(true);
            }
        }
        if ((addOns = ticketItem.getAddOns()) != null) {
            for (TicketItemModifier ticketItemModifier : addOns) {
                if (!ticketItemModifier.isShouldPrintToKitchen().booleanValue()) continue;
                ticketItemModifier.setPrintedToKitchen(true);
            }
        }
        if ((cookingInstructions = ticketItem.getCookingInstructions()) != null) {
            for (TicketItemCookingInstruction cookingInstruction : cookingInstructions) {
                cookingInstruction.setPrintedToKitchen(true);
            }
        }
    }

    public static List<KitchenTicket> fromTicket(Ticket ticket) {
        HashMap<Printer, KitchenTicket> itemMap = new HashMap<Printer, KitchenTicket>();
        ArrayList<KitchenTicket> kitchenTickets = new ArrayList<KitchenTicket>(4);
        Ticket clonedTicket = (Ticket)SerializationUtils.clone((Serializable)ticket);
        clonedTicket.setGlobalId(GlobalIdGenerator.generate());
        clonedTicket.consolidateTicketItems();
        List<TicketItem> ticketItems = clonedTicket.getTicketItems();
        if (ticketItems == null) {
            return kitchenTickets;
        }
        if (ticket.getOrderType().isAllowSeatBasedOrder().booleanValue()) {
            Collections.sort(ticketItems, new Comparator<TicketItem>(){

                @Override
                public int compare(TicketItem o1, TicketItem o2) {
                    return o1.getId() - o2.getId();
                }
            });
            Collections.sort(ticketItems, new Comparator<TicketItem>(){

                @Override
                public int compare(TicketItem o1, TicketItem o2) {
                    return o1.getSeatNumber() - o2.getSeatNumber();
                }
            });
        }
        for (TicketItem ticketItem : ticketItems) {
            List<Printer> printers;
            if (ticketItem.isPrintedToKitchen().booleanValue() || !ticketItem.isShouldPrintToKitchen().booleanValue() || (printers = ticketItem.getPrinters(ticket.getOrderType())) == null) continue;
            for (Printer printer : printers) {
                KitchenTicket kitchenTicket = (KitchenTicket)itemMap.get(printer);
                if (kitchenTicket == null) {
                    kitchenTicket = new KitchenTicket();
                    kitchenTicket.setPrinterGroup(ticketItem.getPrinterGroup());
                    kitchenTicket.setTicketId(ticket.getId());
                    kitchenTicket.setCreateDate(new Date());
                    kitchenTicket.setTicketType(ticket.getTicketType());
                    if (ticket.getTableNumbers() != null) {
                        kitchenTicket.setTableNumbers(new ArrayList<Integer>(ticket.getTableNumbers()));
                    }
                    kitchenTicket.setServerName(ticket.getOwner().getFirstName());
                    kitchenTicket.setStatus(KitchenTicketStatus.WAITING.name());
                    if (StringUtils.isNotEmpty((String)ticket.getProperty("CUSTOMER_NAME"))) {
                        kitchenTicket.setCustomerName(ticket.getProperty("CUSTOMER_NAME"));
                    }
                    KitchenTicketDAO.getInstance().saveOrUpdate(kitchenTicket);
                    kitchenTicket.setPrinter(printer);
                    itemMap.put(printer, kitchenTicket);
                }
                KitchenTicketItem item = new KitchenTicketItem();
                item.setTicketItemId(ticketItem.getId());
                item.setMenuItemCode(ticketItem.getItemCode());
                item.setMenuItemName(ticketItem.getNameDisplay());
                if (ticketItem.getMenuItem() == null) {
                    item.setMenuItemGroupName("MISC.");
                    item.setMenuItemGroupId(1001);
                    item.setSortOrder(10001);
                } else {
                    item.setMenuItemGroupName(ticketItem.getGroupName());
                    item.setMenuItemGroupId(ticketItem.getMenuItem().getParent().getId());
                    item.setSortOrder(ticketItem.getMenuItem().getParent().getSortOrder());
                }
                item.setFractionalUnit(ticketItem.isFractionalUnit());
                item.setUnitName(ticketItem.getItemUnitName());
                if (ticketItem.isFractionalUnit().booleanValue()) {
                    item.setFractionalQuantity(ticketItem.getItemQuantity());
                } else {
                    item.setQuantity(ticketItem.getItemCount());
                }
                item.setStatus(KitchenTicketStatus.WAITING.name());
                kitchenTicket.addToticketItems(item);
                ticketItem.setPrintedToKitchen(true);
                KitchenTicket.includeModifiers(ticketItem, kitchenTicket);
                KitchenTicket.includeCookintInstructions(ticketItem, kitchenTicket);
            }
        }
        Collection values = itemMap.values();
        for (KitchenTicket kitchenTicket : values) {
            kitchenTickets.add(kitchenTicket);
            String kitchenTicketNumber = ticket.getProperty("KITCHEN_TICKET_NUMBER");
            kitchenTicketNumber = kitchenTicketNumber == null ? "1" : String.valueOf(Integer.valueOf(kitchenTicketNumber) + 1);
            ticket.addProperty("KITCHEN_TICKET_NUMBER", kitchenTicketNumber);
            kitchenTicket.setSequenceNumber(Integer.valueOf(kitchenTicketNumber));
        }
        ticket.markPrintedToKitchen();
        return kitchenTickets;
    }

    private static void includeCookintInstructions(TicketItem ticketItem, KitchenTicket kitchenTicket) {
        List<TicketItemCookingInstruction> cookingInstructions = ticketItem.getCookingInstructions();
        if (cookingInstructions != null) {
            for (TicketItemCookingInstruction ticketItemCookingInstruction : cookingInstructions) {
                KitchenTicketItem item = new KitchenTicketItem();
                item.setCookable(false);
                item.setMenuItemName(ticketItemCookingInstruction.getNameDisplay());
                if (ticketItem.getMenuItem() == null) {
                    item.setMenuItemGroupName("MISC.");
                    item.setMenuItemGroupId(1001);
                    item.setSortOrder(10001);
                } else {
                    item.setMenuItemGroupName(ticketItem.getGroupName());
                    item.setMenuItemGroupId(ticketItem.getMenuItem().getParent().getId());
                    item.setSortOrder(ticketItem.getMenuItem().getParent().getSortOrder());
                }
                kitchenTicket.addToticketItems(item);
                ticketItemCookingInstruction.setPrintedToKitchen(true);
            }
        }
    }

    private static void includeModifiers(TicketItem ticketItem, KitchenTicket kitchenTicket) {
        List<TicketItemModifier> addOns;
        List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            for (TicketItemModifier itemModifier : ticketItemModifiers) {
                if (itemModifier.isPrintedToKitchen().booleanValue() || !itemModifier.isShouldPrintToKitchen().booleanValue()) continue;
                KitchenTicketItem item = new KitchenTicketItem();
                item.setMenuItemCode("");
                item.setTicketItemModifierId(itemModifier.getId());
                String nameDisplay = (itemModifier.isInfoOnly() != false ? "" : "  --") + itemModifier.getNameDisplay();
                item.setMenuItemName(nameDisplay);
                if (ticketItem.getMenuItem() == null) {
                    item.setMenuItemGroupName("MISC.");
                    item.setMenuItemGroupId(1001);
                    item.setSortOrder(10001);
                } else {
                    item.setMenuItemGroupName(ticketItem.getGroupName());
                    item.setMenuItemGroupId(ticketItem.getMenuItem().getParent().getId());
                    item.setSortOrder(ticketItem.getMenuItem().getParent().getSortOrder());
                }
                item.setQuantity(itemModifier.getItemCount());
                item.setStatus(KitchenTicketStatus.WAITING.name());
                kitchenTicket.addToticketItems(item);
                itemModifier.setPrintedToKitchen(true);
            }
        }
        if ((addOns = ticketItem.getAddOns()) != null) {
            for (TicketItemModifier ticketItemModifier : addOns) {
                if (ticketItemModifier.isPrintedToKitchen().booleanValue() || !ticketItemModifier.isShouldPrintToKitchen().booleanValue()) continue;
                KitchenTicketItem item = new KitchenTicketItem();
                item.setMenuItemCode("");
                item.setTicketItemModifierId(ticketItem.getId());
                item.setMenuItemName(ticketItemModifier.getNameDisplay());
                if (ticketItem.getMenuItem() == null) {
                    item.setMenuItemGroupName("MISC.");
                    item.setMenuItemGroupId(1001);
                    item.setSortOrder(10001);
                } else {
                    item.setMenuItemGroupName(ticketItem.getGroupName());
                    item.setMenuItemGroupId(ticketItem.getMenuItem().getParent().getId());
                    item.setSortOrder(ticketItem.getMenuItem().getParent().getSortOrder());
                }
                item.setQuantity(ticketItemModifier.getItemCount());
                item.setStatus(KitchenTicketStatus.WAITING.name());
                kitchenTicket.addToticketItems(item);
                ticketItemModifier.setPrintedToKitchen(true);
            }
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public static enum KitchenTicketStatus {
        WAITING,
        VOID,
        DONE;

    }
}


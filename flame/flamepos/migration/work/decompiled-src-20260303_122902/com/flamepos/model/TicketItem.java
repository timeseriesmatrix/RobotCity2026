/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.main.Application;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.Printer;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.base.BaseTicketItem;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.util.DiscountUtil;
import com.floreantpos.util.NumberUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TicketItem
extends BaseTicketItem
implements ITicketItem {
    private static final long serialVersionUID = 1L;
    private MenuItem menuItem;
    private boolean priceIncludesTax;
    private int tableRowNum;

    public PIZZA_SECTION_MODE getPizzaSectionMode() {
        return PIZZA_SECTION_MODE.from(this.getPizzaSectionModeType());
    }

    public void setPizzaSectionMode(PIZZA_SECTION_MODE pizzaSectionMode) {
        this.setPizzaSectionModeType(pizzaSectionMode.getValue());
    }

    public TicketItem() {
    }

    public TicketItem(Integer id) {
        super(id);
    }

    public TicketItem(Integer id, Ticket ticket) {
        super(id, ticket);
    }

    public TicketItem clone(TicketItem source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            out.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (TicketItem)in.readObject();
        }
        catch (Exception cnfe) {
            return null;
        }
    }

    public int getTableRowNum() {
        return this.tableRowNum;
    }

    public void setTableRowNum(int tableRowNum) {
        this.tableRowNum = tableRowNum;
    }

    @Override
    public boolean canAddCookingInstruction() {
        return this.isPrintedToKitchen() == false;
    }

    @Override
    public Double getTaxAmount() {
        if (this.getTicket().isTaxExempt().booleanValue()) {
            return 0.0;
        }
        return super.getTaxAmount();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public TicketItemModifier addTicketItemModifier(MenuModifier menuModifier, int modifierType, OrderType type, Multiplier multiplier) {
        TicketItemModifier ticketItemModifier = new TicketItemModifier();
        ticketItemModifier.setModifierId(menuModifier.getId());
        MenuItemModifierGroup menuItemModifierGroup = menuModifier.getMenuItemModifierGroup();
        if (menuItemModifierGroup != null) {
            ticketItemModifier.setMenuItemModifierGroupId(menuItemModifierGroup.getId());
        }
        ticketItemModifier.setItemCount(1);
        ticketItemModifier.setName(menuModifier.getDisplayName());
        double price = menuModifier.getPriceForMultiplier(multiplier);
        if (multiplier != null) {
            ticketItemModifier.setMultiplierName(multiplier.getName());
            ticketItemModifier.setName(multiplier.getTicketPrefix() + " " + menuModifier.getDisplayName());
        }
        ticketItemModifier.setUnitPrice(price);
        ticketItemModifier.setTaxRate(menuModifier.getTaxByOrderType(type));
        ticketItemModifier.setModifierType(modifierType);
        ticketItemModifier.setShouldPrintToKitchen(menuModifier.isShouldPrintToKitchen());
        ticketItemModifier.setTicketItem(this);
        this.addToticketItemModifiers(ticketItemModifier);
        return ticketItemModifier;
    }

    public TicketItemModifier addTicketItemModifier(MenuModifier menuModifier, boolean addOn) {
        TicketItemModifier ticketItemModifier = new TicketItemModifier();
        ticketItemModifier.setModifierId(menuModifier.getId());
        MenuItemModifierGroup menuItemModifierGroup = menuModifier.getMenuItemModifierGroup();
        if (menuItemModifierGroup != null) {
            ticketItemModifier.setMenuItemModifierGroupId(menuItemModifierGroup.getId());
        }
        ticketItemModifier.setItemCount(1);
        ticketItemModifier.setName(menuModifier.getDisplayName());
        if (addOn) {
            ticketItemModifier.setUnitPrice(menuModifier.getExtraPrice());
            ticketItemModifier.setModifierType(3);
        } else {
            ticketItemModifier.setUnitPrice(menuModifier.getPrice());
            ticketItemModifier.setModifierType(1);
        }
        ticketItemModifier.setTaxRate(menuModifier.getTax() == null ? 0.0 : menuModifier.getTax().getRate());
        ticketItemModifier.setShouldPrintToKitchen(menuModifier.isShouldPrintToKitchen());
        ticketItemModifier.setTicketItem(this);
        this.addToticketItemModifiers(ticketItemModifier);
        return ticketItemModifier;
    }

    public void updateModifiersUnitPrice(double defaultSellPortion) {
        List<TicketItemModifier> ticketItemModifiers = this.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                if (ticketItemModifier.isInfoOnly().booleanValue()) continue;
                ticketItemModifier.setUnitPrice(ticketItemModifier.getUnitPrice() * defaultSellPortion / 100.0);
            }
        }
    }

    public boolean contains(TicketItemModifier ticketItemModifier) {
        List<TicketItemModifier> ticketItemModifiers = this.getTicketItemModifiers();
        int count = 0;
        if (ticketItemModifiers != null) {
            for (TicketItemModifier ticketItemModifier2 : ticketItemModifiers) {
                if (ticketItemModifier2.isInfoOnly().booleanValue() || !ticketItemModifier.getName().trim().equals(ticketItemModifier2.getName().trim())) continue;
                ++count;
            }
        }
        return count > 1;
    }

    public TicketItemModifier removeTicketItemModifier(TicketItemModifier ticketItemModifier) {
        List<TicketItemModifier> ticketItemModifiers = this.getTicketItemModifiers();
        if (ticketItemModifiers == null) {
            return ticketItemModifier;
        }
        Iterator<TicketItemModifier> iter = ticketItemModifiers.iterator();
        while (iter.hasNext()) {
            TicketItemModifier oldTicketItemModifier = iter.next();
            if (oldTicketItemModifier.getModifierId().intValue() != ticketItemModifier.getModifierId().intValue() || oldTicketItemModifier.getModifierType() != ticketItemModifier.getModifierType()) continue;
            iter.remove();
            return oldTicketItemModifier;
        }
        return ticketItemModifier;
    }

    public void addCookingInstruction(TicketItemCookingInstruction cookingInstruction) {
        List<TicketItemCookingInstruction> cookingInstructions = this.getCookingInstructions();
        if (cookingInstructions == null) {
            cookingInstructions = new ArrayList<TicketItemCookingInstruction>(2);
            this.setCookingInstructions(cookingInstructions);
        }
        cookingInstructions.add(cookingInstruction);
    }

    public void addCookingInstructions(List<TicketItemCookingInstruction> instructions) {
        List<TicketItemCookingInstruction> cookingInstructions = this.getCookingInstructions();
        if (cookingInstructions == null) {
            cookingInstructions = new ArrayList<TicketItemCookingInstruction>(2);
            this.setCookingInstructions(cookingInstructions);
        }
        cookingInstructions.addAll(instructions);
    }

    public void removeCookingInstruction(TicketItemCookingInstruction itemCookingInstruction) {
        List<TicketItemCookingInstruction> cookingInstructions2 = this.getCookingInstructions();
        if (cookingInstructions2 == null) {
            return;
        }
        Iterator<TicketItemCookingInstruction> iterator = cookingInstructions2.iterator();
        while (iterator.hasNext()) {
            TicketItemCookingInstruction ticketItemCookingInstruction = iterator.next();
            if (ticketItemCookingInstruction.getTableRowNum() != itemCookingInstruction.getTableRowNum()) continue;
            iterator.remove();
            return;
        }
    }

    public TicketItemModifier findAddOnFor(MenuModifier modifier) {
        List<TicketItemModifier> list = this.getAddOns();
        if (list == null) {
            return null;
        }
        for (TicketItemModifier ticketItemModifier : list) {
            if (!modifier.getId().equals(ticketItemModifier.getModifierId())) continue;
            return ticketItemModifier;
        }
        return null;
    }

    public void addAddOn(MenuModifier menuModifier) {
        List<TicketItemModifier> list = this.getAddOns();
        if (list == null) {
            list = new ArrayList<TicketItemModifier>(2);
            this.setAddOns(list);
        }
        for (int i = list.size() - 1; i >= 0; --i) {
            TicketItemModifier ticketItemModifier = list.get(i);
            if (!menuModifier.getId().equals(ticketItemModifier.getModifierId())) continue;
            if (i != list.size() - 1 || ticketItemModifier.isPrintedToKitchen().booleanValue()) {
                list.add(this.convertToAddOn(menuModifier));
            } else {
                ticketItemModifier.setItemCount(ticketItemModifier.getItemCount() + 1);
            }
            return;
        }
        list.add(this.convertToAddOn(menuModifier));
    }

    public TicketItemModifier convertToAddOn(MenuModifier menuModifier) {
        TicketItemModifier ticketItemModifier = new TicketItemModifier();
        ticketItemModifier.setModifierId(menuModifier.getId());
        ticketItemModifier.setMenuItemModifierGroupId(menuModifier.getModifierGroup().getId());
        ticketItemModifier.setItemCount(1);
        ticketItemModifier.setName(menuModifier.getDisplayName());
        ticketItemModifier.setUnitPrice(menuModifier.getExtraPriceByOrderType(this.getTicket().getOrderType()));
        ticketItemModifier.setModifierType(3);
        ticketItemModifier.setTaxRate(menuModifier.getExtraTaxByOrderType(this.getTicket().getOrderType()));
        ticketItemModifier.setShouldPrintToKitchen(menuModifier.isShouldPrintToKitchen());
        ticketItemModifier.setTicketItem(this);
        return ticketItemModifier;
    }

    public void removeAddOn(TicketItemModifier addOn) {
        List<TicketItemModifier> addOns = this.getAddOns();
        if (addOns == null) {
            return;
        }
        Iterator<TicketItemModifier> iterator = addOns.iterator();
        while (iterator.hasNext()) {
            TicketItemModifier ticketItemModifier = iterator.next();
            if (!ticketItemModifier.getModifierId().equals(addOn.getModifierId())) continue;
            iterator.remove();
        }
    }

    public void calculatePrice() {
        List<TicketItemModifier> addOns;
        List<TicketItemModifier> ticketItemModifiers;
        this.priceIncludesTax = Application.getInstance().isPriceIncludesTax();
        if (this.getSizeModifier() != null) {
            this.getSizeModifier().calculatePrice();
        }
        if ((ticketItemModifiers = this.getTicketItemModifiers()) != null) {
            for (TicketItemModifier modifier : ticketItemModifiers) {
                modifier.calculatePrice();
            }
        }
        if ((addOns = this.getAddOns()) != null) {
            for (TicketItemModifier ticketItemModifier : addOns) {
                ticketItemModifier.calculatePrice();
            }
        }
        this.setSubtotalAmount(NumberUtil.roundToTwoDigit(this.calculateSubtotal(true)));
        this.setSubtotalAmountWithoutModifiers(NumberUtil.roundToTwoDigit(this.calculateSubtotal(false)));
        this.setDiscountAmount(NumberUtil.roundToTwoDigit(this.calculateDiscount()));
        this.setTaxAmount(NumberUtil.roundToTwoDigit(this.calculateTax(true)));
        this.setTaxAmountWithoutModifiers(NumberUtil.roundToTwoDigit(this.calculateTax(false)));
        this.setTotalAmount(NumberUtil.roundToTwoDigit(this.calculateTotal(true)));
        this.setTotalAmountWithoutModifiers(NumberUtil.roundToTwoDigit(this.calculateTotal(false)));
    }

    public boolean isMergable(TicketItem otherItem, boolean merge) {
        if (this.isFractionalUnit().booleanValue() || this.getItemId() == 0 || this.getCookingInstructions() != null && this.getCookingInstructions().size() > 0) {
            return false;
        }
        if (!this.isHasModifiers().booleanValue() && !otherItem.isHasModifiers().booleanValue()) {
            if (this.isTreatAsSeat() == otherItem.isTreatAsSeat() && this.getSeatNumber().intValue() == otherItem.getSeatNumber().intValue()) {
                return true;
            }
            return this.getItemId().equals(otherItem.getItemId()) && this.getSeatNumber() == otherItem.getSeatNumber();
        }
        if (!this.isMergableModifiers(this.getTicketItemModifiers(), otherItem.getTicketItemModifiers(), merge)) {
            return false;
        }
        return this.isMergableModifiers(this.getAddOns(), otherItem.getAddOns(), merge);
    }

    public boolean isMergableModifiers(List<TicketItemModifier> thisModifiers, List<TicketItemModifier> thatModifiers, boolean merge) {
        if (thatModifiers == null) {
            return true;
        }
        if (thisModifiers.size() != thatModifiers.size()) {
            return false;
        }
        Comparator<TicketItemModifier> comparator = new Comparator<TicketItemModifier>(){

            @Override
            public int compare(TicketItemModifier o1, TicketItemModifier o2) {
                return o1.getModifierId() - o2.getModifierId();
            }
        };
        Collections.sort(thisModifiers, comparator);
        Collections.sort(thatModifiers, comparator);
        Iterator<TicketItemModifier> thisIterator = thisModifiers.iterator();
        Iterator<TicketItemModifier> thatIterator = thatModifiers.iterator();
        while (thisIterator.hasNext()) {
            TicketItemModifier next2;
            TicketItemModifier next1 = thisIterator.next();
            if (comparator.compare(next1, next2 = thatIterator.next()) != 0) {
                return false;
            }
            if (!merge) continue;
            next1.merge(next2);
        }
        return true;
    }

    public void merge(TicketItem otherItem) {
        if (!this.isHasModifiers().booleanValue() && !otherItem.isHasModifiers().booleanValue()) {
            this.setItemCount(this.getItemCount() + otherItem.getItemCount());
            return;
        }
        if (this.isMergable(otherItem, true)) {
            this.setItemCount(this.getItemCount() + otherItem.getItemCount());
        }
    }

    private double calculateSubtotal(boolean includeModifierPrice) {
        double subTotalAmount = this.isFractionalUnit() != false ? NumberUtil.roundToTwoDigit(this.getUnitPrice() * this.getItemQuantity()) : NumberUtil.roundToTwoDigit(this.getUnitPrice() * (double)this.getItemCount().intValue());
        if (this.getSizeModifier() != null) {
            subTotalAmount += this.getSizeModifier().getSubTotalAmount().doubleValue();
        }
        if (includeModifierPrice) {
            List<TicketItemModifier> addOns;
            List<TicketItemModifier> ticketItemModifiers = this.getTicketItemModifiers();
            HashSet<Integer> averagePricedModifierList = new HashSet<Integer>();
            if (ticketItemModifiers != null) {
                for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                    if (ticketItemModifier.isInfoOnly().booleanValue()) continue;
                    if (ticketItemModifier.isShouldSectionWisePrice().booleanValue()) {
                        subTotalAmount += ticketItemModifier.getSubTotalAmount().doubleValue();
                        continue;
                    }
                    if (averagePricedModifierList.contains(ticketItemModifier.getModifierId())) continue;
                    subTotalAmount += ticketItemModifier.getSubTotalAmount().doubleValue();
                    averagePricedModifierList.add(ticketItemModifier.getModifierId());
                }
            }
            if ((addOns = this.getAddOns()) != null) {
                for (TicketItemModifier ticketItemModifier : addOns) {
                    subTotalAmount += ticketItemModifier.getSubTotalAmount().doubleValue();
                }
            }
        }
        return subTotalAmount;
    }

    private double calculateDiscount() {
        double discount = 0.0;
        TicketItemDiscount maxDiscount = DiscountUtil.getMaxDiscount(this.getDiscounts());
        if (maxDiscount != null) {
            discount = maxDiscount.calculateDiscount();
        }
        return discount;
    }

    public double getAmountByType(TicketItemDiscount discount) {
        switch (discount.getType()) {
            case 0: {
                return discount.getValue();
            }
            case 1: {
                return discount.getValue() * this.getUnitPrice() / 100.0;
            }
        }
        return 0.0;
    }

    private double calculateTax(boolean includeModifierTax) {
        double subtotal = 0.0;
        subtotal = this.getSubtotalAmountWithoutModifiers();
        double discount = this.getDiscountAmount();
        subtotal -= discount;
        double taxRate = this.getTaxRate();
        double tax = 0.0;
        if (taxRate > 0.0) {
            tax = this.priceIncludesTax ? subtotal - subtotal / (1.0 + taxRate / 100.0) : subtotal * (taxRate / 100.0);
        }
        if (includeModifierTax) {
            List<TicketItemModifier> addOns;
            List<TicketItemModifier> ticketItemModifiers = this.getTicketItemModifiers();
            if (ticketItemModifiers != null) {
                for (TicketItemModifier modifier : ticketItemModifiers) {
                    tax += modifier.getTaxAmount().doubleValue();
                }
            }
            if ((addOns = this.getAddOns()) != null) {
                for (TicketItemModifier ticketItemModifier : addOns) {
                    tax += ticketItemModifier.getTaxAmount().doubleValue();
                }
            }
        }
        return tax;
    }

    private double calculateTotal(boolean includeModifiers) {
        double total = 0.0;
        total = includeModifiers ? (this.priceIncludesTax ? this.getSubtotalAmount() - this.getDiscountAmount() : this.getSubtotalAmount() - this.getDiscountAmount() + this.getTaxAmount()) : (this.priceIncludesTax ? this.getSubtotalAmountWithoutModifiers() - this.getDiscountAmount() : this.getSubtotalAmountWithoutModifiers() - this.getDiscountAmount() + this.getTaxAmountWithoutModifiers());
        return total;
    }

    @Override
    public String getNameDisplay() {
        String name = this.getName();
        if (this.getSizeModifier() != null) {
            name = name + "\n" + this.getSizeModifier().getNameDisplay();
        }
        return name;
    }

    @Override
    public Double getUnitPriceDisplay() {
        if (this.isTreatAsSeat().booleanValue()) {
            return null;
        }
        return this.getUnitPrice();
    }

    @Override
    public String getItemQuantityDisplay() {
        if (this.isTreatAsSeat().booleanValue()) {
            return "";
        }
        if (this.isFractionalUnit().booleanValue()) {
            double itemQuantity = this.getItemQuantity();
            if (itemQuantity % 1.0 == 0.0) {
                return String.valueOf((int)itemQuantity) + this.getItemUnitName();
            }
            itemQuantity = NumberUtil.roundToTwoDigit(itemQuantity);
            return itemQuantity + this.getItemUnitName();
        }
        return String.valueOf(this.getItemCount());
    }

    @Override
    public Double getTaxAmountWithoutModifiersDisplay() {
        return this.getTaxAmountWithoutModifiers();
    }

    @Override
    public Double getTotalAmountWithoutModifiersDisplay() {
        return this.getTotalAmountWithoutModifiers();
    }

    @Override
    public Double getSubTotalAmountDisplay() {
        return this.getSubtotalAmount();
    }

    @Override
    public Double getSubTotalAmountWithoutModifiersDisplay() {
        if (this.isTreatAsSeat().booleanValue()) {
            return null;
        }
        return this.getSubtotalAmountWithoutModifiers();
    }

    public boolean isPriceIncludesTax() {
        return this.priceIncludesTax;
    }

    public void setPriceIncludesTax(boolean priceIncludesTax) {
        this.priceIncludesTax = priceIncludesTax;
    }

    @Override
    public String getItemCode() {
        return String.valueOf(this.getItemId());
    }

    public List<Printer> getPrinters(OrderType orderType) {
        PosPrinters printers = PosPrinters.load();
        PrinterGroup printerGroup = this.getPrinterGroup();
        ArrayList<Printer> printerAll = new ArrayList<Printer>();
        if (printerGroup == null) {
            printerAll.add(printers.getDefaultKitchenPrinter());
            return printerAll;
        }
        List<String> printerNames = printerGroup.getPrinterNames();
        List<Printer> kitchenPrinters = printers.getKitchenPrinters();
        for (Printer printer : kitchenPrinters) {
            if (!printerNames.contains(printer.getVirtualPrinter().getName())) continue;
            printerAll.add(printer);
        }
        return printerAll;
    }

    @Override
    public PrinterGroup getPrinterGroup() {
        if (super.getPrinterGroup() == null) {
            List<PrinterGroup> printerGroups = PrinterGroupDAO.getInstance().findAll();
            for (PrinterGroup printerGroup : printerGroups) {
                if (!printerGroup.isIsDefault()) continue;
                return printerGroup;
            }
        }
        return super.getPrinterGroup();
    }

    @Override
    public boolean canAddDiscount() {
        return true;
    }

    @Override
    public boolean canVoid() {
        return true;
    }

    @Override
    public boolean canAddAdOn() {
        return true;
    }

    public MenuItem getMenuItem() {
        if (this.menuItem == null) {
            this.menuItem = MenuItemDAO.getInstance().loadInitialized(this.getItemId());
        }
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public String getKitchenStatus() {
        if (super.getStatus() == null) {
            return "";
        }
        return super.getStatus();
    }

    public TicketItemModifier findTicketItemModifierFor(MenuModifier menuModifier) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return null;
        }
        for (TicketItemModifier ticketItemModifier : modifiers) {
            Integer itemId = ticketItemModifier.getModifierId();
            if (itemId == null || itemId.intValue() != menuModifier.getId().intValue()) continue;
            return ticketItemModifier;
        }
        return null;
    }

    public TicketItemModifier findTicketItemModifierFor(MenuModifier menuModifier, Multiplier multiplier) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return null;
        }
        for (TicketItemModifier ticketItemModifier : modifiers) {
            Integer itemId = ticketItemModifier.getModifierId();
            if (itemId == null || itemId.intValue() != menuModifier.getId().intValue() || !multiplier.getName().equals(ticketItemModifier.getMultiplierName())) continue;
            return ticketItemModifier;
        }
        return null;
    }

    public TicketItemModifier findTicketItemModifierFor(MenuModifier menuModifier, String sectionName) {
        return this.findTicketItemModifierFor(menuModifier, sectionName, null);
    }

    public TicketItemModifier findTicketItemModifierFor(MenuModifier menuModifier, String sectionName, Multiplier multiplier) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return null;
        }
        for (TicketItemModifier ticketItemModifier : modifiers) {
            Integer itemId = ticketItemModifier.getModifierId();
            if (multiplier != null && itemId != null && itemId.intValue() == menuModifier.getId().intValue() && sectionName != null && sectionName.equals(ticketItemModifier.getSectionName()) && multiplier != null && multiplier.getName().equals(ticketItemModifier.getMultiplierName())) {
                return ticketItemModifier;
            }
            if (itemId == null || itemId.intValue() != menuModifier.getId().intValue() || sectionName == null || !sectionName.equals(ticketItemModifier.getSectionName())) continue;
            return ticketItemModifier;
        }
        return null;
    }

    public int countModifierFromGroup(MenuItemModifierGroup menuItemModifierGroup) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return 0;
        }
        int modifierFromGroupCount = 0;
        for (TicketItemModifier ticketItemModifier : modifiers) {
            Integer groupId = ticketItemModifier.getMenuItemModifierGroupId();
            if (groupId == null || groupId.intValue() != menuItemModifierGroup.getId().intValue()) continue;
            modifierFromGroupCount += ticketItemModifier.getItemCount().intValue();
        }
        return modifierFromGroupCount;
    }

    public boolean requiredModifiersAdded(MenuItemModifierGroup menuItemModifierGroup) {
        int minQuantity = menuItemModifierGroup.getMinQuantity();
        if (minQuantity == 0) {
            return true;
        }
        return this.countModifierFromGroup(menuItemModifierGroup) >= minQuantity;
    }

    public boolean deleteTicketItemModifier(TicketItemModifier ticketItemModifierToRemove) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return false;
        }
        Iterator<TicketItemModifier> iterator = modifiers.iterator();
        while (iterator.hasNext()) {
            TicketItemModifier ticketItemModifier = iterator.next();
            if (ticketItemModifier != ticketItemModifierToRemove) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    public boolean deleteTicketItemModifierByName(TicketItemModifier ticketItemModifierToRemove) {
        List<TicketItemModifier> modifiers = this.getTicketItemModifiers();
        if (modifiers == null) {
            return false;
        }
        Iterator<TicketItemModifier> iterator = modifiers.iterator();
        while (iterator.hasNext()) {
            TicketItemModifier ticketItemModifier = iterator.next();
            if (!ticketItemModifier.getName().equals(ticketItemModifierToRemove.getName())) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    public static enum PIZZA_SECTION_MODE {
        FULL(1),
        HALF(2),
        QUARTER(3);

        private final int value;

        private PIZZA_SECTION_MODE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static PIZZA_SECTION_MODE from(int value) {
            if (value == 2) {
                return HALF;
            }
            if (value == 3) {
                return QUARTER;
            }
            return FULL;
        }

        public String toString() {
            return this.name();
        }
    }
}


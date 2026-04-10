/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.Customer;
import com.floreantpos.model.Discount;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Printer;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.User;
import com.floreantpos.model.base.BaseTicket;
import com.floreantpos.model.base.BaseTicketDiscount;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.util.DiscountUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="ticket")
public class Ticket
extends BaseTicket {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CARD_TRANSACTION_ID = "card_transaction_id";
    public static final String PROPERTY_CARD_TRACKS = "card_tracks";
    public static final String PROPERTY_CARD_NAME = "card_name";
    public static final String PROPERTY_PAYMENT_METHOD = "payment_method";
    public static final String PROPERTY_CARD_READER = "card_reader";
    public static final String PROPERTY_CARD_NUMBER = "card_number";
    public static final String PROPERTY_CARD_EXP_YEAR = "card_exp_year";
    public static final String PROPERTY_CARD_EXP_MONTH = "card_exp_month";
    public static final String PROPERTY_ADVANCE_PAYMENT = "advance_payment";
    public static final String PROPERTY_CARD_AUTH_CODE = "card_auth_code";
    private OrderType orderType;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy, h:m a");
    private List deletedItems;
    private boolean priceIncludesTax;
    public static final String STATUS_WAITING = "Waiting (Kitchen)";
    public static final String STATUS_READY = "Ready";
    public static final String STATUS_NOT_SENT = "Not Sent";
    public static final String STATUS_DRIVING = "Driving";
    public static final String STATUS_VOID = "Void";
    public static final String CUSTOMER_MOBILE = "CUSTOMER_MOBILE";
    public static final String CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final String CUSTOMER_ZIP_CODE = "CUSTOMER_ZIP_CODE";
    public static final String MANAGER_INSTRUCTION = "MANAGER_INSTRUCTION";
    public static final String PHONE_EXTENSION = "PHONE_EXTENSION";
    public static final String DRIVER_OUT_TIME = "OUT_AT";
    private String sortOrder;

    public Ticket() {
    }

    public Ticket(Integer id) {
        super(id);
    }

    public void addTable(int tableNumber) {
        List<Integer> numbers = this.getTableNumbers();
        if (numbers == null) {
            numbers = new ArrayList<Integer>();
            this.setTableNumbers(numbers);
        }
        numbers.add(tableNumber);
    }

    @Override
    public void setClosed(Boolean closed) {
        super.setClosed(closed);
        if (closed.booleanValue()) {
            ShopTableDAO.getInstance().releaseTables(this);
        }
    }

    public void setGratuityAmount(double amount) {
        Gratuity gratuity = this.getGratuity();
        if (gratuity == null) {
            gratuity = this.createGratuity();
            this.setGratuity(gratuity);
        }
        gratuity.setAmount(amount);
    }

    public double getGratuityAmount() {
        Gratuity gratuity = this.getGratuity();
        if (gratuity != null) {
            return gratuity.getAmount();
        }
        return 0.0;
    }

    public Gratuity createGratuity() {
        Gratuity gratuity = new Gratuity();
        gratuity.setTicket(this);
        gratuity.setTerminal(Application.getInstance().getTerminal());
        gratuity.setOwner(this.getOwner());
        gratuity.setPaid(false);
        return gratuity;
    }

    public boolean hasGratuity() {
        return this.getGratuity() != null;
    }

    @Override
    public void setCreateDate(Date createDate) {
        super.setCreateDate(createDate);
        super.setActiveDate(createDate);
    }

    @Override
    public Date getDeliveryDate() {
        Date deliveryDate = super.getDeliveryDate();
        return deliveryDate;
    }

    @Override
    public List<TicketItem> getTicketItems() {
        List<TicketItem> items = super.getTicketItems();
        if (items == null) {
            items = new ArrayList<TicketItem>();
            super.setTicketItems(items);
        }
        return items;
    }

    @Override
    public Integer getNumberOfGuests() {
        Integer guests = super.getNumberOfGuests();
        if (guests == null || guests == 0) {
            return 1;
        }
        return guests;
    }

    public Ticket(User owner, Date createTime) {
        this.setOwner(owner);
        this.setCreateDate(createTime);
    }

    public String getCreateDateFormatted() {
        return dateFormat.format(this.getCreateDate());
    }

    public String getTitle() {
        String title = "";
        if (this.getId() != null) {
            title = title + "#" + this.getId();
        }
        title = title + Messages.getString("Ticket.1") + ": " + this.getOwner();
        title = title + Messages.getString("Ticket.18") + ":" + this.getCreateDateFormatted();
        title = title + Messages.getString("Ticket.20") + ": " + NumberUtil.formatNumber(this.getTotalAmount());
        return title;
    }

    public int getBeverageCount() {
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return 0;
        }
        int count = 0;
        for (TicketItem ticketItem : ticketItems) {
            if (!ticketItem.isBeverage().booleanValue()) continue;
            count += ticketItem.getItemCount().intValue();
        }
        return count;
    }

    public void calculatePrice() {
        this.priceIncludesTax = Application.getInstance().isPriceIncludesTax();
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return;
        }
        for (TicketItem ticketItem : ticketItems) {
            ticketItem.calculatePrice();
        }
        double subtotalAmount = this.calculateSubtotalAmount();
        double discountAmount = this.calculateItemsDiscountAmount();
        double toleranceAmount = this.calculateToleranceAmount();
        double ticketDiscountAmount = this.calculateTicketDiscountAmount(discountAmount);
        if (ticketDiscountAmount > 0.0) {
            discountAmount = ticketDiscountAmount;
        }
        this.setSubtotalAmount(subtotalAmount);
        discountAmount += toleranceAmount;
        double taxAmount = this.calculateTax();
        if (ticketDiscountAmount > 0.0) {
            double discountTax = taxAmount * ticketDiscountAmount / subtotalAmount;
            taxAmount -= discountTax;
        }
        this.setDiscountAmount(discountAmount);
        this.setTaxAmount(taxAmount);
        Double deliveryChargeAmount = NumberUtil.roundToTwoDigit(this.getDeliveryCharge());
        double serviceChargeAmount = this.calculateServiceCharge();
        double totalAmount = 0.0;
        totalAmount = this.priceIncludesTax ? subtotalAmount - discountAmount + deliveryChargeAmount + serviceChargeAmount : subtotalAmount - discountAmount + deliveryChargeAmount + taxAmount + serviceChargeAmount;
        if (this.getGratuity() != null) {
            totalAmount += this.getGratuity().getAmount().doubleValue();
        }
        totalAmount = this.fixInvalidAmount(totalAmount);
        this.setServiceCharge(serviceChargeAmount);
        this.setTotalAmount(NumberUtil.roundToTwoDigit(totalAmount));
        double dueAmount = totalAmount - this.getPaidAmount();
        this.setDueAmount(NumberUtil.roundToTwoDigit(dueAmount));
    }

    public void updateTicketItemPriceByOrderType() {
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return;
        }
        for (TicketItem ticketItem : ticketItems) {
            Integer itemId = Integer.parseInt(ticketItem.getItemId().toString());
            MenuItem menuItem = MenuItemDAO.getInstance().initialize(MenuItemDAO.getInstance().get(itemId));
            if (menuItem == null) continue;
            ticketItem.setUnitPrice(menuItem.getPriceByOrderType(this.getOrderType()));
            ticketItem.setTaxRate(menuItem.getTaxByOrderType(this.getOrderType()));
        }
    }

    public void updateTicketItemPriceByOrderType(String name) {
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return;
        }
        for (TicketItem ticketItem : ticketItems) {
            Integer itemId = Integer.parseInt(ticketItem.getItemId().toString());
            MenuItem menuItem = MenuItemDAO.getInstance().initialize(MenuItemDAO.getInstance().get(itemId));
            if (menuItem == null) continue;
            ticketItem.setUnitPrice(menuItem.getPriceByOrderType(name));
            ticketItem.setTaxRate(menuItem.getTaxByOrderType(name));
        }
    }

    private double calculateSubtotalAmount() {
        double subtotalAmount = 0.0;
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return subtotalAmount;
        }
        for (TicketItem ticketItem : ticketItems) {
            subtotalAmount += ticketItem.getSubtotalAmount().doubleValue();
        }
        subtotalAmount = this.fixInvalidAmount(subtotalAmount);
        return NumberUtil.roundToTwoDigit(subtotalAmount);
    }

    private double calculateItemsDiscountAmount() {
        double ticketItemDiscounts = 0.0;
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems != null) {
            for (TicketItem ticketItem : ticketItems) {
                ticketItemDiscounts += ticketItem.getDiscountAmount().doubleValue();
            }
        }
        ticketItemDiscounts = this.fixInvalidAmount(ticketItemDiscounts);
        return NumberUtil.roundToTwoDigit(ticketItemDiscounts);
    }

    public double calculateToleranceAmount() {
        double discount = 0.0;
        BaseTicketDiscount tolerance = null;
        if (this.getDiscounts() != null) {
            for (TicketDiscount tDiscount : this.getDiscounts()) {
                if (!tDiscount.getName().equals("Tolerance")) continue;
                tolerance = tDiscount;
            }
        }
        if (tolerance != null) {
            discount += tolerance.getValue().doubleValue();
        }
        discount = this.fixInvalidAmount(discount);
        return NumberUtil.roundToTwoDigit(discount);
    }

    private double calculateTicketDiscountAmount(double itemsDiscount) {
        TicketDiscount ticketCouponAndDiscount;
        double discount = 0.0;
        ArrayList<TicketDiscount> discounts = new ArrayList<TicketDiscount>();
        if (this.getDiscounts() != null) {
            for (TicketDiscount tDiscount : this.getDiscounts()) {
                if (tDiscount.getName().equals("Tolerance")) continue;
                discounts.add(tDiscount);
            }
        }
        if ((ticketCouponAndDiscount = DiscountUtil.getMaxDiscount(discounts, itemsDiscount)) != null) {
            discount = DiscountUtil.calculateDiscountAmount(this.getSubtotalAmount() - discount, ticketCouponAndDiscount);
        }
        discount = this.fixInvalidAmount(discount);
        return NumberUtil.roundToTwoDigit(discount);
    }

    @Override
    public Double getDeliveryCharge() {
        Double deliveryCharge = super.getDeliveryCharge();
        if (deliveryCharge == null) {
            return 0.0;
        }
        return deliveryCharge;
    }

    public double getAmountByType(TicketDiscount discount) {
        switch (discount.getType()) {
            case 0: {
                return discount.getValue();
            }
            case 1: {
                return discount.getValue() * this.getSubtotalAmount() / 100.0;
            }
        }
        return 0.0;
    }

    public static TicketDiscount convertToTicketDiscount(Discount discount, Ticket ticket) {
        TicketDiscount ticketDiscount = new TicketDiscount();
        ticketDiscount.setDiscountId(discount.getId());
        ticketDiscount.setName(discount.getName());
        ticketDiscount.setType(discount.getType());
        ticketDiscount.setMinimumAmount(discount.getMinimunBuy());
        ticketDiscount.setValue(discount.getValue());
        ticketDiscount.setTicket(ticket);
        return ticketDiscount;
    }

    private double calculateTax() {
        List<TicketItem> ticketItems = this.getTicketItems();
        if (ticketItems == null) {
            return 0.0;
        }
        double tax = 0.0;
        for (TicketItem ticketItem : ticketItems) {
            tax += ticketItem.getTaxAmount().doubleValue();
        }
        return NumberUtil.roundToTwoDigit(this.fixInvalidAmount(tax));
    }

    private double fixInvalidAmount(double tax) {
        if (tax < 0.0 || Double.isNaN(tax)) {
            tax = 0.0;
        }
        return tax;
    }

    public double calculateDiscountFromType(TicketDiscount coupon, double subtotal) {
        List<TicketItem> ticketItems = this.getTicketItems();
        double discount = 0.0;
        int type = coupon.getType();
        double couponValue = coupon.getValue();
        switch (type) {
            case 3: {
                discount += couponValue;
                break;
            }
            case 1: {
                HashSet<Integer> categoryIds = new HashSet<Integer>();
                for (TicketItem item : ticketItems) {
                    Integer itemId = item.getItemId();
                    if (categoryIds.contains(itemId)) continue;
                    discount += couponValue;
                    categoryIds.add(itemId);
                }
                break;
            }
            case 2: {
                for (TicketItem item : ticketItems) {
                    discount += couponValue * (double)item.getItemCount().intValue();
                }
                break;
            }
            case 6: {
                discount += subtotal * couponValue / 100.0;
                break;
            }
            case 4: {
                HashSet<Integer> categoryIds = new HashSet<Integer>();
                for (TicketItem item : ticketItems) {
                    Integer itemId = item.getItemId();
                    if (categoryIds.contains(itemId)) continue;
                    discount += item.getUnitPrice() * couponValue / 100.0;
                    categoryIds.add(itemId);
                }
                break;
            }
            case 5: {
                for (TicketItem item : ticketItems) {
                    discount += item.getSubtotalAmountWithoutModifiers() * couponValue / 100.0;
                }
                break;
            }
            case 0: {
                discount += couponValue;
            }
        }
        return discount;
    }

    public void addDeletedItems(Object o) {
        if (this.deletedItems == null) {
            this.deletedItems = new ArrayList();
        }
        this.deletedItems.add(o);
    }

    public List getDeletedItems() {
        return this.deletedItems;
    }

    public void clearDeletedItems() {
        if (this.deletedItems != null) {
            this.deletedItems.clear();
        }
        this.deletedItems = null;
    }

    public int countItem(TicketItem ticketItem) {
        List<TicketItem> items = this.getTicketItems();
        if (items == null) {
            return 0;
        }
        int count = 0;
        for (TicketItem ticketItem2 : items) {
            if (!ticketItem.getItemId().equals(ticketItem2.getItemId())) continue;
            ++count;
        }
        return count;
    }

    public boolean needsKitchenPrint() {
        if (this.getDeletedItems() != null && this.getDeletedItems().size() > 0) {
            return true;
        }
        List<TicketItem> ticketItems = this.getTicketItems();
        for (TicketItem item : ticketItems) {
            List<TicketItemCookingInstruction> cookingInstructions;
            if (item.isShouldPrintToKitchen().booleanValue() && !item.isPrintedToKitchen().booleanValue()) {
                return true;
            }
            List<TicketItemModifier> ticketItemModifiers = item.getTicketItemModifiers();
            if (ticketItemModifiers != null) {
                for (TicketItemModifier modifier : ticketItemModifiers) {
                    if (!modifier.isShouldPrintToKitchen().booleanValue() || modifier.isPrintedToKitchen().booleanValue()) continue;
                    return true;
                }
            }
            if ((cookingInstructions = item.getCookingInstructions()) == null) continue;
            for (TicketItemCookingInstruction ticketItemCookingInstruction : cookingInstructions) {
                if (ticketItemCookingInstruction.isPrintedToKitchen().booleanValue()) continue;
                return true;
            }
        }
        return false;
    }

    public double calculateServiceCharge() {
        Restaurant restaurant = Application.getInstance().getRestaurant();
        double serviceChargePercentage = restaurant.getServiceChargePercentage();
        double serviceCharge = 0.0;
        if (serviceChargePercentage > 0.0) {
            serviceCharge = (this.getSubtotalAmount() - this.getDiscountAmount()) * (serviceChargePercentage / 100.0);
        }
        return NumberUtil.roundToTwoDigit(this.fixInvalidAmount(serviceCharge));
    }

    public OrderType getOrderType() {
        if (this.orderType == null) {
            String type = this.getTicketType();
            this.orderType = OrderTypeDAO.getInstance().findByName(type);
        }
        return this.orderType;
    }

    public void setOrderType(OrderType type) {
        this.orderType = type;
        this.setTicketType(type.getName());
    }

    public boolean isPriceIncludesTax() {
        return this.priceIncludesTax;
    }

    public void setPriceIncludesTax(boolean priceIncludesTax) {
        this.priceIncludesTax = priceIncludesTax;
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (this.getProperties() == null) {
            return null;
        }
        String string = this.getProperties().get(key);
        if (StringUtils.isEmpty((String)string)) {
            return defaultValue;
        }
        return string;
    }

    public void removeProperty(String propertyName) {
        Map<String, String> properties = this.getProperties();
        if (properties == null) {
            return;
        }
        properties.remove(propertyName);
    }

    public boolean isPropertyValueTrue(String propertyName) {
        String property = this.getProperty(propertyName);
        return POSUtil.getBoolean(property);
    }

    public String toURLForm() {
        String s = "ticket_id=" + this.getId();
        List<TicketItem> items = this.getTicketItems();
        if (items == null || items.size() == 0) {
            return s;
        }
        for (int i = 0; i < items.size(); ++i) {
            TicketItem ticketItem = items.get(i);
            s = s + "&items[" + i + "][id]=" + ticketItem.getId();
            s = s + "&items[" + i + "][name]=" + POSUtil.encodeURLString(ticketItem.getName());
            s = s + "&items[" + i + "][price]=" + ticketItem.getSubtotalAmount();
        }
        s = s + "&tax=" + this.getTaxAmount();
        s = s + "&subtotal=" + this.getSubtotalAmount();
        s = s + "&grandtotal=" + this.getTotalAmount();
        return s;
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.addProperty(CUSTOMER_ID, String.valueOf(customer.getAutoId()));
            this.addProperty(CUSTOMER_NAME, customer.getFirstName());
            this.addProperty(CUSTOMER_MOBILE, customer.getMobileNo());
            this.addProperty(CUSTOMER_ZIP_CODE, customer.getZipCode());
        }
        if (customer != null) {
            this.setCustomerId(customer.getAutoId());
        }
    }

    public void removeCustomer() {
        this.removeProperty(CUSTOMER_ID);
        this.removeProperty(CUSTOMER_NAME);
        this.removeProperty(CUSTOMER_MOBILE);
        this.removeProperty(CUSTOMER_ZIP_CODE);
    }

    public String getSortOrder() {
        if (this.sortOrder == null) {
            return "";
        }
        return this.sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getStatus() {
        if (super.getStatus() == null) {
            return "";
        }
        return super.getStatus();
    }

    public void consolidateTicketItems() {
        List<TicketItem> ticketItems = this.getTicketItems();
        LinkedHashMap itemMap = new LinkedHashMap();
        for (TicketItem newItem : ticketItems) {
            List itemListInMap = (List)itemMap.get(newItem.getItemId().toString());
            if (itemListInMap == null) {
                ArrayList<TicketItem> list = new ArrayList<TicketItem>();
                list.add(newItem);
                itemMap.put(newItem.getItemId().toString(), list);
                continue;
            }
            boolean merged = false;
            for (TicketItem itemInMap : itemListInMap) {
                if (!itemInMap.isMergable(newItem, false)) continue;
                itemInMap.merge(newItem);
                merged = true;
                break;
            }
            if (merged) continue;
            itemListInMap.add(newItem);
        }
        this.getTicketItems().clear();
        Collection values = itemMap.values();
        for (List list : values) {
            if (list == null) continue;
            this.getTicketItems().addAll(list);
        }
        List<TicketItem> ticketItemList = this.getTicketItems();
        if (this.getOrderType().isAllowSeatBasedOrder().booleanValue()) {
            Collections.sort(ticketItemList, new Comparator<TicketItem>(){

                @Override
                public int compare(TicketItem o1, TicketItem o2) {
                    return o1.getId() - o2.getId();
                }
            });
            Collections.sort(ticketItemList, new Comparator<TicketItem>(){

                @Override
                public int compare(TicketItem o1, TicketItem o2) {
                    return o1.getSeatNumber() - o2.getSeatNumber();
                }
            });
        }
        this.calculatePrice();
    }

    public void markPrintedToKitchen() {
        List<TicketItem> ticketItems = this.getTicketItems();
        for (TicketItem ticketItem : ticketItems) {
            List<TicketItemModifier> addOns;
            List<Printer> printers;
            if ((ticketItem.isPrintedToKitchen().booleanValue() || !ticketItem.isShouldPrintToKitchen().booleanValue()) && !ticketItem.isHasModifiers().booleanValue() || (printers = ticketItem.getPrinters(this.getOrderType())) == null) continue;
            ticketItem.setPrintedToKitchen(true);
            List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
            if (ticketItemModifiers != null) {
                for (TicketItemModifier itemModifier : ticketItemModifiers) {
                    itemModifier.setPrintedToKitchen(true);
                }
            }
            if ((addOns = ticketItem.getAddOns()) == null) continue;
            for (TicketItemModifier ticketItemModifier : addOns) {
                ticketItemModifier.setPrintedToKitchen(true);
            }
        }
    }
}


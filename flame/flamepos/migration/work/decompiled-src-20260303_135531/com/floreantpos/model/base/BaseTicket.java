/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Gratuity;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseTicket
implements Comparable,
Serializable {
    public static String REF = "Ticket";
    public static String PROP_RE_OPENED = "reOpened";
    public static String PROP_VOID_REASON = "voidReason";
    public static String PROP_DUE_AMOUNT = "dueAmount";
    public static String PROP_DISCOUNT_AMOUNT = "discountAmount";
    public static String PROP_CREATE_DATE = "createDate";
    public static String PROP_DELIVERY_CHARGE = "deliveryCharge";
    public static String PROP_NUMBER_OF_GUESTS = "numberOfGuests";
    public static String PROP_PAID = "paid";
    public static String PROP_ADVANCE_AMOUNT = "advanceAmount";
    public static String PROP_CUSTOMER_ID = "customerId";
    public static String PROP_ACTIVE_DATE = "activeDate";
    public static String PROP_ASSIGNED_DRIVER = "assignedDriver";
    public static String PROP_CREATION_HOUR = "creationHour";
    public static String PROP_CUSTOMER_WILL_PICKUP = "customerWillPickup";
    public static String PROP_DRAWER_RESETTED = "drawerResetted";
    public static String PROP_OWNER = "owner";
    public static String PROP_GLOBAL_ID = "globalId";
    public static String PROP_DELIVERY_DATE = "deliveryDate";
    public static String PROP_GRATUITY = "gratuity";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_CLOSED = "closed";
    public static String PROP_CLOSING_DATE = "closingDate";
    public static String PROP_DELIVERY_ADDRESS = "deliveryAddress";
    public static String PROP_SHIFT = "shift";
    public static String PROP_TAX_AMOUNT = "taxAmount";
    public static String PROP_REFUNDED = "refunded";
    public static String PROP_STATUS = "status";
    public static String PROP_SUBTOTAL_AMOUNT = "subtotalAmount";
    public static String PROP_BAR_TAB = "barTab";
    public static String PROP_VOIDED_BY = "voidedBy";
    public static String PROP_TICKET_TYPE = "ticketType";
    public static String PROP_TAX_EXEMPT = "taxExempt";
    public static String PROP_ID = "id";
    public static String PROP_WASTED = "wasted";
    public static String PROP_VOIDED = "voided";
    public static String PROP_TOTAL_AMOUNT = "totalAmount";
    public static String PROP_PAID_AMOUNT = "paidAmount";
    public static String PROP_EXTRA_DELIVERY_INFO = "extraDeliveryInfo";
    public static String PROP_SERVICE_CHARGE = "serviceCharge";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String globalId;
    protected Date createDate;
    protected Date closingDate;
    protected Date activeDate;
    protected Date deliveryDate;
    protected Integer creationHour;
    protected Boolean paid;
    protected Boolean voided;
    protected String voidReason;
    protected Boolean wasted;
    protected Boolean refunded;
    protected Boolean closed;
    protected Boolean drawerResetted;
    protected Double subtotalAmount;
    protected Double discountAmount;
    protected Double taxAmount;
    protected Double totalAmount;
    protected Double paidAmount;
    protected Double dueAmount;
    protected Double advanceAmount;
    protected Integer numberOfGuests;
    protected String status;
    protected Boolean barTab;
    protected Boolean taxExempt;
    protected Boolean reOpened;
    protected Double serviceCharge;
    protected Double deliveryCharge;
    protected Integer customerId;
    protected String deliveryAddress;
    protected Boolean customerWillPickup;
    protected String extraDeliveryInfo;
    protected String ticketType;
    private Shift shift;
    private User owner;
    private User assignedDriver;
    private Gratuity gratuity;
    private User voidedBy;
    private Terminal terminal;
    private Map<String, String> properties;
    private List<TicketItem> ticketItems;
    private List<TicketDiscount> discounts;
    private Set<PosTransaction> transactions;
    private List<Integer> tableNumbers;

    public BaseTicket() {
        this.initialize();
    }

    public BaseTicket(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public String getGlobalId() {
        return this.globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getClosingDate() {
        return this.closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public Date getActiveDate() {
        return this.activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getCreationHour() {
        return this.creationHour == null ? Integer.valueOf(0) : this.creationHour;
    }

    public void setCreationHour(Integer creationHour) {
        this.creationHour = creationHour;
    }

    public Boolean isPaid() {
        return this.paid == null ? Boolean.FALSE : this.paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean isVoided() {
        return this.voided == null ? Boolean.FALSE : this.voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public String getVoidReason() {
        return this.voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public Boolean isWasted() {
        return this.wasted == null ? Boolean.FALSE : this.wasted;
    }

    public void setWasted(Boolean wasted) {
        this.wasted = wasted;
    }

    public Boolean isRefunded() {
        return this.refunded == null ? Boolean.FALSE : this.refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public Boolean isClosed() {
        return this.closed == null ? Boolean.FALSE : this.closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean isDrawerResetted() {
        return this.drawerResetted == null ? Boolean.FALSE : this.drawerResetted;
    }

    public void setDrawerResetted(Boolean drawerResetted) {
        this.drawerResetted = drawerResetted;
    }

    public Double getSubtotalAmount() {
        return this.subtotalAmount == null ? Double.valueOf(0.0) : this.subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public Double getDiscountAmount() {
        return this.discountAmount == null ? Double.valueOf(0.0) : this.discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getTaxAmount() {
        return this.taxAmount == null ? Double.valueOf(0.0) : this.taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getTotalAmount() {
        return this.totalAmount == null ? Double.valueOf(0.0) : this.totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPaidAmount() {
        return this.paidAmount == null ? Double.valueOf(0.0) : this.paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getDueAmount() {
        return this.dueAmount == null ? Double.valueOf(0.0) : this.dueAmount;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Double getAdvanceAmount() {
        return this.advanceAmount == null ? Double.valueOf(0.0) : this.advanceAmount;
    }

    public void setAdvanceAmount(Double advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public Integer getNumberOfGuests() {
        return this.numberOfGuests == null ? Integer.valueOf(0) : this.numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isBarTab() {
        return this.barTab == null ? Boolean.FALSE : this.barTab;
    }

    public void setBarTab(Boolean barTab) {
        this.barTab = barTab;
    }

    public Boolean isTaxExempt() {
        return this.taxExempt == null ? Boolean.FALSE : this.taxExempt;
    }

    public void setTaxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public Boolean isReOpened() {
        return this.reOpened == null ? Boolean.FALSE : this.reOpened;
    }

    public void setReOpened(Boolean reOpened) {
        this.reOpened = reOpened;
    }

    public Double getServiceCharge() {
        return this.serviceCharge == null ? Double.valueOf(0.0) : this.serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Double getDeliveryCharge() {
        return this.deliveryCharge == null ? Double.valueOf(0.0) : this.deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Integer getCustomerId() {
        return this.customerId == null ? Integer.valueOf(0) : this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Boolean isCustomerWillPickup() {
        return this.customerWillPickup == null ? Boolean.FALSE : this.customerWillPickup;
    }

    public void setCustomerWillPickup(Boolean customerWillPickup) {
        this.customerWillPickup = customerWillPickup;
    }

    public String getExtraDeliveryInfo() {
        return this.extraDeliveryInfo;
    }

    public void setExtraDeliveryInfo(String extraDeliveryInfo) {
        this.extraDeliveryInfo = extraDeliveryInfo;
    }

    public String getTicketType() {
        return this.ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(User assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Gratuity getGratuity() {
        return this.gratuity;
    }

    public void setGratuity(Gratuity gratuity) {
        this.gratuity = gratuity;
    }

    public User getVoidedBy() {
        return this.voidedBy;
    }

    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<TicketItem> getTicketItems() {
        return this.ticketItems;
    }

    public void setTicketItems(List<TicketItem> ticketItems) {
        this.ticketItems = ticketItems;
    }

    public void addToticketItems(TicketItem ticketItem) {
        if (null == this.getTicketItems()) {
            this.setTicketItems(new ArrayList<TicketItem>());
        }
        this.getTicketItems().add(ticketItem);
    }

    public List<TicketDiscount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(List<TicketDiscount> discounts) {
        this.discounts = discounts;
    }

    public void addTodiscounts(TicketDiscount ticketDiscount) {
        if (null == this.getDiscounts()) {
            this.setDiscounts(new ArrayList<TicketDiscount>());
        }
        this.getDiscounts().add(ticketDiscount);
    }

    public Set<PosTransaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<PosTransaction> transactions) {
        this.transactions = transactions;
    }

    public void addTotransactions(PosTransaction posTransaction) {
        if (null == this.getTransactions()) {
            this.setTransactions(new TreeSet<PosTransaction>());
        }
        this.getTransactions().add(posTransaction);
    }

    public List<Integer> getTableNumbers() {
        return this.tableNumbers;
    }

    public void setTableNumbers(List<Integer> tableNumbers) {
        this.tableNumbers = tableNumbers;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Ticket)) {
            return false;
        }
        Ticket ticket = (Ticket)obj;
        if (null == this.getId() || null == ticket.getId()) {
            return false;
        }
        return this.getId().equals(ticket.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}


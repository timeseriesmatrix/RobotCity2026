/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.PrinterGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseKitchenTicket
implements Comparable,
Serializable {
    public static String REF = "KitchenTicket";
    public static String PROP_PRINTER_GROUP = "printerGroup";
    public static String PROP_STATUS = "status";
    public static String PROP_CLOSING_DATE = "closingDate";
    public static String PROP_TICKET_TYPE = "ticketType";
    public static String PROP_ID = "id";
    public static String PROP_VOIDED = "voided";
    public static String PROP_SERVER_NAME = "serverName";
    public static String PROP_SEQUENCE_NUMBER = "sequenceNumber";
    public static String PROP_CREATE_DATE = "createDate";
    public static String PROP_TICKET_ID = "ticketId";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer ticketId;
    protected Date createDate;
    protected Date closingDate;
    protected Boolean voided;
    protected Integer sequenceNumber;
    protected String status;
    protected String serverName;
    protected String ticketType;
    private PrinterGroup printerGroup;
    private List<Integer> tableNumbers;
    private List<KitchenTicketItem> ticketItems;

    public BaseKitchenTicket() {
        this.initialize();
    }

    public BaseKitchenTicket(Integer id) {
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

    public Integer getTicketId() {
        return this.ticketId == null ? Integer.valueOf(0) : this.ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
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

    public Boolean isVoided() {
        return this.voided == null ? Boolean.FALSE : this.voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Integer getSequenceNumber() {
        return this.sequenceNumber == null ? Integer.valueOf(0) : this.sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getTicketType() {
        return this.ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public PrinterGroup getPrinterGroup() {
        return this.printerGroup;
    }

    public void setPrinterGroup(PrinterGroup printerGroup) {
        this.printerGroup = printerGroup;
    }

    public List<Integer> getTableNumbers() {
        return this.tableNumbers;
    }

    public void setTableNumbers(List<Integer> tableNumbers) {
        this.tableNumbers = tableNumbers;
    }

    public List<KitchenTicketItem> getTicketItems() {
        return this.ticketItems;
    }

    public void setTicketItems(List<KitchenTicketItem> ticketItems) {
        this.ticketItems = ticketItems;
    }

    public void addToticketItems(KitchenTicketItem kitchenTicketItem) {
        if (null == this.getTicketItems()) {
            this.setTicketItems(new ArrayList<KitchenTicketItem>());
        }
        this.getTicketItems().add(kitchenTicketItem);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof KitchenTicket)) {
            return false;
        }
        KitchenTicket kitchenTicket = (KitchenTicket)obj;
        if (null == this.getId() || null == kitchenTicket.getId()) {
            return false;
        }
        return this.getId().equals(kitchenTicket.getId());
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


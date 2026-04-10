/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.PurchaseOrder;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseInventoryTransaction
implements Comparable,
Serializable {
    public static String REF = "InventoryTransaction";
    public static String PROP_INVENTORY_ITEM = "inventoryItem";
    public static String PROP_QUANTITY = "quantity";
    public static String PROP_TO_WAREHOUSE = "toWarehouse";
    public static String PROP_VENDOR = "vendor";
    public static String PROP_TRANSACTION_DATE = "transactionDate";
    public static String PROP_FROM_WAREHOUSE = "fromWarehouse";
    public static String PROP_ID = "id";
    public static String PROP_UNIT_PRICE = "unitPrice";
    public static String PROP_REMARK = "remark";
    public static String PROP_REFERENCE_NO = "referenceNo";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date transactionDate;
    protected Integer quantity;
    protected Double unitPrice;
    protected String remark;
    private PurchaseOrder referenceNo;
    private InventoryItem inventoryItem;
    private InventoryVendor vendor;
    private InventoryWarehouse fromWarehouse;
    private InventoryWarehouse toWarehouse;

    public BaseInventoryTransaction() {
        this.initialize();
    }

    public BaseInventoryTransaction(Integer id) {
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

    public Date getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getQuantity() {
        return this.quantity == null ? Integer.valueOf(0) : this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return this.unitPrice == null ? Double.valueOf(0.0) : this.unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public PurchaseOrder getReferenceNo() {
        return this.referenceNo;
    }

    public void setReferenceNo(PurchaseOrder referenceNo) {
        this.referenceNo = referenceNo;
    }

    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public InventoryVendor getVendor() {
        return this.vendor;
    }

    public void setVendor(InventoryVendor vendor) {
        this.vendor = vendor;
    }

    public InventoryWarehouse getFromWarehouse() {
        return this.fromWarehouse;
    }

    public void setFromWarehouse(InventoryWarehouse fromWarehouse) {
        this.fromWarehouse = fromWarehouse;
    }

    public InventoryWarehouse getToWarehouse() {
        return this.toWarehouse;
    }

    public void setToWarehouse(InventoryWarehouse toWarehouse) {
        this.toWarehouse = toWarehouse;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryTransaction)) {
            return false;
        }
        InventoryTransaction inventoryTransaction = (InventoryTransaction)obj;
        if (null == this.getId() || null == inventoryTransaction.getId()) {
            return false;
        }
        return this.getId().equals(inventoryTransaction.getId());
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


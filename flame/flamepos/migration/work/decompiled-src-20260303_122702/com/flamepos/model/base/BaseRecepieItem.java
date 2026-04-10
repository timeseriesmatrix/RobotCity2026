/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import java.io.Serializable;

public abstract class BaseRecepieItem
implements Comparable,
Serializable {
    public static String REF = "RecepieItem";
    public static String PROP_INVENTORY_ITEM = "inventoryItem";
    public static String PROP_PERCENTAGE = "percentage";
    public static String PROP_RECEPIE = "recepie";
    public static String PROP_ID = "id";
    public static String PROP_INVENTORY_DEDUCTABLE = "inventoryDeductable";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Double percentage;
    protected Boolean inventoryDeductable;
    private InventoryItem inventoryItem;
    private Recepie recepie;

    public BaseRecepieItem() {
        this.initialize();
    }

    public BaseRecepieItem(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseRecepieItem(Integer id, Recepie recepie) {
        this.setId(id);
        this.setRecepie(recepie);
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

    public Double getPercentage() {
        return this.percentage == null ? Double.valueOf(0.0) : this.percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Boolean isInventoryDeductable() {
        return this.inventoryDeductable == null ? Boolean.FALSE : this.inventoryDeductable;
    }

    public void setInventoryDeductable(Boolean inventoryDeductable) {
        this.inventoryDeductable = inventoryDeductable;
    }

    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Recepie getRecepie() {
        return this.recepie;
    }

    public void setRecepie(Recepie recepie) {
        this.recepie = recepie;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof RecepieItem)) {
            return false;
        }
        RecepieItem recepieItem = (RecepieItem)obj;
        if (null == this.getId() || null == recepieItem.getId()) {
            return false;
        }
        return this.getId().equals(recepieItem.getId());
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


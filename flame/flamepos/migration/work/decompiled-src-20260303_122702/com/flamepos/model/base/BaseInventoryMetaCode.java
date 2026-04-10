/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryMetaCode;
import java.io.Serializable;

public abstract class BaseInventoryMetaCode
implements Comparable,
Serializable {
    public static String REF = "InventoryMetaCode";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_CODE_NO = "codeNo";
    public static String PROP_TYPE = "type";
    public static String PROP_CODE_TEXT = "codeText";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String type;
    protected String codeText;
    protected Integer codeNo;
    protected String description;

    public BaseInventoryMetaCode() {
        this.initialize();
    }

    public BaseInventoryMetaCode(Integer id) {
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCodeText() {
        return this.codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public Integer getCodeNo() {
        return this.codeNo == null ? Integer.valueOf(0) : this.codeNo;
    }

    public void setCodeNo(Integer codeNo) {
        this.codeNo = codeNo;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryMetaCode)) {
            return false;
        }
        InventoryMetaCode inventoryMetaCode = (InventoryMetaCode)obj;
        if (null == this.getId() || null == inventoryMetaCode.getId()) {
            return false;
        }
        return this.getId().equals(inventoryMetaCode.getId());
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


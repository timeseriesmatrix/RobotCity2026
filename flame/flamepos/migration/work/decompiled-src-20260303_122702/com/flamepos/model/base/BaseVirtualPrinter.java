/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.VirtualPrinter;
import java.io.Serializable;
import java.util.List;

public abstract class BaseVirtualPrinter
implements Comparable,
Serializable {
    public static String REF = "VirtualPrinter";
    public static String PROP_TYPE = "type";
    public static String PROP_PRIORITY = "priority";
    public static String PROP_ENABLED = "enabled";
    public static String PROP_ID = "id";
    public static String PROP_NAME = "name";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Integer type;
    protected Integer priority;
    protected Boolean enabled;
    private List<String> orderTypeNames;

    public BaseVirtualPrinter() {
        this.initialize();
    }

    public BaseVirtualPrinter(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseVirtualPrinter(Integer id, String name) {
        this.setId(id);
        this.setName(name);
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return this.type == null ? Integer.valueOf(0) : this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPriority() {
        return this.priority == null ? Integer.valueOf(0) : this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean isEnabled() {
        return this.enabled == null ? Boolean.FALSE : this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getOrderTypeNames() {
        return this.orderTypeNames;
    }

    public void setOrderTypeNames(List<String> orderTypeNames) {
        this.orderTypeNames = orderTypeNames;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof VirtualPrinter)) {
            return false;
        }
        VirtualPrinter virtualPrinter = (VirtualPrinter)obj;
        if (null == this.getId() || null == virtualPrinter.getId()) {
            return false;
        }
        return this.getId().equals(virtualPrinter.getId());
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


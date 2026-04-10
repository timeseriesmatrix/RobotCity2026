/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import java.io.Serializable;

public abstract class BaseTerminal
implements Comparable,
Serializable {
    public static String REF = "Terminal";
    public static String PROP_NAME = "name";
    public static String PROP_ACTIVE = "active";
    public static String PROP_OPENING_BALANCE = "openingBalance";
    public static String PROP_TERMINAL_KEY = "terminalKey";
    public static String PROP_ASSIGNED_USER = "assignedUser";
    public static String PROP_HAS_CASH_DRAWER = "hasCashDrawer";
    public static String PROP_CURRENT_BALANCE = "currentBalance";
    public static String PROP_LOCATION = "location";
    public static String PROP_ID = "id";
    public static String PROP_FLOOR_ID = "floorId";
    public static String PROP_IN_USE = "inUse";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String terminalKey;
    protected Double openingBalance;
    protected Double currentBalance;
    protected Boolean hasCashDrawer;
    protected Boolean inUse;
    protected Boolean active;
    protected String location;
    protected Integer floorId;
    private User assignedUser;

    public BaseTerminal() {
        this.initialize();
    }

    public BaseTerminal(Integer id) {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerminalKey() {
        return this.terminalKey;
    }

    public void setTerminalKey(String terminalKey) {
        this.terminalKey = terminalKey;
    }

    public Double getOpeningBalance() {
        return this.openingBalance == null ? Double.valueOf(0.0) : this.openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getCurrentBalance() {
        return this.currentBalance == null ? Double.valueOf(0.0) : this.currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Boolean isHasCashDrawer() {
        return this.hasCashDrawer == null ? Boolean.FALSE : this.hasCashDrawer;
    }

    public void setHasCashDrawer(Boolean hasCashDrawer) {
        this.hasCashDrawer = hasCashDrawer;
    }

    public Boolean isInUse() {
        return this.inUse == null ? Boolean.FALSE : this.inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public Boolean isActive() {
        return this.active == null ? Boolean.FALSE : this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getFloorId() {
        return this.floorId == null ? Integer.valueOf(0) : this.floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public User getAssignedUser() {
        return this.assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Terminal)) {
            return false;
        }
        Terminal terminal = (Terminal)obj;
        if (null == this.getId() || null == terminal.getId()) {
            return false;
        }
        return this.getId().equals(terminal.getId());
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


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.GlobalConfig;
import java.io.Serializable;

public abstract class BaseGlobalConfig
implements Comparable,
Serializable {
    public static String REF = "GlobalConfig";
    public static String PROP_VALUE = "value";
    public static String PROP_KEY = "key";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String key;
    protected String value;

    public BaseGlobalConfig() {
        this.initialize();
    }

    public BaseGlobalConfig(Integer id) {
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

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof GlobalConfig)) {
            return false;
        }
        GlobalConfig globalConfig = (GlobalConfig)obj;
        if (null == this.getId() || null == globalConfig.getId()) {
            return false;
        }
        return this.getId().equals(globalConfig.getId());
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


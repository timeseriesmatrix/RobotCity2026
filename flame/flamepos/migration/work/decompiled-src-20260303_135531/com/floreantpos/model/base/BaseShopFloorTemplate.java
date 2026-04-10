/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopFloorTemplate;
import java.io.Serializable;
import java.util.Map;

public abstract class BaseShopFloorTemplate
implements Comparable,
Serializable {
    public static String REF = "ShopFloorTemplate";
    public static String PROP_NAME = "name";
    public static String PROP_MAIN = "main";
    public static String PROP_DEFAULT_FLOOR = "defaultFloor";
    public static String PROP_ID = "id";
    public static String PROP_FLOOR = "floor";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Boolean defaultFloor;
    protected Boolean main;
    private ShopFloor floor;
    private Map<String, String> properties;

    public BaseShopFloorTemplate() {
        this.initialize();
    }

    public BaseShopFloorTemplate(Integer id) {
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

    public Boolean isDefaultFloor() {
        return this.defaultFloor == null ? Boolean.FALSE : this.defaultFloor;
    }

    public void setDefaultFloor(Boolean defaultFloor) {
        this.defaultFloor = defaultFloor;
    }

    public Boolean isMain() {
        return this.main == null ? Boolean.FALSE : this.main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public ShopFloor getFloor() {
        return this.floor;
    }

    public void setFloor(ShopFloor floor) {
        this.floor = floor;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ShopFloorTemplate)) {
            return false;
        }
        ShopFloorTemplate shopFloorTemplate = (ShopFloorTemplate)obj;
        if (null == this.getId() || null == shopFloorTemplate.getId()) {
            return this == obj;
        }
        return this.getId().equals(shopFloorTemplate.getId());
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


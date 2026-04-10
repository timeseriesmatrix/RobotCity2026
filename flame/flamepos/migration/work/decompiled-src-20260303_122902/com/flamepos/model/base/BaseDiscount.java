/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseDiscount
implements Comparable,
Serializable {
    public static String REF = "Discount";
    public static String PROP_EXPIRY_DATE = "expiryDate";
    public static String PROP_ENABLED = "enabled";
    public static String PROP_MINIMUN_BUY = "minimunBuy";
    public static String PROP_MODIFIABLE = "modifiable";
    public static String PROP_NAME = "name";
    public static String PROP_APPLY_TO_ALL = "applyToAll";
    public static String PROP_MIXIMUM_OFF = "miximumOff";
    public static String PROP_AUTO_APPLY = "autoApply";
    public static String PROP_TYPE = "type";
    public static String PROP_QUALIFICATION_TYPE = "qualificationType";
    public static String PROP_NEVER_EXPIRE = "neverExpire";
    public static String PROP_BARCODE = "barcode";
    public static String PROP_VALUE = "value";
    public static String PROP_ID = "id";
    public static String PROP_UUID = "UUID";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Integer type;
    protected String barcode;
    protected Integer qualificationType;
    protected Boolean applyToAll;
    protected Integer minimunBuy;
    protected Integer miximumOff;
    protected Double value;
    protected Date expiryDate;
    protected Boolean enabled;
    protected Boolean autoApply;
    protected Boolean modifiable;
    protected Boolean neverExpire;
    protected String uUID;
    private List<MenuItem> menuItems;
    private List<MenuGroup> menuGroups;
    private List<MenuCategory> menuCategories;

    public BaseDiscount() {
        this.initialize();
    }

    public BaseDiscount(Integer id) {
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

    public Integer getType() {
        return this.type == null ? Integer.valueOf(0) : this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getQualificationType() {
        return this.qualificationType == null ? Integer.valueOf(0) : this.qualificationType;
    }

    public void setQualificationType(Integer qualificationType) {
        this.qualificationType = qualificationType;
    }

    public Boolean isApplyToAll() {
        return this.applyToAll == null ? Boolean.FALSE : this.applyToAll;
    }

    public void setApplyToAll(Boolean applyToAll) {
        this.applyToAll = applyToAll;
    }

    public Integer getMinimunBuy() {
        return this.minimunBuy == null ? Integer.valueOf(0) : this.minimunBuy;
    }

    public void setMinimunBuy(Integer minimunBuy) {
        this.minimunBuy = minimunBuy;
    }

    public Integer getMiximumOff() {
        return this.miximumOff == null ? Integer.valueOf(0) : this.miximumOff;
    }

    public void setMiximumOff(Integer miximumOff) {
        this.miximumOff = miximumOff;
    }

    public Double getValue() {
        return this.value == null ? Double.valueOf(0.0) : this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean isEnabled() {
        return this.enabled == null ? Boolean.FALSE : this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isAutoApply() {
        return this.autoApply == null ? Boolean.FALSE : this.autoApply;
    }

    public void setAutoApply(Boolean autoApply) {
        this.autoApply = autoApply;
    }

    public Boolean isModifiable() {
        return this.modifiable == null ? Boolean.FALSE : this.modifiable;
    }

    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }

    public Boolean isNeverExpire() {
        return this.neverExpire == null ? Boolean.FALSE : this.neverExpire;
    }

    public void setNeverExpire(Boolean neverExpire) {
        this.neverExpire = neverExpire;
    }

    public String getUUID() {
        return this.uUID;
    }

    public void setUUID(String uUID) {
        this.uUID = uUID;
    }

    public List<MenuItem> getMenuItems() {
        return this.menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addTomenuItems(MenuItem menuItem) {
        if (null == this.getMenuItems()) {
            this.setMenuItems(new ArrayList<MenuItem>());
        }
        this.getMenuItems().add(menuItem);
    }

    public List<MenuGroup> getMenuGroups() {
        return this.menuGroups;
    }

    public void setMenuGroups(List<MenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public void addTomenuGroups(MenuGroup menuGroup) {
        if (null == this.getMenuGroups()) {
            this.setMenuGroups(new ArrayList<MenuGroup>());
        }
        this.getMenuGroups().add(menuGroup);
    }

    public List<MenuCategory> getMenuCategories() {
        return this.menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public void addTomenuCategories(MenuCategory menuCategory) {
        if (null == this.getMenuCategories()) {
            this.setMenuCategories(new ArrayList<MenuCategory>());
        }
        this.getMenuCategories().add(menuCategory);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Discount)) {
            return false;
        }
        Discount discount = (Discount)obj;
        if (null == this.getId() || null == discount.getId()) {
            return false;
        }
        return this.getId().equals(discount.getId());
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


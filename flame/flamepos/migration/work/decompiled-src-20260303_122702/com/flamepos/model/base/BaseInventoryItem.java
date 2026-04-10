/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.PackagingUnit;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseInventoryItem
implements Comparable,
Serializable {
    public static String REF = "InventoryItem";
    public static String PROP_PACKAGE_BARCODE = "packageBarcode";
    public static String PROP_PACKAGING_UNIT = "packagingUnit";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_RECIPE_UNIT = "recipeUnit";
    public static String PROP_ITEM_VENDOR = "itemVendor";
    public static String PROP_ITEM_GROUP = "itemGroup";
    public static String PROP_VISIBLE = "visible";
    public static String PROP_AVERAGE_PACKAGE_PRICE = "averagePackagePrice";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_UNIT_BARCODE = "unitBarcode";
    public static String PROP_PACKAGE_REPLENISH_LEVEL = "packageReplenishLevel";
    public static String PROP_NAME = "name";
    public static String PROP_LAST_UPDATE_DATE = "lastUpdateDate";
    public static String PROP_TOTAL_PACKAGES = "totalPackages";
    public static String PROP_ITEM_LOCATION = "itemLocation";
    public static String PROP_CREATE_TIME = "createTime";
    public static String PROP_TOTAL_RECEPIE_UNITS = "totalRecepieUnits";
    public static String PROP_ID = "id";
    public static String PROP_UNIT_PER_PACKAGE = "unitPerPackage";
    public static String PROP_PACKAGE_REORDER_LEVEL = "packageReorderLevel";
    public static String PROP_UNIT_SELLING_PRICE = "unitSellingPrice";
    public static String PROP_UNIT_PURCHASE_PRICE = "unitPurchasePrice";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date createTime;
    protected Date lastUpdateDate;
    protected String name;
    protected String packageBarcode;
    protected String unitBarcode;
    protected Double unitPerPackage;
    protected Integer sortOrder;
    protected Integer packageReorderLevel;
    protected Integer packageReplenishLevel;
    protected String description;
    protected Double averagePackagePrice;
    protected Integer totalPackages;
    protected Double totalRecepieUnits;
    protected Double unitPurchasePrice;
    protected Double unitSellingPrice;
    protected Boolean visible;
    private PackagingUnit packagingUnit;
    private PackagingUnit recipeUnit;
    private InventoryGroup itemGroup;
    private InventoryLocation itemLocation;
    private InventoryVendor itemVendor;

    public BaseInventoryItem() {
        this.initialize();
    }

    public BaseInventoryItem(Integer id) {
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

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageBarcode() {
        return this.packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getUnitBarcode() {
        return this.unitBarcode;
    }

    public void setUnitBarcode(String unitBarcode) {
        this.unitBarcode = unitBarcode;
    }

    public Double getUnitPerPackage() {
        return this.unitPerPackage == null ? Double.valueOf(0.0) : this.unitPerPackage;
    }

    public void setUnitPerPackage(Double unitPerPackage) {
        this.unitPerPackage = unitPerPackage;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPackageReorderLevel() {
        return this.packageReorderLevel == null ? Integer.valueOf(0) : this.packageReorderLevel;
    }

    public void setPackageReorderLevel(Integer packageReorderLevel) {
        this.packageReorderLevel = packageReorderLevel;
    }

    public Integer getPackageReplenishLevel() {
        return this.packageReplenishLevel == null ? Integer.valueOf(0) : this.packageReplenishLevel;
    }

    public void setPackageReplenishLevel(Integer packageReplenishLevel) {
        this.packageReplenishLevel = packageReplenishLevel;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAveragePackagePrice() {
        return this.averagePackagePrice == null ? Double.valueOf(0.0) : this.averagePackagePrice;
    }

    public void setAveragePackagePrice(Double averagePackagePrice) {
        this.averagePackagePrice = averagePackagePrice;
    }

    public Integer getTotalPackages() {
        return this.totalPackages == null ? Integer.valueOf(0) : this.totalPackages;
    }

    public void setTotalPackages(Integer totalPackages) {
        this.totalPackages = totalPackages;
    }

    public Double getTotalRecepieUnits() {
        return this.totalRecepieUnits == null ? Double.valueOf(0.0) : this.totalRecepieUnits;
    }

    public void setTotalRecepieUnits(Double totalRecepieUnits) {
        this.totalRecepieUnits = totalRecepieUnits;
    }

    public Double getUnitPurchasePrice() {
        return this.unitPurchasePrice == null ? Double.valueOf(0.0) : this.unitPurchasePrice;
    }

    public void setUnitPurchasePrice(Double unitPurchasePrice) {
        this.unitPurchasePrice = unitPurchasePrice;
    }

    public Double getUnitSellingPrice() {
        return this.unitSellingPrice == null ? Double.valueOf(0.0) : this.unitSellingPrice;
    }

    public void setUnitSellingPrice(Double unitSellingPrice) {
        this.unitSellingPrice = unitSellingPrice;
    }

    public Boolean isVisible() {
        return this.visible == null ? Boolean.FALSE : this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public PackagingUnit getPackagingUnit() {
        return this.packagingUnit;
    }

    public void setPackagingUnit(PackagingUnit packagingUnit) {
        this.packagingUnit = packagingUnit;
    }

    public PackagingUnit getRecipeUnit() {
        return this.recipeUnit;
    }

    public void setRecipeUnit(PackagingUnit recipeUnit) {
        this.recipeUnit = recipeUnit;
    }

    public InventoryGroup getItemGroup() {
        return this.itemGroup;
    }

    public void setItemGroup(InventoryGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public InventoryLocation getItemLocation() {
        return this.itemLocation;
    }

    public void setItemLocation(InventoryLocation itemLocation) {
        this.itemLocation = itemLocation;
    }

    public InventoryVendor getItemVendor() {
        return this.itemVendor;
    }

    public void setItemVendor(InventoryVendor itemVendor) {
        this.itemVendor = itemVendor;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryItem)) {
            return false;
        }
        InventoryItem inventoryItem = (InventoryItem)obj;
        if (null == this.getId() || null == inventoryItem.getId()) {
            return this == obj;
        }
        return this.getId().equals(inventoryItem.getId());
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


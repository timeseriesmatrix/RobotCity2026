/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseInventoryItem;
import com.floreantpos.util.POSUtil;
import org.apache.commons.lang.StringUtils;

public class InventoryItem
extends BaseInventoryItem {
    private static final long serialVersionUID = 1L;

    public InventoryItem() {
    }

    public InventoryItem(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public static InventoryItem fromCSV(String csvLine) {
        if (StringUtils.isEmpty((String)csvLine)) {
            return null;
        }
        String[] strings = csvLine.split(",");
        InventoryItem inventoryItem = new InventoryItem();
        int index = 0;
        try {
            inventoryItem.setName(strings[index++]);
            inventoryItem.setUnitPerPackage(POSUtil.parseDouble(strings[index++]));
            inventoryItem.setTotalPackages(POSUtil.parseInteger(strings[index++]));
            inventoryItem.setAveragePackagePrice(POSUtil.parseDouble(strings[index++]));
            inventoryItem.setTotalRecepieUnits(POSUtil.parseDouble(strings[index++]));
            inventoryItem.setUnitPurchasePrice(POSUtil.parseDouble(strings[index++]));
            inventoryItem.setPackageBarcode(strings[index++]);
            inventoryItem.setUnitBarcode(strings[index++]);
            inventoryItem.setSortOrder(POSUtil.parseInteger(strings[index++]));
            inventoryItem.setPackageReorderLevel(POSUtil.parseInteger(strings[index++]));
            inventoryItem.setPackageReplenishLevel(POSUtil.parseInteger(strings[index++]));
            inventoryItem.setDescription(strings[index++]);
            inventoryItem.setUnitSellingPrice(POSUtil.parseDouble(strings[index++]));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return inventoryItem;
    }
}


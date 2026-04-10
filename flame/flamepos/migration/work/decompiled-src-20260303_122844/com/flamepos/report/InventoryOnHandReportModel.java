/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.InventoryItem;
import com.floreantpos.swing.ListTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class InventoryOnHandReportModel
extends ListTableModel {
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public InventoryOnHandReportModel() {
        super(new String[]{"itemgroup", "items", "barcode", "onHand", "cost", "onhandvalue"});
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryItem data = (InventoryItem)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return data.getItemGroup().getName();
            }
            case 1: {
                return data.getName();
            }
            case 2: {
                return data.getPackageBarcode();
            }
            case 3: {
                return String.valueOf(data.getTotalPackages());
            }
            case 4: {
                return String.valueOf(data.getUnitPurchasePrice());
            }
            case 5: {
                double totalOnHandValue = (double)data.getTotalPackages().intValue() * data.getAveragePackagePrice();
                return String.valueOf(totalOnHandValue);
            }
        }
        return null;
    }
}


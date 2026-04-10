/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.InventoryItem;
import com.floreantpos.swing.ListTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class PurchaseReportModel
extends ListTableModel {
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public PurchaseReportModel() {
        super(new String[]{"item", "description", "quantity", "price", "total"});
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryItem data = (InventoryItem)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(data.getName());
            }
            case 1: {
                return data.getDescription();
            }
            case 2: {
                return String.valueOf(data.getPackageReplenishLevel());
            }
            case 3: {
                return String.valueOf(data.getUnitPurchasePrice());
            }
            case 4: {
                String.valueOf(data.getAveragePackagePrice());
            }
        }
        return null;
    }
}


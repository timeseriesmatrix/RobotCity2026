/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.tableselection;

import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.model.OrderType;
import com.floreantpos.ui.tableselection.DefaultTableSelectionView;
import com.floreantpos.ui.tableselection.TableSelector;
import com.floreantpos.ui.tableselection.TableSelectorDialog;

public class TableSelectorFactory {
    private static TableSelector tableSelector;

    public static TableSelectorDialog createTableSelectorDialog(OrderType orderType) {
        FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);
        if (tableSelector == null) {
            tableSelector = floorLayoutPlugin == null ? new DefaultTableSelectionView() : floorLayoutPlugin.createTableSelector();
        }
        tableSelector.setOrderType(orderType);
        tableSelector.redererTables();
        return new TableSelectorDialog(tableSelector);
    }
}


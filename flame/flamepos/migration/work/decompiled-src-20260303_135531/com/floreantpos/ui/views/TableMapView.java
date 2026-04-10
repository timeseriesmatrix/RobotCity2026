/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.model.OrderType;
import com.floreantpos.ui.tableselection.DefaultTableSelectionView;
import com.floreantpos.ui.tableselection.TableSelector;
import com.floreantpos.ui.views.order.ViewPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.Locale;

public class TableMapView
extends ViewPanel {
    public static final String VIEW_NAME = "TABLE_MAP";
    private TableSelector tableSelector = null;
    private OrderType orderType;
    private static TableMapView instance;

    private TableMapView() {
        this.initComponents();
        this.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);
        this.tableSelector = floorLayoutPlugin == null ? new DefaultTableSelectionView() : floorLayoutPlugin.createTableSelector();
        this.tableSelector.setCreateNewTicket(true);
        this.tableSelector.updateView(false);
        this.add((Component)this.tableSelector, "Center");
    }

    public void updateView() {
        this.tableSelector.redererTables();
    }

    public static TableMapView getInstance() {
        if (instance == null) {
            instance = new TableMapView();
        }
        return instance;
    }

    public static TableMapView getInstance(OrderType orderType) {
        TableMapView instance2 = TableMapView.getInstance();
        instance2.tableSelector.setOrderType(orderType);
        instance2.orderType = orderType;
        return instance2;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }
}


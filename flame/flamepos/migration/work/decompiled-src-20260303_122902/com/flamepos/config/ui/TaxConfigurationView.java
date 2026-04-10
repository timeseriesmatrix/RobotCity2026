/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

public class TaxConfigurationView
extends ConfigurationView {
    public static final String CONFIG_TAB_TAX = Messages.getString("TaxConfigurationView.0");
    private Restaurant restaurant;
    private JCheckBox cbItemSalesPriceIncludesTax;

    public TaxConfigurationView() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[]", "[]"));
        this.cbItemSalesPriceIncludesTax = new JCheckBox(Messages.getString("TaxConfigurationView.4"));
        contentPanel.add((Component)this.cbItemSalesPriceIncludesTax, "cell 0 0");
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    @Override
    public boolean save() throws Exception {
        if (!this.isInitialized()) {
            return true;
        }
        this.restaurant.setItemPriceIncludesTax(this.cbItemSalesPriceIncludesTax.isSelected());
        RestaurantDAO.getInstance().saveOrUpdate(this.restaurant);
        Application.getInstance().refreshRestaurant();
        return true;
    }

    @Override
    public void initialize() throws Exception {
        this.restaurant = RestaurantDAO.getInstance().get(1);
        this.cbItemSalesPriceIncludesTax.setSelected(POSUtil.getBoolean(this.restaurant.isItemPriceIncludesTax()));
        this.setInitialized(true);
    }

    @Override
    public String getName() {
        return CONFIG_TAB_TAX;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.POSTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class RestaurantConfigurationView
extends ConfigurationView {
    private RestaurantDAO dao;
    private Restaurant restaurant;
    private FixedLengthTextField tfRestaurantName;
    private FixedLengthTextField tfAddressLine1;
    private FixedLengthTextField tfAddressLine2;
    private FixedLengthTextField tfAddressLine3;
    private POSTextField tfTelephone;
    private POSTextField tfServiceCharge;
    private POSTextField tfDefaultGratuity;
    private POSTextField tfTicketFooter;
    private JTextField tfZipCode;

    public RestaurantConfigurationView() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fillx", "[][grow][][grow]", "[][][][][][][][][][][][][][][][][]"));
        JLabel lblNewLabel = new JLabel(Messages.getString("RestaurantConfigurationView.3") + ":");
        contentPanel.add((Component)lblNewLabel, "cell 0 1,alignx trailing");
        this.tfRestaurantName = new FixedLengthTextField();
        this.tfRestaurantName.setLength(120);
        contentPanel.add((Component)this.tfRestaurantName, "cell 1 1 3 1,growx");
        JLabel lblAddressLine = new JLabel(Messages.getString("RestaurantConfigurationView.7") + ":");
        contentPanel.add((Component)lblAddressLine, "cell 0 2,alignx trailing");
        this.tfAddressLine1 = new FixedLengthTextField();
        this.tfAddressLine1.setLength(60);
        contentPanel.add((Component)this.tfAddressLine1, "cell 1 2 3 1,growx");
        JLabel lblAddressLine_1 = new JLabel(Messages.getString("RestaurantConfigurationView.11") + ":");
        contentPanel.add((Component)lblAddressLine_1, "cell 0 3,alignx trailing");
        this.tfAddressLine2 = new FixedLengthTextField();
        this.tfAddressLine2.setLength(60);
        contentPanel.add((Component)this.tfAddressLine2, "cell 1 3 3 1,growx");
        JLabel lblAddressLine_2 = new JLabel(Messages.getString("RestaurantConfigurationView.15") + ":");
        contentPanel.add((Component)lblAddressLine_2, "cell 0 4,alignx trailing");
        this.tfAddressLine3 = new FixedLengthTextField();
        this.tfAddressLine3.setLength(60);
        contentPanel.add((Component)this.tfAddressLine3, "cell 1 4 3 1,growx");
        JLabel lblZipCode = new JLabel(Messages.getString("RestaurantConfigurationView.19"));
        contentPanel.add((Component)lblZipCode, "cell 0 5,alignx trailing");
        this.tfZipCode = new JTextField();
        contentPanel.add((Component)this.tfZipCode, "cell 1 5,growx");
        this.tfZipCode.setColumns(10);
        JLabel lblPhone = new JLabel(Messages.getString("RestaurantConfigurationView.22"));
        contentPanel.add((Component)lblPhone, "cell 0 6,alignx trailing");
        this.tfTelephone = new POSTextField();
        contentPanel.add((Component)this.tfTelephone, "cell 1 6,growx");
        JLabel lblServiceCharge = new JLabel(Messages.getString("RestaurantConfigurationView.42") + ":");
        contentPanel.add((Component)lblServiceCharge, "cell 0 12,alignx trailing");
        this.tfServiceCharge = new POSTextField();
        contentPanel.add((Component)this.tfServiceCharge, "cell 1 12,growx");
        JLabel label = new JLabel("%");
        contentPanel.add((Component)label, "cell 2 12");
        JLabel lblDefaultGratuity = new JLabel(Messages.getString("RestaurantConfigurationView.48") + ":");
        contentPanel.add((Component)lblDefaultGratuity, "flowy,cell 0 13,alignx trailing");
        this.tfDefaultGratuity = new POSTextField();
        contentPanel.add((Component)this.tfDefaultGratuity, "cell 1 13,growx");
        JLabel label_1 = new JLabel("%");
        contentPanel.add((Component)label_1, "cell 2 13");
        JLabel lblTicketFooterMessage = new JLabel(Messages.getString("RestaurantConfigurationView.54") + ":");
        contentPanel.add((Component)lblTicketFooterMessage, "cell 0 14,alignx trailing");
        this.tfTicketFooter = new POSTextField();
        contentPanel.add((Component)this.tfTicketFooter, "cell 1 14 3 1,growx");
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    @Override
    public boolean save() throws Exception {
        if (!this.isInitialized()) {
            return true;
        }
        String name = null;
        String addr1 = null;
        String addr2 = null;
        String addr3 = null;
        String telephone = null;
        String currencyName = null;
        String currencySymbol = null;
        int capacity = 0;
        int tables = 0;
        double serviceCharge = 0.0;
        double gratuityPercentage = 0.0;
        name = this.tfRestaurantName.getText();
        addr1 = this.tfAddressLine1.getText();
        addr2 = this.tfAddressLine2.getText();
        addr3 = this.tfAddressLine3.getText();
        telephone = this.tfTelephone.getText();
        if (StringUtils.isEmpty(currencyName)) {
            currencyName = POSConstants.DOLLAR;
        }
        if (StringUtils.isEmpty(currencySymbol)) {
            currencySymbol = "$";
        }
        try {
            serviceCharge = Double.parseDouble(this.tfServiceCharge.getText());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            gratuityPercentage = Double.parseDouble(this.tfDefaultGratuity.getText());
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.restaurant.setName(name);
        this.restaurant.setAddressLine1(addr1);
        this.restaurant.setAddressLine2(addr2);
        this.restaurant.setAddressLine3(addr3);
        this.restaurant.setZipCode(this.tfZipCode.getText());
        this.restaurant.setTelephone(telephone);
        this.restaurant.setCapacity(capacity);
        this.restaurant.setTables(tables);
        this.restaurant.setCurrencyName(currencyName);
        this.restaurant.setCurrencySymbol(currencySymbol);
        this.restaurant.setServiceChargePercentage(serviceCharge);
        this.restaurant.setDefaultGratuityPercentage(gratuityPercentage);
        this.restaurant.setTicketFooterMessage(this.tfTicketFooter.getText());
        this.dao.saveOrUpdate(this.restaurant);
        Application.getInstance().refreshRestaurant();
        return true;
    }

    @Override
    public void initialize() throws Exception {
        this.dao = new RestaurantDAO();
        this.restaurant = this.dao.get(1);
        this.tfRestaurantName.setText(this.restaurant.getName());
        this.tfAddressLine1.setText(this.restaurant.getAddressLine1());
        this.tfAddressLine2.setText(this.restaurant.getAddressLine2());
        this.tfAddressLine3.setText(this.restaurant.getAddressLine3());
        this.tfZipCode.setText(this.restaurant.getZipCode());
        this.tfTelephone.setText(this.restaurant.getTelephone());
        this.tfServiceCharge.setText(String.valueOf(this.restaurant.getServiceChargePercentage()));
        this.tfDefaultGratuity.setText(String.valueOf(this.restaurant.getDefaultGratuityPercentage()));
        this.tfTicketFooter.setText(this.restaurant.getTicketFooterMessage());
        this.setInitialized(true);
    }

    @Override
    public String getName() {
        return POSConstants.CONFIG_TAB_RESTAURANT;
    }
}


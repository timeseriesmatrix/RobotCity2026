/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.cfg.Configuration
 */
package com.floreantpos.model.dao;

import com.floreantpos.Database;
import com.floreantpos.config.AppConfig;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.CashDrawerResetHistory;
import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.Currency;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.Customer;
import com.floreantpos.model.DataUpdateInfo;
import com.floreantpos.model.DeliveryAddress;
import com.floreantpos.model.DeliveryCharge;
import com.floreantpos.model.DeliveryConfiguration;
import com.floreantpos.model.DeliveryInstruction;
import com.floreantpos.model.Discount;
import com.floreantpos.model.DrawerAssignedHistory;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.GlobalConfig;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryMetaCode;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryUnit;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.PayoutReason;
import com.floreantpos.model.PayoutRecepient;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.PrinterConfiguration;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.PurchaseOrder;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Shift;
import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopFloorTemplate;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.ShopTableType;
import com.floreantpos.model.TableBookingInfo;
import com.floreantpos.model.Tax;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.VoidReason;
import com.floreantpos.model.ZipCodeVsDeliveryCharge;
import com.floreantpos.model.dao._BaseRootDAO;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public abstract class _RootDAO
extends _BaseRootDAO {
    public static void initialize(String configFileName, Configuration configuration) {
        _RootDAO.setSessionFactory(configuration.buildSessionFactory());
    }

    public static Configuration getNewConfiguration(String configFileName) {
        Configuration configuration = new Configuration();
        configuration.addClass(ActionHistory.class);
        configuration.addClass(AttendenceHistory.class);
        configuration.addClass(CashDrawerResetHistory.class);
        configuration.addClass(CookingInstruction.class);
        configuration.addClass(Discount.class);
        configuration.addClass(Gratuity.class);
        configuration.addClass(MenuCategory.class);
        configuration.addClass(MenuGroup.class);
        configuration.addClass(MenuItem.class);
        configuration.addClass(MenuItemModifierGroup.class);
        configuration.addClass(MenuItemShift.class);
        configuration.addClass(MenuModifier.class);
        configuration.addClass(MenuModifierGroup.class);
        configuration.addClass(PayoutReason.class);
        configuration.addClass(PayoutRecepient.class);
        configuration.addClass(Restaurant.class);
        configuration.addClass(Shift.class);
        configuration.addClass(Tax.class);
        configuration.addClass(Terminal.class);
        configuration.addClass(Ticket.class);
        configuration.addClass(KitchenTicket.class);
        configuration.addClass(TicketDiscount.class);
        configuration.addClass(TicketItem.class);
        configuration.addClass(TicketItemModifier.class);
        configuration.addClass(TicketItemDiscount.class);
        configuration.addClass(KitchenTicketItem.class);
        configuration.addClass(PosTransaction.class);
        configuration.addClass(User.class);
        configuration.addClass(VirtualPrinter.class);
        configuration.addClass(TerminalPrinters.class);
        configuration.addClass(VoidReason.class);
        configuration.addClass(DrawerPullReport.class);
        configuration.addClass(PrinterConfiguration.class);
        configuration.addClass(UserPermission.class);
        configuration.addClass(UserType.class);
        configuration.addClass(Customer.class);
        configuration.addClass(PurchaseOrder.class);
        configuration.addClass(ZipCodeVsDeliveryCharge.class);
        configuration.addClass(ShopFloor.class);
        configuration.addClass(ShopFloorTemplate.class);
        configuration.addClass(ShopTable.class);
        configuration.addClass(ShopTableType.class);
        configuration.addClass(PrinterGroup.class);
        configuration.addClass(DrawerAssignedHistory.class);
        configuration.addClass(DataUpdateInfo.class);
        configuration.addClass(TableBookingInfo.class);
        configuration.addClass(CustomPayment.class);
        configuration.addClass(OrderType.class);
        configuration.addClass(DeliveryAddress.class);
        configuration.addClass(DeliveryInstruction.class);
        configuration.addClass(DeliveryCharge.class);
        configuration.addClass(DeliveryConfiguration.class);
        configuration.addClass(EmployeeInOutHistory.class);
        configuration.addClass(Currency.class);
        configuration.addClass(CashDrawer.class);
        configuration.addClass(CurrencyBalance.class);
        configuration.addClass(GlobalConfig.class);
        configuration.addClass(MenuItemSize.class);
        configuration.addClass(PizzaCrust.class);
        configuration.addClass(PizzaPrice.class);
        configuration.addClass(PizzaModifierPrice.class);
        configuration.addClass(Multiplier.class);
        configuration.addClass(ModifierMultiplierPrice.class);
        _RootDAO.configureInventoryClasses(configuration);
        Database defaultDatabase = AppConfig.getDefaultDatabase();
        configuration.setProperty("hibernate.dialect", defaultDatabase.getHibernateDialect());
        configuration.setProperty("hibernate.connection.driver_class", defaultDatabase.getHibernateConnectionDriverClass());
        configuration.setProperty("hibernate.connection.url", AppConfig.getConnectString());
        configuration.setProperty("hibernate.connection.username", AppConfig.getDatabaseUser());
        configuration.setProperty("hibernate.connection.password", AppConfig.getDatabasePassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.connection.autocommit", "false");
        configuration.setProperty("hibernate.max_fetch_depth", "3");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.connection.isolation", String.valueOf(2));
        _RootDAO.configureC3p0ConnectionPool(configuration);
        return configuration;
    }

    private static void configureC3p0ConnectionPool(Configuration configuration) {
        configuration.setProperty("hibernate.c3p0.min_size", "0");
        configuration.setProperty("hibernate.c3p0.max_size", "5");
        configuration.setProperty("hibernate.c3p0.timeout", "300");
        configuration.setProperty("hibernate.c3p0.max_statements", "50");
        configuration.setProperty("hibernate.c3p0.checkoutTimeout", "10000");
        configuration.setProperty("hibernate.c3p0.acquireRetryAttempts", "1");
        configuration.setProperty("hibernate.c3p0.acquireIncrement", "1");
        configuration.setProperty("hibernate.c3p0.maxIdleTime", "3000");
        configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");
        configuration.setProperty("hibernate.c3p0.breakAfterAcquireFailure", "false");
    }

    private static Configuration configureInventoryClasses(Configuration configuration) {
        configuration.addClass(InventoryGroup.class);
        configuration.addClass(InventoryItem.class);
        configuration.addClass(InventoryLocation.class);
        configuration.addClass(InventoryMetaCode.class);
        configuration.addClass(InventoryTransaction.class);
        configuration.addClass(InventoryUnit.class);
        configuration.addClass(InventoryVendor.class);
        configuration.addClass(InventoryWarehouse.class);
        configuration.addClass(Recepie.class);
        configuration.addClass(RecepieItem.class);
        configuration.addClass(PackagingUnit.class);
        return configuration;
    }

    public static Configuration reInitialize() {
        Configuration configuration = _RootDAO.getNewConfiguration(null);
        _RootDAO.setSessionFactory(configuration.buildSessionFactory());
        return configuration;
    }

    public void refresh(Object obj) {
        Session session = this.createNewSession();
        super.refresh(obj, session);
        session.close();
    }
}


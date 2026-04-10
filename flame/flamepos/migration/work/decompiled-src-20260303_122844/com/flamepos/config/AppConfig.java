/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.configuration.PropertiesConfiguration
 */
package com.floreantpos.config;

import com.floreantpos.Database;
import com.floreantpos.PosLog;
import java.io.File;
import org.apache.commons.configuration.PropertiesConfiguration;

public class AppConfig {
    public static final String DATABASE_URL = "database_url";
    public static final String DATABASE_PORT = "database_port";
    public static final String DATABASE_NAME = "database_name";
    public static final String DATABASE_USER = "database_user";
    public static final String DATABASE_PASSWORD = "database_pass";
    public static final String CONNECTION_STRING = "connection_string";
    public static final String DATABASE_PROVIDER_NAME = "database_provider_name";
    private static final String KITCHEN_PRINT_ON_ORDER_SETTLE = "kitchen_print_on_order_settle";
    private static final String KITCHEN_PRINT_ON_ORDER_FINISH = "kitchen_print_on_order_finish";
    private static final String PRINT_RECEIPT_ON_ORDER_SETTLE = "print_receipt_on_order_settle";
    private static final String PRINT_RECEIPT_ON_ORDER_FINISH = "print_receipt_on_order_finish";
    private static PropertiesConfiguration config;

    public static PropertiesConfiguration getConfig() {
        return config;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return config.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return config.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        config.setProperty(key, (Object)value);
    }

    public static String getString(String key) {
        return config.getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public static void put(String key, boolean value) {
        config.setProperty(key, (Object)value);
    }

    public static void put(String key, String value) {
        config.setProperty(key, (Object)value);
    }

    public static String getDatabaseHost() {
        return config.getString(DATABASE_URL, "localhost");
    }

    public static void setDatabaseHost(String url) {
        config.setProperty(DATABASE_URL, (Object)url);
    }

    public static String getConnectString() {
        return config.getString(CONNECTION_STRING, Database.DERBY_SINGLE.getConnectString("", "", ""));
    }

    public static void setConnectString(String connectionString) {
        config.setProperty(CONNECTION_STRING, (Object)connectionString);
    }

    public static String getDatabasePort() {
        return config.getString(DATABASE_PORT, null);
    }

    public static void setDatabasePort(String port) {
        config.setProperty(DATABASE_PORT, (Object)port);
    }

    public static String getDatabaseName() {
        return config.getString(DATABASE_NAME, "posdb");
    }

    public static void setDatabaseName(String name) {
        config.setProperty(DATABASE_NAME, (Object)name);
    }

    public static String getDatabaseUser() {
        return config.getString(DATABASE_USER, "app");
    }

    public static void setDatabaseUser(String user) {
        config.setProperty(DATABASE_USER, (Object)user);
    }

    public static String getDatabasePassword() {
        return config.getString(DATABASE_PASSWORD, "sa");
    }

    public static void setDatabasePassword(String password) {
        config.setProperty(DATABASE_PASSWORD, (Object)password);
    }

    public static void setDatabaseProviderName(String databaseProviderName) {
        config.setProperty(DATABASE_PROVIDER_NAME, (Object)databaseProviderName);
    }

    public static String getDatabaseProviderName() {
        return config.getString(DATABASE_PROVIDER_NAME, Database.DERBY_SINGLE.getProviderName());
    }

    public static Database getDefaultDatabase() {
        return Database.getByProviderName(AppConfig.getDatabaseProviderName());
    }

    public static boolean isPrintReceiptOnOrderFinish() {
        return AppConfig.getBoolean(PRINT_RECEIPT_ON_ORDER_FINISH, false);
    }

    public static void setPrintReceiptOnOrderFinish(boolean print) {
        config.setProperty(PRINT_RECEIPT_ON_ORDER_FINISH, (Object)print);
    }

    public static boolean isPrintReceiptOnOrderSettle() {
        return AppConfig.getBoolean(PRINT_RECEIPT_ON_ORDER_SETTLE, false);
    }

    public static void setPrintReceiptOnOrderSettle(boolean print) {
        config.setProperty(PRINT_RECEIPT_ON_ORDER_SETTLE, (Object)print);
    }

    public static boolean isPrintToKitchenOnOrderFinish() {
        return AppConfig.getBoolean(KITCHEN_PRINT_ON_ORDER_FINISH, false);
    }

    public static void setPrintToKitchenOnOrderFinish(boolean print) {
        config.setProperty(KITCHEN_PRINT_ON_ORDER_FINISH, (Object)print);
    }

    public static boolean isPrintToKitchenOnOrderSettle() {
        return AppConfig.getBoolean(KITCHEN_PRINT_ON_ORDER_SETTLE, false);
    }

    public static void setPrintToKitchenOnOrderSettle(boolean print) {
        config.setProperty(KITCHEN_PRINT_ON_ORDER_SETTLE, (Object)print);
    }

    static {
        try {
            File configFile = new File("floreantpos.config.properties");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            config = new PropertiesConfiguration(configFile);
            config.setAutoSave(true);
        }
        catch (Exception e) {
            PosLog.error(AppConfig.class, e.getMessage());
        }
    }
}


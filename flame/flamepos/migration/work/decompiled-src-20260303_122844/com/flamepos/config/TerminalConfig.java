/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.configuration.PropertiesConfiguration
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config;

import com.floreantpos.config.AppConfig;
import com.floreantpos.model.PaymentStatusFilter;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.util.PasswordHasher;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

public class TerminalConfig {
    private static final String ALLOW_TO_DELETE_PRINTED_TICKET_ITEM = "AllowToDeletePrintedTicketItem";
    private static final String USE_SETTLEMENT_PROMPT = "UseSettlementPrompt";
    private static final String SHOW_GUEST_SELECTION = "show_guest_selection";
    private static final String ORDER_TYPE_FILTER = "order_type_filter";
    private static final String PS_FILTER = "ps_filter";
    private static final String SHOW_TABLE_SELECTION = "show_table_selection";
    private static final String REGULAR_MODE = "regular_mode";
    private static final String KITCHEN_MODE = "kitchen_mode";
    private static final String CASHIER_MODE = "cashier_mode";
    private static final String SHOW_DB_CONFIGURATION = "show_db_configuration";
    private static final String UI_DEFAULT_FONT = "ui_default_font";
    private static final String AUTO_LOGOFF_TIME = "AUTO_LOGOFF_TIME";
    private static final String AUTO_LOGOFF_ENABLE = "AUTO_LOGOFF_ENABLE";
    private static final String DEFAULT_PASS_LEN = "DEFAULT_PASS_LEN";
    private static final String TOUCH_FONT_SIZE = "TOUCH_FONT_SIZE";
    private static final String SCREEN_COMPONENT_SIZE_RATIO = "SCREEN_COMPONENT_SIZE_RATIO";
    private static final String TOUCH_BUTTON_HEIGHT = "TOUCH_BUTTON_HEIGHT";
    private static final String FLOOR_BUTTON_WIDTH = "FLOOR_BUTTON_WIDTH";
    private static final String FLOOR_BUTTON_HEIGHT = "FLOOR_BUTTON_HEIGHT";
    private static final String FLOOR_BUTTON_FONT_SIZE = "FLOOR_BUTTON_FONT_SIZE";
    private static final String ADMIN_PASSWORD = "admin_pass";
    private static final String SHOW_BARCODE_ON_RECEIPT = "show_barcode_on_receipt";
    private static final String GROUP_KITCHEN_ITEMS_ON_RECEIPT = "group_kitchen_items_on_receipt";
    private static final String ENABLE_MULTI_CURRENCY = "enable_multi_currency";
    private static final String DEFAULT_VIEW = "default_view";
    private static final String ACTIVE_CUSTOMER_DISPLAY = "active_customer_display";
    private static final String ACTIVE_SCALE_DISPLAY = "active_scale_display";
    private static final String ACTIVE_CALLER_ID_DEVICE = "active_caller_id_device";
    private static final String CALLER_ID_DEVICE = "caller_id_device";
    static final String TERMINAL_ID = "terminal_id";
    static final String FULLSCREEN_MODE = "fullscreen_mode";
    private static final String DEFAULT_LOCALE = "defaultLocal";
    private static final String ALLOW_QUICK_MAINTENANCE = "quick_maintenance";
    private static boolean multiple_order_supported = true;
    private static final String Kitchen_Display_Button = "kitchendisplay";
    private static PropertiesConfiguration config = AppConfig.getConfig();

    public static int getTerminalId() {
        return config.getInt(TERMINAL_ID, -1);
    }

    public static void setTerminalId(int id) {
        config.setProperty(TERMINAL_ID, (Object)id);
    }

    public static boolean isFullscreenMode() {
        return config.getBoolean(FULLSCREEN_MODE, false);
    }

    public static void setFullscreenMode(boolean fullscreen) {
        config.setProperty(FULLSCREEN_MODE, (Object)fullscreen);
    }

    public static boolean isShowKitchenBtnOnLoginScreen() {
        return config.getBoolean(Kitchen_Display_Button, true);
    }

    public static void setShowKitchenBtnOnLoginScreen(boolean kitchenBtn) {
        config.setProperty(Kitchen_Display_Button, (Object)kitchenBtn);
    }

    public static String getAdminPassword() {
        return config.getString(ADMIN_PASSWORD, PasswordHasher.hashPassword("1111"));
    }

    public static void setAdminPassword(String password) {
        config.setProperty(ADMIN_PASSWORD, (Object)PasswordHasher.hashPassword(password));
    }

    public static boolean matchAdminPassword(String password) {
        return TerminalConfig.getAdminPassword().equals(PasswordHasher.hashPassword(password));
    }

    public static void setTouchScreenButtonHeight(int height) {
        config.setProperty(TOUCH_BUTTON_HEIGHT, (Object)height);
    }

    public static int getTouchScreenButtonHeight() {
        return config.getInt(TOUCH_BUTTON_HEIGHT, 80);
    }

    public static void setFloorButtonWidth(int width) {
        config.setProperty(FLOOR_BUTTON_WIDTH, (Object)width);
    }

    public static int getFloorButtonWidth() {
        return config.getInt(FLOOR_BUTTON_WIDTH, 55);
    }

    public static void setFloorButtonHeight(int height) {
        config.setProperty(FLOOR_BUTTON_HEIGHT, (Object)height);
    }

    public static int getFloorButtonHeight() {
        return config.getInt(FLOOR_BUTTON_HEIGHT, 90);
    }

    public static void setFloorButtonFontSize(int size) {
        config.setProperty(FLOOR_BUTTON_FONT_SIZE, (Object)size);
    }

    public static int getFloorButtonFontSize() {
        return config.getInt(FLOOR_BUTTON_FONT_SIZE, 30);
    }

    public static void setMenuItemButtonHeight(int height) {
        config.setProperty("menu_button_height", (Object)height);
    }

    public static int getMenuItemButtonHeight() {
        return config.getInt("menu_button_height", 80);
    }

    public static void setMenuItemButtonWidth(int width) {
        config.setProperty("menu_button_width", (Object)width);
    }

    public static int getMenuItemButtonWidth() {
        return config.getInt("menu_button_width", 80);
    }

    public static void setTouchScreenFontSize(int size) {
        config.setProperty(TOUCH_FONT_SIZE, (Object)size);
    }

    public static int getTouchScreenFontSize() {
        return config.getInt(TOUCH_FONT_SIZE, 12);
    }

    public static void setScreenScaleFactor(double size) {
        config.setProperty(SCREEN_COMPONENT_SIZE_RATIO, (Object)size);
    }

    public static double getScreenScaleFactor() {
        return config.getDouble(SCREEN_COMPONENT_SIZE_RATIO, 1.0);
    }

    public static void setDefaultPassLen(int defaultPassLen) {
        config.setProperty(DEFAULT_PASS_LEN, (Object)defaultPassLen);
    }

    public static int getDefaultPassLen() {
        return config.getInt(DEFAULT_PASS_LEN, 4);
    }

    public static boolean isAutoLogoffEnable() {
        return config.getBoolean(AUTO_LOGOFF_ENABLE, true);
    }

    public static void setAutoLogoffEnable(boolean enable) {
        config.setProperty(AUTO_LOGOFF_ENABLE, (Object)enable);
    }

    public static void setAutoLogoffTime(int time) {
        config.setProperty(AUTO_LOGOFF_TIME, (Object)time);
    }

    public static int getAutoLogoffTime() {
        return config.getInt(AUTO_LOGOFF_TIME, 60);
    }

    public static String getUiDefaultFont() {
        return config.getString(UI_DEFAULT_FONT);
    }

    public static void setUiDefaultFont(String fontName) {
        config.setProperty(UI_DEFAULT_FONT, (Object)fontName);
    }

    public static String getDefaultView() {
        return config.getString(DEFAULT_VIEW, SwitchboardView.VIEW_NAME);
    }

    public static void setDefaultView(String viewName) {
        config.setProperty(DEFAULT_VIEW, (Object)viewName);
    }

    public static void setShowDbConfigureButton(boolean show) {
        config.setProperty(SHOW_DB_CONFIGURATION, (Object)show);
    }

    public static boolean isShowDbConfigureButton() {
        return config.getBoolean(SHOW_DB_CONFIGURATION, true);
    }

    public static void setShowBarcodeOnReceipt(boolean show) {
        config.setProperty(SHOW_BARCODE_ON_RECEIPT, (Object)show);
    }

    public static boolean isShowBarcodeOnReceipt() {
        return config.getBoolean(SHOW_BARCODE_ON_RECEIPT, false);
    }

    public static void setEnabledCallerIdDevice(boolean show) {
        config.setProperty(ACTIVE_CALLER_ID_DEVICE, (Object)show);
    }

    public static boolean isEanbledCallerIdDevice() {
        return config.getBoolean(ACTIVE_CALLER_ID_DEVICE, false);
    }

    public static void setGroupKitchenReceiptItems(boolean group) {
        config.setProperty(GROUP_KITCHEN_ITEMS_ON_RECEIPT, (Object)group);
    }

    public static boolean isGroupKitchenReceiptItems() {
        return config.getBoolean(GROUP_KITCHEN_ITEMS_ON_RECEIPT, false);
    }

    public static boolean isEnabledMultiCurrency() {
        return config.getBoolean(ENABLE_MULTI_CURRENCY, false);
    }

    public static void setEnabledMultiCurrency(boolean enable) {
        config.setProperty(ENABLE_MULTI_CURRENCY, (Object)enable);
    }

    public static boolean isMultipleOrderSupported() {
        return multiple_order_supported;
    }

    public static void setCustomerDisplay(boolean show) {
        config.setProperty(ACTIVE_CUSTOMER_DISPLAY, (Object)show);
    }

    public static boolean isActiveCustomerDisplay() {
        return config.getBoolean(ACTIVE_CUSTOMER_DISPLAY, false);
    }

    public static void setScaleDisplay(boolean show) {
        config.setProperty(ACTIVE_SCALE_DISPLAY, (Object)show);
    }

    public static boolean isActiveScaleDisplay() {
        return config.getBoolean(ACTIVE_SCALE_DISPLAY, false);
    }

    public static boolean isCashierMode() {
        return false;
    }

    public static void setCashierMode(boolean cashierMode) {
        config.setProperty(CASHIER_MODE, (Object)cashierMode);
    }

    public static boolean isRegularMode() {
        return config.getBoolean(REGULAR_MODE, false);
    }

    public static void setRegularMode(boolean regularMode) {
        config.setProperty(REGULAR_MODE, (Object)regularMode);
    }

    public static boolean isKitchenMode() {
        return config.getBoolean(KITCHEN_MODE, false);
    }

    public static void setKitchenMode(boolean kitchenMode) {
        config.setProperty(KITCHEN_MODE, (Object)kitchenMode);
    }

    public static boolean isUseTranslatedName() {
        return config.getBoolean("use_translated_name", false);
    }

    public static void setUseTranslatedName(boolean useTranslatedName) {
        config.setProperty("use_translated_name", (Object)useTranslatedName);
    }

    public static String getOrderTypeFilter() {
        return config.getString(ORDER_TYPE_FILTER, "ALL");
    }

    public static void setOrderTypeFilter(String filter) {
        config.setProperty(ORDER_TYPE_FILTER, (Object)filter);
    }

    public static String getCallerIdDevice() {
        return config.getString(CALLER_ID_DEVICE, "NONE");
    }

    public static void setCallerIdDevice(String device) {
        config.setProperty(CALLER_ID_DEVICE, (Object)device);
    }

    public static PaymentStatusFilter getPaymentStatusFilter() {
        return PaymentStatusFilter.fromString(config.getString(PS_FILTER));
    }

    public static void setPaymentStatusFilter(String filter) {
        config.setProperty(PS_FILTER, (Object)filter);
    }

    public static void setShouldShowTableSelection(boolean showTableSelection) {
        config.setProperty(SHOW_TABLE_SELECTION, (Object)showTableSelection);
    }

    public static boolean isShouldShowTableSelection() {
        return config.getBoolean(SHOW_TABLE_SELECTION, Boolean.TRUE);
    }

    public static void setShouldShowGuestSelection(boolean showGuestSelection) {
        config.setProperty(SHOW_GUEST_SELECTION, (Object)showGuestSelection);
    }

    public static boolean isShouldShowGuestSelection() {
        return config.getBoolean(SHOW_GUEST_SELECTION, Boolean.TRUE);
    }

    public static void setUseSettlementPrompt(boolean settlementPrompt) {
        config.setProperty(USE_SETTLEMENT_PROMPT, (Object)settlementPrompt);
    }

    public static boolean isUseSettlementPrompt() {
        return config.getBoolean(USE_SETTLEMENT_PROMPT, Boolean.FALSE);
    }

    public static void setMiscItemDefaultTaxId(int id) {
        config.setProperty("mistitemdefaulttaxid", (Object)id);
    }

    public static int getMiscItemDefaultTaxId() {
        return config.getInt("mistitemdefaulttaxid", -1);
    }

    public static void setDrawerPortName(String drawerPortName) {
        config.setProperty("drawerPortName", (Object)drawerPortName);
    }

    public static String getDrawerPortName() {
        return config.getString("drawerPortName", "COM1");
    }

    public static void setCustomerDisplayPort(String customerDisplayPort) {
        config.setProperty("customerDisplayPort", (Object)customerDisplayPort);
    }

    public static String getCustomerDisplayPort() {
        return config.getString("customerDisplayPort", "COM2");
    }

    public static void setCustomerDisplayMessage(String customerDisplayMessage) {
        config.setProperty("customerDisplayMessage", (Object)customerDisplayMessage);
    }

    public static String getCustomerDisplayMessage() {
        return config.getString("customerDisplayMessage", "12345678912345678912");
    }

    public static String getScaleActivationValue() {
        return config.getString("wd", "");
    }

    public static void setScalePort(String scalePort) {
        config.setProperty("scaleDisplayPort", (Object)scalePort);
    }

    public static String getScalePort() {
        return config.getString("scaleDisplayPort", "COM3");
    }

    public static void setScaleDisplayMessage(String scaleDisplayMessage) {
        config.setProperty("scaleDisplayMessage", (Object)scaleDisplayMessage);
    }

    public static String getScaleDisplayMessage() {
        return config.getString("scaleDisplayMessage", "1234");
    }

    public static void setDrawerControlCodes(String controlCode) {
        config.setProperty("controlCode", (Object)controlCode);
    }

    public static String getDrawerControlCodes() {
        return config.getString("controlCode", "27,112,0,25,250");
    }

    public static String getDefaultDrawerControlCodes() {
        return "27,112,0,25,250";
    }

    public static String getDrawerPullReportHiddenColumns() {
        return config.getString("drawerPullReportColumns", "");
    }

    public static void setDrawerPullReportHiddenColumns(String value) {
        config.setProperty("drawerPullReportColumns", (Object)value);
    }

    public static String getTicketListViewHiddenColumns() {
        return config.getString("listViewColumns", "");
    }

    public static void setTicketListViewHiddenColumns(String value) {
        config.setProperty("listViewColumns", (Object)value);
    }

    public static char[] getDrawerControlCodesArray() {
        String drawerControlCodes = TerminalConfig.getDefaultDrawerControlCodes();
        if (StringUtils.isEmpty((String)drawerControlCodes)) {
            drawerControlCodes = TerminalConfig.getDefaultDrawerControlCodes();
        }
        String[] split = drawerControlCodes.split(",");
        char[] codes = new char[split.length];
        for (int i = 0; i < split.length; ++i) {
            try {
                codes[i] = (char)Integer.parseInt(split[i]);
                continue;
            }
            catch (Exception x) {
                codes[i] = 48;
            }
        }
        return codes;
    }

    public static void setCheckUpdateStatus(String status) {
        config.setProperty("update_check", (Object)status);
    }

    public static String getCheckUpdateStatus() {
        return config.getString("update_check", "Daily");
    }

    public static String getWebServiceUrl() {
        return config.getString("web_service_url", "http://team.orocube.net:8080");
    }

    public static String getPosDownloadUrl() {
        return config.getString("pos_url", "http://floreant.org/");
    }

    public static void setDefaultLocale(String defaultLocal) {
        config.setProperty(DEFAULT_LOCALE, (Object)defaultLocal);
    }

    public static Locale getDefaultLocale() {
        String defaultLocaleString = config.getString(DEFAULT_LOCALE, null);
        if (StringUtils.isEmpty((String)defaultLocaleString)) {
            return null;
        }
        String language = "";
        String country = "";
        String variant = "";
        StringTokenizer st = new StringTokenizer(defaultLocaleString, "_");
        if (st.hasMoreTokens()) {
            language = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            country = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            variant = st.nextToken();
        }
        Locale disName = new Locale(language, country, variant);
        return disName;
    }

    public static void setAllowToDeletePrintedTicketItem(boolean allow) {
        config.setProperty(ALLOW_TO_DELETE_PRINTED_TICKET_ITEM, (Object)allow);
    }

    public static boolean isAllowedToDeletePrintedTicketItem() {
        return config.getBoolean(ALLOW_TO_DELETE_PRINTED_TICKET_ITEM, true);
    }

    public static void setAllowQuickMaintenance(boolean selected) {
        config.setProperty(ALLOW_QUICK_MAINTENANCE, (Object)selected);
    }

    public static boolean isAllowedQuickMaintenance() {
        return config.getBoolean(ALLOW_QUICK_MAINTENANCE, true);
    }
}


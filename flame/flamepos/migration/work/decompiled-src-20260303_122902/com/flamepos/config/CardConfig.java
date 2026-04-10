/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config;

import com.floreantpos.PosLog;
import com.floreantpos.config.AppConfig;
import com.floreantpos.extension.AuthorizeNetGatewayPlugin;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.model.CardReader;
import com.floreantpos.util.AESencrp;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class CardConfig {
    private static final String MERCHANT_PASS = "MerchantPass";
    private static final String MERCHANT_ACCOUNT = "MerchantAccount";
    private static final String CARD_READER = "CARD_READER";

    public static boolean isSwipeCardSupported() {
        return AppConfig.getBoolean("support-swipe-card", true);
    }

    public static void setSwipeCardSupported(boolean b) {
        AppConfig.put("support-swipe-card", b);
    }

    public static boolean isManualEntrySupported() {
        return AppConfig.getBoolean("support-card-manual-entry", true);
    }

    public static void setManualEntrySupported(boolean b) {
        AppConfig.put("support-card-manual-entry", b);
    }

    public static boolean isExtTerminalSupported() {
        return AppConfig.getBoolean("support-ext-terminal", true);
    }

    public static void setExtTerminalSupported(boolean b) {
        AppConfig.put("support-ext-terminal", b);
    }

    public static void setCardReader(CardReader card) {
        if (card == null) {
            AppConfig.put(CARD_READER, "");
            return;
        }
        AppConfig.put(CARD_READER, card.name());
    }

    public static CardReader getCardReader() {
        String string = AppConfig.getString(CARD_READER, "SWIPE");
        return CardReader.fromString(string);
    }

    public static void setMerchantAccount(String account) {
        AppConfig.put(MERCHANT_ACCOUNT, account);
    }

    public static String getMerchantAccount() {
        return AppConfig.getString(MERCHANT_ACCOUNT, null);
    }

    public static void setMerchantPass(String pass) {
        try {
            if (StringUtils.isEmpty((String)pass)) {
                AppConfig.put(MERCHANT_PASS, "");
                return;
            }
            AppConfig.put(MERCHANT_PASS, AESencrp.encrypt(pass));
        }
        catch (Exception e) {
            PosLog.error(CardConfig.class, e.getMessage());
        }
    }

    public static String getMerchantPass() throws Exception {
        String string = AppConfig.getString(MERCHANT_PASS);
        if (StringUtils.isNotEmpty((String)string)) {
            return AESencrp.decrypt(string);
        }
        return string;
    }

    public static boolean isSandboxMode() {
        return AppConfig.getBoolean("sandboxMode", true);
    }

    public static void setSandboxMode(boolean sandbosMode) {
        AppConfig.put("sandboxMode", sandbosMode);
    }

    public static double getBartabLimit() {
        try {
            return Double.parseDouble(AppConfig.getString("bartablimit", "25"));
        }
        catch (Exception e) {
            return 25.0;
        }
    }

    public static void setBartabLimit(double limit) {
        AppConfig.put("bartablimit", String.valueOf(limit));
    }

    public static double getAdvanceTipsPercentage() {
        try {
            return Double.parseDouble(AppConfig.getString("advanceTipsPercentage"));
        }
        catch (Exception e) {
            return 20.0;
        }
    }

    public static void setAdvanceTipsPercentage(double advanceTipsPercentage) {
        AppConfig.put("advanceTipsPercentage", String.valueOf(advanceTipsPercentage));
    }

    public static void setPaymentGateway(PaymentGatewayPlugin paymentGateway) {
        AppConfig.put("payment-gateway-id", paymentGateway.getId());
    }

    public static PaymentGatewayPlugin getPaymentGateway() {
        String gatewayId = AppConfig.getString("payment-gateway-id", AuthorizeNetGatewayPlugin.ID);
        List<FloreantPlugin> plugins = ExtensionManager.getPlugins(PaymentGatewayPlugin.class);
        for (FloreantPlugin plugin : plugins) {
            if (!gatewayId.equals(plugin.getId())) continue;
            return (PaymentGatewayPlugin)plugin;
        }
        return new AuthorizeNetGatewayPlugin();
    }
}


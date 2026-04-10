/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.xeoh.plugins.base.annotations.PluginImplementation
 */
package com.floreantpos.extension;

import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.config.ui.DefaultMerchantGatewayConfigurationView;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.ui.views.payment.AuthorizeDotNetProcessor;
import com.floreantpos.ui.views.payment.CardProcessor;
import java.awt.Component;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class AuthorizeNetGatewayPlugin
extends PaymentGatewayPlugin {
    public static final String ID = String.valueOf("Authorize.Net".hashCode());
    protected DefaultMerchantGatewayConfigurationView view;

    public boolean requireLicense() {
        return false;
    }

    public String getProductName() {
        return "Authorize.Net";
    }

    @Override
    public ConfigurationView getConfigurationPane() throws Exception {
        if (this.view == null) {
            this.view = new DefaultMerchantGatewayConfigurationView();
            this.view.setMerchantDefaultValue("6tuU4N3H", "4k6955x3T8bCVPVm");
            this.view.initialize();
        }
        return this.view;
    }

    public void initUI() {
    }

    public void initBackoffice() {
    }

    public void initConfigurationView(JDialog dialog) {
    }

    public String toString() {
        return this.getProductName();
    }

    public String getId() {
        return ID;
    }

    @Override
    public CardProcessor getProcessor() {
        return new AuthorizeDotNetProcessor();
    }

    @Override
    public boolean shouldShowCardInputProcessor() {
        return true;
    }

    public List<AbstractAction> getSpecialFunctionActions() {
        return null;
    }

    public void initLicense() {
    }

    public boolean hasValidLicense() {
        return true;
    }

    public String getProductVersion() {
        return null;
    }

    public Component getParent() {
        return null;
    }
}


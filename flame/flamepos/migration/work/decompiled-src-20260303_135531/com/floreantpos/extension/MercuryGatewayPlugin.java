/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.xeoh.plugins.base.annotations.PluginImplementation
 */
package com.floreantpos.extension;

import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.config.ui.DefaultMerchantGatewayConfigurationView;
import com.floreantpos.extension.AuthorizeNetGatewayPlugin;
import com.floreantpos.ui.views.payment.CardProcessor;
import com.floreantpos.ui.views.payment.MercuryPayProcessor;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class MercuryGatewayPlugin
extends AuthorizeNetGatewayPlugin {
    @Override
    public String getProductName() {
        return "Mercury Pay";
    }

    @Override
    public ConfigurationView getConfigurationPane() {
        if (this.view == null) {
            this.view = new DefaultMerchantGatewayConfigurationView();
            this.view.setMerchantDefaultValue("118725340908147", "XYZ");
        }
        return this.view;
    }

    @Override
    public void initUI() {
    }

    @Override
    public void initBackoffice() {
    }

    @Override
    public String toString() {
        return this.getProductName();
    }

    @Override
    public String getId() {
        return String.valueOf("Mercury Pay".hashCode());
    }

    @Override
    public CardProcessor getProcessor() {
        return new MercuryPayProcessor();
    }
}


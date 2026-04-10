/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.xeoh.plugins.base.annotations.PluginImplementation
 */
package com.floreantpos.extension;

import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.config.ui.InginicoConfigurationView;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.ui.views.payment.CardProcessor;
import java.awt.Component;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class InginicoPlugin
extends PaymentGatewayPlugin {
    InginicoConfigurationView view;

    public String getProductName() {
        return "Ingenico IWL220 TGI";
    }

    public void initUI() {
    }

    public void initBackoffice() {
    }

    public void initConfigurationView(JDialog dialog) {
    }

    public String getId() {
        return String.valueOf("Inginico".hashCode());
    }

    public String toString() {
        return this.getProductName();
    }

    @Override
    public ConfigurationView getConfigurationPane() throws Exception {
        if (this.view == null) {
            this.view = new InginicoConfigurationView();
            this.view.initialize();
        }
        return this.view;
    }

    @Override
    public CardProcessor getProcessor() {
        return null;
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


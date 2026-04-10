/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.AbstractFloreantPlugin
 */
package com.floreantpos.extension;

import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.extension.AbstractFloreantPlugin;
import com.floreantpos.ui.views.payment.CardProcessor;

public abstract class PaymentGatewayPlugin
extends AbstractFloreantPlugin {
    public abstract boolean shouldShowCardInputProcessor();

    public abstract ConfigurationView getConfigurationPane() throws Exception;

    public abstract CardProcessor getProcessor();
}


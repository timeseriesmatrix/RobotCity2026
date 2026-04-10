/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.demo.KitchenDisplayView;
import com.floreantpos.ui.views.order.RootView;

public class ShowKitchenDisplayAction
extends PosAction {
    public ShowKitchenDisplayAction() {
        super(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
    }

    @Override
    public void execute() {
        RootView.getInstance().showView(KitchenDisplayView.getInstance());
        RootView.getInstance().getHeaderPanel().setVisible(false);
    }
}


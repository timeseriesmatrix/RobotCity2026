/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.actions.ViewChangeAction;
import com.floreantpos.main.Application;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.order.RootView;

public class SwithboardViewAction
extends ViewChangeAction {
    public SwithboardViewAction() {
    }

    public SwithboardViewAction(boolean showText, boolean showIcon) {
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("order.png"));
        }
    }

    @Override
    public void execute() {
        try {
            RootView.getInstance().showView(SwitchboardView.getInstance());
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


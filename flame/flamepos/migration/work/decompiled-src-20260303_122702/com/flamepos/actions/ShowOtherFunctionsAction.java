/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.actions.ViewChangeAction;
import com.floreantpos.ui.views.SwitchboardOtherFunctionsView;
import com.floreantpos.ui.views.order.RootView;

public class ShowOtherFunctionsAction
extends ViewChangeAction {
    public ShowOtherFunctionsAction() {
    }

    public ShowOtherFunctionsAction(boolean showText, boolean showIcon) {
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("other_functions.png"));
        }
    }

    @Override
    public void execute() {
        SwitchboardOtherFunctionsView view = SwitchboardOtherFunctionsView.getInstance();
        RootView.getInstance().showView(view);
    }
}


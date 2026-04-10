/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customPayment;

import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.customPayment.CustomPaymentBrowser;
import com.floreantpos.util.POSUtil;
import javax.swing.JTabbedPane;

public class CustomPaymentBrowserAction
extends PosAction {
    public CustomPaymentBrowserAction() {
        super(Messages.getString("CustomPaymentBrowserAction.0"));
    }

    @Override
    public void execute() {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        CustomPaymentBrowser explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("CustomPaymentBrowserAction.1"));
        if (index == -1) {
            explorer = new CustomPaymentBrowser();
            tabbedPane.addTab(Messages.getString("CustomPaymentBrowserAction.2"), explorer);
        } else {
            explorer = (CustomPaymentBrowser)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


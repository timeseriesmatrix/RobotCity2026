/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.table;

import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.table.ShopTableBrowser;
import com.floreantpos.util.POSUtil;
import javax.swing.JTabbedPane;

public class ShowTableBrowserAction
extends PosAction {
    public ShowTableBrowserAction() {
        super(Messages.getString("ShowTableBrowserAction.0"));
    }

    @Override
    public void execute() {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        ShopTableBrowser explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("ShowTableBrowserAction.1"));
        if (index == -1) {
            explorer = new ShopTableBrowser();
            tabbedPane.addTab(Messages.getString("ShowTableBrowserAction.2"), explorer);
        } else {
            explorer = (ShopTableBrowser)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


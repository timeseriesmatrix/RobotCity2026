/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.OrderTypeExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class OrdersTypeExplorerAction
extends AbstractAction {
    public OrdersTypeExplorerAction() {
        super(POSConstants.ORDER_TYPE);
    }

    public OrdersTypeExplorerAction(String name) {
        super(name);
    }

    public OrdersTypeExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        OrderTypeExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.ORDER_TYPE);
        if (index == -1) {
            explorer = new OrderTypeExplorer();
            tabbedPane.addTab(POSConstants.ORDER_TYPE, explorer);
        } else {
            explorer = (OrderTypeExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


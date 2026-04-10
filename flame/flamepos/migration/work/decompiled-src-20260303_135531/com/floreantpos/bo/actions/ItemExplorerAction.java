/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.MenuItemExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ItemExplorerAction
extends AbstractAction {
    public ItemExplorerAction() {
        super(POSConstants.MENU_ITEMS);
    }

    public ItemExplorerAction(String name) {
        super(name);
    }

    public ItemExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItemExplorer item;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.ITEM_EXPLORER);
        if (index == -1) {
            item = new MenuItemExplorer();
            tabbedPane.addTab(POSConstants.ITEM_EXPLORER, item);
        } else {
            item = (MenuItemExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(item);
    }
}


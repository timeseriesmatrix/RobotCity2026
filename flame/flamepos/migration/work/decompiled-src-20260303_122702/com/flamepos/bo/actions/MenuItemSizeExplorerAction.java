/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.MenuItemSizeExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class MenuItemSizeExplorerAction
extends AbstractAction {
    public MenuItemSizeExplorerAction() {
        super(Messages.getString("MenuItemSizeExplorerAction.0"));
    }

    public MenuItemSizeExplorerAction(String name) {
        super(name);
    }

    public MenuItemSizeExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        MenuItemSizeExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("MenuItemSizeExplorerAction.0"));
        if (index == -1) {
            explorer = new MenuItemSizeExplorer();
            tabbedPane.addTab(Messages.getString("MenuItemSizeExplorerAction.0"), explorer);
        } else {
            explorer = (MenuItemSizeExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.MenuGroupExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class GroupExplorerAction
extends AbstractAction {
    public GroupExplorerAction() {
        super(POSConstants.MENU_GROUPS);
    }

    public GroupExplorerAction(String name) {
        super(name);
    }

    public GroupExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuGroupExplorer group;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.GROUP_EXPLORER);
        if (index == -1) {
            group = new MenuGroupExplorer();
            tabbedPane.addTab(POSConstants.GROUP_EXPLORER, group);
        } else {
            group = (MenuGroupExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(group);
    }
}


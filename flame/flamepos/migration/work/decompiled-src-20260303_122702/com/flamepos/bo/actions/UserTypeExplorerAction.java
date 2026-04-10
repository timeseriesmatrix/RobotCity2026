/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.UserTypeExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class UserTypeExplorerAction
extends AbstractAction {
    public UserTypeExplorerAction() {
        super(POSConstants.USER_TYPES);
    }

    public UserTypeExplorerAction(String name) {
        super(name);
    }

    public UserTypeExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        UserTypeExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.USER_TYPE_EXPLORER);
        if (index == -1) {
            explorer = new UserTypeExplorer();
            tabbedPane.addTab(POSConstants.USER_TYPE_EXPLORER, explorer);
        } else {
            explorer = (UserTypeExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


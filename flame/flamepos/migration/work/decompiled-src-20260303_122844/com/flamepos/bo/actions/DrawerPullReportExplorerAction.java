/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.DrawerPullReportExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class DrawerPullReportExplorerAction
extends AbstractAction {
    public DrawerPullReportExplorerAction() {
        super(POSConstants.DRAWER_PULL_REPORTS);
    }

    public DrawerPullReportExplorerAction(String name) {
        super(name);
    }

    public DrawerPullReportExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        DrawerPullReportExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.DRAWER_PULL_REPORTS);
        if (index == -1) {
            explorer = new DrawerPullReportExplorer();
            tabbedPane.addTab(POSConstants.DRAWER_PULL_REPORTS, explorer);
        } else {
            explorer = (DrawerPullReportExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


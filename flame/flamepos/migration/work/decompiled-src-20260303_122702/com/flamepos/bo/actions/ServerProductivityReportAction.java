/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.ServerProductivityReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ServerProductivityReportAction
extends AbstractAction {
    public ServerProductivityReportAction() {
        super(POSConstants.SERVER_PRODUCTIVITY_REPORT);
    }

    public ServerProductivityReportAction(String name) {
        super(name);
    }

    public ServerProductivityReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        ServerProductivityReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.SERVER_PRODUCTIVITY_REPORT);
        if (index == -1) {
            reportView = new ServerProductivityReportView();
            tabbedPane.addTab(POSConstants.SERVER_PRODUCTIVITY_REPORT, reportView);
        } else {
            reportView = (ServerProductivityReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


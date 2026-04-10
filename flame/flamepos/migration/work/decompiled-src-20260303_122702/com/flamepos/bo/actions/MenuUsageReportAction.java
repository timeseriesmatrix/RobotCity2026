/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.MenuUsageReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class MenuUsageReportAction
extends AbstractAction {
    public MenuUsageReportAction() {
        super(POSConstants.MENU_USAGE_REPORT);
    }

    public MenuUsageReportAction(String name) {
        super(name);
    }

    public MenuUsageReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        MenuUsageReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.MENU_USAGE_REPORT);
        if (index == -1) {
            reportView = new MenuUsageReportView();
            tabbedPane.addTab(POSConstants.MENU_USAGE_REPORT, reportView);
        } else {
            reportView = (MenuUsageReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


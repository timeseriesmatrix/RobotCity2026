/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.HourlyLaborReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class HourlyLaborReportAction
extends AbstractAction {
    public HourlyLaborReportAction() {
        super(POSConstants.HOURLY_LABOR_REPORT);
    }

    public HourlyLaborReportAction(String name) {
        super(name);
    }

    public HourlyLaborReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        HourlyLaborReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.HOURLY_LABOR_REPORT);
        if (index == -1) {
            reportView = new HourlyLaborReportView();
            tabbedPane.addTab(POSConstants.HOURLY_LABOR_REPORT, reportView);
        } else {
            reportView = (HourlyLaborReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


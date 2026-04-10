/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.ReportViewer;
import com.floreantpos.report.SalesReport;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class SalesReportAction
extends AbstractAction {
    public SalesReportAction() {
        super(POSConstants.SALES_REPORT);
    }

    public SalesReportAction(String name) {
        super(name);
    }

    public SalesReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        ReportViewer viewer = null;
        int index = tabbedPane.indexOfTab(POSConstants.SALES_REPORT);
        if (index == -1) {
            viewer = new ReportViewer(new SalesReport());
            tabbedPane.addTab(POSConstants.SALES_REPORT, viewer);
        } else {
            viewer = (ReportViewer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(viewer);
    }
}


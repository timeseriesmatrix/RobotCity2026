/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.OpenTicketSummaryReport;
import com.floreantpos.report.ReportViewer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class OpenTicketSummaryReportAction
extends AbstractAction {
    public OpenTicketSummaryReportAction() {
        super(POSConstants.OPEN_TICKET_SUMMARY);
    }

    public OpenTicketSummaryReportAction(String name) {
        super(name);
    }

    public OpenTicketSummaryReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        ReportViewer viewer = null;
        int index = tabbedPane.indexOfTab(POSConstants.OPEN_TICKET_SUMMARY);
        if (index == -1) {
            viewer = new ReportViewer(new OpenTicketSummaryReport());
            tabbedPane.addTab(POSConstants.OPEN_TICKET_SUMMARY_REPORT, viewer);
        } else {
            viewer = (ReportViewer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(viewer);
    }
}


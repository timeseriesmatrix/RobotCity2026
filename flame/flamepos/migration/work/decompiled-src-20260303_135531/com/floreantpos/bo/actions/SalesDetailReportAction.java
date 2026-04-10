/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.SalesDetailReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class SalesDetailReportAction
extends AbstractAction {
    public SalesDetailReportAction() {
        super(POSConstants.SALES_DETAILED_REPORT);
    }

    public SalesDetailReportAction(String name) {
        super(name);
    }

    public SalesDetailReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        SalesDetailReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.SALES_DETAILED_REPORT);
        if (index == -1) {
            reportView = new SalesDetailReportView();
            tabbedPane.addTab(POSConstants.SALES_DETAILED_REPORT, reportView);
        } else {
            reportView = (SalesDetailReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


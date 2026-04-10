/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.SalesSummaryReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class SalesAnalysisReportAction
extends AbstractAction {
    public SalesAnalysisReportAction() {
        super(POSConstants.SALES_ANALYSIS);
    }

    public SalesAnalysisReportAction(String name) {
        super(name);
    }

    public SalesAnalysisReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        SalesSummaryReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.SALES_ANALYSIS);
        if (index == -1) {
            reportView = new SalesSummaryReportView();
            reportView.setReportType(2);
            tabbedPane.addTab(POSConstants.SALES_ANALYSIS, reportView);
        } else {
            reportView = (SalesSummaryReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


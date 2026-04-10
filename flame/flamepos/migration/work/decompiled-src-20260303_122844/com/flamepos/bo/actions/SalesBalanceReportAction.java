/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.SalesBalanceReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class SalesBalanceReportAction
extends AbstractAction {
    public SalesBalanceReportAction() {
        super(POSConstants.SALES_BALANCE_REPORT);
    }

    public SalesBalanceReportAction(String name) {
        super(name);
    }

    public SalesBalanceReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        SalesBalanceReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.SALES_BALANCE_REPORT);
        if (index == -1) {
            reportView = new SalesBalanceReportView();
            tabbedPane.addTab(POSConstants.SALES_BALANCE_REPORT, reportView);
        } else {
            reportView = (SalesBalanceReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


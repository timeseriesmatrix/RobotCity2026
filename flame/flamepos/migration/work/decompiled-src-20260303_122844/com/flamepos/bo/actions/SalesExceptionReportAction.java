/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.SalesExceptionReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class SalesExceptionReportAction
extends AbstractAction {
    public SalesExceptionReportAction() {
        super(POSConstants.SALES_EXCEPTION_REPORT);
    }

    public SalesExceptionReportAction(String name) {
        super(name);
    }

    public SalesExceptionReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        SalesExceptionReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.SALES_EXCEPTION_REPORT);
        if (index == -1) {
            reportView = new SalesExceptionReportView();
            tabbedPane.addTab(POSConstants.SALES_EXCEPTION_REPORT, reportView);
        } else {
            reportView = (SalesExceptionReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.PayrollReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PayrollReportAction
extends AbstractAction {
    public PayrollReportAction() {
        super(POSConstants.PAYROLL_REPORT);
    }

    public PayrollReportAction(String name) {
        super(name);
    }

    public PayrollReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        PayrollReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.PAYROLL_REPORT);
        if (index == -1) {
            reportView = new PayrollReportView();
            tabbedPane.addTab(POSConstants.PAYROLL_REPORT, reportView);
        } else {
            reportView = (PayrollReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


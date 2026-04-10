/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.CustomPaymentReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CustomPaymentReportAction
extends AbstractAction {
    public CustomPaymentReportAction() {
        super(POSConstants.CUSTOM_PAYMENT_REPORT);
    }

    public CustomPaymentReportAction(String name) {
        super(name);
    }

    public CustomPaymentReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        CustomPaymentReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.CUSTOM_PAYMENT_REPORT);
        if (index == -1) {
            reportView = new CustomPaymentReportView();
            tabbedPane.addTab(POSConstants.CUSTOM_PAYMENT_REPORT, reportView);
        } else {
            reportView = (CustomPaymentReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


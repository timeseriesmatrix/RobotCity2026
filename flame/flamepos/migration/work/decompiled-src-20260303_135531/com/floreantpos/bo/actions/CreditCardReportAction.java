/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.CreditCardReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CreditCardReportAction
extends AbstractAction {
    public CreditCardReportAction() {
        super(POSConstants.CREDIT_CARD_REPORT);
    }

    public CreditCardReportAction(String name) {
        super(name);
    }

    public CreditCardReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        CreditCardReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.CREDIT_CARD_REPORT);
        if (index == -1) {
            reportView = new CreditCardReportView();
            tabbedPane.addTab(POSConstants.CREDIT_CARD_REPORT, reportView);
        } else {
            reportView = (CreditCardReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.PurchaseReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PurchaseReportAction
extends AbstractAction {
    public PurchaseReportAction() {
        super(Messages.getString("PurchaseReportAction.0"));
    }

    public PurchaseReportAction(String name) {
        super(name);
    }

    public PurchaseReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        PurchaseReportView reportView = null;
        int index = tabbedPane.indexOfTab(Messages.getString("PurchaseReportAction.0"));
        if (index == -1) {
            reportView = new PurchaseReportView();
            tabbedPane.addTab(Messages.getString("PurchaseReportAction.0"), reportView);
        } else {
            reportView = (PurchaseReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.InventoryOnHandReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class InventoryOnHandReportAction
extends AbstractAction {
    public InventoryOnHandReportAction() {
        super(Messages.getString("InventoryOnHandReportAction.0"));
    }

    public InventoryOnHandReportAction(String name) {
        super(name);
    }

    public InventoryOnHandReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        InventoryOnHandReportView reportView = null;
        int index = tabbedPane.indexOfTab(Messages.getString("InventoryOnHandReportAction.0"));
        if (index == -1) {
            reportView = new InventoryOnHandReportView();
            tabbedPane.addTab(Messages.getString("InventoryOnHandReportAction.0"), reportView);
        } else {
            reportView = (InventoryOnHandReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


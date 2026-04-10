/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.JournalReportView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class JournalReportAction
extends AbstractAction {
    public JournalReportAction() {
        super(POSConstants.JOURNAL_REPORT);
    }

    public JournalReportAction(String name) {
        super(name);
    }

    public JournalReportAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        JournalReportView reportView = null;
        int index = tabbedPane.indexOfTab(POSConstants.JOURNAL_REPORT);
        if (index == -1) {
            reportView = new JournalReportView();
            tabbedPane.addTab(POSConstants.JOURNAL_REPORT, reportView);
        } else {
            reportView = (JournalReportView)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(reportView);
    }
}


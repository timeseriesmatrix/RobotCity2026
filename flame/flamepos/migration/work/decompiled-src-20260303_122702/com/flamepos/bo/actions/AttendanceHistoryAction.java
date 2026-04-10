/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.AttendanceHistoryExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class AttendanceHistoryAction
extends AbstractAction {
    public AttendanceHistoryAction() {
        super(Messages.getString("AttendanceHistoryAction.0"));
    }

    public AttendanceHistoryAction(String name) {
        super(name);
    }

    public AttendanceHistoryAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        AttendanceHistoryExplorer explorer = null;
        int index = tabbedPane.indexOfTab(Messages.getString("AttendanceHistoryAction.1"));
        if (index == -1) {
            explorer = new AttendanceHistoryExplorer();
            tabbedPane.addTab(Messages.getString("AttendanceHistoryAction.2"), explorer);
        } else {
            explorer = (AttendanceHistoryExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.TicketExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class TicketExplorerAction
extends AbstractAction {
    public TicketExplorerAction() {
        super(POSConstants.CLOSED_TICKETS);
    }

    public TicketExplorerAction(String name) {
        super(name);
    }

    public TicketExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        TicketExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.CLOSED_TICKETS);
        if (index == -1) {
            explorer = new TicketExplorer();
            tabbedPane.addTab(POSConstants.CLOSED_TICKETS, explorer);
        } else {
            explorer = (TicketExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


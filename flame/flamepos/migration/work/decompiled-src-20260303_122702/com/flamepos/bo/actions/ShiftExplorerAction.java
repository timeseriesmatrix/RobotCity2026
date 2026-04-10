/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ShiftExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ShiftExplorerAction
extends AbstractAction {
    public ShiftExplorerAction() {
        super(POSConstants.SHIFTS);
    }

    public ShiftExplorerAction(String name) {
        super(name);
    }

    public ShiftExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        ShiftExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.SHIFTS);
        if (index == -1) {
            explorer = new ShiftExplorer();
            tabbedPane.addTab(POSConstants.SHIFTS, explorer);
        } else {
            explorer = (ShiftExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


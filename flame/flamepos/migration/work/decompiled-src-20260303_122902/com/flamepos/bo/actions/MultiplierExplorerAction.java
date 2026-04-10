/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.MultiplierExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class MultiplierExplorerAction
extends AbstractAction {
    public MultiplierExplorerAction() {
        super("Multipliers");
    }

    public MultiplierExplorerAction(String name) {
        super(name);
    }

    public MultiplierExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        MultiplierExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab("Multipliers");
        if (index == -1) {
            explorer = new MultiplierExplorer();
            tabbedPane.addTab("Multipliers", explorer);
        } else {
            explorer = (MultiplierExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.PizzaCrustExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PizzaCrustExplorerAction
extends AbstractAction {
    public PizzaCrustExplorerAction() {
        super(Messages.getString("PizzaCrustExplorerAction.0"));
    }

    public PizzaCrustExplorerAction(String name) {
        super(name);
    }

    public PizzaCrustExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        PizzaCrustExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("PizzaCrustExplorerAction.0"));
        if (index == -1) {
            explorer = new PizzaCrustExplorer();
            tabbedPane.addTab(Messages.getString("PizzaCrustExplorerAction.0"), explorer);
        } else {
            explorer = (PizzaCrustExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


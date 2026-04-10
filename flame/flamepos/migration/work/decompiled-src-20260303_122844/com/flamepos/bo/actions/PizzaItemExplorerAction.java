/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.PizzaItemExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PizzaItemExplorerAction
extends AbstractAction {
    public PizzaItemExplorerAction() {
        super(Messages.getString("PizzaItemExplorerAction.0"));
    }

    public PizzaItemExplorerAction(String name) {
        super(name);
    }

    public PizzaItemExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PizzaItemExplorer item;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("PizzaItemExplorerAction.1"));
        if (index == -1) {
            item = new PizzaItemExplorer();
            tabbedPane.addTab(Messages.getString("PizzaItemExplorerAction.1"), item);
        } else {
            item = (PizzaItemExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(item);
    }
}


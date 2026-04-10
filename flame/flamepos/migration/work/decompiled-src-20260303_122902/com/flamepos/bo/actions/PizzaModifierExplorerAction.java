/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.PizzaModifierExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PizzaModifierExplorerAction
extends AbstractAction {
    public PizzaModifierExplorerAction() {
        super(Messages.getString("PizzaModifierExplorerAction.0"));
    }

    public PizzaModifierExplorerAction(String name) {
        super(name);
    }

    public PizzaModifierExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PizzaModifierExplorer modifier;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(Messages.getString("PizzaModifierExplorerAction.1"));
        if (index == -1) {
            modifier = new PizzaModifierExplorer();
            tabbedPane.addTab(Messages.getString("PizzaModifierExplorerAction.1"), modifier);
        } else {
            modifier = (PizzaModifierExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(modifier);
    }
}


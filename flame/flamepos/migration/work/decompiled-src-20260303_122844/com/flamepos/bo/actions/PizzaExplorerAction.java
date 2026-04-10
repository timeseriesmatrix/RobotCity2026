/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.PizzaExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class PizzaExplorerAction
extends AbstractAction {
    public PizzaExplorerAction() {
        super("Pizza");
    }

    public PizzaExplorerAction(String name) {
        super(name);
    }

    public PizzaExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = window.getTabbedPane();
        PizzaExplorer explorer = null;
        int index = tabbedPane.indexOfTab("Pizza");
        if (index == -1) {
            explorer = new PizzaExplorer();
            tabbedPane.addTab("Pizza", explorer);
        } else {
            explorer = (PizzaExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


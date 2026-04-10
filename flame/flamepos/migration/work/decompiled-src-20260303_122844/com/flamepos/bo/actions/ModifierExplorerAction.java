/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ModifierExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ModifierExplorerAction
extends AbstractAction {
    public ModifierExplorerAction() {
        super(POSConstants.MENU_MODIFIERS);
    }

    public ModifierExplorerAction(String name) {
        super(name);
    }

    public ModifierExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModifierExplorer modifier;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.MODIFIER_EXPLORER);
        if (index == -1) {
            modifier = new ModifierExplorer();
            tabbedPane.addTab(POSConstants.MODIFIER_EXPLORER, modifier);
        } else {
            modifier = (ModifierExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(modifier);
    }
}


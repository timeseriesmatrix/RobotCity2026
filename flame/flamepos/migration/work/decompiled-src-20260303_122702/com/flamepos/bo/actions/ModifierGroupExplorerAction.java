/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.ModifierGroupExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ModifierGroupExplorerAction
extends AbstractAction {
    public ModifierGroupExplorerAction() {
        super(POSConstants.MENU_MODIFIER_GROUPS);
    }

    public ModifierGroupExplorerAction(String name) {
        super(name);
    }

    public ModifierGroupExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModifierGroupExplorer mGroup;
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.MODIFIER_GROUP_EXPLORER);
        if (index == -1) {
            mGroup = new ModifierGroupExplorer();
            tabbedPane.addTab(POSConstants.MODIFIER_GROUP_EXPLORER, mGroup);
        } else {
            mGroup = (ModifierGroupExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(mGroup);
    }
}


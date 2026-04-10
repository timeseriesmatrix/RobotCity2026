/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.CookingInstructionExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CookingInstructionExplorerAction
extends AbstractAction {
    public CookingInstructionExplorerAction() {
        super(POSConstants.COOKING_INSTRUCTIONS);
    }

    public CookingInstructionExplorerAction(String name) {
        super(name);
    }

    public CookingInstructionExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        CookingInstructionExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.COOKING_INSTRUCTIONS);
        if (index == -1) {
            explorer = new CookingInstructionExplorer();
            tabbedPane.addTab(POSConstants.COOKING_INSTRUCTIONS, explorer);
        } else {
            explorer = (CookingInstructionExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


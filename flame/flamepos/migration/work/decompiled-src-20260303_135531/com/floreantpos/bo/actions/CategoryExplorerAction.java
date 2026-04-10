/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.MenuCategoryExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CategoryExplorerAction
extends AbstractAction {
    public CategoryExplorerAction() {
        super(POSConstants.MENU_CATEGORIES);
    }

    public CategoryExplorerAction(String name) {
        super(name);
    }

    public CategoryExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        MenuCategoryExplorer explorer = null;
        JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
        int index = tabbedPane.indexOfTab(POSConstants.CATEGORY_EXPLORER);
        if (index == -1) {
            explorer = new MenuCategoryExplorer();
            tabbedPane.addTab(POSConstants.CATEGORY_EXPLORER, explorer);
        } else {
            explorer = (MenuCategoryExplorer)tabbedPane.getComponentAt(index);
        }
        tabbedPane.setSelectedComponent(explorer);
    }
}


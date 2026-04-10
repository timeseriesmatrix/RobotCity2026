/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.GratuityViewer2;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ViewGratuitiesAction
extends AbstractAction {
    public ViewGratuitiesAction() {
        super(POSConstants.GRATUITY_ADMINISTRATION);
    }

    public ViewGratuitiesAction(String name) {
        super(name);
    }

    public ViewGratuitiesAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
        try {
            GratuityViewer2 explorer = null;
            JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
            int index = tabbedPane.indexOfTab(POSConstants.GRATUITY_ADMINISTRATION);
            if (index == -1) {
                explorer = new GratuityViewer2();
                tabbedPane.addTab(POSConstants.GRATUITY_ADMINISTRATION, explorer);
            } else {
                explorer = (GratuityViewer2)tabbedPane.getComponentAt(index);
            }
            tabbedPane.setSelectedComponent(explorer);
        }
        catch (PosException x) {
            BOMessageDialog.showError(backOfficeWindow, x.getMessage(), x);
        }
        catch (Exception ex) {
            BOMessageDialog.showError(backOfficeWindow, POSConstants.ERROR_MESSAGE, ex);
        }
    }
}


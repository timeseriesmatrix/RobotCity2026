/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.CouponExplorer;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CouponExplorerAction
extends AbstractAction {
    public CouponExplorerAction() {
        super(POSConstants.COUPONS_AND_DISCOUNTS);
    }

    public CouponExplorerAction(String name) {
        super(name);
    }

    public CouponExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            BackOfficeWindow backOfficeWindow = POSUtil.getBackOfficeWindow();
            CouponExplorer explorer = null;
            JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
            int index = tabbedPane.indexOfTab(POSConstants.COUPON_DISCOUNT_EXPLORER);
            if (index == -1) {
                explorer = new CouponExplorer();
                explorer.initData();
                tabbedPane.addTab(POSConstants.COUPON_DISCOUNT_EXPLORER, explorer);
            } else {
                explorer = (CouponExplorer)tabbedPane.getComponentAt(index);
            }
            tabbedPane.setSelectedComponent(explorer);
        }
        catch (Exception x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }
}


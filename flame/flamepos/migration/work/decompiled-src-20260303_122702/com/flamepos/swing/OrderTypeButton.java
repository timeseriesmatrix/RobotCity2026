/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.customer.CustomerSelectorDialog;
import com.floreantpos.customer.CustomerSelectorFactory;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.tableselection.TableSelectorDialog;
import com.floreantpos.ui.tableselection.TableSelectorFactory;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class OrderTypeButton
extends PosButton
implements ActionListener {
    private OrderType orderType;

    public OrderTypeButton() {
        super("");
    }

    public OrderTypeButton(OrderType orderType) {
        this.orderType = orderType;
        if (orderType != null) {
            if (orderType.getId() == null) {
                this.setIcon(IconFactory.getIcon("/ui_icons/", "add+user.png"));
            } else {
                this.setText(orderType.name());
            }
        } else {
            this.setText(POSConstants.TAKE_OUT_BUTTON_TEXT);
        }
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.hasPermission()) {
            POSMessageDialog.showError("You do not have permission to create order");
            return;
        }
        if (RootView.getInstance().isMaintenanceMode()) {
            QuickMaintenanceExplorer.quickMaintain(this.orderType);
            return;
        }
        if (this.orderType.isShowTableSelection().booleanValue()) {
            TableSelectorDialog dialog = TableSelectorFactory.createTableSelectorDialog(this.orderType);
            dialog.setCreateNewTicket(true);
            dialog.updateView(true);
            dialog.openUndecoratedFullScreen();
            if (!dialog.isCanceled()) {
                return;
            }
        } else if (this.orderType.isRequiredCustomerData().booleanValue()) {
            CustomerSelectorDialog dialog = CustomerSelectorFactory.createCustomerSelectorDialog(this.orderType);
            dialog.setCreateNewTicket(true);
            dialog.updateView(true);
            dialog.openUndecoratedFullScreen();
            if (!dialog.isCanceled()) {
                return;
            }
        } else {
            try {
                OrderServiceFactory.getOrderService().createNewTicket(this.orderType, null, null);
            }
            catch (TicketAlreadyExistsException e1) {
                PosLog.error(this.getClass(), e1);
            }
        }
    }

    private boolean hasPermission() {
        User user = Application.getCurrentUser();
        UserType userType = user.getType();
        if (userType != null) {
            Set<UserPermission> permissions = userType.getPermissions();
            for (UserPermission permission : permissions) {
                if (!permission.equals(UserPermission.CREATE_TICKET)) continue;
                return true;
            }
        }
        return false;
    }
}


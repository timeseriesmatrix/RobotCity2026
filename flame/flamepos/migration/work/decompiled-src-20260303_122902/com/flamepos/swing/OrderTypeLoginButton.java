/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.views.LoginView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class OrderTypeLoginButton
extends PosButton
implements ActionListener {
    private OrderType orderType;

    public OrderTypeLoginButton() {
        super("");
    }

    public OrderTypeLoginButton(OrderType orderType) {
        this.orderType = orderType;
        if (orderType != null) {
            this.setText(orderType.getName());
        } else {
            this.setText(POSConstants.TAKE_OUT_BUTTON_TEXT);
        }
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TerminalConfig.setDefaultView(this.orderType.getName());
        LoginView.getInstance().doLogin();
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


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import java.awt.Color;
import org.apache.commons.lang.StringUtils;

public class ShopTableButton
extends PosButton {
    private ShopTable shopTable;
    private User user;
    private Ticket ticket;

    public ShopTableButton(ShopTable shopTable) {
        this.shopTable = shopTable;
        if (shopTable.getId() != null) {
            this.setText(shopTable.toString());
        }
        this.update();
    }

    public int getId() {
        return this.shopTable.getId();
    }

    public void setShopTable(ShopTable shopTable) {
        this.shopTable = shopTable;
    }

    public ShopTable getShopTable() {
        return this.shopTable;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ShopTableButton)) {
            return false;
        }
        ShopTableButton that = (ShopTableButton)obj;
        return this.shopTable.equals(that.shopTable);
    }

    public int hashCode() {
        return this.shopTable.hashCode();
    }

    @Override
    public String toString() {
        return this.shopTable.toString();
    }

    public void update() {
        if (this.shopTable != null && this.shopTable.isServing().booleanValue()) {
            this.setBackground(Color.red);
            this.setForeground(Color.BLACK);
        } else if (this.shopTable != null && this.shopTable.isBooked().booleanValue()) {
            this.setEnabled(false);
            this.setOpaque(true);
            this.setBackground(Color.orange);
            this.setForeground(Color.BLACK);
        } else {
            this.setEnabled(true);
            this.setBackground(Color.white);
            this.setForeground(Color.black);
        }
    }

    public void setUser(User user) {
        if (user != null) {
            this.user = user;
        }
    }

    public User getUser() {
        return this.user;
    }

    public boolean hasUserAccess() {
        int ticketUserId;
        if (this.user == null) {
            return false;
        }
        User currentUser = Application.getCurrentUser();
        int currentUserId = currentUser.getUserId();
        if (currentUserId == (ticketUserId = this.user.getUserId().intValue())) {
            return true;
        }
        if (currentUser.hasPermission(UserPermission.PERFORM_MANAGER_TASK) || currentUser.hasPermission(UserPermission.PERFORM_ADMINISTRATIVE_TASK)) {
            return true;
        }
        String password = PasswordEntryDialog.show(Application.getPosWindow(), Messages.getString("PosAction.0"));
        if (StringUtils.isEmpty((String)password)) {
            return false;
        }
        int inputUserId = UserDAO.getInstance().findUserBySecretKey(password).getAutoId();
        if (inputUserId != this.user.getAutoId()) {
            POSMessageDialog.showError(Application.getPosWindow(), "Incorrect password");
            return false;
        }
        return true;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return this.ticket;
    }
}


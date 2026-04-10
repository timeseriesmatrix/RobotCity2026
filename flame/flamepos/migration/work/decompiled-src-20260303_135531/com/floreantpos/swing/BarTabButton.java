/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import java.awt.Color;
import org.apache.commons.lang.StringUtils;

public class BarTabButton
extends PosButton {
    private User user;
    private Ticket ticket;

    public BarTabButton(Ticket ticket) {
        this.ticket = ticket;
        if (ticket.getId() != null) {
            this.setText(ticket.getId() + "");
        }
        this.update();
    }

    public int getId() {
        return this.ticket.getId();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BarTabButton)) {
            return false;
        }
        BarTabButton that = (BarTabButton)obj;
        return this.ticket.equals(that.ticket);
    }

    public int hashCode() {
        return this.ticket.hashCode();
    }

    @Override
    public String toString() {
        return this.ticket.toString();
    }

    public void update() {
        this.setEnabled(true);
        this.setBackground(Color.white);
        this.setForeground(Color.black);
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


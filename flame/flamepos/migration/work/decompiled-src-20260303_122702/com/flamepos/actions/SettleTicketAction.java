/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.payment.SettleTicketDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class SettleTicketAction
extends AbstractAction {
    private int ticketId;

    public SettleTicketAction(int ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.execute();
    }

    public boolean execute() {
        Ticket ticket = TicketDAO.getInstance().loadFullTicket(this.ticketId);
        if (ticket.isPaid().booleanValue()) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketAction.0"));
            return false;
        }
        SettleTicketDialog posDialog = new SettleTicketDialog(ticket);
        if (ticket.isBarTab().booleanValue()) {
            posDialog.doSettleBarTabTicket(ticket);
            return true;
        }
        posDialog.setSize(Application.getPosWindow().getSize());
        posDialog.setDefaultCloseOperation(2);
        posDialog.openUndecoratedFullScreen();
        return !posDialog.isCanceled();
    }
}


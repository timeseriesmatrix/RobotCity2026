/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.ui.views.SplitTicketDialog;

public class SplitTicketAction
extends PosAction {
    private Ticket ticket;

    public SplitTicketAction() {
        super(POSConstants.SPLIT_TICKET);
    }

    public SplitTicketAction(Ticket ticket) {
        super(POSConstants.SPLIT_TICKET);
        this.ticket = ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    @Override
    public void execute() {
        if (this.ticket == null) {
            return;
        }
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(this.ticket.getId());
        try {
            SplitTicketDialog dialog = new SplitTicketDialog();
            dialog.setTicket(ticketToEdit);
            dialog.setLocationRelativeTo(Application.getPosWindow());
            dialog.setVisible(true);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }
}


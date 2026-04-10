/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.services.TicketService;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.CurrencyUtil;

public class RefundAction
extends PosAction {
    private ITicketList ticketList;

    public RefundAction(ITicketList ticketList) {
        super(Messages.getString("RefundAction.0"), UserPermission.REFUND);
        this.ticketList = ticketList;
    }

    @Override
    public void execute() {
        try {
            double refundAmount;
            Ticket ticket = this.ticketList.getSelectedTicket();
            if (ticket == null) {
                int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("RefundAction.1"));
                if (ticketId == -1) {
                    return;
                }
                ticket = TicketService.getTicket(ticketId);
            }
            if (!ticket.isPaid().booleanValue()) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.2"));
                return;
            }
            if (ticket.isRefunded().booleanValue()) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.3"));
                return;
            }
            Double paidAmount = ticket.getPaidAmount();
            String message = CurrencyUtil.getCurrencySymbol() + paidAmount + Messages.getString("RefundAction.4");
            ticket = TicketDAO.getInstance().loadFullTicket(ticket.getId());
            message = "<html>" + Messages.getString("RefundAction.6") + ticket.getId() + Messages.getString("RefundAction.7") + ticket.getPaidAmount();
            if (ticket.getGratuity() != null) {
                message = message + Messages.getString("RefundAction.8") + ticket.getGratuity().getAmount();
            }
            if (Double.isNaN(refundAmount = NumberSelectionDialog2.takeDoubleInput(message = message + "</html>", Messages.getString("RefundAction.10"), paidAmount))) {
                return;
            }
            if (refundAmount > paidAmount) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.11"));
                return;
            }
            PosTransactionService.getInstance().refundTicket(ticket, refundAmount);
            POSMessageDialog.showMessage(Messages.getString("RefundAction.12") + CurrencyUtil.getCurrencySymbol() + refundAmount);
            this.ticketList.updateTicketList();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


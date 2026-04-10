/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.services.TicketService;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.VoidTicketDialog;
import com.floreantpos.util.POSUtil;

public class VoidTicketAction
extends PosAction {
    private final ITicketList ticketList;

    public VoidTicketAction(ITicketList ticketList) {
        super(POSConstants.VOID_TICKET_BUTTON_TEXT, UserPermission.VOID_TICKET);
        this.ticketList = ticketList;
    }

    @Override
    public void execute() {
        try {
            Ticket ticket = this.ticketList.getSelectedTicket();
            if (ticket == null) {
                int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("VoidTicketAction.0"));
                if (ticketId == -1) {
                    return;
                }
                ticket = TicketService.getTicket(ticketId);
            }
            if (ticket.isVoided().booleanValue() && POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "This ticket is already voided. \nDo you want to re-void?", "Confirm") != 0) {
                return;
            }
            Ticket ticketToVoid = TicketDAO.getInstance().loadFullTicket(ticket.getId());
            VoidTicketDialog voidTicketDialog = new VoidTicketDialog();
            voidTicketDialog.setTicket(ticketToVoid);
            voidTicketDialog.open();
            if (!voidTicketDialog.isCanceled()) {
                this.ticketList.updateTicketList();
            }
        }
        catch (PosException e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage());
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


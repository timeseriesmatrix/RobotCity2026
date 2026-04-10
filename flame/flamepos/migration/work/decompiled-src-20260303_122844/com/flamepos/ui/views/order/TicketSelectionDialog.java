/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;

public class TicketSelectionDialog
extends OkCancelOptionDialog {
    private ScrollableFlowPanel buttonsPanel;
    private List<Ticket> addedTicketListModel = new ArrayList<Ticket>();

    public TicketSelectionDialog() {
        super((Window)Application.getPosWindow(), Messages.getString("TicketSelectionDialog.0"));
        this.initComponent();
        this.initData();
    }

    public TicketSelectionDialog(List<Ticket> tickets) {
        this.initComponent();
        this.rendererTickets(tickets);
        this.setResizable(true);
    }

    private void initComponent() {
        this.setOkButtonText(Messages.getString("TicketSelectionDialog.3"));
        this.buttonsPanel = new ScrollableFlowPanel(3);
        PosScrollPane scrollPane = new PosScrollPane(this.buttonsPanel, 20, 31);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(80, 0));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));
        this.getContentPanel().add((Component)scrollPane, "Center");
        this.setSize(1024, 600);
    }

    private void initData() {
        TicketDAO dao = new TicketDAO();
        try {
            List<Ticket> tickets = dao.getTicketsWithSpecificFields(Ticket.PROP_ID, Ticket.PROP_DUE_AMOUNT);
            Dimension size = PosUIManager.getSize(115, 80);
            for (Ticket ticket : tickets) {
                if (ticket.getDueAmount() <= 0.0) continue;
                TicketButton btnTicket = new TicketButton(ticket);
                this.buttonsPanel.add(btnTicket);
                btnTicket.setPreferredSize(size);
            }
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getLocalizedMessage(), e);
        }
    }

    private void rendererTickets(List<Ticket> tickets) {
        try {
            for (Ticket ticket : tickets) {
                if (ticket.getDueAmount() <= 0.0) continue;
                this.buttonsPanel.add(new TicketButton(ticket));
            }
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void doOk() {
        if (this.addedTicketListModel.isEmpty()) {
            POSMessageDialog.showMessage(Messages.getString("TicketSelectionDialog.5"));
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }

    @Override
    public void doCancel() {
        this.addedTicketListModel.clear();
        this.setCanceled(true);
        this.dispose();
    }

    public List<Ticket> getSelectedTickets() {
        return this.addedTicketListModel;
    }

    private class TicketButton
    extends POSToggleButton
    implements ActionListener {
        private Ticket ticket;

        TicketButton(Ticket ticket) {
            this.ticket = ticket;
            this.setFont(this.getFont().deriveFont(1, PosUIManager.getFontSize(18)));
            this.setText("<html><body><center>#" + ticket.getId() + "<br>" + POSConstants.DUE + ":" + ticket.getDueAmount() + "</center></body></html>");
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isSelected()) {
                TicketSelectionDialog.this.addedTicketListModel.add(this.ticket);
            } else {
                TicketSelectionDialog.this.addedTicketListModel.remove(this.ticket);
            }
        }
    }
}


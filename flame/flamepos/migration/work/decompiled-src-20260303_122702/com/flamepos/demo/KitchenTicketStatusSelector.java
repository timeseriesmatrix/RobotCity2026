/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.demo;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.KitchenTicketItemDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class KitchenTicketStatusSelector
extends POSDialog
implements ActionListener {
    private PosButton btnVoid = new PosButton(KitchenTicket.KitchenTicketStatus.VOID.name());
    private PosButton btnReady = new PosButton(Messages.getString("KitchenTicketStatusSelector.2"));
    private KitchenTicket kitchenTicket;
    private KitchenTicketItem ticketItem;

    public KitchenTicketStatusSelector(Frame parent) {
        super(parent, true);
        this.initComponent();
    }

    public KitchenTicketStatusSelector(Frame parent, KitchenTicket kitchenTicket) {
        super(parent, true);
        this.kitchenTicket = kitchenTicket;
        this.initComponent();
    }

    private void initComponent() {
        this.setTitle(Messages.getString("KitchenTicketStatusSelector.0"));
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.setDefaultCloseOperation(2);
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("KitchenTicketStatusSelector.1"));
        this.add((Component)titlePanel, "North");
        JPanel panel = new JPanel(new GridLayout(1, 0, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(this.btnReady);
        this.add(panel);
        this.btnReady.setActionCommand(KitchenTicket.KitchenTicketStatus.DONE.name());
        this.btnVoid.addActionListener(this);
        this.btnReady.addActionListener(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            KitchenTicket.KitchenTicketStatus status = KitchenTicket.KitchenTicketStatus.valueOf(e.getActionCommand());
            this.ticketItem.setStatus(status.name());
            int itemCount = this.ticketItem.getQuantity();
            Ticket ticket = TicketDAO.getInstance().load(this.kitchenTicket.getTicketId());
            for (TicketItem item : ticket.getTicketItems()) {
                if (this.ticketItem.getMenuItemCode() == null || !this.ticketItem.getMenuItemCode().equals(item.getItemCode()) || item.getStatus() != null && item.getStatus().equals("Ready")) continue;
                if (itemCount == 0) break;
                if (status.equals((Object)KitchenTicket.KitchenTicketStatus.DONE)) {
                    item.setStatus("Ready");
                } else {
                    item.setStatus("Void");
                }
                itemCount -= item.getItemCount().intValue();
            }
            Transaction tx = null;
            try (Session session = null;){
                session = KitchenTicketItemDAO.getInstance().createNewSession();
                tx = session.beginTransaction();
                session.saveOrUpdate((Object)ticket);
                session.saveOrUpdate((Object)this.ticketItem);
                tx.commit();
            }
            this.dispose();
        }
        catch (Exception e2) {
            POSMessageDialog.showError(this, e2.getMessage(), e2);
        }
    }

    public KitchenTicketItem getTicketItem() {
        return this.ticketItem;
    }

    public void setTicketItem(KitchenTicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }
}


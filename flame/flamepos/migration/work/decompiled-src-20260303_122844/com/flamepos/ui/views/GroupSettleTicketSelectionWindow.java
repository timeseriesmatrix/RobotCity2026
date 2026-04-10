/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.PaymentStatusFilter;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class GroupSettleTicketSelectionWindow
extends POSDialog {
    private JXTable ticketList;
    private BeanTableModel<Ticket> tableModel;

    public GroupSettleTicketSelectionWindow() {
        super((Frame)Application.getPosWindow(), true);
        this.setTitle(Messages.getString("GroupSettleTicketSelectionWindow.0"));
        this.createTicketList();
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new PosScrollPane((Component)this.ticketList, 20, 31));
        JPanel buttonPanel = new JPanel();
        PosButton btnConfirm = new PosButton(Messages.getString("GroupSettleTicketSelectionWindow.1"));
        btnConfirm.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                GroupSettleTicketSelectionWindow.this.setCanceled(false);
                GroupSettleTicketSelectionWindow.this.dispose();
            }
        });
        buttonPanel.add(btnConfirm);
        PosButton psbtnCancel = new PosButton(Messages.getString("GroupSettleTicketSelectionWindow.2"));
        psbtnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                GroupSettleTicketSelectionWindow.this.setCanceled(true);
                GroupSettleTicketSelectionWindow.this.dispose();
            }
        });
        buttonPanel.add(psbtnCancel);
        contentPanel.add((Component)buttonPanel, "South");
        this.add(contentPanel);
        this.setSize(800, 600);
    }

    private void createTicketList() {
        this.tableModel = new BeanTableModel(Ticket.class);
        this.tableModel.addColumn(POSConstants.TICKET_LIST_COLUMN_ID, "id");
        this.tableModel.addColumn(POSConstants.TICKET_LIST_COLUMN_TICKET_TYPE, "ticketType");
        this.tableModel.addColumn(POSConstants.TICKET_LIST_COLUMN_SERVER, "owner");
        this.tableModel.addColumn(POSConstants.TICKET_LIST_COLUMN_TOTAL, "totalAmount");
        this.tableModel.addColumn(POSConstants.TICKET_LIST_COLUMN_DUE, "dueAmount");
        this.ticketList = new JXTable(this.tableModel);
        this.ticketList.setSortable(true);
        this.ticketList.setColumnControlVisible(true);
        this.ticketList.setRowHeight(60);
        this.ticketList.setAutoResizeMode(3);
        this.ticketList.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.ticketList.setGridColor(Color.LIGHT_GRAY);
        this.ticketList.getTableHeader().setPreferredSize(new Dimension(100, 40));
        User user = Application.getCurrentUser();
        TicketDAO dao = TicketDAO.getInstance();
        List<Ticket> tickets = null;
        PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
        String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
        tickets = user.canViewAllOpenTickets() ? dao.findTickets(paymentStatusFilter, orderTypeFilter) : dao.findTicketsForUser(paymentStatusFilter, orderTypeFilter, user);
        this.tableModel.addRows(tickets);
    }
}


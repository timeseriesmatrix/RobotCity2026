/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jidesoft.swing.JideScrollPane
 */
package com.floreantpos.ui.tableselection;

import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.BarTabButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.CurrencyUtil;
import com.jidesoft.swing.JideScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class BarTabSelectionView
extends JPanel {
    private Map<Ticket, BarTabButton> tableButtonMap = new HashMap<Ticket, BarTabButton>();
    private ScrollableFlowPanel buttonsPanel;
    private OrderType orderType;

    public BarTabSelectionView() {
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout(10, 10));
        this.buttonsPanel = new ScrollableFlowPanel(1);
        TitledBorder titledBorder1 = BorderFactory.createTitledBorder(null, "Bar Tab Tickets", 2, 0);
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(new CompoundBorder(titledBorder1, new EmptyBorder(2, 2, 2, 2)));
        JideScrollPane scrollPane = new JideScrollPane((Component)this.buttonsPanel, 20, 31);
        scrollPane.getVerticalScrollBar().setPreferredSize(PosUIManager.getSize(60, 0));
        leftPanel.add((Component)scrollPane, "Center");
        this.add((Component)leftPanel, "Center");
    }

    private void rendererBarTickets() {
        List<Ticket> openTickets = TicketDAO.getInstance().findOpenTicketsByOrderType(this.orderType);
        for (Ticket ticket : openTickets) {
            BarTabButton barTabButton = new BarTabButton(ticket);
            barTabButton.setPreferredSize(PosUIManager.getSize(157, 138));
            barTabButton.setFont(new Font(barTabButton.getFont().getName(), 1, 24));
            barTabButton.setText(barTabButton.getText());
            barTabButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    BarTabSelectionView.this.editTab(e);
                }
            });
            barTabButton.update();
            this.buttonsPanel.add(barTabButton);
            this.tableButtonMap.put(ticket, barTabButton);
            String customerName = barTabButton.getTicket().getProperty("CUSTOMER_NAME");
            if (customerName == null) {
                customerName = "Guest";
            }
            barTabButton.setText("<html><center>" + customerName + "<br><h4 style='margin:0px;'>" + ticket.getOwner().getFirstName() + "<br>Chk#" + ticket.getId() + "</h4>" + CurrencyUtil.getCurrencySymbol() + ticket.getTotalAmount() + "<br><small style='margin:0px;'>Due: " + CurrencyUtil.getCurrencySymbol() + ticket.getDueAmount() + "</small></center></html>");
            if (!ticket.getOwner().getUserId().toString().equals(Application.getCurrentUser().getUserId().toString())) {
                barTabButton.setBackground(new Color(139, 0, 139));
                barTabButton.setForeground(Color.WHITE);
            }
            barTabButton.setTicket(ticket);
            barTabButton.setUser(ticket.getOwner());
        }
    }

    private boolean editTab(ActionEvent e) {
        BarTabButton button = (BarTabButton)e.getSource();
        if (!button.hasUserAccess()) {
            return false;
        }
        this.editTicket(button.getTicket());
        return true;
    }

    private void closeDialog(boolean canceled) {
        Window windowAncestor = SwingUtilities.getWindowAncestor(this);
        if (windowAncestor instanceof POSDialog) {
            ((POSDialog)windowAncestor).setCanceled(false);
            windowAncestor.dispose();
        }
    }

    private boolean editTicket(Ticket ticket) {
        if (ticket == null || RootView.getInstance().isMaintenanceMode()) {
            return false;
        }
        this.closeDialog(false);
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        OrderView.getInstance().setCurrentTicket(ticketToEdit);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
        return true;
    }

    public void updateView(OrderType orderType) {
        this.orderType = orderType;
        this.buttonsPanel.getContentPane().removeAll();
        this.tableButtonMap.clear();
        this.rendererBarTickets();
        this.buttonsPanel.getContentPane().revalidate();
        this.buttonsPanel.getContentPane().repaint();
    }
}


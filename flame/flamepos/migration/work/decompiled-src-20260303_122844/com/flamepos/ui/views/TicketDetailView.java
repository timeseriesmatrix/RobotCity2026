/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  net.sf.jasperreports.engine.JasperPrint
 */
package com.floreantpos.ui.views;

import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.report.TicketPrintProperties;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.TicketReceiptView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperPrint;

public class TicketDetailView
extends JPanel {
    public static final String VIEW_NAME = "TICKET_DETAIL";
    private JPanel topPanel;
    private List<Ticket> tickets;

    public TicketDetailView() {
        this.setLayout(new BorderLayout(5, 5));
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.topPanel = new JPanel(new GridLayout());
        this.add((Component)this.topPanel, "Center");
        this.setOpaque(false);
    }

    public void clearView() {
        this.topPanel.removeAll();
    }

    public void updateView() {
        try {
            this.clearView();
            List<Ticket> tickets = this.getTickets();
            int totalTicket = tickets.size();
            if (totalTicket <= 0) {
                return;
            }
            JPanel reportPanel = new JPanel((LayoutManager)new MigLayout("wrap 1, ax 50%", "", ""));
            PosScrollPane scrollPane = new PosScrollPane(reportPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            for (Ticket ticket : tickets) {
                TicketPrintProperties printProperties = new TicketPrintProperties("*** ORDER " + ticket.getId() + " ***", false, true, true);
                HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, null);
                map.put("IS_IGNORE_PAGINATION", true);
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
                TicketReceiptView receiptView = new TicketReceiptView(jasperPrint);
                reportPanel.add(receiptView.getReportPanel());
            }
            this.topPanel.add((Component)scrollPane, "Center");
            this.revalidate();
            this.repaint();
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public List<Ticket> getTickets() {
        return this.tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        this.updateView();
    }

    public void setTicket(Ticket ticket) {
        this.tickets = new ArrayList<Ticket>(1);
        this.tickets.add(ticket);
        this.updateView();
    }

    public void cleanup() {
        this.tickets = null;
    }
}


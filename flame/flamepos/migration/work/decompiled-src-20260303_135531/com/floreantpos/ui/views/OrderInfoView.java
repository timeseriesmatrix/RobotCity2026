/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  net.sf.jasperreports.engine.JasperPrint
 */
package com.floreantpos.ui.views;

import com.floreantpos.model.Ticket;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.report.TicketPrintProperties;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.ui.views.TicketReceiptView;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperPrint;

public class OrderInfoView
extends JPanel {
    private List<Ticket> tickets;
    private JPanel reportPanel;

    public OrderInfoView(List<Ticket> tickets) throws Exception {
        this.tickets = tickets;
        this.createUI();
    }

    public void createUI() throws Exception {
        this.reportPanel = new JPanel((LayoutManager)new MigLayout("wrap 1, ax 50%", "", ""));
        PosScrollPane scrollPane = new PosScrollPane(this.reportPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        this.createReport();
        this.setLayout(new BorderLayout());
        this.add(scrollPane);
    }

    public void createReport() throws Exception {
        for (int i = 0; i < this.tickets.size(); ++i) {
            Ticket ticket = this.tickets.get(i);
            TicketPrintProperties printProperties = new TicketPrintProperties("*** ORDER " + ticket.getId() + " ***", false, true, true);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, null);
            map.put("IS_IGNORE_PAGINATION", true);
            JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
            TicketReceiptView receiptView = new TicketReceiptView(jasperPrint);
            this.reportPanel.add(receiptView.getReportPanel());
        }
    }

    public void print() throws Exception {
        for (Ticket ticket : this.tickets) {
            ReceiptPrintService.printTicket(ticket);
        }
    }

    public void printCopy(String copyType) throws Exception {
        for (Ticket ticket : this.tickets) {
            ReceiptPrintService.printTicket(ticket, copyType);
        }
    }

    public List<Ticket> getTickets() {
        return this.tickets;
    }

    public JPanel getReportPanel() {
        return this.reportPanel;
    }
}


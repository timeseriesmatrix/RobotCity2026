/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.view.JRViewer
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.Report;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.TicketReportModel;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.util.CurrencyUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

public class OpenTicketSummaryReport
extends Report {
    @Override
    public void refresh() throws Exception {
        List<Ticket> tickets = TicketDAO.getInstance().findOpenTickets(this.getTerminal(), this.getUserType());
        TicketReportModel reportModel = new TicketReportModel();
        reportModel.setItems(tickets);
        reportModel.calculateGrandTotal();
        HashMap<String, String> map = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(map);
        map.put("reportTitle", Messages.getString("OpenTicketSummaryReport.0"));
        map.put("reportTime", ReportService.formatFullDate(new Date()));
        map.put("userType", this.getUserType() == null ? POSConstants.ALL : this.getUserType().getName());
        map.put("terminalName", this.getTerminal() == null ? POSConstants.ALL : this.getTerminal().getName());
        map.put("currency", Messages.getString("SalesReport.8") + CurrencyUtil.getCurrencyName() + " (" + CurrencyUtil.getCurrencySymbol() + ")");
        map.put("grandTotal", reportModel.getGrandTotalAsString());
        JasperReport masterReport = ReportUtil.getReport("open_ticket_summary_report");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)masterReport, map, (JRDataSource)new JRTableModelDataSource((TableModel)reportModel));
        this.viewer = new JRViewer(print);
    }

    @Override
    public boolean isDateRangeSupported() {
        return false;
    }

    @Override
    public boolean isTypeSupported() {
        return false;
    }
}


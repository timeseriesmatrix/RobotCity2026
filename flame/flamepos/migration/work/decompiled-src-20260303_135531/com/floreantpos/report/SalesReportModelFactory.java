/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JREmptyDataSource
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.view.JasperViewer
 *  org.jdesktop.swingx.calendar.DateUtils
 */
package com.floreantpos.report;

import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.ReportItem;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.SalesReportModel;
import com.floreantpos.util.CurrencyUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.calendar.DateUtils;

public class SalesReportModelFactory {
    private Date startDate;
    private Date endDate;
    private boolean settled = true;
    private SalesReportModel itemReportModel;
    private SalesReportModel modifierReportModel;

    public void createModels() {
        Date currentDate = new Date();
        if (this.startDate == null) {
            this.startDate = DateUtils.startOfDay((Date)currentDate);
        }
        if (this.endDate == null) {
            this.endDate = DateUtils.endOfDay((Date)currentDate);
        }
        List<Ticket> tickets = TicketDAO.getInstance().findTickets(this.startDate, this.endDate, this.settled);
        HashMap<String, ReportItem> itemMap = new HashMap<String, ReportItem>();
        HashMap modifierMap = new HashMap();
        Iterator<Ticket> iter = tickets.iterator();
        while (iter.hasNext()) {
            Ticket t = iter.next();
            Ticket ticket = TicketDAO.getInstance().loadFullTicket(t.getId());
            List<TicketItem> ticketItems = ticket.getTicketItems();
            if (ticketItems == null) continue;
            String key = null;
            for (TicketItem ticketItem : ticketItems) {
                key = ticketItem.getItemId() == null ? ticketItem.getName() : ticketItem.getItemId().toString();
                ReportItem reportItem = (ReportItem)itemMap.get(key);
                if (reportItem == null) {
                    reportItem = new ReportItem();
                    reportItem.setId(key);
                    reportItem.setPrice(ticketItem.getUnitPrice());
                    reportItem.setName(ticketItem.getName());
                    reportItem.setTaxRate(ticketItem.getTaxRate());
                    itemMap.put(key, reportItem);
                }
                reportItem.setQuantity((double)ticketItem.getItemCount().intValue() + reportItem.getQuantity());
                reportItem.setTotal(reportItem.getTotal() + ticketItem.getSubtotalAmountWithoutModifiers());
            }
            ticket = null;
            iter.remove();
        }
        this.itemReportModel = new SalesReportModel();
        this.itemReportModel.setItems(new ArrayList<ReportItem>(itemMap.values()));
        this.itemReportModel.calculateGrandTotal();
        this.modifierReportModel = new SalesReportModel();
        this.modifierReportModel.setItems(new ArrayList<ReportItem>(modifierMap.values()));
        this.modifierReportModel.calculateGrandTotal();
    }

    public static void main(String[] args) throws Exception {
        SalesReportModelFactory factory = new SalesReportModelFactory();
        factory.createModels();
        SalesReportModel itemReportModel = factory.getItemReportModel();
        SalesReportModel modifierReportModel = factory.getModifierReportModel();
        JasperReport itemReport = ReportUtil.getReport("SalesSubReport");
        JasperReport modifierReport = ReportUtil.getReport("SalesSubReport");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemDataSource", new JRTableModelDataSource((TableModel)itemReportModel));
        map.put("modifierDataSource", new JRTableModelDataSource((TableModel)modifierReportModel));
        map.put("currencySymbol", CurrencyUtil.getCurrencySymbol());
        map.put("itemGrandTotal", itemReportModel.getGrandTotalAsString());
        map.put("modifierGrandTotal", modifierReportModel.getGrandTotalAsString());
        map.put("itemReport", itemReport);
        map.put("modifierReport", modifierReport);
        JasperReport masterReport = ReportUtil.getReport("SalesReport");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)masterReport, map, (JRDataSource)new JREmptyDataSource());
        JasperViewer.viewReport((JasperPrint)print, (boolean)false);
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isSettled() {
        return this.settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public SalesReportModel getItemReportModel() {
        return this.itemReportModel;
    }

    public SalesReportModel getModifierReportModel() {
        return this.modifierReportModel;
    }
}


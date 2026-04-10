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
 *  net.sf.jasperreports.view.JRViewer
 *  org.jdesktop.swingx.calendar.DateUtils
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.Report;
import com.floreantpos.report.ReportItem;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.SalesReportModel;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.util.CurrencyUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.swingx.calendar.DateUtils;

public class SalesReport
extends Report {
    private SalesReportModel itemReportModel;
    private SalesReportModel modifierReportModel;

    @Override
    public void refresh() throws Exception {
        this.createModels();
        JasperReport itemReport = ReportUtil.getReport("sales_sub_report");
        JasperReport modifierReport = ReportUtil.getReport("sales_sub_report");
        HashMap<String, Object> map = new HashMap<String, Object>();
        ReportUtil.populateRestaurantProperties(map);
        map.put("reportTitle", Messages.getString("SalesReport.3"));
        map.put("reportTime", ReportService.formatFullDate(new Date()));
        map.put("dateRange", ReportService.formatShortDate(this.getStartDate()) + " to " + ReportService.formatShortDate(this.getEndDate()));
        map.put("terminalName", this.getTerminal() == null ? POSConstants.ALL : this.getTerminal().getName());
        map.put("itemDataSource", new JRTableModelDataSource((TableModel)this.itemReportModel));
        map.put("modifierDataSource", new JRTableModelDataSource((TableModel)this.modifierReportModel));
        map.put("currency", Messages.getString("SalesReport.8") + CurrencyUtil.getCurrencyName() + " (" + CurrencyUtil.getCurrencySymbol() + ")");
        map.put("itemTotalQuantity", this.itemReportModel.getTotalQuantity());
        map.put("itemTotal", this.itemReportModel.getTotalAsString());
        map.put("itemGrossTotal", this.itemReportModel.getGrossTotalAsDouble());
        map.put("itemDiscountTotal", this.itemReportModel.getDiscountTotalAsString());
        map.put("itemTaxTotal", this.itemReportModel.getTaxTotalAsString());
        map.put("itemGrandTotal", this.itemReportModel.getGrandTotalAsString());
        map.put("modifierTotalQuantity", this.modifierReportModel.getTotalQuantity());
        map.put("modifierGrossTotal", this.modifierReportModel.getGrossTotalAsDouble());
        map.put("modifierTaxTotal", this.modifierReportModel.getTaxTotalAsString());
        map.put("modifierGrandTotal", this.modifierReportModel.getGrandTotalAsString());
        map.put("modifierTotal", this.modifierReportModel.getTotalAsString());
        map.put("itemReport", itemReport);
        map.put("modifierReport", modifierReport);
        JasperReport masterReport = ReportUtil.getReport("sales_report");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)masterReport, map, (JRDataSource)new JREmptyDataSource());
        this.viewer = new JRViewer(print);
    }

    @Override
    public boolean isDateRangeSupported() {
        return true;
    }

    @Override
    public boolean isTypeSupported() {
        return true;
    }

    public void createModels() {
        Date date1 = DateUtils.startOfDay((Date)this.getStartDate());
        Date date2 = DateUtils.endOfDay((Date)this.getEndDate());
        List<Ticket> tickets = TicketDAO.getInstance().findTickets(date1, date2, this.getReportType() == 0, this.getTerminal());
        HashMap<String, ReportItem> itemMap = new HashMap<String, ReportItem>();
        HashMap<String, ReportItem> modifierMap = new HashMap<String, ReportItem>();
        Iterator<Ticket> iter = tickets.iterator();
        while (iter.hasNext()) {
            Ticket t = iter.next();
            Ticket ticket = TicketDAO.getInstance().loadFullTicket(t.getId());
            List<TicketItem> ticketItems = ticket.getTicketItems();
            if (ticketItems == null) continue;
            String key = null;
            for (TicketItem ticketItem : ticketItems) {
                if (ticketItem.getUnitPrice() == 0.0 && !this.isIncludedFreeItems()) continue;
                key = ticketItem.getItemId() == null ? ticketItem.getName() : ticketItem.getItemId().toString();
                ReportItem reportItem = (ReportItem)itemMap.get(key = key + "-" + ticketItem.getName() + ticketItem.getUnitPrice() + ticketItem.getTaxRate());
                if (reportItem == null) {
                    reportItem = new ReportItem();
                    reportItem.setId(key);
                    reportItem.setUniqueId(ticketItem.getItemId().toString());
                    reportItem.setPrice(ticketItem.getUnitPrice());
                    reportItem.setName(ticketItem.getName());
                    reportItem.setTaxRate(ticketItem.getTaxRate());
                    itemMap.put(key, reportItem);
                }
                if (ticketItem.isFractionalUnit().booleanValue()) {
                    reportItem.setQuantity(ticketItem.getItemQuantity() + reportItem.getQuantity());
                } else {
                    reportItem.setQuantity((double)ticketItem.getItemCount().intValue() + reportItem.getQuantity());
                }
                reportItem.setGrossTotal(reportItem.getGrossTotal() + ticketItem.getTotalAmountWithoutModifiers());
                reportItem.setDiscount(reportItem.getDiscount() + ticketItem.getDiscountAmount());
                reportItem.setTaxTotal(reportItem.getTaxTotal() + ticketItem.getTaxAmountWithoutModifiers());
                reportItem.setTotal(reportItem.getTotal() + ticketItem.getSubtotalAmountWithoutModifiers());
                List<TicketItemModifier> modifiers = ticketItem.getTicketItemModifiers();
                if (modifiers == null) continue;
                for (TicketItemModifier modifier : modifiers) {
                    if (modifier.getUnitPrice() == 0.0 && !this.isIncludedFreeItems()) continue;
                    key = modifier.getModifierId() == null ? modifier.getName() : modifier.getModifierId().toString();
                    ReportItem modifierReportItem = (ReportItem)modifierMap.get(key = key + "-" + modifier.getName() + modifier.getModifierType() + "-" + modifier.getUnitPrice() + modifier.getTaxRate());
                    if (modifierReportItem == null) {
                        modifierReportItem = new ReportItem();
                        modifierReportItem.setId(key);
                        modifierReportItem.setUniqueId(modifier.getModifierId().toString());
                        modifierReportItem.setPrice(modifier.getUnitPrice());
                        modifierReportItem.setName(modifier.getName());
                        modifierReportItem.setTaxRate(modifier.getTaxRate());
                        modifierMap.put(key, modifierReportItem);
                    }
                    modifierReportItem.setQuantity(modifierReportItem.getQuantity() + (double)modifier.getItemCount().intValue());
                    modifierReportItem.setGrossTotal(modifierReportItem.getGrossTotal() + modifier.getTotalAmount());
                    modifierReportItem.setTaxTotal(modifierReportItem.getTaxTotal() + modifier.getTaxAmount());
                    modifierReportItem.setTotal(modifierReportItem.getTotal() + modifier.getSubTotalAmount());
                }
            }
            ticket = null;
            iter.remove();
        }
        this.itemReportModel = new SalesReportModel();
        ArrayList<ReportItem> itemList = new ArrayList<ReportItem>(itemMap.values());
        Collections.sort(itemList, new Comparator<ReportItem>(){

            @Override
            public int compare(ReportItem o1, ReportItem o2) {
                return Integer.parseInt(o1.getUniqueId()) - Integer.parseInt(o2.getUniqueId());
            }
        });
        this.itemReportModel.setItems(itemList);
        this.itemReportModel.calculateTotalQuantity();
        this.itemReportModel.calculateDiscountTotal();
        this.itemReportModel.calculateGrossTotal();
        this.itemReportModel.calculateTaxTotal();
        this.itemReportModel.calculateGrandTotal();
        this.itemReportModel.calculateTotal();
        this.modifierReportModel = new SalesReportModel();
        ArrayList<ReportItem> modifierList = new ArrayList<ReportItem>(modifierMap.values());
        Collections.sort(modifierList, new Comparator<ReportItem>(){

            @Override
            public int compare(ReportItem o1, ReportItem o2) {
                return Integer.parseInt(o1.getUniqueId()) - Integer.parseInt(o2.getUniqueId());
            }
        });
        this.modifierReportModel.setItems(modifierList);
        this.modifierReportModel.calculateTotalQuantity();
        this.modifierReportModel.calculateGrossTotal();
        this.modifierReportModel.calculateTaxTotal();
        this.modifierReportModel.calculateGrandTotal();
        this.modifierReportModel.calculateTotal();
    }
}


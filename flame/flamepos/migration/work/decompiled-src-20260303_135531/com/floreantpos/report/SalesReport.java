/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.dao.GenericDAO;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.util.CurrencyUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jdesktop.swingx.calendar.DateUtils;

public class SalesReport extends Report {
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
        boolean drawerResetted = this.getReportType() == 0;
        HashMap<String, ReportItem> itemMap = new HashMap<String, ReportItem>();
        HashMap<String, ReportItem> modifierMap = new HashMap<String, ReportItem>();
        GenericDAO dao = new GenericDAO();
        Session session = null;
        try {
            session = dao.getSession();
            Criteria criteria = session.createCriteria(TicketItem.class, "item");
            criteria.createCriteria(TicketItem.PROP_TICKET, "t");
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.groupProperty("item." + TicketItem.PROP_ITEM_ID));
            projectionList.add(Projections.groupProperty("item." + TicketItem.PROP_NAME));
            projectionList.add(Projections.groupProperty("item." + TicketItem.PROP_UNIT_PRICE));
            projectionList.add(Projections.groupProperty("item." + TicketItem.PROP_TAX_RATE));
            projectionList.add(Projections.groupProperty("item.fractionalUnit"));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_ITEM_COUNT));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_ITEM_QUANTITY));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_SUBTOTAL_AMOUNT_WITHOUT_MODIFIERS));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_TOTAL_AMOUNT_WITHOUT_MODIFIERS));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_DISCOUNT_AMOUNT));
            projectionList.add(Projections.sum("item." + TicketItem.PROP_TAX_AMOUNT_WITHOUT_MODIFIERS));
            criteria.setProjection(projectionList);
            criteria.add(Restrictions.ge("t." + Ticket.PROP_CREATE_DATE, (Object)date1));
            criteria.add(Restrictions.le("t." + Ticket.PROP_CREATE_DATE, (Object)date2));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.valueOf(drawerResetted)));
            if (this.getTerminal() != null) {
                criteria.add(Restrictions.eq("t." + Ticket.PROP_TERMINAL, (Object)this.getTerminal()));
            }
            if (!this.isIncludedFreeItems()) {
                criteria.add(Restrictions.ne("item." + TicketItem.PROP_UNIT_PRICE, (Object)Double.valueOf(0.0)));
            }
            List rows = criteria.list();
            for (Object rowObject : rows) {
                Object[] row = (Object[])rowObject;
                Integer itemId = (Integer)row[0];
                String name = (String)row[1];
                double unitPrice = SalesReport.numberValue(row[2]);
                double taxRate = SalesReport.numberValue(row[3]);
                boolean fractionalUnit = row[4] != null && ((Boolean)row[4]).booleanValue();
                double itemCount = SalesReport.numberValue(row[5]);
                double itemQuantity = SalesReport.numberValue(row[6]);
                double subtotal = SalesReport.numberValue(row[7]);
                double grossTotal = SalesReport.numberValue(row[8]);
                double discount = SalesReport.numberValue(row[9]);
                double taxTotal = SalesReport.numberValue(row[10]);
                String uniqueId = itemId == null ? "0" : itemId.toString();
                String key = uniqueId + "-" + name + unitPrice + taxRate;
                ReportItem reportItem = new ReportItem();
                reportItem.setId(key);
                reportItem.setUniqueId(uniqueId);
                reportItem.setPrice(unitPrice);
                reportItem.setName(name);
                reportItem.setTaxRate(taxRate);
                reportItem.setQuantity(fractionalUnit ? itemQuantity : itemCount);
                reportItem.setGrossTotal(grossTotal);
                reportItem.setDiscount(discount);
                reportItem.setTaxTotal(taxTotal);
                reportItem.setTotal(subtotal);
                itemMap.put(key, reportItem);
            }
            criteria = session.createCriteria(TicketItemModifier.class, "modifier");
            criteria.createCriteria(TicketItemModifier.PROP_TICKET_ITEM, "item");
            criteria.createCriteria("item." + TicketItem.PROP_TICKET, "t");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.groupProperty("modifier." + TicketItemModifier.PROP_MODIFIER_ID));
            projectionList.add(Projections.groupProperty("modifier." + TicketItemModifier.PROP_NAME));
            projectionList.add(Projections.groupProperty("modifier." + TicketItemModifier.PROP_MODIFIER_TYPE));
            projectionList.add(Projections.groupProperty("modifier." + TicketItemModifier.PROP_UNIT_PRICE));
            projectionList.add(Projections.groupProperty("modifier." + TicketItemModifier.PROP_TAX_RATE));
            projectionList.add(Projections.sum("modifier." + TicketItemModifier.PROP_ITEM_COUNT));
            projectionList.add(Projections.sum("modifier." + TicketItemModifier.PROP_SUB_TOTAL_AMOUNT));
            projectionList.add(Projections.sum("modifier." + TicketItemModifier.PROP_TOTAL_AMOUNT));
            projectionList.add(Projections.sum("modifier." + TicketItemModifier.PROP_TAX_AMOUNT));
            criteria.setProjection(projectionList);
            criteria.add(Restrictions.ge("t." + Ticket.PROP_CREATE_DATE, (Object)date1));
            criteria.add(Restrictions.le("t." + Ticket.PROP_CREATE_DATE, (Object)date2));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add(Restrictions.eq("t." + Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.valueOf(drawerResetted)));
            if (this.getTerminal() != null) {
                criteria.add(Restrictions.eq("t." + Ticket.PROP_TERMINAL, (Object)this.getTerminal()));
            }
            if (!this.isIncludedFreeItems()) {
                criteria.add(Restrictions.ne("modifier." + TicketItemModifier.PROP_UNIT_PRICE, (Object)Double.valueOf(0.0)));
            }
            rows = criteria.list();
            for (Object rowObject : rows) {
                Object[] row = (Object[])rowObject;
                Integer modifierId = (Integer)row[0];
                String name = (String)row[1];
                Integer modifierType = (Integer)row[2];
                double unitPrice = SalesReport.numberValue(row[3]);
                double taxRate = SalesReport.numberValue(row[4]);
                double itemCount = SalesReport.numberValue(row[5]);
                double subtotal = SalesReport.numberValue(row[6]);
                double grossTotal = SalesReport.numberValue(row[7]);
                double taxTotal = SalesReport.numberValue(row[8]);
                String uniqueId = modifierId == null ? "0" : modifierId.toString();
                String key = uniqueId + "-" + name + (modifierType == null ? 0 : modifierType.intValue()) + "-" + unitPrice + taxRate;
                ReportItem reportItem = new ReportItem();
                reportItem.setId(key);
                reportItem.setUniqueId(uniqueId);
                reportItem.setPrice(unitPrice);
                reportItem.setName(name);
                reportItem.setTaxRate(taxRate);
                reportItem.setQuantity(itemCount);
                reportItem.setGrossTotal(grossTotal);
                reportItem.setTaxTotal(taxTotal);
                reportItem.setTotal(subtotal);
                modifierMap.put(key, reportItem);
            }
        }
        finally {
            dao.closeSession(session);
        }
        this.itemReportModel = new SalesReportModel();
        ArrayList<ReportItem> itemList = new ArrayList<ReportItem>(itemMap.values());
        Collections.sort(itemList, new Comparator<ReportItem>() {

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
        Collections.sort(modifierList, new Comparator<ReportItem>() {

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

    private static double numberValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        return ((Number)value).doubleValue();
    }
}

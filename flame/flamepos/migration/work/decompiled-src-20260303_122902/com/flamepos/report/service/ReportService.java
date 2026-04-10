/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Projection
 *  org.hibernate.criterion.ProjectionList
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.report.service;

import com.floreantpos.POSConstants;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.model.Discount;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.PayOutTransaction;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.model.dao.GenericDAO;
import com.floreantpos.report.JournalReportModel;
import com.floreantpos.report.MenuUsageReport;
import com.floreantpos.report.SalesBalanceReport;
import com.floreantpos.report.SalesDetailedReport;
import com.floreantpos.report.SalesExceptionReport;
import com.floreantpos.report.ServerProductivityReport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ReportService {
    private static SimpleDateFormat fullDateFormatter = new SimpleDateFormat("MMM dd yyyy, hh:mm a");
    private static SimpleDateFormat shortDateFormatter = new SimpleDateFormat("MMM dd yyyy ");

    public static String formatFullDate(Date date) {
        return fullDateFormatter.format(date);
    }

    public static String formatShortDate(Date date) {
        return shortDateFormatter.format(date);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MenuUsageReport getMenuUsageReport(Date fromDate, Date toDate) {
        GenericDAO dao = new GenericDAO();
        MenuUsageReport report = new MenuUsageReport();
        try (Session session = null;){
            session = dao.getSession();
            Criteria criteria = session.createCriteria(MenuCategory.class);
            List categories = criteria.list();
            MenuCategory miscCategory = new MenuCategory();
            miscCategory.setName(POSConstants.MISC_BUTTON_TEXT);
            categories.add(miscCategory);
            for (MenuCategory category : categories) {
                criteria = session.createCriteria(TicketItem.class, "item");
                criteria.createCriteria("ticket", "t");
                ProjectionList projectionList = Projections.projectionList();
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
                criteria.setProjection((Projection)projectionList);
                criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_CATEGORY_NAME), (Object)category.getName()));
                criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_CREATE_DATE), (Object)fromDate));
                criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_CREATE_DATE), (Object)toDate));
                criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_PAID), (Object)Boolean.TRUE));
                List datas = criteria.list();
                if (datas.size() <= 0) continue;
                Object[] objects = (Object[])datas.get(0);
                MenuUsageReport.MenuUsageReportData data = new MenuUsageReport.MenuUsageReportData();
                data.setCategoryName(category.getName());
                if (objects.length > 0 && objects[0] != null) {
                    data.setCount(((Number)objects[0]).intValue());
                }
                if (objects.length > 1 && objects[1] != null) {
                    data.setGrossSales(((Number)objects[1]).doubleValue());
                }
                if (objects.length > 2 && objects[2] != null) {
                    data.setDiscount(((Number)objects[2]).doubleValue());
                }
                data.calculate();
                report.addReportData(data);
            }
            MenuUsageReport menuUsageReport = report;
            return menuUsageReport;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerProductivityReport getServerProductivityReport(Date fromDate, Date toDate) {
        GenericDAO dao = new GenericDAO();
        ServerProductivityReport report = new ServerProductivityReport();
        try (Session session = null;){
            session = dao.getSession();
            Criteria criteria = session.createCriteria(User.class);
            List servers = criteria.list();
            criteria = session.createCriteria(MenuCategory.class);
            List categories = criteria.list();
            MenuCategory miscCategory = new MenuCategory();
            miscCategory.setName(POSConstants.MISC_BUTTON_TEXT);
            categories.add(miscCategory);
            for (User server : servers) {
                ServerProductivityReport.ServerProductivityReportData data = new ServerProductivityReport.ServerProductivityReportData();
                data.setServerName(server.getUserId() + "/" + server.toString());
                criteria = session.createCriteria(Ticket.class);
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)server));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.TRUE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
                ProjectionList projectionList = Projections.projectionList();
                projectionList.add(Projections.rowCount());
                projectionList.add((Projection)Projections.sum((String)Ticket.PROP_NUMBER_OF_GUESTS));
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_TOTAL_AMOUNT));
                criteria.setProjection((Projection)projectionList);
                Object[] o = (Object[])criteria.uniqueResult();
                int totalCheckCount = 0;
                double totalServerSale = 0.0;
                if (o != null) {
                    int i;
                    if (o.length > 0 && o[0] != null) {
                        totalCheckCount = i = ((Number)o[0]).intValue();
                        data.setTotalCheckCount(totalCheckCount);
                    }
                    if (o.length > 1 && o[1] != null) {
                        i = ((Number)o[1]).intValue();
                        data.setTotalGuestCount(i);
                    }
                    if (o.length > 2 && o[2] != null) {
                        totalServerSale = ((Number)o[2]).doubleValue();
                        data.setTotalSales(totalServerSale);
                    }
                }
                data.calculate();
                report.addReportData(data);
                for (MenuCategory category : categories) {
                    double d;
                    data = new ServerProductivityReport.ServerProductivityReportData();
                    data.setServerName(server.getUserId() + "/" + server.toString());
                    criteria = session.createCriteria(TicketItem.class, "item");
                    criteria.createCriteria(TicketItem.PROP_TICKET, "t");
                    projectionList = Projections.projectionList();
                    criteria.setProjection((Projection)projectionList);
                    projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
                    projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
                    projectionList.add((Projection)Projections.sum((String)("t." + Ticket.PROP_DISCOUNT_AMOUNT)));
                    projectionList.add(Projections.rowCount());
                    criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_CATEGORY_NAME), (Object)category.getName()));
                    criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_CREATE_DATE), (Object)fromDate));
                    criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_CREATE_DATE), (Object)toDate));
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_OWNER), (Object)server));
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_PAID), (Object)Boolean.TRUE));
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_VOIDED), (Object)Boolean.FALSE));
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_REFUNDED), (Object)Boolean.FALSE));
                    List datas = criteria.list();
                    if (datas.size() <= 0) continue;
                    Object[] objects = (Object[])datas.get(0);
                    data.setCategoryName(category.getName());
                    data.setTotalCheckCount(totalCheckCount);
                    if (objects.length > 0 && objects[0] != null) {
                        int i = ((Number)objects[0]).intValue();
                        data.setCheckCount(i);
                    }
                    if (objects.length > 1 && objects[1] != null) {
                        double d2 = ((Number)objects[1]).doubleValue();
                        data.setGrossSales(d2);
                    }
                    if (objects.length > 2 && objects[2] != null && (d = ((Number)objects[2]).doubleValue()) > 0.0) {
                        data.setSalesDiscount(d);
                    }
                    data.setAllocation(data.getGrossSales() / totalServerSale * 100.0);
                    data.calculate();
                    report.addReportData(data);
                }
            }
            ServerProductivityReport serverProductivityReport = report;
            return serverProductivityReport;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JournalReportModel getJournalReport(Date fromDate, Date toDate) {
        GenericDAO dao = new GenericDAO();
        JournalReportModel report = new JournalReportModel();
        Session session = null;
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportTime(new Date());
        try {
            session = dao.getSession();
            Criteria criteria = session.createCriteria(ActionHistory.class);
            criteria.add((Criterion)Restrictions.ge((String)ActionHistory.PROP_ACTION_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)ActionHistory.PROP_ACTION_TIME, (Object)toDate));
            List list = criteria.list();
            for (ActionHistory history : list) {
                JournalReportModel.JournalReportData data = new JournalReportModel.JournalReportData();
                data.setRefId(history.getId());
                data.setAction(history.getActionName());
                data.setUserInfo(history.getPerformer().getUserId() + "/" + history.getPerformer());
                data.setTime(history.getActionTime());
                data.setComments(history.getDescription());
                report.addReportData(data);
            }
            JournalReportModel journalReportModel = report;
            return journalReportModel;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SalesBalanceReport getSalesBalanceReport(Date fromDate, Date toDate, User user) {
        GenericDAO dao = new GenericDAO();
        SalesBalanceReport report = new SalesBalanceReport();
        Session session = null;
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportTime(new Date());
        try {
            session = dao.getSession();
            report.setGrossTaxableSalesAmount(this.calculateGrossSales(session, fromDate, toDate, user, true));
            report.setGrossNonTaxableSalesAmount(this.calculateGrossSales(session, fromDate, toDate, user, false));
            report.setDiscountAmount(this.calculateDiscount(session, fromDate, toDate, user));
            report.setSalesTaxAmount(this.calculateTax(session, fromDate, toDate, user));
            report.setChargedTipsAmount(this.calculateTips(session, fromDate, toDate, user));
            report.setCashReceiptsAmount(this.calculateCreditReceipt(session, CashTransaction.class, fromDate, toDate, user));
            report.setCreditCardReceiptsAmount(this.calculateCreditReceipt(session, CreditCardTransaction.class, fromDate, toDate, user));
            report.setGrossTipsPaidAmount(this.calculateTipsPaid(session, fromDate, toDate, user));
            report.setCashPayoutAmount(this.calculateCashPayout(session, fromDate, toDate, user));
            this.calculateDrawerPullAmount(session, report, fromDate, toDate, user);
            report.setVisaCreditCardAmount(this.calculateVisaCreditCardSummery(session, CreditCardTransaction.class, fromDate, toDate, user));
            report.setMasterCardAmount(this.calculateMasterCardSummery(session, CreditCardTransaction.class, fromDate, toDate, user));
            report.setAmexAmount(this.calculateAmexSummery(session, CreditCardTransaction.class, fromDate, toDate, user));
            report.setDiscoveryAmount(this.calculateDiscoverySummery(session, CreditCardTransaction.class, fromDate, toDate, user));
            report.calculate();
            SalesBalanceReport salesBalanceReport = report;
            return salesBalanceReport;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void calculateDrawerPullAmount(Session session, SalesBalanceReport report, Date fromDate, Date toDate, User user) {
        double amount;
        Criteria criteria = session.createCriteria(DrawerPullReport.class);
        criteria.add((Criterion)Restrictions.ge((String)DrawerPullReport.PROP_REPORT_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)DrawerPullReport.PROP_REPORT_TIME, (Object)toDate));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)DrawerPullReport.PROP_ASSIGNED_USER, (Object)user));
        }
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add((Projection)Projections.sum((String)DrawerPullReport.PROP_DRAWER_ACCOUNTABLE));
        projectionList.add((Projection)Projections.sum((String)DrawerPullReport.PROP_BEGIN_CASH));
        criteria.setProjection((Projection)projectionList);
        Object[] o = (Object[])criteria.uniqueResult();
        if (o.length > 0 && o[0] instanceof Number) {
            amount = ((Number)o[0]).doubleValue();
            report.setDrawerPullsAmount(amount);
        }
        if (o.length > 1 && o[1] instanceof Number) {
            amount = ((Number)o[1]).doubleValue();
            report.setDrawerPullsAmount(report.getDrawerPullsAmount() - amount);
        }
    }

    private double calculateCashPayout(Session session, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(PayOutTransaction.class);
        criteria.add((Criterion)Restrictions.ge((String)PayOutTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PayOutTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PayOutTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)PayOutTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateTipsPaid(Session session, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.createAlias(Ticket.PROP_GRATUITY, "gratuity");
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)("gratuity." + Gratuity.PROP_PAID), (Object)Boolean.TRUE));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)("gratuity." + Gratuity.PROP_AMOUNT)));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateCreditReceipt(Session session, Class transactionClass, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(transactionClass);
        criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateCashReceipt(Session session, Date fromDate, Date toDate) {
        Criteria criteria = session.createCriteria(CashTransaction.class);
        criteria.add((Criterion)Restrictions.ge((String)CashTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)CashTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)CashTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateCreditCardReceipt(Session session, Date fromDate, Date toDate) {
        Criteria criteria = session.createCriteria(CashTransaction.class);
        criteria.add((Criterion)Restrictions.ge((String)CreditCardTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)CreditCardTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)CreditCardTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateGiftCertSoldAmount(Session session, Date fromDate, Date toDate) {
        Criteria criteria = session.createCriteria(GiftCertificateTransaction.class);
        criteria.add((Criterion)Restrictions.ge((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)GiftCertificateTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.setProjection((Projection)Projections.sum((String)GiftCertificateTransaction.PROP_GIFT_CERT_FACE_VALUE));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateTips(Session session, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.createAlias(Ticket.PROP_GRATUITY, "g");
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)("g." + Gratuity.PROP_AMOUNT)));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateDiscount(Session session, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)Ticket.PROP_DISCOUNT_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double getDoubleAmount(Object result) {
        if (result != null && result instanceof Number) {
            return ((Number)result).doubleValue();
        }
        return 0.0;
    }

    private double calculateTax(Session session, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)Ticket.PROP_TAX_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateGrossSales(Session session, Date fromDate, Date toDate, User user, boolean taxableSales) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
        }
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TAX_EXEMPT, (Object)(!taxableSales ? 1 : 0)));
        criteria.setProjection((Projection)Projections.sum((String)Ticket.PROP_SUBTOTAL_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SalesExceptionReport getSalesExceptionReport(Date fromDate, Date toDate) {
        GenericDAO dao = new GenericDAO();
        SalesExceptionReport report = new SalesExceptionReport();
        Session session = null;
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportTime(new Date());
        try {
            session = dao.getSession();
            Criteria criteria = session.createCriteria(Ticket.class);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.TRUE));
            List list = criteria.list();
            for (Ticket ticket : list) {
                report.addVoidToVoidData(ticket);
            }
            criteria = session.createCriteria(Ticket.class);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            list = criteria.list();
            for (Ticket ticket : list) {
                report.addDiscountData(ticket);
            }
            DiscountDAO discountDAO = new DiscountDAO();
            List<Discount> availableCoupons = discountDAO.getValidCoupons();
            report.addEmptyDiscounts(availableCoupons);
            SalesExceptionReport salesExceptionReport = report;
            return salesExceptionReport;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SalesDetailedReport getSalesDetailedReport(Date fromDate, Date toDate) {
        GenericDAO dao = new GenericDAO();
        SalesDetailedReport report = new SalesDetailedReport();
        Session session = null;
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        report.setReportTime(new Date());
        try {
            session = dao.getSession();
            Criteria criteria = session.createCriteria(DrawerPullReport.class);
            criteria.add((Criterion)Restrictions.ge((String)DrawerPullReport.PROP_REPORT_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)DrawerPullReport.PROP_REPORT_TIME, (Object)toDate));
            List list = criteria.list();
            for (DrawerPullReport drawerPullReport : list) {
                SalesDetailedReport.DrawerPullData data = new SalesDetailedReport.DrawerPullData();
                data.setDrawerPullId(drawerPullReport.getId());
                data.setTicketCount(drawerPullReport.getTicketCount());
                data.setIdealAmount(drawerPullReport.getDrawerAccountable());
                data.setActualAmount(drawerPullReport.getCashToDeposit());
                data.setVarinceAmount(drawerPullReport.getDrawerAccountable() - drawerPullReport.getCashToDeposit());
                report.addDrawerPullData(data);
            }
            criteria = session.createCriteria(CreditCardTransaction.class);
            criteria.add((Criterion)Restrictions.ge((String)CreditCardTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)CreditCardTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
            list = criteria.list();
            for (PosTransaction t : list) {
                report.addCreditCardData((CreditCardTransaction)t);
            }
            criteria = session.createCriteria(DebitCardTransaction.class);
            criteria.add((Criterion)Restrictions.ge((String)DebitCardTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)DebitCardTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
            list = criteria.list();
            for (PosTransaction t : list) {
                report.addCreditCardData((DebitCardTransaction)t);
            }
            criteria = session.createCriteria(GiftCertificateTransaction.class);
            criteria.add((Criterion)Restrictions.ge((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)GiftCertificateTransaction.PROP_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            Object[] object = (Object[])criteria.uniqueResult();
            if (object != null && object.length > 0 && object[0] instanceof Number) {
                report.setGiftCertReturnCount(((Number)object[0]).intValue());
            }
            if (object != null && object.length > 1 && object[1] instanceof Number) {
                report.setGiftCertReturnAmount(((Number)object[1]).doubleValue());
            }
            criteria = session.createCriteria(GiftCertificateTransaction.class);
            criteria.add((Criterion)Restrictions.ge((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)GiftCertificateTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
            criteria.add((Criterion)Restrictions.gt((String)GiftCertificateTransaction.PROP_GIFT_CERT_CASH_BACK_AMOUNT, (Object)0.0));
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)GiftCertificateTransaction.PROP_GIFT_CERT_CASH_BACK_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            object = (Object[])criteria.uniqueResult();
            if (object != null && object.length > 0 && object[0] instanceof Number) {
                report.setGiftCertChangeCount(((Number)object[0]).intValue());
            }
            if (object != null && object.length > 1 && object[1] instanceof Number) {
                report.setGiftCertChangeAmount(((Number)object[1]).doubleValue());
            }
            criteria = session.createCriteria(Ticket.class);
            criteria.createAlias(Ticket.PROP_GRATUITY, "g");
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
            criteria.add((Criterion)Restrictions.gt((String)("g." + Gratuity.PROP_AMOUNT), (Object)0.0));
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)("g." + Gratuity.PROP_AMOUNT)));
            criteria.setProjection((Projection)projectionList);
            object = (Object[])criteria.uniqueResult();
            if (object != null && object.length > 0 && object[0] instanceof Number) {
                report.setTipsCount(((Number)object[0]).intValue());
            }
            if (object != null && object.length > 1 && object[1] instanceof Number) {
                report.setChargedTips(((Number)object[1]).doubleValue());
            }
            criteria = session.createCriteria(Ticket.class);
            criteria.createAlias(Ticket.PROP_GRATUITY, "g");
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
            criteria.add((Criterion)Restrictions.gt((String)("g." + Gratuity.PROP_AMOUNT), (Object)0.0));
            criteria.add((Criterion)Restrictions.gt((String)("g." + Gratuity.PROP_PAID), (Object)Boolean.TRUE));
            projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.sum((String)("g." + Gratuity.PROP_AMOUNT)));
            criteria.setProjection((Projection)projectionList);
            object = (Object[])criteria.uniqueResult();
            if (object != null && object.length > 0 && object[0] instanceof Number) {
                report.setTipsPaid(((Number)object[0]).doubleValue());
            }
            SalesDetailedReport salesDetailedReport = report;
            return salesDetailedReport;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private double calculateVisaCreditCardSummery(Session session, Class transactionClass, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(transactionClass);
        criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CARD_TYPE, (Object)PaymentType.CREDIT_VISA.getDisplayString()));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateMasterCardSummery(Session session, Class transactionClass, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(transactionClass);
        criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CARD_TYPE, (Object)PaymentType.CREDIT_MASTER_CARD.getDisplayString()));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateAmexSummery(Session session, Class transactionClass, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(transactionClass);
        criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CARD_TYPE, (Object)PaymentType.CREDIT_AMEX.getDisplayString()));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }

    private double calculateDiscoverySummery(Session session, Class transactionClass, Date fromDate, Date toDate, User user) {
        Criteria criteria = session.createCriteria(transactionClass);
        criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)fromDate));
        criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)toDate));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
        criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CARD_TYPE, (Object)PaymentType.CREDIT_DISCOVERY.getDisplayString()));
        if (user != null) {
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)user));
        }
        criteria.setProjection((Projection)Projections.sum((String)CashTransaction.PROP_AMOUNT));
        return this.getDoubleAmount(criteria.uniqueResult());
    }
}


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
package com.floreantpos.model.dao;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao._RootDAO;
import com.floreantpos.report.SalesAnalysisReportModel;
import com.floreantpos.report.SalesStatistics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class SalesSummaryDAO
extends _RootDAO {
    @Override
    protected Class getReferenceClass() {
        return null;
    }

    @Override
    public Serializable save(Object obj) {
        return super.save(obj);
    }

    @Override
    public void saveOrUpdate(Object obj) {
        super.saveOrUpdate(obj);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<SalesAnalysisReportModel.SalesAnalysisData> findSalesAnalysis(Date start, Date end, UserType userType, Terminal terminal) {
        Session session = null;
        try {
            ArrayList<SalesAnalysisReportModel.SalesAnalysisData> list = new ArrayList<SalesAnalysisReportModel.SalesAnalysisData>();
            session = this.getSession();
            Criteria criteria = session.createCriteria(Shift.class);
            List<Shift> shifts = criteria.list();
            criteria = session.createCriteria(MenuCategory.class);
            List<MenuCategory> categories = criteria.list();
            MenuCategory miscCategory = new MenuCategory();
            miscCategory.setName(Messages.getString("SalesSummaryDAO.0"));
            categories.add(miscCategory);
            this.addSalesAnalysisTotals(list, session, start, end, userType, terminal, Boolean.FALSE, Messages.getString("SalesSummaryDAO.1"));
            this.addSalesAnalysisTotals(list, session, start, end, userType, terminal, Boolean.TRUE, Messages.getString("SalesSummaryDAO.2"));
            criteria = session.createCriteria(TicketItem.class, "item");
            criteria.createCriteria("ticket", "t");
            criteria.createCriteria("t.owner", "u");
            criteria.createCriteria("t.shift", "shift");
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.groupProperty((String)"shift.id"));
            projectionList.add((Projection)Projections.groupProperty((String)("item." + TicketItem.PROP_CATEGORY_NAME)));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            this.applyTicketItemFilters(criteria, start, end, userType, terminal);
            List groupedShiftRows = criteria.list();
            HashMap<String, Object[]> shiftCategorySummaryMap = new HashMap<String, Object[]>();
            for (Object groupedShiftRow : groupedShiftRows) {
                Object[] summaryRow = (Object[])groupedShiftRow;
                shiftCategorySummaryMap.put(this.buildSalesSummaryKey((Integer)summaryRow[0], (String)summaryRow[1]), summaryRow);
            }
            criteria = session.createCriteria(TicketItem.class, "item");
            criteria.createCriteria("ticket", "t");
            criteria.createCriteria("t.owner", "u");
            projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.groupProperty((String)("item." + TicketItem.PROP_CATEGORY_NAME)));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            this.applyTicketItemFilters(criteria, start, end, userType, terminal);
            List groupedAllDayRows = criteria.list();
            HashMap<String, Object[]> allDaySummaryMap = new HashMap<String, Object[]>();
            for (Object groupedAllDayRow : groupedAllDayRows) {
                Object[] summaryRow = (Object[])groupedAllDayRow;
                allDaySummaryMap.put((String)summaryRow[0], summaryRow);
            }
            for (Shift shift : shifts) {
                for (MenuCategory category : categories) {
                    Object[] summaryRow = shiftCategorySummaryMap.get(this.buildSalesSummaryKey(shift.getId(), category.getName()));
                    list.add(this.createSalesAnalysisData(shift.getName(), category.getName(), summaryRow == null ? null : summaryRow[2], summaryRow == null ? null : summaryRow[3], summaryRow == null ? null : summaryRow[4]));
                }
            }
            for (MenuCategory category : categories) {
                Object[] summaryRow = allDaySummaryMap.get(category.getName());
                list.add(this.createSalesAnalysisData("ALL DAY", category.getName(), summaryRow == null ? null : summaryRow[1], summaryRow == null ? null : summaryRow[2], summaryRow == null ? null : summaryRow[3]));
            }
            ArrayList<SalesAnalysisReportModel.SalesAnalysisData> arrayList = list;
            return arrayList;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SalesStatistics findKeyStatistics(Date start, Date end, UserType userType, Terminal terminal) {
        Session session = null;
        try {
            Object[] objects;
            List list;
            SalesStatistics salesSummary = new SalesStatistics();
            session = this.getSession();
            Restaurant restaurant = (Restaurant)this.get(Restaurant.class, new Integer(1), session);
            if (restaurant != null) {
                salesSummary.setCapacity(restaurant.getCapacity() != null ? restaurant.getCapacity() : 0);
                salesSummary.setTables(restaurant.getTables() != null ? restaurant.getTables() : 0);
            }
            Criteria criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_SUBTOTAL_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_DISCOUNT_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TAX_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setGrossSale(((Number)objects[1]).doubleValue());
                }
                if (objects.length > 2 && objects[2] != null) {
                    salesSummary.setDiscount(((Number)objects[2]).intValue());
                }
                if (objects.length > 3 && objects[3] != null) {
                    salesSummary.setTax(((Number)objects[3]).intValue());
                }
            }
            criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_NUMBER_OF_GUESTS));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                salesSummary.setCheckCount(((Number)objects[0]).intValue());
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setGuestCount(((Number)objects[1]).intValue());
                }
            }
            criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                salesSummary.setOpenChecks(((Number)objects[0]).intValue());
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setOpenAmount(((Number)objects[1]).doubleValue());
                }
            }
            criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.TRUE));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                salesSummary.setVoidChecks(((Number)objects[0]).intValue());
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setVoidAmount(((Number)objects[1]).doubleValue());
                }
            }
            criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TAX_EXEMPT, (Object)Boolean.TRUE));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                salesSummary.setNtaxChecks(((Number)objects[0]).intValue());
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setNtaxAmount(((Number)objects[1]).doubleValue());
                }
            }
            criteria = session.createCriteria(Ticket.class, "ticket");
            criteria.createCriteria(Ticket.PROP_OWNER, "u");
            projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_RE_OPENED, (Object)Boolean.TRUE));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            if ((list = criteria.list()).size() > 0) {
                objects = (Object[])list.get(0);
                salesSummary.setRopnChecks(((Number)objects[0]).intValue());
                if (objects.length > 1 && objects[1] != null) {
                    salesSummary.setRopnAmount(((Number)objects[1]).doubleValue());
                }
            }
            criteria = session.createCriteria(AttendenceHistory.class, "history");
            criteria.createCriteria(AttendenceHistory.PROP_USER, "u");
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)end));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_TERMINAL, (Object)terminal));
            }
            List list2 = criteria.list();
            double laborHours = 0.0;
            double laborCost = 0.0;
            for (Object object : list2) {
                AttendenceHistory attendenceHistory = (AttendenceHistory)object;
                double laborHourInMillisecond = 0.0;
                if (!attendenceHistory.isClockedOut().booleanValue() || attendenceHistory.getClockOutTime() == null) {
                    Shift attendenceShift = attendenceHistory.getShift();
                    laborHourInMillisecond = Math.abs(end.getTime() - attendenceHistory.getClockInTime().getTime());
                    if (laborHourInMillisecond > (double)attendenceShift.getShiftLength().longValue()) {
                        laborHourInMillisecond = attendenceShift.getShiftLength().longValue();
                    }
                } else {
                    laborHourInMillisecond = Math.abs(attendenceHistory.getClockInTime().getTime() - attendenceHistory.getClockInTime().getTime());
                }
                double hour = laborHourInMillisecond * (2.77777778 * Math.pow(10.0, -7.0));
                laborHours += hour;
                laborCost += hour * (attendenceHistory.getUser().getCostPerHour() == null ? 0.0 : attendenceHistory.getUser().getCostPerHour());
            }
            salesSummary.setLaborHour(laborHours);
            salesSummary.setLaborCost(laborCost);
            this.addProfitCenterSummaries(start, end, userType, terminal, session, salesSummary);
            salesSummary.calculateOthers();
            SalesStatistics salesStatistics = salesSummary;
            return salesStatistics;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    private void addSalesAnalysisTotals(List<SalesAnalysisReportModel.SalesAnalysisData> list, Session session, Date start, Date end, UserType userType, Terminal terminal, Boolean beverage, String categoryName) {
        Criteria criteria = session.createCriteria(TicketItem.class, "item");
        criteria.createCriteria("ticket", "t");
        criteria.createCriteria("t.owner", "u");
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
        projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
        projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
        criteria.setProjection((Projection)projectionList);
        criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_BEVERAGE), (Object)beverage));
        this.applyTicketItemFilters(criteria, start, end, userType, terminal);
        Object[] summaryRow = (Object[])criteria.uniqueResult();
        list.add(this.createSalesAnalysisData("", categoryName, summaryRow == null ? null : summaryRow[0], summaryRow == null ? null : summaryRow[1], summaryRow == null ? null : summaryRow[2]));
    }

    private void applyTicketItemFilters(Criteria criteria, Date start, Date end, UserType userType, Terminal terminal) {
        criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)start));
        criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)end));
        if (userType != null) {
            criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
        }
        if (terminal != null) {
            criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_TERMINAL), (Object)terminal));
        }
    }

    private SalesAnalysisReportModel.SalesAnalysisData createSalesAnalysisData(String shiftName, String categoryName, Object countValue, Object grossValue, Object discountValue) {
        SalesAnalysisReportModel.SalesAnalysisData data = new SalesAnalysisReportModel.SalesAnalysisData();
        data.setShiftName(shiftName);
        data.setCategoryName(categoryName);
        data.setCount(this.toInt(countValue));
        data.setGross(this.toDouble(grossValue));
        data.setDiscount(this.toDouble(discountValue));
        data.calculate();
        return data;
    }

    private String buildSalesSummaryKey(Integer shiftId, String categoryName) {
        return String.valueOf(shiftId) + "|" + String.valueOf(categoryName);
    }

    private void addProfitCenterSummaries(Date start, Date end, UserType userType, Terminal terminal, Session session, SalesStatistics salesSummary) {
        Criteria criteria = session.createCriteria(Ticket.class, "ticket");
        criteria.createCriteria(Ticket.PROP_OWNER, "u");
        criteria.createCriteria(Ticket.PROP_SHIFT, "shift");
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add((Projection)Projections.groupProperty((String)"shift.id"));
        projectionList.add((Projection)Projections.groupProperty((String)("ticket." + Ticket.PROP_TICKET_TYPE)));
        projectionList.add(Projections.rowCount());
        projectionList.add((Projection)Projections.sum((String)Ticket.PROP_NUMBER_OF_GUESTS));
        projectionList.add((Projection)Projections.sum((String)Ticket.PROP_SUBTOTAL_AMOUNT));
        criteria.setProjection((Projection)projectionList);
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
        if (userType != null) {
            criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
        }
        if (terminal != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
        }
        List groupedRows = criteria.list();
        Map<String, Object[]> groupedSummaryMap = new HashMap<String, Object[]>();
        for (Object groupedRow : groupedRows) {
            Object[] summaryRow = (Object[])groupedRow;
            groupedSummaryMap.put(this.buildSalesSummaryKey((Integer)summaryRow[0], (String)summaryRow[1]), summaryRow);
        }
        criteria = session.createCriteria(Shift.class);
        List<Shift> shifts = criteria.list();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        for (Shift shift : shifts) {
            for (OrderType orderType : orderTypes) {
                Object[] summaryRow = groupedSummaryMap.get(this.buildSalesSummaryKey(shift.getId(), orderType.name()));
                SalesStatistics.ShiftwiseSalesTableData data = new SalesStatistics.ShiftwiseSalesTableData();
                data.setProfitCenter(orderType.toString());
                data.setShiftName(shift.getName());
                data.setCheckCount(this.toInt(summaryRow == null ? null : summaryRow[2]));
                data.setGuestCount(this.toInt(summaryRow == null ? null : summaryRow[3]));
                data.setTotalSales(this.toDouble(summaryRow == null ? null : summaryRow[4]));
                if (salesSummary.getGrossSale() > 0.0) {
                    data.setPercentage(data.getTotalSales() * 100.0 / salesSummary.getGrossSale());
                }
                data.calculateOthers();
                salesSummary.addSalesTableData(data);
            }
        }
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        return ((Number)value).intValue();
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        return ((Number)value).doubleValue();
    }
}

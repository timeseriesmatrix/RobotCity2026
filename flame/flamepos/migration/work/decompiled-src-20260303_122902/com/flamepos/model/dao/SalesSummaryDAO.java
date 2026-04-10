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
import java.util.List;
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
            SalesAnalysisReportModel.SalesAnalysisData data;
            Object[] objects;
            List datas;
            ArrayList<SalesAnalysisReportModel.SalesAnalysisData> list = new ArrayList<SalesAnalysisReportModel.SalesAnalysisData>();
            session = this.getSession();
            Criteria criteria = session.createCriteria(Shift.class);
            List shifts = criteria.list();
            criteria = session.createCriteria(MenuCategory.class);
            List categories = criteria.list();
            MenuCategory miscCategory = new MenuCategory();
            miscCategory.setName(Messages.getString("SalesSummaryDAO.0"));
            categories.add(miscCategory);
            criteria = session.createCriteria(TicketItem.class, "item");
            criteria.createCriteria("ticket", "t");
            criteria.createCriteria("t.owner", "u");
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_BEVERAGE), (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)start));
            criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)end));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_TERMINAL), (Object)terminal));
            }
            if ((datas = criteria.list()).size() > 0) {
                objects = (Object[])datas.get(0);
                data = new SalesAnalysisReportModel.SalesAnalysisData();
                data.setShiftName("");
                data.setCategoryName(Messages.getString("SalesSummaryDAO.1"));
                if (objects.length > 0 && objects[0] != null) {
                    data.setCount(((Number)objects[0]).intValue());
                }
                if (objects.length > 1 && objects[1] != null) {
                    data.setGross(((Number)objects[1]).doubleValue());
                }
                if (objects.length > 2 && objects[2] != null) {
                    data.setDiscount(((Number)objects[2]).doubleValue());
                }
                data.calculate();
                list.add(data);
            }
            criteria = session.createCriteria(TicketItem.class, "item");
            criteria.createCriteria("ticket", "t");
            criteria.createCriteria("t.owner", "u");
            projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_BEVERAGE), (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)start));
            criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)end));
            if (userType != null) {
                criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_TERMINAL), (Object)terminal));
            }
            if ((datas = criteria.list()).size() > 0) {
                objects = (Object[])datas.get(0);
                data = new SalesAnalysisReportModel.SalesAnalysisData();
                data.setShiftName("");
                data.setCategoryName(Messages.getString("SalesSummaryDAO.2"));
                if (objects.length > 0 && objects[0] != null) {
                    data.setCount(((Number)objects[0]).intValue());
                }
                if (objects.length > 1 && objects[1] != null) {
                    data.setGross(((Number)objects[1]).doubleValue());
                }
                if (objects.length > 2 && objects[2] != null) {
                    data.setDiscount(((Number)objects[2]).doubleValue());
                }
                data.calculate();
                list.add(data);
            }
            for (Shift shift : shifts) {
                for (MenuCategory category : categories) {
                    criteria = session.createCriteria(TicketItem.class, "item");
                    criteria.createCriteria("ticket", "t");
                    criteria.createCriteria("t.owner", "u");
                    projectionList = Projections.projectionList();
                    projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
                    projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
                    projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
                    criteria.setProjection((Projection)projectionList);
                    criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_CATEGORY_NAME), (Object)category.getName()));
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_SHIFT), (Object)shift));
                    criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)start));
                    criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)end));
                    if (userType != null) {
                        criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
                    }
                    if (terminal != null) {
                        criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_TERMINAL), (Object)terminal));
                    }
                    if ((datas = criteria.list()).size() <= 0) continue;
                    Object[] objects2 = (Object[])datas.get(0);
                    SalesAnalysisReportModel.SalesAnalysisData data2 = new SalesAnalysisReportModel.SalesAnalysisData();
                    data2.setShiftName(shift.getName());
                    data2.setCategoryName(category.getName());
                    if (objects2.length > 0 && objects2[0] != null) {
                        data2.setCount(((Number)objects2[0]).intValue());
                    }
                    if (objects2.length > 1 && objects2[1] != null) {
                        data2.setGross(((Number)objects2[1]).doubleValue());
                    }
                    if (objects2.length > 2 && objects2[2] != null) {
                        data2.setDiscount(((Number)objects2[2]).doubleValue());
                    }
                    data2.calculate();
                    list.add(data2);
                }
            }
            for (MenuCategory category : categories) {
                criteria = session.createCriteria(TicketItem.class, "item");
                criteria.createCriteria("ticket", "t");
                criteria.createCriteria("t.owner", "u");
                projectionList = Projections.projectionList();
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_ITEM_COUNT));
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_SUBTOTAL_AMOUNT));
                projectionList.add((Projection)Projections.sum((String)TicketItem.PROP_DISCOUNT_AMOUNT));
                criteria.setProjection((Projection)projectionList);
                criteria.add((Criterion)Restrictions.eq((String)("item." + TicketItem.PROP_CATEGORY_NAME), (Object)category.getName()));
                criteria.add((Criterion)Restrictions.ge((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)start));
                criteria.add((Criterion)Restrictions.le((String)("t." + Ticket.PROP_ACTIVE_DATE), (Object)end));
                if (userType != null) {
                    criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
                }
                if (terminal != null) {
                    criteria.add((Criterion)Restrictions.eq((String)("t." + Ticket.PROP_TERMINAL), (Object)terminal));
                }
                if ((datas = criteria.list()).size() <= 0) continue;
                Object[] objects3 = (Object[])datas.get(0);
                SalesAnalysisReportModel.SalesAnalysisData data3 = new SalesAnalysisReportModel.SalesAnalysisData();
                data3.setShiftName("ALL DAY");
                data3.setCategoryName(category.getName());
                if (objects3.length > 0 && objects3[0] != null) {
                    data3.setCount(((Number)objects3[0]).intValue());
                }
                if (objects3.length > 1 && objects3[1] != null) {
                    data3.setGross(((Number)objects3[1]).doubleValue());
                }
                if (objects3.length > 2 && objects3[2] != null) {
                    data3.setDiscount(((Number)objects3[2]).doubleValue());
                }
                data3.calculate();
                list.add(data3);
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
            criteria = session.createCriteria(Shift.class);
            List shifts = criteria.list();
            for (Object object : shifts) {
                Shift shift = (Shift)object;
                List<OrderType> values = Application.getInstance().getOrderTypes();
                for (OrderType ticketType : values) {
                    this.findRecordByProfitCenter(start, end, userType, terminal, session, salesSummary, shift, ticketType);
                }
            }
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

    private void findRecordByProfitCenter(Date start, Date end, UserType userType, Terminal terminal, Session session, SalesStatistics salesSummary, Shift shift, OrderType ticketType) {
        List list;
        Criteria criteria = session.createCriteria(Ticket.class, "ticket");
        criteria.createCriteria(Ticket.PROP_OWNER, "u");
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.rowCount());
        projectionList.add((Projection)Projections.sum((String)Ticket.PROP_NUMBER_OF_GUESTS));
        projectionList.add((Projection)Projections.sum((String)Ticket.PROP_SUBTOTAL_AMOUNT));
        criteria.setProjection((Projection)projectionList);
        criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)start));
        criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)end));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_SHIFT, (Object)shift));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TICKET_TYPE, (Object)ticketType.name()));
        if (userType != null) {
            criteria.add((Criterion)Restrictions.eq((String)("u." + User.PROP_TYPE), (Object)userType));
        }
        if (terminal != null) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
        }
        if ((list = criteria.list()).size() > 0) {
            SalesStatistics.ShiftwiseSalesTableData data = new SalesStatistics.ShiftwiseSalesTableData();
            data.setProfitCenter(ticketType.toString());
            Object[] objects = (Object[])list.get(0);
            data.setShiftName(shift.getName());
            data.setCheckCount(((Number)objects[0]).intValue());
            if (objects.length > 1 && objects[1] != null) {
                data.setGuestCount(((Number)objects[1]).intValue());
            }
            if (objects.length > 2 && objects[2] != null) {
                data.setTotalSales(((Number)objects[2]).doubleValue());
            }
            data.setPercentage(data.getTotalSales() * 100.0 / salesSummary.getGrossSale());
            data.calculateOthers();
            salesSummary.addSalesTableData(data);
        }
    }
}


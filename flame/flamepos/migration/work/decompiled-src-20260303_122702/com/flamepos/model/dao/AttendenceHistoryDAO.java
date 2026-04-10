/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.BaseAttendenceHistoryDAO;
import com.floreantpos.report.AttendanceReportData;
import com.floreantpos.report.PayrollReportData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class AttendenceHistoryDAO
extends BaseAttendenceHistoryDAO {
    public List<User> findNumberOfClockedInUserAtHour(Date fromDay, Date toDay, int hour, UserType userType, Terminal terminal) {
        Session session = null;
        ArrayList<User> users = new ArrayList<User>();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)fromDay));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)toDay));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_IN_HOUR, (Object)new Short((short)hour)));
            if (userType != null) {
                criteria.createAlias(AttendenceHistory.PROP_USER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.type", (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            List list = criteria.list();
            for (Object object : list) {
                AttendenceHistory history = (AttendenceHistory)object;
                if (!history.isClockedOut().booleanValue()) {
                    users.add(history.getUser());
                    continue;
                }
                if (history.getClockOutHour() < hour) continue;
                users.add(history.getUser());
            }
            ArrayList<User> arrayList = users;
            return arrayList;
        }
        catch (Exception e) {
            throw new PosException(Messages.getString("AttendenceHistoryDAO.2"), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public List<User> findNumberOfClockedInUserAtShift(Date fromDay, Date toDay, Shift shift, UserType userType, Terminal terminal) {
        Session session = null;
        ArrayList<User> users = new ArrayList<User>();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)fromDay));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)toDay));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_SHIFT, (Object)shift));
            if (userType != null) {
                criteria.createAlias(AttendenceHistory.PROP_USER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.type", (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            List list = criteria.list();
            for (Object object : list) {
                AttendenceHistory history = (AttendenceHistory)object;
                users.add(history.getUser());
            }
            ArrayList<User> arrayList = users;
            return arrayList;
        }
        catch (Exception e) {
            throw new PosException(Messages.getString("AttendenceHistoryDAO.5"), e);
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
    public AttendenceHistory findHistoryByClockedInTime(User user) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(AttendenceHistory.class);
            criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)user.getLastClockInTime()));
            criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_USER, (Object)user));
            AttendenceHistory attendenceHistory = (AttendenceHistory)criteria.uniqueResult();
            return attendenceHistory;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public List<PayrollReportData> findPayroll(Date from, Date to) {
        ArrayList<PayrollReportData> list = new ArrayList<PayrollReportData>();
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(AttendenceHistory.class);
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)from));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_OUT_TIME, (Object)to));
            criteria.addOrder(Order.asc((String)AttendenceHistory.PROP_USER));
            List list2 = criteria.list();
            for (AttendenceHistory history : list2) {
                PayrollReportData data = new PayrollReportData();
                data.setFrom(history.getClockInTime());
                data.setTo(history.getClockOutTime());
                data.setDate(history.getClockInTime());
                data.setUser(history.getUser());
                data.calculate();
                list.add(data);
            }
            ArrayList<PayrollReportData> arrayList = list;
            return arrayList;
        }
    }

    public List<AttendanceReportData> findAttendance(Date from, Date to, User user) {
        ArrayList<AttendanceReportData> list = new ArrayList<AttendanceReportData>();
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(AttendenceHistory.class);
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)from));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_OUT_TIME, (Object)to));
            criteria.addOrder(Order.asc((String)AttendenceHistory.PROP_USER));
            if (user != null) {
                criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_USER, (Object)user));
            }
            List list2 = criteria.list();
            for (AttendenceHistory history : list2) {
                AttendanceReportData data = new AttendanceReportData();
                data.setClockIn(history.getClockInTime());
                data.setClockOut(history.getClockOutTime());
                data.setUser(history.getUser());
                data.setName(history.getUser().getFirstName());
                data.calculate();
                list.add(data);
            }
            ArrayList<AttendanceReportData> arrayList = list;
            return arrayList;
        }
    }

    public List<AttendenceHistory> findHistory(Date from, Date to, User user) {
        try (Session session = null;){
            List list2;
            session = this.getSession();
            Criteria criteria = session.createCriteria(AttendenceHistory.class);
            criteria.add((Criterion)Restrictions.ge((String)AttendenceHistory.PROP_CLOCK_IN_TIME, (Object)from));
            criteria.add((Criterion)Restrictions.le((String)AttendenceHistory.PROP_CLOCK_OUT_TIME, (Object)to));
            criteria.addOrder(Order.asc((String)AttendenceHistory.PROP_ID));
            if (user != null) {
                criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_USER, (Object)user));
            }
            List list = list2 = criteria.list();
            return list;
        }
    }
}


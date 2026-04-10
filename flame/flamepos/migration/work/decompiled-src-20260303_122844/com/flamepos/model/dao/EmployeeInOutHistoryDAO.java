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

import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BaseEmployeeInOutHistoryDAO;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class EmployeeInOutHistoryDAO
extends BaseEmployeeInOutHistoryDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public EmployeeInOutHistory findDriverHistoryByClockedInTime(User user) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(EmployeeInOutHistory.class);
            criteria.add((Criterion)Restrictions.eq((String)EmployeeInOutHistory.PROP_OUT_TIME, (Object)user.getLastClockOutTime()));
            criteria.add((Criterion)Restrictions.eq((String)EmployeeInOutHistory.PROP_USER, (Object)user));
            EmployeeInOutHistory employeeInOutHistory = (EmployeeInOutHistory)criteria.uniqueResult();
            return employeeInOutHistory;
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
    public List<EmployeeInOutHistory> findAttendanceHistory(Date from, Date to, User user) {
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(EmployeeInOutHistory.class);
            criteria.add((Criterion)Restrictions.le((String)EmployeeInOutHistory.PROP_OUT_TIME, (Object)from));
            criteria.add((Criterion)Restrictions.ge((String)EmployeeInOutHistory.PROP_IN_TIME, (Object)to));
            criteria.addOrder(Order.asc((String)EmployeeInOutHistory.PROP_USER));
            if (user != null) {
                criteria.add((Criterion)Restrictions.eq((String)AttendenceHistory.PROP_USER, (Object)user));
            }
            List list = criteria.list();
            return list;
        }
        return null;
    }
}


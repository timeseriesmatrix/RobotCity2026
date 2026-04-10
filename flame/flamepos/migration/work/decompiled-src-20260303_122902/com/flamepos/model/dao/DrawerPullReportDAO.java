/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.dao.BaseDrawerPullReportDAO;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class DrawerPullReportDAO
extends BaseDrawerPullReportDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<DrawerPullReport> findReports(Date start, Date end) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)DrawerPullReport.PROP_REPORT_TIME, (Object)start));
            criteria.add((Criterion)Restrictions.le((String)DrawerPullReport.PROP_REPORT_TIME, (Object)end));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }
}


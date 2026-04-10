/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Projection
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.DeliveryCharge;
import com.floreantpos.model.dao.BaseDeliveryChargeDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class DeliveryChargeDAO
extends BaseDeliveryChargeDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<DeliveryCharge> findByDistance(double distance) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.and((Criterion)Restrictions.le((String)DeliveryCharge.PROP_START_RANGE, (Object)distance), (Criterion)Restrictions.ge((String)DeliveryCharge.PROP_END_RANGE, (Object)distance)));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<DeliveryCharge> findByZipCode(String zipCode) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)DeliveryCharge.PROP_ZIP_CODE, (Object)zipCode));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double findMinRange() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.setProjection((Projection)Projections.min((String)DeliveryCharge.PROP_START_RANGE));
            double d = (Double)criteria.uniqueResult();
            return d;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Double findMaxRange() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.setProjection((Projection)Projections.max((String)DeliveryCharge.PROP_END_RANGE));
            Double d = (Double)criteria.uniqueResult();
            return d;
        }
        finally {
            this.closeSession(session);
        }
    }
}


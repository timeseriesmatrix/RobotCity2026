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

import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.BaseDiscountDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class DiscountDAO
extends BaseDiscountDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Discount> findAllValidCoupons() {
        Session session = null;
        Date currentDate = new Date();
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Discount.PROP_ENABLED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)Discount.PROP_NEVER_EXPIRE, (Object)Boolean.TRUE), (Criterion)Restrictions.ge((String)Discount.PROP_EXPIRY_DATE, (Object)currentDate)));
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
    public List<Discount> getValidCoupons() {
        Session session = null;
        Date currentDate = new Date();
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Discount.PROP_ENABLED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Discount.PROP_QUALIFICATION_TYPE, (Object)0));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)Discount.PROP_NEVER_EXPIRE, (Object)Boolean.TRUE), (Criterion)Restrictions.ge((String)Discount.PROP_EXPIRY_DATE, (Object)currentDate)));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public List<Discount> getValidCoupon(MenuItem menuItem) {
        ArrayList<Discount> discountList = new ArrayList<Discount>();
        for (Discount discount : this.getValidCoupons()) {
            if (!discount.getMenuItems().contains(menuItem) && !discount.isApplyToAll().booleanValue()) continue;
            discountList.add(discount);
        }
        return discountList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Discount> getTicketValidCoupon() {
        Session session = null;
        Date currentDate = new Date();
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Discount.PROP_ENABLED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Discount.PROP_QUALIFICATION_TYPE, (Object)1));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)Discount.PROP_NEVER_EXPIRE, (Object)Boolean.TRUE), (Criterion)Restrictions.ge((String)Discount.PROP_EXPIRY_DATE, (Object)currentDate)));
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
    public Discount getDiscountByBarcode(String barcode) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(Discount.class);
            criteria.add((Criterion)Restrictions.like((String)Discount.PROP_BARCODE, (Object)barcode));
            List result = criteria.list();
            if (result == null || result.isEmpty()) {
                Discount discount = null;
                return discount;
            }
            Discount discount = (Discount)result.get(0);
            return discount;
        }
        finally {
            this.closeSession(session);
        }
    }
}


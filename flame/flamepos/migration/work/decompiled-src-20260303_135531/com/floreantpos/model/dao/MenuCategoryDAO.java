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

import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.dao.BaseMenuCategoryDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MenuCategoryDAO
extends BaseMenuCategoryDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuCategory> findAllEnable() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuCategory.PROP_VISIBLE, (Object)Boolean.TRUE));
            criteria.addOrder(Order.asc((String)MenuCategory.PROP_SORT_ORDER));
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
    public List<MenuCategory> findNonBevegares() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuCategory.PROP_VISIBLE, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isNull((String)MenuCategory.PROP_BEVERAGE), (Criterion)Restrictions.eq((String)MenuCategory.PROP_BEVERAGE, (Object)Boolean.FALSE)));
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
    public List<MenuCategory> findBevegares() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuCategory.PROP_VISIBLE, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)MenuCategory.PROP_BEVERAGE, (Object)Boolean.TRUE));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.SQLQuery
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.PosLog;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.BaseOrderTypeDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class OrderTypeDAO
extends BaseOrderTypeDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<OrderType> findEnabledOrderTypes() {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)OrderType.PROP_ENABLED, (Object)true));
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
    public List<OrderType> findLoginScreenViewOrderTypes() {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)OrderType.PROP_ENABLED, (Object)true));
            criteria.add((Criterion)Restrictions.eq((String)OrderType.PROP_SHOW_IN_LOGIN_SCREEN, (Object)true));
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
    public OrderType findByName(String orderType) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)OrderType.PROP_NAME, (Object)orderType));
            OrderType orderType2 = (OrderType)criteria.uniqueResult();
            return orderType2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsOrderTypeObj() {
        Session session = null;
        try {
            session = this.createNewSession();
            SQLQuery query = session.createSQLQuery("select count(s.MENU_ITEM_ID), count(s.ORDER_TYPE_ID) from ITEM_ORDER_TYPE s");
            List result = query.list();
            Object[] object = (Object[])result.get(0);
            Integer menuItemCount = this.getInt(object, 0);
            Integer orderTypeCount = this.getInt(object, 1);
            if (menuItemCount < 1) {
                boolean bl = true;
                return bl;
            }
            boolean bl = orderTypeCount > 0;
            return bl;
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateMenuItemOrderType() {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            SQLQuery query = session.createSQLQuery("Update ITEM_ORDER_TYPE t SET t.ORDER_TYPE_ID=(Select o.id from ORDER_TYPE o where o.NAME=t.ORDER_TYPE)");
            query.executeUpdate();
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    private Integer getInt(Object[] array, int index) {
        if (array.length < index + 1) {
            return null;
        }
        if (array[index] instanceof Number) {
            return ((Number)array[index]).intValue();
        }
        return null;
    }
}


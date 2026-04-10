/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.Multiplier;
import com.floreantpos.model.dao.BaseMultiplierDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class MultiplierDAO
extends BaseMultiplierDAO {
    @Override
    public List<Multiplier> findAll() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.addOrder(Order.asc((String)Multiplier.PROP_SORT_ORDER));
            List list = criteria.list();
            return list;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            this.closeSession(session);
        }
    }

    public void saveOrUpdateMultipliers(List<Multiplier> items) {
        Transaction tx = null;
        try (Session session = null;){
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.saveOrUpdateMultipliers(items, session);
            tx.commit();
        }
    }

    public void saveOrUpdateMultipliers(List<Multiplier> items, Session session) {
        for (Multiplier multiplier : items) {
            session.saveOrUpdate((Object)multiplier);
        }
    }
}


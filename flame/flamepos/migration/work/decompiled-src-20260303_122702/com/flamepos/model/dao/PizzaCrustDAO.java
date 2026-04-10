/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.dao.BasePizzaCrustDAO;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class PizzaCrustDAO
extends BasePizzaCrustDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)PizzaCrust.PROP_SORT_ORDER);
    }

    public void setDefault(List<PizzaCrust> items) {
        Transaction tx = null;
        try (Session session = null;){
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.saveOrUpdateCrustList(items, session);
            tx.commit();
        }
    }

    public void saveOrUpdateCrustList(List<PizzaCrust> items, Session session) {
        for (PizzaCrust pizzaCrust : items) {
            session.saveOrUpdate((Object)pizzaCrust);
        }
    }
}


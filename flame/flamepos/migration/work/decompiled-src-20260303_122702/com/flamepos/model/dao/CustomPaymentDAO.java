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

import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.dao.BaseCustomPaymentDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CustomPaymentDAO
extends BaseCustomPaymentDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)CustomPayment.PROP_ID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CustomPayment getByName(String name) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.like((String)CustomPayment.PROP_NAME, (Object)name));
            CustomPayment customPayment = (CustomPayment)criteria.uniqueResult();
            return customPayment;
        }
        finally {
            this.closeSession(session);
        }
    }
}


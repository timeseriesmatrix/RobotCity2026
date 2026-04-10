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

import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.BaseTaxDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class TaxDAO
extends BaseTaxDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Tax findByTaxRate(double taxRate) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(Tax.class);
            criteria.add((Criterion)Restrictions.eq((String)Tax.PROP_RATE, (Object)taxRate));
            Tax tax = (Tax)criteria.uniqueResult();
            return tax;
        }
        finally {
            this.closeSession(session);
        }
    }
}


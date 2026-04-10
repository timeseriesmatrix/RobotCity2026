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

import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BaseCashDrawerDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class CashDrawerDAO
extends BaseCashDrawerDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CashDrawer findByTerminal(Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)CashDrawer.PROP_TERMINAL, (Object)terminal));
            CashDrawer cashDrawer = (CashDrawer)criteria.uniqueResult();
            return cashDrawer;
        }
        finally {
            this.closeSession(session);
        }
    }
}


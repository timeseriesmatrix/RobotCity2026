/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.BasePackagingUnitDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class PackagingUnitDAO
extends BasePackagingUnitDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean nameExists(String name) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)PackagingUnit.PROP_NAME, (Object)name).ignoreCase());
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount == null) {
                boolean bl = false;
                return bl;
            }
            boolean bl = rowCount > 0;
            return bl;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }
}


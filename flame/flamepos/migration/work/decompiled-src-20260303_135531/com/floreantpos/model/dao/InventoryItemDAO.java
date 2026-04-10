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

import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.BaseInventoryItemDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class InventoryItemDAO
extends BaseInventoryItemDAO {
    public boolean hasInventoryItemByName(String name) {
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_NAME, (Object)name));
            boolean bl = criteria.list().size() > 0;
            return bl;
        }
    }
}


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

import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.BaseVirtualPrinterDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class VirtualPrinterDAO
extends BaseVirtualPrinterDAO {
    public VirtualPrinter findPrinterByName(String name) {
        Session session = this.getSession();
        Criteria criteria = session.createCriteria(this.getReferenceClass());
        criteria.add((Criterion)Restrictions.eq((String)VirtualPrinter.PROP_NAME, (Object)name));
        return (VirtualPrinter)criteria.uniqueResult();
    }
}


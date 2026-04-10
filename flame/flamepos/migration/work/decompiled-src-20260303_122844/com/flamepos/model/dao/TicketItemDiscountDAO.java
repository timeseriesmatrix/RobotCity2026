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

import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.dao.BaseTicketItemDiscountDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class TicketItemDiscountDAO
extends BaseTicketItemDiscountDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<TicketItemDiscount> findTicketItemDiscounts(TicketItem ticketItem) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)TicketItemDiscount.PROP_TICKET_ITEM, (Object)ticketItem));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }
}


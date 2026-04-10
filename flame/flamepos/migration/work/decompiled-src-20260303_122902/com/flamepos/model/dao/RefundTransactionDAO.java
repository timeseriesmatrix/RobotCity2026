/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Projection
 *  org.hibernate.criterion.ProjectionList
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BaseRefundTransactionDAO;
import com.floreantpos.model.util.RefundSummary;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class RefundTransactionDAO
extends BaseRefundTransactionDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RefundSummary getTotalRefundForTerminal(Terminal terminal) {
        Session session = null;
        RefundSummary refundSummary = new RefundSummary();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.rowCount());
            projectionList.add((Projection)Projections.sum((String)RefundTransaction.PROP_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            criteria.add((Criterion)Restrictions.eq((String)RefundTransaction.PROP_TERMINAL, (Object)terminal));
            criteria.add((Criterion)Restrictions.eq((String)RefundTransaction.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            List list = criteria.list();
            if (list.size() > 0) {
                Object[] objects = (Object[])list.get(0);
                if (objects.length > 0 && objects[0] != null) {
                    refundSummary.setCount(((Number)objects[0]).intValue());
                }
                if (objects.length > 1 && objects[1] != null) {
                    refundSummary.setAmount(((Number)objects[1]).doubleValue());
                }
            }
            RefundSummary refundSummary2 = refundSummary;
            return refundSummary2;
        }
        finally {
            this.closeSession(session);
        }
    }
}


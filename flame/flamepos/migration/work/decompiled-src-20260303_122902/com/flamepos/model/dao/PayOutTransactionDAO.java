/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PayOutTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BasePayOutTransactionDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class PayOutTransactionDAO
extends BasePayOutTransactionDAO {
    public void saveTransaction(PayOutTransaction t, Terminal terminal) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)t);
            session.saveOrUpdate((Object)terminal);
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw e;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<PayOutTransaction> getUnsettled(Terminal terminal) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)PayOutTransaction.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)PayOutTransaction.PROP_TERMINAL, (Object)terminal));
            List list2 = list = criteria.list();
            return list2;
        }
        finally {
            this.closeSession(session);
        }
    }
}


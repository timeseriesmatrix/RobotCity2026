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

import com.floreantpos.model.CashDropTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BaseCashDropTransactionDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class CashDropTransactionDAO
extends BaseCashDropTransactionDAO {
    public List<CashDropTransaction> findUnsettled(Terminal terminal) throws Exception {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)CashDropTransaction.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)CashDropTransaction.PROP_TERMINAL, (Object)terminal));
            List list = criteria.list();
            return list;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteCashDrop(CashDropTransaction transaction, Terminal terminal) {
        Session session = null;
        Transaction tx = null;
        terminal.setCurrentBalance(terminal.getCurrentBalance() + transaction.getAmount());
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            session.delete((Object)transaction);
            session.update((Object)terminal);
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveNewCashDrop(CashDropTransaction transaction, Terminal terminal) {
        Session session = null;
        Transaction tx = null;
        terminal.setCurrentBalance(terminal.getCurrentBalance() - transaction.getAmount());
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            session.save((Object)transaction);
            session.update((Object)terminal);
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        finally {
            this.closeSession(session);
        }
    }
}


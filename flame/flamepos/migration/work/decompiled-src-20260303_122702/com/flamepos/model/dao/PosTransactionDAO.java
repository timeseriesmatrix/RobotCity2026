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

import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BasePosTransactionDAO;
import com.floreantpos.model.dao.HibernateProjectionsUtil;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.model.util.TransactionSummary;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class PosTransactionDAO
extends BasePosTransactionDAO {
    public List<PosTransaction> findUnauthorizedTransactions() {
        return this.findUnauthorizedTransactions(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<PosTransaction> findUnauthorizedTransactions(User owner) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CAPTURED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_AUTHORIZABLE, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isNull((String)PosTransaction.PROP_VOIDED), (Criterion)Restrictions.eq((String)PosTransaction.PROP_VOIDED, (Object)Boolean.FALSE)));
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TRANSACTION_TYPE, (Object)TransactionType.CREDIT.name()));
            criteria.add(Restrictions.isNotNull((String)PosTransaction.PROP_TICKET));
            if (owner != null) {
                criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_USER, (Object)owner));
            }
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<PosTransaction> findAuthorizedTransactions(User owner) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(CreditCardTransaction.class);
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_CAPTURED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isNull((String)PosTransaction.PROP_VOIDED), (Criterion)Restrictions.eq((String)PosTransaction.PROP_VOIDED, (Object)Boolean.FALSE)));
            criteria.add(Restrictions.isNotNull((String)PosTransaction.PROP_TICKET));
            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            Date startOfDay = DateUtil.startOfDay(calendar.getTime());
            Date endOfDay = DateUtil.endOfDay(new Date());
            criteria.add(Restrictions.between((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)startOfDay, (Object)endOfDay));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<? extends PosTransaction> findTransactions(Terminal terminal, Class transactionClass, Date from, Date to) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(transactionClass);
            criteria.add(Restrictions.isNotNull((String)PosTransaction.PROP_TICKET));
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TERMINAL, (Object)terminal));
            }
            if (from == null || to != null) {
                // empty if block
            }
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<? extends PosTransaction> findTransactions(Class transactionClass, Date from, Date to) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(transactionClass);
            criteria.add(Restrictions.isNotNull((String)PosTransaction.PROP_TICKET));
            if (from != null && to != null) {
                criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)from));
                criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)to));
            }
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TransactionSummary getTransactionSummary(Terminal terminal, Class transactionClass, Date from, Date to) {
        Session session = null;
        TransactionSummary summary = new TransactionSummary();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(transactionClass);
            criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)PosTransaction.PROP_TERMINAL, (Object)terminal));
            }
            if (from != null && to != null) {
                criteria.add((Criterion)Restrictions.ge((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)from));
                criteria.add((Criterion)Restrictions.le((String)PosTransaction.PROP_TRANSACTION_TIME, (Object)to));
            }
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.count((String)PosTransaction.PROP_ID));
            projectionList.add((Projection)Projections.sum((String)PosTransaction.PROP_AMOUNT));
            projectionList.add((Projection)Projections.sum((String)PosTransaction.PROP_TIPS_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            List list = criteria.list();
            if (list == null || list.size() == 0) {
                TransactionSummary transactionSummary = summary;
                return transactionSummary;
            }
            Object[] o = (Object[])list.get(0);
            int index = 0;
            summary.setCount(HibernateProjectionsUtil.getInt(o, index++));
            summary.setAmount(HibernateProjectionsUtil.getDouble(o, index++));
            summary.setTipsAmount(HibernateProjectionsUtil.getDouble(o, index++));
            TransactionSummary transactionSummary = summary;
            return transactionSummary;
        }
        finally {
            this.closeSession(session);
        }
    }
}


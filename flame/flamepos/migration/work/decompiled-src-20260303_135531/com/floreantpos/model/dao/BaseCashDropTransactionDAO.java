/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.CashDropTransaction;
import com.floreantpos.model.dao.CashDropTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCashDropTransactionDAO
extends _RootDAO {
    public static CashDropTransactionDAO instance;

    public static CashDropTransactionDAO getInstance() {
        if (null == instance) {
            instance = new CashDropTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CashDropTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CashDropTransaction cast(Object object) {
        return (CashDropTransaction)object;
    }

    public CashDropTransaction get(Integer key) throws HibernateException {
        return (CashDropTransaction)this.get(this.getReferenceClass(), key);
    }

    public CashDropTransaction get(Integer key, Session s) throws HibernateException {
        return (CashDropTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public CashDropTransaction load(Integer key) throws HibernateException {
        return (CashDropTransaction)this.load(this.getReferenceClass(), key);
    }

    public CashDropTransaction load(Integer key, Session s) throws HibernateException {
        return (CashDropTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public CashDropTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        CashDropTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CashDropTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CashDropTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CashDropTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CashDropTransaction cashDropTransaction) throws HibernateException {
        return (Integer)super.save(cashDropTransaction);
    }

    public Integer save(CashDropTransaction cashDropTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)cashDropTransaction, s);
    }

    public void saveOrUpdate(CashDropTransaction cashDropTransaction) throws HibernateException {
        this.saveOrUpdate((Object)cashDropTransaction);
    }

    public void saveOrUpdate(CashDropTransaction cashDropTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)cashDropTransaction, s);
    }

    public void update(CashDropTransaction cashDropTransaction) throws HibernateException {
        this.update((Object)cashDropTransaction);
    }

    public void update(CashDropTransaction cashDropTransaction, Session s) throws HibernateException {
        this.update((Object)cashDropTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CashDropTransaction cashDropTransaction) throws HibernateException {
        this.delete((Object)cashDropTransaction);
    }

    public void delete(CashDropTransaction cashDropTransaction, Session s) throws HibernateException {
        this.delete((Object)cashDropTransaction, s);
    }

    public void refresh(CashDropTransaction cashDropTransaction, Session s) throws HibernateException {
        this.refresh((Object)cashDropTransaction, s);
    }
}


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

import com.floreantpos.model.CardTransaction;
import com.floreantpos.model.dao.CardTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCardTransactionDAO
extends _RootDAO {
    public static CardTransactionDAO instance;

    public static CardTransactionDAO getInstance() {
        if (null == instance) {
            instance = new CardTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CardTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CardTransaction cast(Object object) {
        return (CardTransaction)object;
    }

    public CardTransaction get(Integer key) throws HibernateException {
        return (CardTransaction)this.get(this.getReferenceClass(), key);
    }

    public CardTransaction get(Integer key, Session s) throws HibernateException {
        return (CardTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public CardTransaction load(Integer key) throws HibernateException {
        return (CardTransaction)this.load(this.getReferenceClass(), key);
    }

    public CardTransaction load(Integer key, Session s) throws HibernateException {
        return (CardTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public CardTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        CardTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CardTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CardTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CardTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CardTransaction cardTransaction) throws HibernateException {
        return (Integer)super.save(cardTransaction);
    }

    public Integer save(CardTransaction cardTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)cardTransaction, s);
    }

    public void saveOrUpdate(CardTransaction cardTransaction) throws HibernateException {
        this.saveOrUpdate((Object)cardTransaction);
    }

    public void saveOrUpdate(CardTransaction cardTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)cardTransaction, s);
    }

    public void update(CardTransaction cardTransaction) throws HibernateException {
        this.update((Object)cardTransaction);
    }

    public void update(CardTransaction cardTransaction, Session s) throws HibernateException {
        this.update((Object)cardTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CardTransaction cardTransaction) throws HibernateException {
        this.delete((Object)cardTransaction);
    }

    public void delete(CardTransaction cardTransaction, Session s) throws HibernateException {
        this.delete((Object)cardTransaction, s);
    }

    public void refresh(CardTransaction cardTransaction, Session s) throws HibernateException {
        this.refresh((Object)cardTransaction, s);
    }
}


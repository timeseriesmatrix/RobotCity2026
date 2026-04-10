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

import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.dao.GiftCertificateTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseGiftCertificateTransactionDAO
extends _RootDAO {
    public static GiftCertificateTransactionDAO instance;

    public static GiftCertificateTransactionDAO getInstance() {
        if (null == instance) {
            instance = new GiftCertificateTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return GiftCertificateTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public GiftCertificateTransaction cast(Object object) {
        return (GiftCertificateTransaction)object;
    }

    public GiftCertificateTransaction get(Integer key) throws HibernateException {
        return (GiftCertificateTransaction)this.get(this.getReferenceClass(), key);
    }

    public GiftCertificateTransaction get(Integer key, Session s) throws HibernateException {
        return (GiftCertificateTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public GiftCertificateTransaction load(Integer key) throws HibernateException {
        return (GiftCertificateTransaction)this.load(this.getReferenceClass(), key);
    }

    public GiftCertificateTransaction load(Integer key, Session s) throws HibernateException {
        return (GiftCertificateTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public GiftCertificateTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        GiftCertificateTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<GiftCertificateTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<GiftCertificateTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<GiftCertificateTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(GiftCertificateTransaction giftCertificateTransaction) throws HibernateException {
        return (Integer)super.save(giftCertificateTransaction);
    }

    public Integer save(GiftCertificateTransaction giftCertificateTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)giftCertificateTransaction, s);
    }

    public void saveOrUpdate(GiftCertificateTransaction giftCertificateTransaction) throws HibernateException {
        this.saveOrUpdate((Object)giftCertificateTransaction);
    }

    public void saveOrUpdate(GiftCertificateTransaction giftCertificateTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)giftCertificateTransaction, s);
    }

    public void update(GiftCertificateTransaction giftCertificateTransaction) throws HibernateException {
        this.update((Object)giftCertificateTransaction);
    }

    public void update(GiftCertificateTransaction giftCertificateTransaction, Session s) throws HibernateException {
        this.update((Object)giftCertificateTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(GiftCertificateTransaction giftCertificateTransaction) throws HibernateException {
        this.delete((Object)giftCertificateTransaction);
    }

    public void delete(GiftCertificateTransaction giftCertificateTransaction, Session s) throws HibernateException {
        this.delete((Object)giftCertificateTransaction, s);
    }

    public void refresh(GiftCertificateTransaction giftCertificateTransaction, Session s) throws HibernateException {
        this.refresh((Object)giftCertificateTransaction, s);
    }
}


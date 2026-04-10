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

import com.floreantpos.model.ZipCodeVsDeliveryCharge;
import com.floreantpos.model.dao.ZipCodeVsDeliveryChargeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseZipCodeVsDeliveryChargeDAO
extends _RootDAO {
    public static ZipCodeVsDeliveryChargeDAO instance;

    public static ZipCodeVsDeliveryChargeDAO getInstance() {
        if (null == instance) {
            instance = new ZipCodeVsDeliveryChargeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ZipCodeVsDeliveryCharge.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public ZipCodeVsDeliveryCharge cast(Object object) {
        return (ZipCodeVsDeliveryCharge)object;
    }

    public ZipCodeVsDeliveryCharge get(Integer key) throws HibernateException {
        return (ZipCodeVsDeliveryCharge)this.get(this.getReferenceClass(), key);
    }

    public ZipCodeVsDeliveryCharge get(Integer key, Session s) throws HibernateException {
        return (ZipCodeVsDeliveryCharge)this.get(this.getReferenceClass(), key, s);
    }

    public ZipCodeVsDeliveryCharge load(Integer key) throws HibernateException {
        return (ZipCodeVsDeliveryCharge)this.load(this.getReferenceClass(), key);
    }

    public ZipCodeVsDeliveryCharge load(Integer key, Session s) throws HibernateException {
        return (ZipCodeVsDeliveryCharge)this.load(this.getReferenceClass(), key, s);
    }

    public ZipCodeVsDeliveryCharge loadInitialize(Integer key, Session s) throws HibernateException {
        ZipCodeVsDeliveryCharge obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ZipCodeVsDeliveryCharge> findAll() {
        return super.findAll();
    }

    @Override
    public List<ZipCodeVsDeliveryCharge> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ZipCodeVsDeliveryCharge> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge) throws HibernateException {
        return (Integer)super.save(zipCodeVsDeliveryCharge);
    }

    public Integer save(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge, Session s) throws HibernateException {
        return (Integer)this.save((Object)zipCodeVsDeliveryCharge, s);
    }

    public void saveOrUpdate(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge) throws HibernateException {
        this.saveOrUpdate((Object)zipCodeVsDeliveryCharge);
    }

    public void saveOrUpdate(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge, Session s) throws HibernateException {
        this.saveOrUpdate((Object)zipCodeVsDeliveryCharge, s);
    }

    public void update(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge) throws HibernateException {
        this.update((Object)zipCodeVsDeliveryCharge);
    }

    public void update(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge, Session s) throws HibernateException {
        this.update((Object)zipCodeVsDeliveryCharge, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge) throws HibernateException {
        this.delete((Object)zipCodeVsDeliveryCharge);
    }

    public void delete(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge, Session s) throws HibernateException {
        this.delete((Object)zipCodeVsDeliveryCharge, s);
    }

    public void refresh(ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge, Session s) throws HibernateException {
        this.refresh((Object)zipCodeVsDeliveryCharge, s);
    }
}


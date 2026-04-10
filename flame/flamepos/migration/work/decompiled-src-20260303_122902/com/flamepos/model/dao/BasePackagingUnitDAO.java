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

import com.floreantpos.model.PackagingUnit;
import com.floreantpos.model.dao.PackagingUnitDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePackagingUnitDAO
extends _RootDAO {
    public static PackagingUnitDAO instance;

    public static PackagingUnitDAO getInstance() {
        if (null == instance) {
            instance = new PackagingUnitDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PackagingUnit.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public PackagingUnit cast(Object object) {
        return (PackagingUnit)object;
    }

    public PackagingUnit get(Integer key) throws HibernateException {
        return (PackagingUnit)this.get(this.getReferenceClass(), key);
    }

    public PackagingUnit get(Integer key, Session s) throws HibernateException {
        return (PackagingUnit)this.get(this.getReferenceClass(), key, s);
    }

    public PackagingUnit load(Integer key) throws HibernateException {
        return (PackagingUnit)this.load(this.getReferenceClass(), key);
    }

    public PackagingUnit load(Integer key, Session s) throws HibernateException {
        return (PackagingUnit)this.load(this.getReferenceClass(), key, s);
    }

    public PackagingUnit loadInitialize(Integer key, Session s) throws HibernateException {
        PackagingUnit obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PackagingUnit> findAll() {
        return super.findAll();
    }

    @Override
    public List<PackagingUnit> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PackagingUnit> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PackagingUnit packagingUnit) throws HibernateException {
        return (Integer)super.save(packagingUnit);
    }

    public Integer save(PackagingUnit packagingUnit, Session s) throws HibernateException {
        return (Integer)this.save((Object)packagingUnit, s);
    }

    public void saveOrUpdate(PackagingUnit packagingUnit) throws HibernateException {
        this.saveOrUpdate((Object)packagingUnit);
    }

    public void saveOrUpdate(PackagingUnit packagingUnit, Session s) throws HibernateException {
        this.saveOrUpdate((Object)packagingUnit, s);
    }

    public void update(PackagingUnit packagingUnit) throws HibernateException {
        this.update((Object)packagingUnit);
    }

    public void update(PackagingUnit packagingUnit, Session s) throws HibernateException {
        this.update((Object)packagingUnit, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PackagingUnit packagingUnit) throws HibernateException {
        this.delete((Object)packagingUnit);
    }

    public void delete(PackagingUnit packagingUnit, Session s) throws HibernateException {
        this.delete((Object)packagingUnit, s);
    }

    public void refresh(PackagingUnit packagingUnit, Session s) throws HibernateException {
        this.refresh((Object)packagingUnit, s);
    }
}


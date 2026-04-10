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

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.dao.ShopFloorDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseShopFloorDAO
extends _RootDAO {
    public static ShopFloorDAO instance;

    public static ShopFloorDAO getInstance() {
        if (null == instance) {
            instance = new ShopFloorDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ShopFloor.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public ShopFloor cast(Object object) {
        return (ShopFloor)object;
    }

    public ShopFloor get(Integer key) throws HibernateException {
        return (ShopFloor)this.get(this.getReferenceClass(), key);
    }

    public ShopFloor get(Integer key, Session s) throws HibernateException {
        return (ShopFloor)this.get(this.getReferenceClass(), key, s);
    }

    public ShopFloor load(Integer key) throws HibernateException {
        return (ShopFloor)this.load(this.getReferenceClass(), key);
    }

    public ShopFloor load(Integer key, Session s) throws HibernateException {
        return (ShopFloor)this.load(this.getReferenceClass(), key, s);
    }

    public ShopFloor loadInitialize(Integer key, Session s) throws HibernateException {
        ShopFloor obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ShopFloor> findAll() {
        return super.findAll();
    }

    @Override
    public List<ShopFloor> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ShopFloor> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ShopFloor shopFloor) throws HibernateException {
        return (Integer)super.save(shopFloor);
    }

    public Integer save(ShopFloor shopFloor, Session s) throws HibernateException {
        return (Integer)this.save((Object)shopFloor, s);
    }

    public void saveOrUpdate(ShopFloor shopFloor) throws HibernateException {
        this.saveOrUpdate((Object)shopFloor);
    }

    public void saveOrUpdate(ShopFloor shopFloor, Session s) throws HibernateException {
        this.saveOrUpdate((Object)shopFloor, s);
    }

    public void update(ShopFloor shopFloor) throws HibernateException {
        this.update((Object)shopFloor);
    }

    public void update(ShopFloor shopFloor, Session s) throws HibernateException {
        this.update((Object)shopFloor, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ShopFloor shopFloor) throws HibernateException {
        this.delete((Object)shopFloor);
    }

    public void delete(ShopFloor shopFloor, Session s) throws HibernateException {
        this.delete((Object)shopFloor, s);
    }

    public void refresh(ShopFloor shopFloor, Session s) throws HibernateException {
        this.refresh((Object)shopFloor, s);
    }
}


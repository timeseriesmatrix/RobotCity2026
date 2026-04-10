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

import com.floreantpos.model.ShopFloorTemplate;
import com.floreantpos.model.dao.ShopFloorTemplateDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseShopFloorTemplateDAO
extends _RootDAO {
    public static ShopFloorTemplateDAO instance;

    public static ShopFloorTemplateDAO getInstance() {
        if (null == instance) {
            instance = new ShopFloorTemplateDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ShopFloorTemplate.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public ShopFloorTemplate cast(Object object) {
        return (ShopFloorTemplate)object;
    }

    public ShopFloorTemplate get(Integer key) throws HibernateException {
        return (ShopFloorTemplate)this.get(this.getReferenceClass(), key);
    }

    public ShopFloorTemplate get(Integer key, Session s) throws HibernateException {
        return (ShopFloorTemplate)this.get(this.getReferenceClass(), key, s);
    }

    public ShopFloorTemplate load(Integer key) throws HibernateException {
        return (ShopFloorTemplate)this.load(this.getReferenceClass(), key);
    }

    public ShopFloorTemplate load(Integer key, Session s) throws HibernateException {
        return (ShopFloorTemplate)this.load(this.getReferenceClass(), key, s);
    }

    public ShopFloorTemplate loadInitialize(Integer key, Session s) throws HibernateException {
        ShopFloorTemplate obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ShopFloorTemplate> findAll() {
        return super.findAll();
    }

    @Override
    public List<ShopFloorTemplate> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ShopFloorTemplate> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ShopFloorTemplate shopFloorTemplate) throws HibernateException {
        return (Integer)super.save(shopFloorTemplate);
    }

    public Integer save(ShopFloorTemplate shopFloorTemplate, Session s) throws HibernateException {
        return (Integer)this.save((Object)shopFloorTemplate, s);
    }

    public void saveOrUpdate(ShopFloorTemplate shopFloorTemplate) throws HibernateException {
        this.saveOrUpdate((Object)shopFloorTemplate);
    }

    public void saveOrUpdate(ShopFloorTemplate shopFloorTemplate, Session s) throws HibernateException {
        this.saveOrUpdate((Object)shopFloorTemplate, s);
    }

    public void update(ShopFloorTemplate shopFloorTemplate) throws HibernateException {
        this.update((Object)shopFloorTemplate);
    }

    public void update(ShopFloorTemplate shopFloorTemplate, Session s) throws HibernateException {
        this.update((Object)shopFloorTemplate, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ShopFloorTemplate shopFloorTemplate) throws HibernateException {
        this.delete((Object)shopFloorTemplate);
    }

    public void delete(ShopFloorTemplate shopFloorTemplate, Session s) throws HibernateException {
        this.delete((Object)shopFloorTemplate, s);
    }

    public void refresh(ShopFloorTemplate shopFloorTemplate, Session s) throws HibernateException {
        this.refresh((Object)shopFloorTemplate, s);
    }
}


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

import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseUserDAO
extends _RootDAO {
    public static UserDAO instance;

    public static UserDAO getInstance() {
        if (null == instance) {
            instance = new UserDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return User.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"lastName");
    }

    public User cast(Object object) {
        return (User)object;
    }

    public User get(Integer key) throws HibernateException {
        return (User)this.get(this.getReferenceClass(), key);
    }

    public User get(Integer key, Session s) throws HibernateException {
        return (User)this.get(this.getReferenceClass(), key, s);
    }

    public User load(Integer key) throws HibernateException {
        return (User)this.load(this.getReferenceClass(), key);
    }

    public User load(Integer key, Session s) throws HibernateException {
        return (User)this.load(this.getReferenceClass(), key, s);
    }

    public User loadInitialize(Integer key, Session s) throws HibernateException {
        User obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    public List<User> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<User> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(User user) throws HibernateException {
        return (Integer)super.save(user);
    }

    public Integer save(User user, Session s) throws HibernateException {
        return (Integer)this.save((Object)user, s);
    }

    public void saveOrUpdate(User user) throws HibernateException {
        this.saveOrUpdate((Object)user);
    }

    public void saveOrUpdate(User user, Session s) throws HibernateException {
        this.saveOrUpdate((Object)user, s);
    }

    public void update(User user) throws HibernateException {
        this.update((Object)user);
    }

    public void update(User user, Session s) throws HibernateException {
        this.update((Object)user, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(User user) throws HibernateException {
        this.delete((Object)user);
    }

    public void delete(User user, Session s) throws HibernateException {
        this.delete((Object)user, s);
    }

    public void refresh(User user, Session s) throws HibernateException {
        this.refresh((Object)user, s);
    }
}


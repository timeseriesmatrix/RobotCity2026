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

import com.floreantpos.model.Customer;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCustomerDAO
extends _RootDAO {
    public static CustomerDAO instance;

    public static CustomerDAO getInstance() {
        if (null == instance) {
            instance = new CustomerDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Customer.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Customer cast(Object object) {
        return (Customer)object;
    }

    public Customer get(Integer key) throws HibernateException {
        return (Customer)this.get(this.getReferenceClass(), key);
    }

    public Customer get(Integer key, Session s) throws HibernateException {
        return (Customer)this.get(this.getReferenceClass(), key, s);
    }

    public Customer load(Integer key) throws HibernateException {
        return (Customer)this.load(this.getReferenceClass(), key);
    }

    public Customer load(Integer key, Session s) throws HibernateException {
        return (Customer)this.load(this.getReferenceClass(), key, s);
    }

    public Customer loadInitialize(Integer key, Session s) throws HibernateException {
        Customer obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Customer> findAll() {
        return super.findAll();
    }

    @Override
    public List<Customer> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Customer> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Customer customer) throws HibernateException {
        return (Integer)super.save(customer);
    }

    public Integer save(Customer customer, Session s) throws HibernateException {
        return (Integer)this.save((Object)customer, s);
    }

    public void saveOrUpdate(Customer customer) throws HibernateException {
        this.saveOrUpdate((Object)customer);
    }

    public void saveOrUpdate(Customer customer, Session s) throws HibernateException {
        this.saveOrUpdate((Object)customer, s);
    }

    public void update(Customer customer) throws HibernateException {
        this.update((Object)customer);
    }

    public void update(Customer customer, Session s) throws HibernateException {
        this.update((Object)customer, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Customer customer) throws HibernateException {
        this.delete((Object)customer);
    }

    public void delete(Customer customer, Session s) throws HibernateException {
        this.delete((Object)customer, s);
    }

    public void refresh(Customer customer, Session s) throws HibernateException {
        this.refresh((Object)customer, s);
    }
}


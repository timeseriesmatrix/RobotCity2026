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

import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.dao.EmployeeInOutHistoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseEmployeeInOutHistoryDAO
extends _RootDAO {
    public static EmployeeInOutHistoryDAO instance;

    public static EmployeeInOutHistoryDAO getInstance() {
        if (null == instance) {
            instance = new EmployeeInOutHistoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return EmployeeInOutHistory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public EmployeeInOutHistory cast(Object object) {
        return (EmployeeInOutHistory)object;
    }

    public EmployeeInOutHistory get(Integer key) throws HibernateException {
        return (EmployeeInOutHistory)this.get(this.getReferenceClass(), key);
    }

    public EmployeeInOutHistory get(Integer key, Session s) throws HibernateException {
        return (EmployeeInOutHistory)this.get(this.getReferenceClass(), key, s);
    }

    public EmployeeInOutHistory load(Integer key) throws HibernateException {
        return (EmployeeInOutHistory)this.load(this.getReferenceClass(), key);
    }

    public EmployeeInOutHistory load(Integer key, Session s) throws HibernateException {
        return (EmployeeInOutHistory)this.load(this.getReferenceClass(), key, s);
    }

    public EmployeeInOutHistory loadInitialize(Integer key, Session s) throws HibernateException {
        EmployeeInOutHistory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<EmployeeInOutHistory> findAll() {
        return super.findAll();
    }

    @Override
    public List<EmployeeInOutHistory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<EmployeeInOutHistory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(EmployeeInOutHistory employeeInOutHistory) throws HibernateException {
        return (Integer)super.save(employeeInOutHistory);
    }

    public Integer save(EmployeeInOutHistory employeeInOutHistory, Session s) throws HibernateException {
        return (Integer)this.save((Object)employeeInOutHistory, s);
    }

    public void saveOrUpdate(EmployeeInOutHistory employeeInOutHistory) throws HibernateException {
        this.saveOrUpdate((Object)employeeInOutHistory);
    }

    public void saveOrUpdate(EmployeeInOutHistory employeeInOutHistory, Session s) throws HibernateException {
        this.saveOrUpdate((Object)employeeInOutHistory, s);
    }

    public void update(EmployeeInOutHistory employeeInOutHistory) throws HibernateException {
        this.update((Object)employeeInOutHistory);
    }

    public void update(EmployeeInOutHistory employeeInOutHistory, Session s) throws HibernateException {
        this.update((Object)employeeInOutHistory, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(EmployeeInOutHistory employeeInOutHistory) throws HibernateException {
        this.delete((Object)employeeInOutHistory);
    }

    public void delete(EmployeeInOutHistory employeeInOutHistory, Session s) throws HibernateException {
        this.delete((Object)employeeInOutHistory, s);
    }

    public void refresh(EmployeeInOutHistory employeeInOutHistory, Session s) throws HibernateException {
        this.refresh((Object)employeeInOutHistory, s);
    }
}


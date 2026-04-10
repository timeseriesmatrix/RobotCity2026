/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Disjunction
 *  org.hibernate.criterion.MatchMode
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.Customer;
import com.floreantpos.model.dao.BaseCustomerDAO;
import com.floreantpos.swing.PaginatedTableModel;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CustomerDAO
extends BaseCustomerDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)Customer.PROP_AUTO_ID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumberOfCustomers() {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.setProjection(Projections.rowCount());
            Number rowCount = (Number)criteria.uniqueResult();
            if (rowCount != null) {
                int n = rowCount.intValue();
                return n;
            }
            int n = 0;
            return n;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumberOfCustomers(String searchString) {
        Session session;
        block5: {
            Criteria criteria;
            block4: {
                int n;
                session = null;
                criteria = null;
                try {
                    if (!StringUtils.isEmpty((String)searchString)) break block4;
                    n = 0;
                }
                catch (Throwable throwable) {
                    this.closeSession(session);
                    throw throwable;
                }
                this.closeSession(session);
                return n;
            }
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike((String)Customer.PROP_MOBILE_NO, (Object)("%" + searchString + "%")));
            disjunction.add(Restrictions.ilike((String)Customer.PROP_NAME, (Object)("%" + searchString + "%")));
            criteria.add((Criterion)disjunction);
            List list = criteria.list();
            if (list == null) break block5;
            int n = list.size();
            this.closeSession(session);
            return n;
        }
        int n = 0;
        this.closeSession(session);
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumberOfCustomers(String mobile, String loyalty, String name) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            if (StringUtils.isNotEmpty((String)mobile)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_MOBILE_NO, (Object)("%" + mobile + "%")));
            }
            if (StringUtils.isNotEmpty((String)loyalty)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_LOYALTY_NO, (Object)("%" + loyalty + "%")));
            }
            if (StringUtils.isNotEmpty((String)name)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_NAME, (Object)("%" + name + "%")));
            }
            criteria.add((Criterion)disjunction);
            List list = criteria.list();
            if (list != null) {
                int n = list.size();
                return n;
            }
            int n = 0;
            return n;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void findBy(String mobile, String loyalty, String name, PaginatedTableModel tableModel) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            if (StringUtils.isNotEmpty((String)mobile)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_MOBILE_NO, (Object)("%" + mobile + "%")));
            }
            if (StringUtils.isNotEmpty((String)loyalty)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_LOYALTY_NO, (Object)("%" + loyalty + "%")));
            }
            if (StringUtils.isNotEmpty((String)name)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_NAME, (Object)("%" + name + "%")));
            }
            criteria.add((Criterion)disjunction);
            criteria.setFirstResult(tableModel.getCurrentRowIndex());
            criteria.setMaxResults(tableModel.getPageSize());
            tableModel.setRows(criteria.list());
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Customer> findBy(String mobile, String loyalty, String name) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            if (StringUtils.isNotEmpty((String)mobile)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_MOBILE_NO, (Object)("%" + mobile + "%")));
            }
            if (StringUtils.isNotEmpty((String)loyalty)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_LOYALTY_NO, (Object)("%" + loyalty + "%")));
            }
            if (StringUtils.isNotEmpty((String)name)) {
                disjunction.add(Restrictions.ilike((String)Customer.PROP_NAME, (Object)("%" + name + "%")));
            }
            criteria.add((Criterion)disjunction);
            List list = criteria.list();
            if (list != null || list.size() != 0) {
                List list2 = list;
                return list2;
            }
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void findBy(String searchString, PaginatedTableModel tableModel) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            if (StringUtils.isEmpty((String)searchString)) {
                return;
            }
            disjunction.add(Restrictions.ilike((String)Customer.PROP_MOBILE_NO, (Object)("%" + searchString + "%")));
            disjunction.add(Restrictions.ilike((String)Customer.PROP_NAME, (Object)("%" + searchString + "%")));
            criteria.add((Criterion)disjunction);
            criteria.setFirstResult(tableModel.getCurrentRowIndex());
            criteria.setMaxResults(tableModel.getPageSize());
            tableModel.setRows(criteria.list());
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Customer> findByMobileNumber(String mobileNo) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            if (StringUtils.isNotEmpty((String)mobileNo)) {
                criteria.add((Criterion)Restrictions.eq((String)Customer.PROP_MOBILE_NO, (Object)mobileNo));
            }
            List list = criteria.list();
            return list;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Customer> findByName(String name) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            if (StringUtils.isNotEmpty((String)name)) {
                criteria.add(Restrictions.ilike((String)Customer.PROP_FIRST_NAME, (String)(name + "%".trim()), (MatchMode)MatchMode.ANYWHERE));
            }
            List list = criteria.list();
            return list;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Customer findById(int customerId) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Customer.PROP_AUTO_ID, (Object)customerId));
            Customer customer = (Customer)criteria.uniqueResult();
            return customer;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadCustomers(PaginatedTableModel tableModel) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.addOrder(this.getDefaultOrder());
            criteria.setFirstResult(tableModel.getCurrentRowIndex());
            criteria.setMaxResults(tableModel.getPageSize());
            tableModel.setRows(criteria.list());
            return;
        }
        finally {
            this.closeSession(session);
        }
    }
}


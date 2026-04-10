/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Query
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Junction
 *  org.hibernate.criterion.Projection
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BaseUserDAO;
import com.floreantpos.util.UserNotFoundException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class UserDAO
extends BaseUserDAO {
    public static final UserDAO instance = new UserDAO();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<User> findAllActive() {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            Junction activeUserCriteria = Restrictions.disjunction().add(Restrictions.isNull((String)User.PROP_ACTIVE)).add((Criterion)Restrictions.eq((String)User.PROP_ACTIVE, (Object)Boolean.TRUE));
            criteria.add((Criterion)activeUserCriteria);
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_CLOCKED_IN, (Object)Boolean.TRUE));
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
    public List<User> findDrivers() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_DRIVER, (Object)Boolean.TRUE));
            List list = criteria.list();
            return list;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public User findUser(int id) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_USER_ID, (Object)id));
            Object result = criteria.uniqueResult();
            if (result != null) {
                User user = (User)result;
                return user;
            }
            throw new UserNotFoundException(Messages.getString("UserDAO.0") + id + Messages.getString("UserDAO.1"));
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
    public User findUserBySecretKey(String secretKey) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_PASSWORD, (Object)secretKey));
            Object result = criteria.uniqueResult();
            User user = (User)result;
            return user;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public boolean isUserExist(int id) {
        try {
            User user = this.findUser(id);
            return user != null;
        }
        catch (UserNotFoundException x) {
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer findUserWithMaxId() {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.setProjection((Projection)Projections.max((String)User.PROP_USER_ID));
            List list = criteria.list();
            if (list != null && list.size() > 0) {
                Integer n = (Integer)list.get(0);
                return n;
            }
            Integer n = null;
            return n;
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
    public List<User> getClockedInUser(Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_CLOCKED_IN, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)User.PROP_CURRENT_TERMINAL, (Object)terminal));
            List list = criteria.list();
            return list;
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public void saveClockIn(User user, AttendenceHistory attendenceHistory, Shift shift, Calendar currentTime) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)user);
            session.saveOrUpdate((Object)attendenceHistory);
            tx.commit();
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            if (tx != null) {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            throw new PosException(Messages.getString("UserDAO.2"), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public void saveClockOut(User user, AttendenceHistory attendenceHistory, Shift shift, Calendar currentTime) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)user);
            session.saveOrUpdate((Object)attendenceHistory);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            throw new PosException(Messages.getString("UserDAO.3"), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public void saveDriverOut(User user, EmployeeInOutHistory attendenceHistory, Shift shift, Calendar currentTime) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)user);
            session.saveOrUpdate((Object)attendenceHistory);
            tx.commit();
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            if (tx != null) {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            throw new PosException(Messages.getString("UserDAO.2"), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    public void saveDriverIn(User user, EmployeeInOutHistory attendenceHistory, Shift shift, Calendar currentTime) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)user);
            session.saveOrUpdate((Object)attendenceHistory);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            throw new PosException(Messages.getString("UserDAO.3"), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
    }

    private boolean validate(User user, boolean editMode) throws PosException {
        String hql = "from User u where u.userId=:userId and u.type=:userType";
        Session session = this.getSession();
        Query query = session.createQuery(hql);
        query = query.setParameter("userId", (Object)user.getUserId());
        if ((query = query.setParameter("userType", (Object)user.getType())).list().size() > 0) {
            throw new PosException(Messages.getString("UserDAO.7"));
        }
        return true;
    }

    public void saveOrUpdate(User user, boolean editMode) {
        Session session = null;
        try {
            if (!editMode) {
                this.validate(user, editMode);
            }
            super.saveOrUpdate(user);
        }
        catch (Exception x) {
            throw new PosException(Messages.getString("UserDAO.8"), x);
        }
        finally {
            this.closeSession(session);
        }
    }

    public int findNumberOfOpenTickets(User user) throws PosException {
        Session session = null;
        Transaction tx = null;
        String hql = "select count(*) from Ticket ticket where ticket.owner=:owner and ticket." + Ticket.PROP_CLOSED + "settled=false";
        int count = 0;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query = query.setEntity("owner", (Object)user);
            Iterator iterator = query.iterate();
            if (iterator.hasNext()) {
                count = (Integer)iterator.next();
            }
            tx.commit();
            int n = count;
            return n;
        }
        catch (Exception e) {
            try {
                if (tx != null) {
                    tx.rollback();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw new PosException(Messages.getString("UserDAO.12"), e);
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.HibernateException
 *  org.hibernate.Query
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Expression
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.dao._RootDAO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.TimeLimitExceededException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

public abstract class _BaseRootDAO {
    protected static Map<String, SessionFactory> sessionFactoryMap;
    protected static SessionFactory sessionFactory;
    protected static ThreadLocal<Map> mappedSessions;
    protected static ThreadLocal<Session> sessions;

    public static void initialize() {
        _RootDAO.initialize(null);
    }

    public static void initialize(String configFileName) {
        _RootDAO.initialize(configFileName, _RootDAO.getNewConfiguration(null));
    }

    public static void initialize(String configFileName, Configuration configuration) {
        if (null == configFileName && null != sessionFactory) {
            return;
        }
        if (null != sessionFactoryMap && null != sessionFactoryMap.get(configFileName)) {
            return;
        }
        if (null == configFileName) {
            configuration.configure();
            _RootDAO.setSessionFactory(configuration.buildSessionFactory());
        } else {
            configuration.configure(configFileName);
            _RootDAO.setSessionFactory(configFileName, configuration.buildSessionFactory());
        }
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        _BaseRootDAO.setSessionFactory(null, sessionFactory);
    }

    public static void setSessionFactory(String configFileName, SessionFactory sf) {
        if (null == configFileName) {
            sessionFactory = sf;
        } else {
            if (null == sessionFactoryMap) {
                sessionFactoryMap = new HashMap<String, SessionFactory>();
            }
            sessionFactoryMap.put(configFileName, sessionFactory);
        }
    }

    protected SessionFactory getSessionFactory() {
        return this.getSessionFactory(this.getConfigurationFileName());
    }

    protected SessionFactory getSessionFactory(String configFile) {
        if (null == configFile) {
            if (null == sessionFactory) {
                throw new RuntimeException("The session factory has not been initialized (or an error occured during initialization)");
            }
            return sessionFactory;
        }
        if (null == sessionFactoryMap) {
            throw new RuntimeException("The session factory for '" + configFile + "' has not been initialized (or an error occured during initialization)");
        }
        SessionFactory sf = sessionFactoryMap.get(configFile);
        if (null == sf) {
            throw new RuntimeException("The session factory for '" + configFile + "' has not been initialized (or an error occured during initialization)");
        }
        return sf;
    }

    public Session getSession() {
        return this.getSession(this.getConfigurationFileName(), false);
    }

    public Session createNewSession() {
        return this.getSession(this.getConfigurationFileName(), true);
    }

    protected Session getSession(String configFile, boolean createNew) {
        Session session;
        HashMap<String, Session> map;
        if (createNew) {
            return this.getSessionFactory(configFile).openSession();
        }
        if (null == configFile) {
            Session session2;
            if (null == sessions) {
                sessions = new ThreadLocal();
            }
            if (null == (session2 = sessions.get()) || !session2.isOpen()) {
                session2 = this.getSessionFactory(null).openSession();
                sessions.set(session2);
            }
            return session2;
        }
        if (null == mappedSessions) {
            mappedSessions = new ThreadLocal();
        }
        if (null == (map = mappedSessions.get())) {
            map = new HashMap<String, Session>(1);
            mappedSessions.set(map);
        }
        if (null == (session = (Session)map.get(configFile)) || !session.isOpen()) {
            session = this.getSessionFactory(configFile).openSession();
            map.put(configFile, session);
        }
        return session;
    }

    public static void closeCurrentThreadSessions() {
        Map map;
        Session session;
        if (null != sessions && null != (session = sessions.get()) && session.isOpen()) {
            session.close();
            sessions.set(null);
        }
        if (null != mappedSessions && null != (map = mappedSessions.get())) {
            HibernateException thrownException = null;
            for (Session session2 : map.values()) {
                try {
                    if (null == session2 || !session2.isOpen()) continue;
                    session2.close();
                }
                catch (HibernateException e) {
                    thrownException = e;
                }
            }
            map.clear();
            if (null != thrownException) {
                throw thrownException;
            }
        }
    }

    public void closeSession(Session session) {
        if (null != session) {
            session.close();
        }
    }

    public Transaction beginTransaction(Session s) {
        return s.beginTransaction();
    }

    public void commitTransaction(Transaction t) {
        t.commit();
    }

    public static Configuration getNewConfiguration(String configFileName) {
        return new Configuration();
    }

    public String getConfigurationFileName() {
        return null;
    }

    protected abstract Class getReferenceClass();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object get(Class refClass, Serializable key) {
        Session s = null;
        try {
            s = this.getSession();
            Object object = this.get(refClass, key, s);
            return object;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Object get(Class refClass, Serializable key, Session s) {
        return s.get(refClass, key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object load(Class refClass, Serializable key) {
        Session s = null;
        try {
            s = this.getSession();
            Object object = this.load(refClass, key, s);
            return object;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Object load(Class refClass, Serializable key, Session s) {
        return s.load(refClass, key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List findAll() {
        Session s = null;
        try {
            s = this.getSession();
            List list = this.findAll(s);
            return list;
        }
        finally {
            this.closeSession(s);
        }
    }

    public List findAll(Session s) {
        return this.findAll(s, this.getDefaultOrder());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List findAll(Order defaultOrder) {
        Session s = null;
        try {
            s = this.getSession();
            List list = this.findAll(s, defaultOrder);
            return list;
        }
        finally {
            this.closeSession(s);
        }
    }

    public List findAll(Session s, Order defaultOrder) {
        Criteria crit = s.createCriteria(this.getReferenceClass());
        if (null != defaultOrder) {
            crit.addOrder(defaultOrder);
        }
        return crit.list();
    }

    protected Criteria findFiltered(String propName, Object filter) {
        return this.findFiltered(propName, filter, this.getDefaultOrder());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Criteria findFiltered(String propName, Object filter, Order order) {
        Session s = null;
        try {
            s = this.getSession();
            Criteria criteria = this.findFiltered(s, propName, filter, order);
            return criteria;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Criteria findFiltered(Session s, String propName, Object filter, Order order) {
        Criteria crit = s.createCriteria(this.getReferenceClass());
        crit.add((Criterion)Expression.eq((String)propName, (Object)filter));
        if (null != order) {
            crit.addOrder(order);
        }
        return crit;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getNamedQuery(String name) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getNamedQuery(name, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getNamedQuery(String name, Session s) {
        Query q = s.getNamedQuery(name);
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getNamedQuery(String name, Serializable param) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getNamedQuery(name, param, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getNamedQuery(String name, Serializable param, Session s) {
        Query q = s.getNamedQuery(name);
        q.setParameter(0, (Object)param);
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getNamedQuery(String name, Serializable[] params) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getNamedQuery(name, params, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getNamedQuery(String name, Serializable[] params, Session s) {
        Query q = s.getNamedQuery(name);
        if (null != params) {
            for (int i = 0; i < params.length; ++i) {
                q.setParameter(i, (Object)params[i]);
            }
        }
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getNamedQuery(String name, Map params) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getNamedQuery(name, params, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getNamedQuery(String name, Map params, Session s) {
        Query q = s.getNamedQuery(name);
        if (null != params) {
            for (Map.Entry entry : params.entrySet()) {
                q.setParameter((String)entry.getKey(), entry.getValue());
            }
        }
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Query getQuery(String queryStr) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getQuery(queryStr, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    public Query getQuery(String queryStr, Session s) {
        return s.createQuery(queryStr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getQuery(String queryStr, Serializable param) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getQuery(queryStr, param, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getQuery(String queryStr, Serializable param, Session s) {
        Query q = this.getQuery(queryStr, s);
        q.setParameter(0, (Object)param);
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getQuery(String queryStr, Serializable[] params) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getQuery(queryStr, params, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getQuery(String queryStr, Serializable[] params, Session s) {
        Query q = this.getQuery(queryStr, s);
        if (null != params) {
            for (int i = 0; i < params.length; ++i) {
                q.setParameter(i, (Object)params[i]);
            }
        }
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Query getQuery(String queryStr, Map params) {
        Session s = null;
        try {
            s = this.getSession();
            Query query = this.getQuery(queryStr, params, s);
            return query;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected Query getQuery(String queryStr, Map params, Session s) {
        Query q = this.getQuery(queryStr, s);
        if (null != params) {
            for (Map.Entry entry : params.entrySet()) {
                q.setParameter((String)entry.getKey(), entry.getValue());
            }
        }
        return q;
    }

    protected Order getDefaultOrder() {
        return null;
    }

    protected Serializable save(final Object obj) {
        return (Serializable)this.run(new TransactionRunnable(){

            @Override
            public Object run(Session s) {
                return _BaseRootDAO.this.save(obj, s);
            }
        });
    }

    protected Serializable save(Object obj, Session s) {
        return s.save(obj);
    }

    protected void saveOrUpdate(final Object obj) {
        this.run(new TransactionRunnable(){

            @Override
            public Object run(Session s) {
                _BaseRootDAO.this.saveOrUpdate(obj, s);
                return null;
            }
        });
    }

    protected void saveOrUpdate(Object obj, Session s) {
        s.saveOrUpdate(obj);
    }

    protected void update(final Object obj) {
        this.run(new TransactionRunnable(){

            @Override
            public Object run(Session s) {
                _BaseRootDAO.this.update(obj, s);
                return null;
            }
        });
    }

    protected void update(Object obj, Session s) {
        s.update(obj);
    }

    protected int delete(final Query query) {
        Integer rtn = (Integer)this.run(new TransactionRunnable(){

            @Override
            public Object run(Session s) {
                return new Integer(_BaseRootDAO.this.delete(query, s));
            }
        });
        return rtn;
    }

    protected int delete(Query query, Session s) {
        List list = query.list();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            this.delete(i.next(), s);
        }
        return list.size();
    }

    protected void delete(final Object obj) {
        this.run(new TransactionRunnable(){

            @Override
            public Object run(Session s) {
                _BaseRootDAO.this.delete(obj, s);
                return null;
            }
        });
    }

    protected void delete(Object obj, Session s) {
        s.delete(obj);
    }

    protected void refresh(Object obj, Session s) {
        s.refresh(obj);
    }

    protected void throwException(Throwable t) {
        if (t instanceof HibernateException) {
            throw (HibernateException)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new HibernateException(t);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object run(TransactionRunnable transactionRunnable) {
        Transaction t = null;
        Session s = null;
        try {
            s = this.getSession();
            t = this.beginTransaction(s);
            Object obj = transactionRunnable.run(s);
            this.commitTransaction(t);
            Object object = obj;
            return object;
        }
        catch (Throwable throwable) {
            if (null != t) {
                try {
                    t.rollback();
                }
                catch (HibernateException e) {
                    this.handleError(e);
                }
            }
            if (transactionRunnable instanceof TransactionFailHandler) {
                try {
                    ((TransactionFailHandler)((Object)transactionRunnable)).onFail(s);
                }
                catch (Throwable e) {
                    this.handleError(e);
                }
            }
            this.throwException(throwable);
            Object var5_9 = null;
            return var5_9;
        }
        finally {
            this.closeSession(s);
        }
    }

    protected TransactionPointer runAsnyc(TransactionRunnable transactionRunnable) {
        TransactionPointer transactionPointer = new TransactionPointer(transactionRunnable);
        ThreadRunner threadRunner = new ThreadRunner(transactionPointer);
        threadRunner.start();
        return transactionPointer;
    }

    protected void handleError(Throwable t) {
    }

    private class ThreadRunner
    extends Thread {
        private TransactionPointer transactionPointer;

        public ThreadRunner(TransactionPointer transactionPointer) {
            this.transactionPointer = transactionPointer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Transaction t = null;
            Session s = null;
            try {
                s = _BaseRootDAO.this.getSession();
                t = _BaseRootDAO.this.beginTransaction(s);
                Object obj = this.transactionPointer.getTransactionRunnable().run(s);
                t.commit();
                this.transactionPointer.setReturnValue(obj);
            }
            catch (Throwable throwable) {
                if (null != t) {
                    try {
                        t.rollback();
                    }
                    catch (HibernateException e) {
                        _BaseRootDAO.this.handleError(e);
                    }
                }
                if (this.transactionPointer.getTransactionRunnable() instanceof TransactionFailHandler) {
                    try {
                        ((TransactionFailHandler)((Object)this.transactionPointer.getTransactionRunnable())).onFail(s);
                    }
                    catch (Throwable e) {
                        _BaseRootDAO.this.handleError(e);
                    }
                }
                this.transactionPointer.setThrownException(throwable);
            }
            finally {
                this.transactionPointer.complete();
                try {
                    _BaseRootDAO.this.closeSession(s);
                }
                catch (HibernateException e) {
                    this.transactionPointer.setThrownException(e);
                }
            }
        }
    }

    public class TransactionPointer {
        private TransactionRunnable transactionRunnable;
        private Throwable thrownException;
        private Object returnValue;
        private boolean hasCompleted = false;

        public TransactionPointer(TransactionRunnable transactionRunnable) {
            this.transactionRunnable = transactionRunnable;
        }

        public boolean hasCompleted() {
            return this.hasCompleted;
        }

        public void complete() {
            this.hasCompleted = true;
        }

        public Object getReturnValue() {
            return this.returnValue;
        }

        public void setReturnValue(Object returnValue) {
            this.returnValue = returnValue;
        }

        public Throwable getThrownException() {
            return this.thrownException;
        }

        public void setThrownException(Throwable thrownException) {
            this.thrownException = thrownException;
        }

        public TransactionRunnable getTransactionRunnable() {
            return this.transactionRunnable;
        }

        public void setTransactionRunnable(TransactionRunnable transactionRunnable) {
            this.transactionRunnable = transactionRunnable;
        }

        public Object waitUntilFinish(long timeout) throws Throwable {
            long killTime = -1L;
            if (timeout > 0L) {
                killTime = System.currentTimeMillis() + timeout;
            }
            do {
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            } while (!this.hasCompleted && (killTime > 0L && System.currentTimeMillis() < killTime || killTime <= 0L));
            if (!this.hasCompleted) {
                throw new TimeLimitExceededException();
            }
            if (null != this.thrownException) {
                throw this.thrownException;
            }
            return this.returnValue;
        }
    }

    public abstract class TransactionRunnableFailHandler
    extends TransactionRunnable
    implements TransactionFailHandler {
    }

    public static interface TransactionFailHandler {
        public void onFail(Session var1);
    }

    public abstract class TransactionRunnable {
        public abstract Object run(Session var1) throws Exception;
    }
}


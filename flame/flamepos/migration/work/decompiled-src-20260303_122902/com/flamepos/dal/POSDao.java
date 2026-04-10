/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.dal;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.dal.PosSessionFactory;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class POSDao {
    public static void save(Object o) throws Exception {
        Transaction tx = null;
        try {
            Session session = PosSessionFactory.currentSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(o);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
        finally {
            PosSessionFactory.closeSession();
        }
    }

    public static void delete(Object o) throws PosException {
        Transaction tx = null;
        try {
            Session session = PosSessionFactory.currentSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PosException(POSConstants.UNNABLE_TO_SAVE_ + o.getClass(), e);
        }
        finally {
            PosSessionFactory.closeSession();
        }
    }

    public static List findAll(Class clazz) throws PosException {
        try {
            List list;
            Session session = PosSessionFactory.currentSession();
            List list2 = list = session.createCriteria(clazz).list();
            return list2;
        }
        catch (Exception e) {
            throw new PosException(e);
        }
        finally {
            PosSessionFactory.closeSession();
        }
    }

    public static Object findUnique(Class clazz) throws PosException {
        try {
            Object object;
            Session session = PosSessionFactory.currentSession();
            Object object2 = object = session.createCriteria(clazz).uniqueResult();
            return object2;
        }
        catch (Exception e) {
            throw new PosException(e);
        }
        finally {
            PosSessionFactory.closeSession();
        }
    }
}


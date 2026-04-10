/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Projections
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.dao.BaseShopFloorDAO;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

public class ShopFloorDAO
extends BaseShopFloorDAO {
    public boolean hasFloor() {
        Number result = (Number)this.getSession().createCriteria(this.getReferenceClass()).setProjection(Projections.rowCount()).uniqueResult();
        return result.intValue() != 0;
    }

    @Override
    public void delete(ShopFloor shopFloor) throws HibernateException {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            Set<ShopTable> tables = shopFloor.getTables();
            if (tables != null && !tables.isEmpty()) {
                shopFloor.getTables().removeAll(tables);
                this.saveOrUpdate(shopFloor);
            }
            super.delete(shopFloor, session);
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            LogFactory.getLog(ShopFloorDAO.class).error((Object)e);
            throw new HibernateException((Throwable)e);
        }
        finally {
            this.closeSession(session);
        }
    }
}


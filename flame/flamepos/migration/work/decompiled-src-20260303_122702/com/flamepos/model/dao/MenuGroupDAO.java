/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BaseMenuGroupDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class MenuGroupDAO
extends BaseMenuGroupDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuGroup> findEnabledByParent(MenuCategory category) throws PosException {
        if (category.getId() == null) {
            return null;
        }
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuGroup.PROP_VISIBLE, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)MenuGroup.PROP_PARENT, (Object)category));
            criteria.addOrder(Order.asc((String)MenuGroup.PROP_SORT_ORDER));
            List list = criteria.list();
            for (MenuGroup menuGroup : list) {
                menuGroup.setParent(category);
            }
            List list2 = list;
            return list2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuGroup> findByParent(MenuCategory category) throws PosException {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuGroup.PROP_PARENT, (Object)category));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public boolean hasChildren(Terminal terminal, MenuGroup group, OrderType orderType) throws PosException {
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(MenuItem.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PARENT, (Object)group));
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_VISIBLE, (Object)Boolean.TRUE));
            criteria.setProjection(Projections.rowCount());
            criteria.createAlias("orderTypeList", "type", 1);
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isEmpty((String)"orderTypeList"), (Criterion)Restrictions.eq((String)"type.id", (Object)orderType.getId())));
            int uniqueResult = (Integer)criteria.uniqueResult();
            boolean bl = uniqueResult > 0;
            return bl;
        }
    }

    public void releaseParent(List<MenuGroup> menuGroupList) {
        if (menuGroupList == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (MenuGroup menuGroup : menuGroupList) {
                menuGroup.setParent(null);
                session.saveOrUpdate((Object)menuGroup);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            LogFactory.getLog(ShopTableDAO.class).error((Object)e);
            throw new RuntimeException(e);
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveAll(List<MenuGroup> menuGroups) {
        if (menuGroups == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (MenuGroup group : menuGroups) {
                session.saveOrUpdate((Object)group);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            PosLog.error(this.getClass(), e);
        }
        finally {
            this.closeSession(session);
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.dao.BaseMenuItemSizeDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MenuItemSizeDAO
extends BaseMenuItemSizeDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)MenuItemSize.PROP_SORT_ORDER);
    }

    public void setDefault(List<MenuItemSize> items) {
        Transaction tx = null;
        try (Session session = null;){
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.saveOrUpdateSizeList(items, session);
            tx.commit();
        }
    }

    public void saveOrUpdateSizeList(List<MenuItemSize> items, Session session) {
        for (MenuItemSize menuItemSize : items) {
            session.saveOrUpdate((Object)menuItemSize);
        }
    }

    public MenuItemSize findByName(String sizeName) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(MenuItemSize.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuItemSize.PROP_NAME, (Object)sizeName));
            return (MenuItemSize)criteria.list().get(0);
        }
        catch (Exception exception) {
            return null;
        }
    }
}


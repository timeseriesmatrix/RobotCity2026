/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.authorize.util.StringUtils
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.MatchMode
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.BaseMenuItemDAO;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import java.util.ArrayList;
import java.util.List;
import net.authorize.util.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MenuItemDAO
extends BaseMenuItemDAO {
    public MenuItem loadInitialized(Integer key) throws HibernateException {
        MenuItem menuItem = super.get(key);
        menuItem = this.initialize(menuItem);
        return menuItem;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MenuItem initialize(MenuItem menuItem) {
        if (menuItem == null || menuItem.getId() == null) {
            return menuItem;
        }
        Session session = null;
        try {
            session = this.createNewSession();
            menuItem = (MenuItem)session.merge((Object)menuItem);
            Hibernate.initialize(menuItem.getMenuItemModiferGroups());
            List<MenuItemModifierGroup> menuItemModiferGroups = menuItem.getMenuItemModiferGroups();
            if (menuItemModiferGroups != null) {
                for (MenuItemModifierGroup menuItemModifierGroup : menuItemModiferGroups) {
                    Hibernate.initialize(menuItemModifierGroup.getModifierGroup().getModifiers());
                }
            }
            Hibernate.initialize(menuItem.getShifts());
            MenuItem menuItem2 = menuItem;
            return menuItem2;
        }
        finally {
            this.closeSession(session);
        }
    }

    public List<MenuItem> findByParent(Terminal terminal, MenuGroup group, boolean includeInvisibleItems) throws PosException {
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PARENT, (Object)group));
            criteria.addOrder(Order.asc((String)MenuItem.PROP_SORT_ORDER));
            if (!includeInvisibleItems) {
                criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_VISIBLE, (Object)Boolean.TRUE));
            }
            List list = criteria.list();
            return list;
        }
    }

    public List<MenuItem> findByParent(Terminal terminal, MenuGroup menuGroup, Object selectedOrderType, boolean includeInvisibleItems) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(MenuItem.class);
            if (menuGroup != null) {
                criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PARENT, (Object)menuGroup));
            }
            criteria.addOrder(Order.asc((String)MenuItem.PROP_SORT_ORDER));
            if (!includeInvisibleItems) {
                criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_VISIBLE, (Object)Boolean.TRUE));
            }
            if (selectedOrderType instanceof OrderType) {
                OrderType orderType = (OrderType)selectedOrderType;
                criteria.createAlias("orderTypeList", "type", 1);
                criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isEmpty((String)"orderTypeList"), (Criterion)Restrictions.eq((String)"type.id", (Object)orderType.getId())));
            }
            return criteria.list();
        }
        catch (Exception exception) {
            return criteria.list();
        }
    }

    public List<MenuItemModifierGroup> findModifierGroups(MenuItem item) throws PosException {
        try (Session session = null;){
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_ID, (Object)item.getId()));
            MenuItem newItem = (MenuItem)criteria.uniqueResult();
            Hibernate.initialize(newItem.getMenuItemModiferGroups());
            List<MenuItemModifierGroup> list = newItem.getMenuItemModiferGroups();
            return list;
        }
    }

    public List<MenuItem> getMenuItems(String itemName, Object menuGroup, Object selectedType) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(MenuItem.class);
            if (menuGroup != null && menuGroup instanceof MenuGroup) {
                criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PARENT, (Object)((MenuGroup)menuGroup)));
            } else if (menuGroup != null && menuGroup instanceof String) {
                criteria.add(Restrictions.isNull((String)MenuItem.PROP_PARENT));
            }
            if (StringUtils.isNotEmpty((String)itemName)) {
                criteria.add(Restrictions.ilike((String)MenuItem.PROP_NAME, (String)itemName.trim(), (MatchMode)MatchMode.ANYWHERE));
            }
            if (selectedType != null && selectedType instanceof OrderType) {
                OrderType orderType = (OrderType)selectedType;
                criteria.createAlias("orderTypeList", "type");
                criteria.add((Criterion)Restrictions.eq((String)"type.id", (Object)orderType.getId()));
            } else if (selectedType != null && selectedType instanceof String) {
                criteria.add(Restrictions.isEmpty((String)"orderTypeList"));
            }
            return criteria.list();
        }
        catch (Exception exception) {
            return criteria.list();
        }
    }

    public List<MenuItem> getPizzaItems(String itemName, MenuGroup menuGroup, Object selectedType) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(MenuItem.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PIZZA_TYPE, (Object)true));
            if (menuGroup != null) {
                criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PARENT, (Object)menuGroup));
            }
            if (StringUtils.isNotEmpty((String)itemName)) {
                criteria.add(Restrictions.ilike((String)MenuItem.PROP_NAME, (String)itemName.trim(), (MatchMode)MatchMode.ANYWHERE));
            }
            if (selectedType instanceof OrderType) {
                OrderType orderType = (OrderType)selectedType;
                criteria.createAlias("orderTypeList", "type");
                criteria.add((Criterion)Restrictions.eq((String)"type.id", (Object)orderType.getId()));
            }
            return criteria.list();
        }
        catch (Exception exception) {
            return criteria.list();
        }
    }

    public void releaseParent(List<MenuItem> menuItemList) {
        if (menuItemList == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (MenuItem menuItem : menuItemList) {
                menuItem.setParent(null);
                session.saveOrUpdate((Object)menuItem);
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

    public void releaseParentAndDelete(MenuItem item) {
        if (item == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            List<Discount> itemCoupons = item.getDiscounts();
            for (Discount coupon : itemCoupons) {
                ArrayList<MenuItem> mergeItems = new ArrayList<MenuItem>();
                for (MenuItem menuItem : coupon.getMenuItems()) {
                    if (menuItem == item) continue;
                    mergeItems.add(menuItem);
                }
                coupon.setMenuItems(mergeItems);
                DiscountDAO.getInstance().saveOrUpdate(coupon);
            }
            this.delete(item);
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
    public MenuItem getMenuItemByBarcode(String barcode) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(MenuItem.class);
            criteria.add((Criterion)Restrictions.like((String)MenuItem.PROP_BARCODE, (Object)barcode));
            List result = criteria.list();
            if (result == null || result.isEmpty()) {
                MenuItem menuItem = null;
                return menuItem;
            }
            MenuItem menuItem = (MenuItem)result.get(0);
            return menuItem;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuItem> getPizzaItems() {
        Session session = null;
        Criteria criteria = null;
        try {
            List result;
            session = this.createNewSession();
            criteria = session.createCriteria(MenuItem.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuItem.PROP_PIZZA_TYPE, (Object)true));
            List list = result = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuItem> getMenuItems() {
        Session session = null;
        Criteria criteria = null;
        try {
            List result;
            session = this.createNewSession();
            criteria = session.createCriteria(MenuItem.class);
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)MenuItem.PROP_PIZZA_TYPE, (Object)false), (Criterion)Restrictions.isNull((String)MenuItem.PROP_PIZZA_TYPE)));
            List list = result = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveAll(List<MenuItem> menuItems) {
        if (menuItems == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (MenuItem menuItem : menuItems) {
                session.saveOrUpdate((Object)menuItem);
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


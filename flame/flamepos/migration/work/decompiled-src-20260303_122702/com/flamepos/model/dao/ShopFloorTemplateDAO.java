/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopFloorTemplate;
import com.floreantpos.model.dao.BaseShopFloorTemplateDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class ShopFloorTemplateDAO
extends BaseShopFloorTemplateDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ShopFloorTemplate> findByParent(ShopFloor selectedValue) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)ShopFloorTemplate.PROP_FLOOR, (Object)selectedValue));
            List list = criteria.list();
            return list;
        }
        catch (Exception exception) {
        }
        finally {
            this.closeSession(session);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveOrUpdateTemplates(List<ShopFloorTemplate> templates) {
        if (templates == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopFloorTemplate template : templates) {
                session.saveOrUpdate((Object)template);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteTemplates(List<ShopFloorTemplate> templates) {
        if (templates == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopFloorTemplate template : templates) {
                session.delete((Object)template);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDefaultTemplate(ShopFloorTemplate template, ShopFloor selectedFloor) {
        Session session = null;
        Transaction tx = null;
        try {
            template.setDefaultFloor(true);
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(ShopFloorTemplate.class);
            criteria.add((Criterion)Restrictions.eq((String)ShopFloorTemplate.PROP_DEFAULT_FLOOR, (Object)true));
            criteria.add((Criterion)Restrictions.eq((String)ShopFloorTemplate.PROP_FLOOR, (Object)selectedFloor));
            List list = criteria.list();
            session.close();
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopFloorTemplate t : list) {
                t.setDefaultFloor(false);
                session.saveOrUpdate((Object)t);
            }
            session.saveOrUpdate((Object)template);
            session.saveOrUpdate((Object)selectedFloor);
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Throwable throwable) {
                this.closeSession(session);
                throw throwable;
            }
            this.closeSession(session);
        }
        this.closeSession(session);
    }
}


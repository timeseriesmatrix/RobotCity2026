/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.dao.BasePizzaPriceDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class PizzaPriceDAO
extends BasePizzaPriceDAO {
    public PizzaPrice findBySizeAndCrust(MenuItemSize menuItemSize, PizzaCrust pizzaCrust) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.getSession();
            criteria = session.createCriteria(PizzaPrice.class);
            criteria.add((Criterion)Restrictions.eq((String)PizzaPrice.PROP_SIZE, (Object)menuItemSize));
            if (pizzaCrust != null) {
                criteria.add((Criterion)Restrictions.eq((String)PizzaPrice.PROP_CRUST, (Object)pizzaCrust));
            }
            return (PizzaPrice)criteria.list().get(0);
        }
        catch (Exception exception) {
            return null;
        }
    }
}


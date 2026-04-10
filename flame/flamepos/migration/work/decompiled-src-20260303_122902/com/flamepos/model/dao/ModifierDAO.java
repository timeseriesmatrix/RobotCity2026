/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.MatchMode
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.BaseModifierDAO;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class ModifierDAO
extends BaseModifierDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuModifier> findModifier(String name, MenuModifierGroup menuModifierGroup) {
        Criteria criteria = null;
        try (Session session = null;){
            session = this.getSession();
            criteria = session.createCriteria(MenuModifier.class);
            if (StringUtils.isNotEmpty((String)name)) {
                criteria.add(Restrictions.ilike((String)MenuModifier.PROP_NAME, (String)(name + "%".trim()), (MatchMode)MatchMode.ANYWHERE));
            }
            if (menuModifierGroup != null) {
                criteria.add((Criterion)Restrictions.eq((String)MenuModifier.PROP_MODIFIER_GROUP, (Object)menuModifierGroup));
            }
            List list = criteria.list();
            return list;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuModifier> findPizzaModifier(String name, MenuModifierGroup menuModifierGroup) {
        Criteria criteria = null;
        try (Session session = null;){
            session = this.getSession();
            criteria = session.createCriteria(MenuModifier.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuModifier.PROP_PIZZA_MODIFIER, (Object)true));
            if (StringUtils.isNotEmpty((String)name)) {
                criteria.add(Restrictions.ilike((String)MenuModifier.PROP_NAME, (String)(name + "%".trim()), (MatchMode)MatchMode.ANYWHERE));
            }
            if (menuModifierGroup != null) {
                criteria.add((Criterion)Restrictions.eq((String)MenuModifier.PROP_MODIFIER_GROUP, (Object)menuModifierGroup));
            }
            List list = criteria.list();
            return list;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MenuModifier> getPizzaModifiers() {
        Criteria criteria = null;
        try (Session session = null;){
            session = this.createNewSession();
            criteria = session.createCriteria(MenuModifier.class);
            criteria.add((Criterion)Restrictions.eq((String)MenuModifier.PROP_PIZZA_MODIFIER, (Object)true));
            List list = criteria.list();
            return list;
        }
    }
}


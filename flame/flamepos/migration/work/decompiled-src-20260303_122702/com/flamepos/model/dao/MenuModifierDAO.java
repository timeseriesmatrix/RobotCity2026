/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.model.dao;

import com.floreantpos.PosLog;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.dao.BaseMenuModifierDAO;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MenuModifierDAO
extends BaseMenuModifierDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveAll(List<MenuModifier> menuModifiers) {
        if (menuModifiers == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (MenuModifier menuModifier : menuModifiers) {
                session.saveOrUpdate((Object)menuModifier);
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


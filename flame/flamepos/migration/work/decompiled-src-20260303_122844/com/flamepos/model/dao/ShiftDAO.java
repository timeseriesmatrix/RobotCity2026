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

import com.floreantpos.PosException;
import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.BaseShiftDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class ShiftDAO
extends BaseShiftDAO {
    public boolean exists(String shiftName) throws PosException {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Shift.PROP_NAME, (Object)shiftName));
            List list = criteria.list();
            if (list != null && list.size() > 0) {
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        catch (Exception e) {
            throw new PosException("An error occured while trying to check Shift duplicacy", e);
        }
        finally {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    public void refresh(Shift shift) throws PosException {
        Session session = null;
        try {
            session = this.createNewSession();
            session.refresh((Object)shift);
        }
        catch (Exception e) {
            throw new PosException("An error occured while refreshing Shift state.", e);
        }
        finally {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception exception) {}
            }
        }
    }
}


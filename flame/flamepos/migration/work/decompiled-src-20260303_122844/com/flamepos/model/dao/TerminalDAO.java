/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Query
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.CashDrawerResetHistory;
import com.floreantpos.model.DrawerAssignedHistory;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BaseTerminalDAO;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class TerminalDAO
extends BaseTerminalDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refresh(Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            session.refresh((Object)terminal);
        }
        finally {
            this.closeSession(session);
        }
    }

    public void performBatchSave(Object ... objects) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (Object object : objects) {
                if (object == null) continue;
                session.saveOrUpdate(object);
            }
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw e;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Terminal getByTerminalKey(String terminalKey) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Terminal.PROP_TERMINAL_KEY, (Object)terminalKey));
            List list = criteria.list();
            if (list.size() > 0) {
                Terminal terminal = (Terminal)list.get(0);
                return terminal;
            }
            Terminal terminal = null;
            return terminal;
        }
        finally {
            this.closeSession(session);
        }
    }

    public void resetCashDrawer(DrawerPullReport report, Terminal terminal, User user, double balance) throws Exception {
        Session session = null;
        Transaction tx = null;
        CashDrawerResetHistory history = new CashDrawerResetHistory();
        history.setDrawerPullReport(report);
        history.setResetedBy(user);
        history.setResetTime(new Date());
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            String hql = "update Ticket t set t.drawerResetted=true where t." + Ticket.PROP_CLOSED + "=true and t.drawerResetted=false and t.terminal=:terminal";
            Query query = session.createQuery(hql);
            query.setEntity("terminal", (Object)terminal);
            query.executeUpdate();
            hql = "update PosTransaction t set t.drawerResetted=true where t.drawerResetted=false and t.terminal=:terminal";
            query = session.createQuery(hql);
            query.setEntity("terminal", (Object)terminal);
            query.executeUpdate();
            terminal.setAssignedUser(null);
            terminal.setOpeningBalance(balance);
            terminal.setCurrentBalance(balance);
            this.update(terminal, session);
            this.save(report, session);
            this.save(history, session);
            DrawerAssignedHistory history2 = new DrawerAssignedHistory();
            history2.setTime(new Date());
            history2.setOperation(DrawerAssignedHistory.CLOSE_OPERATION);
            history2.setUser(user);
            this.save(history2, session);
            tx.commit();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw e;
        }
        finally {
            this.closeSession(session);
        }
    }
}


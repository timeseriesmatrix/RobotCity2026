/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Disjunction
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.ShopTable;
import com.floreantpos.model.ShopTableType;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.BaseShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ShopTableDAO
extends BaseShopTableDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)ShopTable.PROP_ID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNextTableNumber() {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.setProjection(Projections.rowCount());
            int n = (Integer)criteria.uniqueResult();
            return n;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ShopTable getByNumber(int tableNumber) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)ShopTable.PROP_ID, (Object)tableNumber));
            ShopTable shopTable = (ShopTable)criteria.uniqueResult();
            return shopTable;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ShopTable> getAllUnassigned() {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add(Restrictions.isNull((String)ShopTable.PROP_FLOOR));
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ShopTable> getByNumbers(Collection<Integer> tableNumbers) {
        if (tableNumbers == null || tableNumbers.size() == 0) {
            return null;
        }
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            Disjunction disjunction = Restrictions.disjunction();
            for (Integer tableNumber : tableNumbers) {
                disjunction.add((Criterion)Restrictions.eq((String)ShopTable.PROP_ID, (Object)tableNumber));
            }
            criteria.add((Criterion)disjunction);
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public List<ShopTable> getTables(Ticket ticket) {
        return this.getByNumbers(ticket.getTableNumbers());
    }

    public void occupyTables(Ticket ticket) {
        List<ShopTable> tables = this.getTables(ticket);
        if (tables == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopTable shopTable : tables) {
                shopTable.setServing(true);
                this.saveOrUpdate(shopTable, session);
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

    public void bookedTables(List<ShopTable> tables) {
        if (tables == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopTable shopTable : tables) {
                shopTable.setBooked(true);
                shopTable.setFree(false);
                session.saveOrUpdate((Object)shopTable);
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

    public void freeTables(List<ShopTable> tables) {
        if (tables == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopTable shopTable : tables) {
                shopTable.setBooked(false);
                shopTable.setFree(true);
                session.saveOrUpdate((Object)shopTable);
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

    public void releaseTables(Ticket ticket) {
        List<ShopTable> tables = this.getTables(ticket);
        if (tables == null) {
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopTable shopTable : tables) {
                shopTable.setServing(false);
                shopTable.setBooked(false);
                shopTable.setFree(true);
                this.saveOrUpdate(shopTable, session);
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

    public void releaseAndDeleteTicketTables(Ticket ticket) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.releaseTables(ticket);
            ticket.setTableNumbers(null);
            TicketDAO.getInstance().saveOrUpdate(ticket);
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

    public void deleteTables(Collection<ShopTable> tables) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (ShopTable shopTable : tables) {
                super.delete(shopTable, session);
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

    public List<ShopTableType> getTableByTypes(List<ShopTableType> types) {
        ArrayList<Integer> typeIds = new ArrayList<Integer>();
        for (ShopTableType shopTableType : types) {
            typeIds.add(shopTableType.getId());
        }
        Session session = this.getSession();
        Criteria criteria = session.createCriteria(ShopTable.class);
        criteria.createAlias("types", "t");
        criteria.add(Restrictions.in((String)"t.id", typeIds));
        criteria.addOrder(Order.asc((String)ShopTable.PROP_ID));
        return criteria.list();
    }

    public void createNewTables(int totalNumberOfTableHaveToCreate) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (int i = 0; i < totalNumberOfTableHaveToCreate; ++i) {
                ShopTable table = new ShopTable();
                table.setId(i + 1);
                table.setCapacity(4);
                super.save(table, session);
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
}


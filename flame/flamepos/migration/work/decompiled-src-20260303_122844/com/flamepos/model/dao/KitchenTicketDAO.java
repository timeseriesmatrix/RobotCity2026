/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.BaseKitchenTicketDAO;
import com.floreantpos.swing.PaginatedTableModel;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class KitchenTicketDAO
extends BaseKitchenTicketDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<KitchenTicket> findAllOpen() {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)KitchenTicket.PROP_STATUS, (Object)KitchenTicket.KitchenTicketStatus.WAITING.name()));
            List list2 = list = criteria.list();
            return list2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<KitchenTicket> findByParentId(Integer ticketId) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)KitchenTicket.PROP_TICKET_ID, (Object)ticketId));
            List list2 = list = criteria.list();
            return list2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findNextKitchenTickets(PaginatedTableModel tableModel) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int nextIndex = tableModel.getNextRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.setFirstResult(nextIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            List kitchenTicketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(nextIndex);
            list = kitchenTicketList;
        }
        catch (Throwable throwable) {
            this.closeSession(session);
            throw throwable;
        }
        this.closeSession(session);
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findPreviousKitchenTickets(PaginatedTableModel tableModel) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int previousIndex = tableModel.getPreviousRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.setFirstResult(previousIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            List kitchenTicketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(previousIndex);
            list = kitchenTicketList;
        }
        catch (Throwable throwable) {
            this.closeSession(session);
            throw throwable;
        }
        this.closeSession(session);
        return list;
    }
}


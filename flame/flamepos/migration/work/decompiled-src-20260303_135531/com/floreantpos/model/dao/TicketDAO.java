/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Order
 *  org.hibernate.criterion.Projection
 *  org.hibernate.criterion.ProjectionList
 *  org.hibernate.criterion.Projections
 *  org.hibernate.criterion.Restrictions
 *  org.hibernate.transform.ResultTransformer
 */
package com.floreantpos.model.dao;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.DataUpdateInfo;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.InventoryTransactionType;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PaymentStatusFilter;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.VoidTransaction;
import com.floreantpos.model.base.BasePosTransaction;
import com.floreantpos.model.dao.BaseTicketDAO;
import com.floreantpos.model.dao.DataUpdateInfoDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.model.util.TicketSummary;
import com.floreantpos.swing.PaginatedTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class TicketDAO
extends BaseTicketDAO {
    private static final TicketDAO instance = new TicketDAO();

    @Override
    public Order getDefaultOrder() {
        return Order.desc((String)Ticket.PROP_CREATE_DATE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void saveOrUpdate(Ticket ticket) {
        Transaction tx = null;
        try (Session session = null;){
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.saveOrUpdate(ticket, session);
            tx.commit();
        }
    }

    @Override
    public void saveOrUpdate(Ticket ticket, Session session) {
        this.adjustInventoryItems(session, ticket);
        ticket.setActiveDate(Calendar.getInstance().getTime());
        this.adjustStockAmount(ticket, session);
        session.saveOrUpdate((Object)ticket);
        ticket.clearDeletedItems();
        DataUpdateInfo lastUpdateInfo = DataUpdateInfoDAO.getLastUpdateInfo();
        lastUpdateInfo.setLastUpdateTime(new Date());
        session.update((Object)lastUpdateInfo);
    }

    public void voidTicket(Ticket ticket) throws Exception {
        Session session = null;
        Transaction tx = null;
        Terminal terminal = Application.getInstance().getTerminal();
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            ticket.setVoided(true);
            ticket.setClosed(true);
            ticket.setClosingDate(new Date());
            ticket.setTerminal(terminal);
            if (ticket.isPaid().booleanValue()) {
                BasePosTransaction transaction = null;
                if (ticket.getTransactions() != null) {
                    for (PosTransaction t : ticket.getTransactions()) {
                        if (!(t instanceof VoidTransaction)) continue;
                        transaction = (VoidTransaction)t;
                    }
                }
                if (transaction == null) {
                    transaction = new VoidTransaction();
                }
                transaction.setTicket(ticket);
                transaction.setTerminal(terminal);
                transaction.setTransactionTime(new Date());
                transaction.setTransactionType(TransactionType.DEBIT.name());
                transaction.setPaymentType(PaymentType.CASH.name());
                transaction.setAmount(ticket.getPaidAmount());
                transaction.setTerminal(Application.getInstance().getTerminal());
                transaction.setCaptured(true);
                ticket.addTotransactions((PosTransaction)transaction);
            }
            session.update((Object)ticket);
            session.update((Object)terminal);
            session.flush();
            tx.commit();
        }
        catch (Exception x) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw x;
        }
        finally {
            this.closeSession(session);
        }
    }

    public Ticket loadFullTicket(int id) {
        Session session = this.createNewSession();
        Ticket ticket = (Ticket)session.get(this.getReferenceClass(), (Serializable)Integer.valueOf(id));
        if (ticket == null) {
            return null;
        }
        Hibernate.initialize(ticket.getTicketItems());
        Hibernate.initialize(ticket.getDiscounts());
        Hibernate.initialize(ticket.getTransactions());
        session.close();
        return ticket;
    }

    public Ticket loadCouponsAndTransactions(int id) {
        Session session = this.createNewSession();
        Ticket ticket = (Ticket)session.get(this.getReferenceClass(), (Serializable)Integer.valueOf(id));
        Hibernate.initialize(ticket.getDiscounts());
        Hibernate.initialize(ticket.getTransactions());
        session.close();
        return ticket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Gratuity> getServerGratuities(Terminal terminal, String transactionType) {
        Session session = null;
        ArrayList<Gratuity> gratuities = new ArrayList<Gratuity>();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            criteria.createAlias(Ticket.PROP_GRATUITY, "gratuity");
            criteria.add((Criterion)Restrictions.eq((String)"gratuity.paid", (Object)Boolean.FALSE));
            List list = criteria.list();
            for (Ticket ticket : list) {
                gratuities.add(ticket.getGratuity());
            }
            ArrayList<Gratuity> arrayList = gratuities;
            return arrayList;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getPaidGratuityAmount(Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass(), "t");
            criteria = criteria.createAlias(Ticket.PROP_GRATUITY, "gratuity");
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            criteria.add((Criterion)Restrictions.eq((String)"gratuity.paid", (Object)Boolean.TRUE));
            criteria.setProjection((Projection)Projections.sum((String)"gratuity.amount"));
            List list = criteria.list();
            if (list.size() > 0 && list.get(0) instanceof Number) {
                double d = ((Number)list.get(0)).doubleValue();
                return d;
            }
            double d = 0.0;
            return d;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findOpenTickets() {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.addOrder(this.getDefaultOrder());
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
    public List<Ticket> findOpenTicketsByOrderType(OrderType orderType) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            User user = Application.getCurrentUser();
            if (user != null && !user.canViewAllOpenTickets()) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            }
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TICKET_TYPE, (Object)orderType.getName()));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_BAR_TAB, (Object)true));
            criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.isEmpty((String)"tableNumbers"), (Criterion)Restrictions.isNull((String)"tableNumbers")));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.addOrder(this.getDefaultOrder());
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
    public List<Ticket> findOpenTickets(Integer customerId) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CUSTOMER_ID, (Object)customerId));
            criteria.addOrder(this.getDefaultOrder());
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
    public List<Ticket> findOpenTickets(Terminal terminal, UserType userType) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (userType != null) {
                criteria.createAlias(Ticket.PROP_OWNER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.type", (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            criteria.addOrder(this.getDefaultOrder());
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
    public void loadTickets(PaginatedTableModel tableModel) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            this.updateCriteriaFilters(criteria);
            criteria.addOrder(this.getDefaultOrder());
            criteria.setFirstResult(tableModel.getCurrentRowIndex());
            criteria.setMaxResults(tableModel.getPageSize());
            tableModel.setRows(criteria.list());
            return;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findCustomerTickets(Integer customerId, PaginatedTableModel tableModel) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CUSTOMER_ID, (Object)customerId));
            criteria.setFirstResult(0);
            criteria.setMaxResults(tableModel.getPageSize());
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(0);
            List list = ticketList;
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findNextCustomerTickets(Integer customerId, PaginatedTableModel tableModel, String filter) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int nextIndex = tableModel.getNextRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CUSTOMER_ID, (Object)customerId));
            if (filter.equals((Object)PaymentStatusFilter.OPEN)) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            }
            criteria.setFirstResult(nextIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(nextIndex);
            list = ticketList;
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
    public List<Ticket> findPreviousCustomerTickets(Integer customerId, PaginatedTableModel tableModel, String filter) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int previousIndex = tableModel.getPreviousRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CUSTOMER_ID, (Object)customerId));
            if (filter.equals((Object)PaymentStatusFilter.OPEN)) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            }
            criteria.setFirstResult(previousIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(previousIndex);
            list = ticketList;
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
    public List<Ticket> findTicketByCustomer(Integer customerId) {
        Session session = null;
        Criteria criteria = null;
        try {
            List ticketList;
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CUSTOMER_ID, (Object)customerId));
            List list = ticketList = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public List<Ticket> findTickets(PaymentStatusFilter psFilter, String otFilter) {
        return this.findTicketsForUser(psFilter, otFilter, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findTicketsForUser(PaymentStatusFilter psFilter, String otFilter, User user) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            if (psFilter == PaymentStatusFilter.OPEN) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            } else if (psFilter == PaymentStatusFilter.PAID) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.TRUE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            } else if (psFilter == PaymentStatusFilter.CLOSED) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(11, -24);
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CLOSING_DATE, (Object)currentTime.getTime()));
            }
            if (otFilter != POSConstants.ALL) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TICKET_TYPE, (Object)otFilter));
            }
            if (user != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            }
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
    public List<Ticket> findOpenTicketsForUser(User user) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
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
    public List<Ticket> findOpenTickets(Date startDate, Date endDate) {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)startDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)endDate));
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
    public List<Ticket> findClosedTickets(Date startDate, Date endDate) {
        Session session = null;
        try {
            List list;
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            if (startDate != null && endDate != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)startDate));
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)endDate));
            }
            List list2 = list = criteria.list();
            return list2;
        }
        finally {
            this.closeSession(session);
        }
    }

    public void closeOrder(Ticket ticket) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            this.saveOrUpdate(ticket);
            User driver = ticket.getAssignedDriver();
            if (driver != null) {
                driver.setAvailableForDelivery(true);
                UserDAO.getInstance().saveOrUpdate(driver);
            }
            ShopTableDAO.getInstance().releaseTables(ticket);
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            LogFactory.getLog(TicketDAO.class).error((Object)e);
            throw new RuntimeException(e);
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TicketSummary getOpenTicketSummary() {
        Session session = null;
        TicketSummary ticketSummary = new TicketSummary();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(Ticket.class);
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.count((String)Ticket.PROP_ID));
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            List list = criteria.list();
            if (list.size() > 0) {
                Object[] o = (Object[])list.get(0);
                ticketSummary.setTotalTicket((Integer)o[0]);
                ticketSummary.setTotalPrice(o[1] == null ? 0.0 : (Double)o[1]);
            }
            TicketSummary ticketSummary2 = ticketSummary;
            return ticketSummary2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TicketSummary getClosedTicketSummary(Terminal terminal) {
        Session session = null;
        TicketSummary ticketSummary = new TicketSummary();
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(Ticket.class);
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            ProjectionList projectionList = Projections.projectionList();
            projectionList.add((Projection)Projections.count((String)Ticket.PROP_ID));
            projectionList.add((Projection)Projections.sum((String)Ticket.PROP_TOTAL_AMOUNT));
            criteria.setProjection((Projection)projectionList);
            List list = criteria.list();
            if (list.size() > 0) {
                Object[] o = (Object[])list.get(0);
                ticketSummary.setTotalTicket((Integer)o[0]);
                ticketSummary.setTotalPrice(o[1] == null ? 0.0 : (Double)o[1]);
            }
            TicketSummary ticketSummary2 = ticketSummary;
            return ticketSummary2;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findTickets(Date startDate, Date endDate, boolean closed) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)startDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)endDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)closed));
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
    public List<Ticket> findTickets(Date startDate, Date endDate, boolean closed, Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)startDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)endDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)closed));
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
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
    public List<Ticket> findTicketsForLaborHour(Date startDate, Date endDate, int hour, UserType userType, Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_ACTIVE_DATE, (Object)startDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_ACTIVE_DATE, (Object)endDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CREATION_HOUR, (Object)hour));
            if (userType != null) {
                criteria.createAlias(Ticket.PROP_OWNER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.type", (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
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
    public List<Ticket> findTicketsForShift(Date startDate, Date endDate, Shift shit, UserType userType, Terminal terminal) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)startDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)endDate));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_SHIFT, (Object)shit));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            if (userType != null) {
                criteria.createAlias(Ticket.PROP_OWNER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.type", (Object)userType));
            }
            if (terminal != null) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            }
            List list = criteria.list();
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public static TicketDAO getInstance() {
        return instance;
    }

    private void adjustInventoryItems(Session session, Ticket ticket) {
        List<TicketItem> ticketItems = ticket.getTicketItems();
        if (ticketItems == null) {
            return;
        }
        for (TicketItem ticketItem : ticketItems) {
            Recepie recepie;
            if (ticketItem.isInventoryHandled().booleanValue()) continue;
            Integer menuItemId = ticketItem.getItemId();
            MenuItem menuItem = MenuItemDAO.getInstance().get(menuItemId);
            if (menuItem == null || (recepie = menuItem.getRecepie()) == null) continue;
            List<RecepieItem> recepieItems = recepie.getRecepieItems();
            for (RecepieItem recepieItem : recepieItems) {
                if (!recepieItem.isInventoryDeductable().booleanValue()) continue;
                Double percentage = recepieItem.getPercentage() / 100.0;
                InventoryItem inventoryItem = recepieItem.getInventoryItem();
                Double totalRecepieUnits = inventoryItem.getTotalRecepieUnits();
                inventoryItem.setTotalRecepieUnits(totalRecepieUnits - (double)ticketItem.getItemCount().intValue() * percentage);
                session.saveOrUpdate((Object)inventoryItem);
                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setType(InventoryTransactionType.OUT);
                transaction.setUnitPrice(inventoryItem.getUnitSellingPrice());
                transaction.setInventoryItem(inventoryItem);
                transaction.setQuantity(ticketItem.getItemCount());
                transaction.setRemark(Messages.getString("TicketDAO.0") + ticketItem.getName() + Messages.getString("TicketDAO.11") + ticket.getId());
                session.save((Object)transaction);
            }
            ticketItem.setInventoryHandled(true);
        }
        List deletedItems = ticket.getDeletedItems();
        if (deletedItems == null) {
            return;
        }
        for (Object o : deletedItems) {
            TicketItem ticketItem;
            if (!(o instanceof TicketItem) || !(ticketItem = (TicketItem)o).isInventoryHandled().booleanValue()) continue;
            Integer menuItemId = ticketItem.getItemId();
            MenuItem menuItem = MenuItemDAO.getInstance().get(menuItemId);
            Recepie recepie = menuItem.getRecepie();
            if (recepie == null) {
                return;
            }
            List<RecepieItem> recepieItems = recepie.getRecepieItems();
            for (RecepieItem recepieItem : recepieItems) {
                if (!recepieItem.isInventoryDeductable().booleanValue()) continue;
                InventoryItem inventoryItem = recepieItem.getInventoryItem();
                inventoryItem.setTotalPackages(inventoryItem.getTotalPackages() + ticketItem.getItemCount());
                Double totalRecepieUnits = inventoryItem.getTotalRecepieUnits();
                inventoryItem.setTotalRecepieUnits(totalRecepieUnits + (double)ticketItem.getItemCount().intValue());
                session.saveOrUpdate((Object)inventoryItem);
                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setType(InventoryTransactionType.IN);
                transaction.setUnitPrice(inventoryItem.getUnitSellingPrice());
                transaction.setInventoryItem(inventoryItem);
                transaction.setQuantity(ticketItem.getItemCount());
                transaction.setRemark(Messages.getString("TicketDAO.1") + ticketItem.getName() + " was canceled for ticket " + ticket.getId());
                session.save((Object)transaction);
            }
            ticketItem.setInventoryHandled(true);
        }
    }

    private void updateCriteriaFilters(Criteria criteria) {
        User user = Application.getCurrentUser();
        PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
        String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
        if (paymentStatusFilter == PaymentStatusFilter.OPEN) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (!user.canViewAllOpenTickets()) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            }
        } else if (paymentStatusFilter == PaymentStatusFilter.PAID) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_PAID, (Object)Boolean.TRUE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (!user.canViewAllOpenTickets() || !user.canViewAllCloseTickets()) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            }
        } else if (paymentStatusFilter == PaymentStatusFilter.CLOSED) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.TRUE));
            if (!user.canViewAllCloseTickets()) {
                criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            }
        }
        if (!orderTypeFilter.equals(POSConstants.ALL)) {
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TICKET_TYPE, (Object)orderTypeFilter));
        }
    }

    public void deleteTickets(List<Ticket> tickets) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            for (Ticket ticket : tickets) {
                super.delete(ticket, session);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            LogFactory.getLog(TicketDAO.class).error((Object)e);
            throw new RuntimeException(e);
        }
        finally {
            this.closeSession(session);
        }
    }

    private void adjustStockAmount(Ticket ticket, Session session) {
        LinkedHashMap<Integer, Double> itemMap;
        List<TicketItem> items = ticket.getTicketItems();
        if (!this.getAdjustedMap(items, itemMap = new LinkedHashMap<Integer, Double>())) {
            return;
        }
        for (Integer ticketItemId : ((HashMap)itemMap).keySet()) {
            MenuItem menuItem = MenuItemDAO.getInstance().get(ticketItemId);
            if (menuItem == null || menuItem.getStockAmount() <= 0.0) continue;
            double stockAmount = menuItem.getStockAmount();
            double availableStockAmount = menuItem.isFractionalUnit() != false ? stockAmount - (Double)((HashMap)itemMap).get(ticketItemId) : stockAmount - (Double)((HashMap)itemMap).get(ticketItemId);
            menuItem.setStockAmount(availableStockAmount);
            session.saveOrUpdate((Object)menuItem);
        }
    }

    private boolean getAdjustedMap(List<TicketItem> items, HashMap<Integer, Double> itemMap) {
        for (TicketItem ticketItem : items) {
            if (ticketItem.isStockAmountAdjusted().booleanValue()) {
                return false;
            }
            Double previousValue = itemMap.get(ticketItem.getItemId());
            if (previousValue == null) {
                previousValue = 0.0;
            }
            if (ticketItem.isFractionalUnit().booleanValue()) {
                itemMap.put(ticketItem.getItemId(), ticketItem.getItemQuantity() + previousValue);
            } else {
                itemMap.put(ticketItem.getItemId(), (double)ticketItem.getItemCount().intValue() + previousValue);
            }
            ticketItem.setStockAmountAdjusted(true);
        }
        return true;
    }

    public List<Ticket> findTickets(PaginatedTableModel tableModel, boolean filter) {
        return this.findTickets(tableModel, null, null, filter);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findTickets(PaginatedTableModel tableModel, Date start, Date end, boolean filter) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            if (filter) {
                this.updateCriteriaFilters(criteria);
            }
            criteria.setFirstResult(0);
            criteria.setMaxResults(tableModel.getPageSize());
            if (start != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_DELIVERY_DATE, (Object)start));
            }
            if (end != null) {
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_DELIVERY_DATE, (Object)end));
            }
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(0);
            List list = ticketList;
            return list;
        }
        finally {
            this.closeSession(session);
        }
    }

    public List<Ticket> findNextTickets(PaginatedTableModel tableModel, boolean filter) {
        return this.findNextTickets(tableModel, null, null, filter);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findNextTickets(PaginatedTableModel tableModel, Date start, Date end, boolean filter) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int nextIndex = tableModel.getNextRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            if (filter) {
                this.updateCriteriaFilters(criteria);
            }
            criteria.setFirstResult(nextIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            if (start != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_DELIVERY_DATE, (Object)start));
            }
            if (end != null) {
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_DELIVERY_DATE, (Object)end));
            }
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(nextIndex);
            list = ticketList;
        }
        catch (Throwable throwable) {
            this.closeSession(session);
            throw throwable;
        }
        this.closeSession(session);
        return list;
    }

    public List<Ticket> findPreviousTickets(PaginatedTableModel tableModel, boolean filter) {
        return this.findPreviousTickets(tableModel, null, null, filter);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Ticket> findPreviousTickets(PaginatedTableModel tableModel, Date start, Date end, boolean filter) {
        List list;
        Session session = null;
        Criteria criteria = null;
        try {
            int previousIndex = tableModel.getPreviousRowIndex();
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            if (filter) {
                this.updateCriteriaFilters(criteria);
            }
            criteria.setFirstResult(previousIndex);
            criteria.setMaxResults(tableModel.getPageSize());
            if (start != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_DELIVERY_DATE, (Object)start));
            }
            if (end != null) {
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_DELIVERY_DATE, (Object)end));
            }
            List ticketList = criteria.list();
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = (Integer)criteria.uniqueResult();
            if (rowCount != null) {
                tableModel.setNumRows(rowCount);
            }
            tableModel.setCurrentRowIndex(previousIndex);
            list = ticketList;
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
    public List<Ticket> getTicketsWithSpecificFields(String ... fields) {
        Session session = null;
        Criteria criteria = null;
        User currentUser = Application.getCurrentUser();
        boolean filterUser = !currentUser.isAdministrator() || !currentUser.isManager();
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(Ticket.class);
            ProjectionList projectionList = Projections.projectionList();
            for (String field : fields) {
                projectionList.add((Projection)Projections.property((String)field));
            }
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (filterUser) {
                criteria.createAlias(Ticket.PROP_OWNER, "u");
                criteria.add((Criterion)Restrictions.eq((String)"u.userId", (Object)currentUser.getUserId()));
            }
            ResultTransformer transformer = new ResultTransformer(){

                public Object transformTuple(Object[] row, String[] arg1) {
                    Ticket ticket = new Ticket();
                    ticket.setId(Integer.valueOf("" + row[0]));
                    ticket.setDueAmount(Double.valueOf("" + row[1]));
                    return ticket;
                }

                public List transformList(List arg0) {
                    return arg0;
                }
            };
            criteria.setProjection((Projection)projectionList).setResultTransformer(transformer);
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
    public int getNumTickets() {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            this.updateCriteriaFilters(criteria);
            criteria.setProjection(Projections.rowCount());
            Number rowCount = (Number)criteria.uniqueResult();
            if (rowCount != null) {
                int n = rowCount.intValue();
                return n;
            }
            int n = 0;
            return n;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumTickets(Date start, Date end) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (start != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_DELIVERY_DATE, (Object)start));
            }
            if (end != null) {
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_DELIVERY_DATE, (Object)end));
            }
            criteria.add(Restrictions.isNotNull((String)Ticket.PROP_DELIVERY_DATE));
            criteria.setProjection(Projections.rowCount());
            Number rowCount = (Number)criteria.uniqueResult();
            if (rowCount != null) {
                int n = rowCount.intValue();
                return n;
            }
            int n = 0;
            return n;
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadTickets(PaginatedTableModel tableModel, Date start, Date end) {
        Session session = null;
        Criteria criteria = null;
        try {
            session = this.createNewSession();
            criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_CLOSED, (Object)Boolean.FALSE));
            if (start != null) {
                criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_DELIVERY_DATE, (Object)start));
            }
            if (end != null) {
                criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_DELIVERY_DATE, (Object)end));
            }
            criteria.add(Restrictions.isNotNull((String)Ticket.PROP_DELIVERY_DATE));
            criteria.setFirstResult(tableModel.getCurrentRowIndex());
            criteria.setMaxResults(tableModel.getPageSize());
            tableModel.setRows(criteria.list());
            return;
        }
        finally {
            this.closeSession(session);
        }
    }
}


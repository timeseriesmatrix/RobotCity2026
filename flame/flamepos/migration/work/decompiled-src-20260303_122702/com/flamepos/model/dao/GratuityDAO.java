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

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TipsCashoutReport;
import com.floreantpos.model.TipsCashoutReportData;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.BaseGratuityDAO;
import com.floreantpos.model.util.DateUtil;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class GratuityDAO
extends BaseGratuityDAO {
    public List<Gratuity> findByUser(User user) throws PosException {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)Gratuity.PROP_OWNER, (Object)user));
            criteria.add((Criterion)Restrictions.eq((String)Gratuity.PROP_PAID, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Gratuity.PROP_REFUNDED, (Object)Boolean.FALSE));
            List list = criteria.list();
            return list;
        }
        catch (Exception e) {
            throw new PosException(Messages.getString("GratuityDAO.0") + user.getFirstName() + " " + user.getLastName());
        }
        finally {
            this.closeSession(session);
        }
    }

    public void payGratuities(List<Gratuity> gratuities) {
        Session session = null;
        Transaction tx = null;
        double total = 0.0;
        try {
            session = this.getSession();
            tx = session.beginTransaction();
            for (Gratuity gratuity : gratuities) {
                total += gratuity.getAmount().doubleValue();
                gratuity.setPaid(true);
                session.saveOrUpdate((Object)gratuity);
                Terminal terminal = gratuity.getTerminal();
                terminal.setCurrentBalance(terminal.getCurrentBalance() - gratuity.getAmount());
                session.saveOrUpdate((Object)terminal);
            }
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PosException(Messages.getString("GratuityDAO.2"));
        }
        finally {
            this.closeSession(session);
        }
    }

    public TipsCashoutReport createReport(Date fromDate, Date toDate, User user) {
        Session session = null;
        try {
            session = this.getSession();
            fromDate = DateUtil.startOfDay(fromDate);
            toDate = DateUtil.endOfDay(toDate);
            Criteria criteria = session.createCriteria(Ticket.class);
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_OWNER, (Object)user));
            criteria.add((Criterion)Restrictions.ge((String)Ticket.PROP_CREATE_DATE, (Object)fromDate));
            criteria.add((Criterion)Restrictions.le((String)Ticket.PROP_CREATE_DATE, (Object)toDate));
            List list = criteria.list();
            TipsCashoutReport report = new TipsCashoutReport();
            report.setServer(user.getUserId() + "/" + user.toString());
            report.setFromDate(fromDate);
            report.setToDate(toDate);
            report.setReportTime(new Date());
            for (Ticket ticket : list) {
                Gratuity gratuity = ticket.getGratuity();
                TipsCashoutReportData data = new TipsCashoutReportData();
                data.setTicketId(ticket.getId());
                data.setTicketTotal(ticket.getTotalAmount());
                if (gratuity != null && !gratuity.isRefunded().booleanValue()) {
                    data.setTips(gratuity.getAmount());
                    data.setPaid(gratuity.isPaid());
                } else {
                    data.setTips(0.0);
                }
                report.addReportData(data);
            }
            report.calculateOthers();
            TipsCashoutReport tipsCashoutReport = report;
            return tipsCashoutReport;
        }
        catch (Exception e) {
            throw new PosException(Messages.getString("GratuityDAO.4") + user.getFirstName() + " " + user.getLastName(), e);
        }
        finally {
            this.closeSession(session);
        }
    }
}


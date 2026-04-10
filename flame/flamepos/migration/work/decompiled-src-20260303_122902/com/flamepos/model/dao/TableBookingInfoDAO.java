/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.model.dao;

import com.floreantpos.PosLog;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.TableBookingInfo;
import com.floreantpos.model.dao.BaseTableBookingInfoDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.util.DateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class TableBookingInfoDAO
extends BaseTableBookingInfoDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<ShopTable> getBookedTables(Date startDate, Date endDate) {
        Session session = null;
        try {
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ge((String)TableBookingInfo.PROP_TO_DATE, (Object)startDate)).add((Criterion)Restrictions.ne((String)TableBookingInfo.PROP_STATUS, (Object)"cancel")).add((Criterion)Restrictions.ne((String)TableBookingInfo.PROP_STATUS, (Object)"no appear")).add((Criterion)Restrictions.ne((String)TableBookingInfo.PROP_STATUS, (Object)"close"));
            List list = criteria.list();
            ArrayList<TableBookingInfo> bookings = new ArrayList<TableBookingInfo>();
            for (TableBookingInfo tableBookingInfo : list) {
                if (!DateUtil.between(tableBookingInfo.getFromDate(), tableBookingInfo.getToDate(), startDate) && !DateUtil.between(tableBookingInfo.getFromDate(), tableBookingInfo.getToDate(), endDate)) continue;
                bookings.add(tableBookingInfo);
            }
            HashSet<ShopTable> bookedTables = new HashSet<ShopTable>();
            for (TableBookingInfo tableBookingInfo : bookings) {
                List<ShopTable> tables = tableBookingInfo.getTables();
                if (tables == null) continue;
                bookedTables.addAll(tables);
            }
            HashSet<ShopTable> hashSet = bookedTables;
            return hashSet;
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        finally {
            if (session != null) {
                this.closeSession(session);
            }
        }
        return null;
    }

    public List<ShopTable> getFreeTables(Date startDate, Date endDate) {
        Collection<ShopTable> bookedTables = this.getBookedTables(startDate, endDate);
        List<ShopTable> allTables = ShopTableDAO.getInstance().findAll();
        allTables.removeAll(bookedTables);
        return allTables;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<TableBookingInfo> getAllOpenBooking() {
        Session session = null;
        try {
            List list;
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.ne((String)TableBookingInfo.PROP_STATUS, (Object)"close"));
            List list2 = list = criteria.list();
            return list2;
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        finally {
            try {
                session.close();
            }
            catch (Exception exception) {}
        }
        return null;
    }

    public void setBookingStatus(TableBookingInfo bookingInfo, String bookingStatus, List<ShopTable> tables) {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.createNewSession();
            tx = session.beginTransaction();
            bookingInfo.setStatus(bookingStatus);
            this.saveOrUpdate(bookingInfo);
            if (bookingStatus.equals("seat") || bookingStatus.equals("delay")) {
                ShopTableDAO.getInstance().bookedTables(tables);
            }
            if (bookingStatus.equals("cancel") || bookingStatus.equals("no appear") || bookingStatus.equals("close")) {
                ShopTableDAO.getInstance().freeTables(tables);
            }
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            LogFactory.getLog(TableBookingInfo.class).error((Object)e);
            throw new RuntimeException(e);
        }
        finally {
            this.closeSession(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getTodaysBooking() {
        try (Session session = null;){
            List list;
            Calendar startDate = Calendar.getInstance();
            startDate.setLenient(false);
            startDate.setTime(new Date());
            startDate.set(11, 0);
            startDate.set(12, 0);
            startDate.set(13, 0);
            startDate.set(14, 0);
            Calendar endDate = Calendar.getInstance();
            endDate.setLenient(false);
            endDate.setTime(new Date());
            endDate.set(11, 23);
            endDate.set(12, 59);
            endDate.set(13, 59);
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(TableBookingInfo.class);
            criteria.add((Criterion)Restrictions.ge((String)TableBookingInfo.PROP_FROM_DATE, (Object)startDate.getTime())).add((Criterion)Restrictions.le((String)TableBookingInfo.PROP_FROM_DATE, (Object)endDate.getTime())).add((Criterion)Restrictions.eq((String)TableBookingInfo.PROP_STATUS, (Object)"open"));
            List list2 = list = criteria.list();
            return list2;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getAllBookingByDate(Date startDate, Date endDate) {
        try (Session session = null;){
            List list;
            session = this.createNewSession();
            Criteria criteria = session.createCriteria(TableBookingInfo.class);
            criteria.add((Criterion)Restrictions.ge((String)TableBookingInfo.PROP_FROM_DATE, (Object)startDate)).add((Criterion)Restrictions.le((String)TableBookingInfo.PROP_FROM_DATE, (Object)endDate)).add((Criterion)Restrictions.ne((String)TableBookingInfo.PROP_STATUS, (Object)"close"));
            List list2 = list = criteria.list();
            return list2;
        }
        return null;
    }
}


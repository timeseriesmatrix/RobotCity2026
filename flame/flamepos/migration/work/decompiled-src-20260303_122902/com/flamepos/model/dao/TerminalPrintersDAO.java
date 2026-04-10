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

import com.floreantpos.main.Application;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.BaseTerminalPrintersDAO;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class TerminalPrintersDAO
extends BaseTerminalPrintersDAO {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<TerminalPrinters> findTerminalPrinters() {
        Session session = null;
        try {
            List list;
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.eq((String)TerminalPrinters.PROP_TERMINAL, (Object)Application.getInstance().getTerminal()));
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
    public TerminalPrinters findPrinters(VirtualPrinter virtualPrinter) {
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteria = session.createCriteria(this.getReferenceClass());
            criteria.add((Criterion)Restrictions.and((Criterion)Restrictions.eq((String)TerminalPrinters.PROP_TERMINAL, (Object)Application.getInstance().getTerminal()), (Criterion)Restrictions.eq((String)TerminalPrinters.PROP_VIRTUAL_PRINTER, (Object)virtualPrinter)));
            TerminalPrinters terminalPrinters = (TerminalPrinters)criteria.uniqueResult();
            return terminalPrinters;
        }
        finally {
            this.closeSession(session);
        }
    }
}


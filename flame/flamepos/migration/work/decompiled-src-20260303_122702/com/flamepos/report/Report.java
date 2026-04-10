/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.view.JRViewer
 */
package com.floreantpos.report;

import com.floreantpos.model.Terminal;
import com.floreantpos.model.UserType;
import java.util.Date;
import net.sf.jasperreports.view.JRViewer;

public abstract class Report {
    public static final int REPORT_TYPE_1 = 0;
    public static final int REPORT_TYPE_2 = 1;
    private Date startDate;
    private Date endDate;
    private Terminal terminal;
    private UserType userType;
    private int reportType = 0;
    private boolean includeFreeItem = false;
    protected JRViewer viewer;

    public abstract void refresh() throws Exception;

    public abstract boolean isDateRangeSupported();

    public abstract boolean isTypeSupported();

    public JRViewer getViewer() {
        return this.viewer;
    }

    public Date getEndDate() {
        if (this.endDate == null) {
            return new Date();
        }
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getReportType() {
        return this.reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public boolean isIncludedFreeItems() {
        return this.includeFreeItem;
    }

    public void setIncludeFreeItems(boolean includeFreeItem) {
        this.includeFreeItem = includeFreeItem;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Date getStartDate() {
        if (this.startDate == null) {
            return new Date();
        }
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}


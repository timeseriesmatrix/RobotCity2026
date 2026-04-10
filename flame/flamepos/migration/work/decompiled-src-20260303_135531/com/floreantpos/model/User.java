/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.model;

import com.floreantpos.main.Application;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.base.BaseUser;
import com.floreantpos.model.dao.UserDAO;
import java.util.Calendar;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.logging.LogFactory;

@XmlRootElement(name="user")
public class User
extends BaseUser {
    private static final long serialVersionUID = 1L;
    public static final String USER_TYPE_MANAGER = "MANAGER";
    public static final String USER_TYPE_CASHIER = "CASHIER";
    public static final String USER_TYPE_SERVER = "SERVER";

    public User() {
    }

    public User(Integer autoId) {
        super(autoId);
    }

    public User(Integer autoId, String password) {
        super(autoId, password);
    }

    @Override
    public Boolean isActive() {
        return this.active == null ? Boolean.TRUE : this.active;
    }

    public boolean hasPermission(UserPermission permission) {
        return this.getType().hasPermission(permission);
    }

    public void doClockIn(Terminal terminal, Shift shift, Calendar currentTime) {
        this.setClockedIn(true);
        this.setCurrentShift(shift);
        this.setCurrentTerminal(terminal);
        this.setLastClockInTime(currentTime.getTime());
        if (this.isDriver().booleanValue()) {
            this.setAvailableForDelivery(true);
        }
        LogFactory.getLog(Application.class).info((Object)("terminal id befor saving clockIn=" + terminal.getId()));
        AttendenceHistory attendenceHistory = new AttendenceHistory();
        attendenceHistory.setClockInTime(currentTime.getTime());
        attendenceHistory.setClockInHour((short)currentTime.get(11));
        attendenceHistory.setUser(this);
        attendenceHistory.setTerminal(terminal);
        attendenceHistory.setShift(shift);
        UserDAO.getInstance().saveClockIn(this, attendenceHistory, shift, currentTime);
    }

    public void doClockOut(AttendenceHistory attendenceHistory, Shift shift, Calendar currentTime) {
        this.setClockedIn(false);
        this.setCurrentShift(null);
        this.setCurrentTerminal(null);
        this.setLastClockInTime(null);
        this.setLastClockOutTime(null);
        if (this.isDriver().booleanValue()) {
            this.setAvailableForDelivery(false);
        }
        attendenceHistory.setClockedOut(true);
        attendenceHistory.setClockOutTime(currentTime.getTime());
        attendenceHistory.setClockOutHour((short)currentTime.get(11));
        UserDAO.getInstance().saveClockOut(this, attendenceHistory, shift, currentTime);
    }

    public boolean canViewAllOpenTickets() {
        if (this.getType() == null) {
            return false;
        }
        Set<UserPermission> permissions = this.getType().getPermissions();
        if (permissions == null) {
            return false;
        }
        for (UserPermission permission : permissions) {
            if (!permission.equals(UserPermission.VIEW_ALL_OPEN_TICKETS)) continue;
            return true;
        }
        return false;
    }

    public boolean canViewAllCloseTickets() {
        if (this.getType() == null) {
            return false;
        }
        Set<UserPermission> permissions = this.getType().getPermissions();
        if (permissions == null) {
            return false;
        }
        for (UserPermission permission : permissions) {
            if (!permission.equals(UserPermission.VIEW_ALL_CLOSE_TICKETS)) continue;
            return true;
        }
        return false;
    }

    public void setFullName(String str) {
    }

    public String getStatus() {
        if (this.isClockedIn().booleanValue()) {
            if (this.isAvailableForDelivery().booleanValue()) {
                return "Available";
            }
            return "Driving";
        }
        return "Not available";
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    @Override
    public String toString() {
        return this.getFirstName() + " " + this.getLastName();
    }

    public boolean isManager() {
        return this.hasPermission(UserPermission.PERFORM_MANAGER_TASK);
    }

    public boolean isAdministrator() {
        return this.hasPermission(UserPermission.PERFORM_ADMINISTRATIVE_TASK);
    }
}


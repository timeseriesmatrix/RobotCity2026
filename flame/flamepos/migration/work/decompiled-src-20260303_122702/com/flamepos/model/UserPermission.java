/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.model.base.BaseUserPermission;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="user-permission")
public class UserPermission
extends BaseUserPermission {
    private static final long serialVersionUID = 1L;
    private String resourceBundlePropertyName;
    private boolean visibleWithoutPermission = true;
    public static final UserPermission CREATE_TICKET = new UserPermission("Create New Ticket", "UserPermission.0");
    public static final UserPermission VIEW_ALL_OPEN_TICKETS = new UserPermission("View All Open Ticket", "UserPermission.1");
    public static final UserPermission VOID_TICKET = new UserPermission("Void Ticket", "UserPermission.3");
    public static final UserPermission PERFORM_ADMINISTRATIVE_TASK = new UserPermission("Perform Administrative Task", "UserPermission.4");
    public static final UserPermission PERFORM_MANAGER_TASK = new UserPermission("Perform Manager Task", "UserPermission.5");
    public static final UserPermission VIEW_BACK_OFFICE = new UserPermission("View Back Office", "UserPermission.6", false);
    public static final UserPermission AUTHORIZE_TICKETS = new UserPermission("Authorize Tickets", "UserPermission.7");
    public static final UserPermission DRAWER_ASSIGNMENT = new UserPermission("Drawer Assignment", "UserPermission.8");
    public static final UserPermission DRAWER_PULL = new UserPermission("Drawer Pull", "UserPermission.9");
    public static final UserPermission SPLIT_TICKET = new UserPermission("Split Ticket", "UserPermission.10");
    public static final UserPermission SETTLE_TICKET = new UserPermission("Settle Ticket", "UserPermission.11");
    public static final UserPermission REOPEN_TICKET = new UserPermission("Reopen Ticket", "UserPermission.12");
    public static final UserPermission PAY_OUT = new UserPermission("Pay Out", "UserPermission.13");
    public static final UserPermission SHUT_DOWN = new UserPermission("Shut Down", "UserPermission.15");
    public static final UserPermission ADD_DISCOUNT = new UserPermission("Add Discount", "UserPermission.16");
    public static final UserPermission REFUND = new UserPermission("Refund", "UserPermission.17");
    public static final UserPermission VIEW_EXPLORERS = new UserPermission("View Explorers", "UserPermission.18");
    public static final UserPermission VIEW_REPORTS = new UserPermission("View Reports", "UserPermission.19");
    public static final UserPermission MANAGE_TABLE_LAYOUT = new UserPermission("Manage Table Layout", "UserPermission.20");
    public static final UserPermission TABLE_BOOKING = new UserPermission("Booking", "UserPermission.22");
    public static final UserPermission MODIFY_PRINTED_TICKET = new UserPermission("Modify Printed Ticket", "UserPermission.21");
    public static final UserPermission TRANSFER_TICKET = new UserPermission("Transfer Ticket", "UserPermission.23");
    public static final UserPermission KITCHEN_DISPLAY = new UserPermission("Kitchen Display", "UserPermission.24");
    public static final UserPermission ALL_FUNCTIONS = new UserPermission("All Functions", "UserPermission.25");
    public static final UserPermission HOLD_TICKET = new UserPermission("Hold Ticket", "UserPermission.26");
    public static final UserPermission VIEW_ALL_CLOSE_TICKETS = new UserPermission("View All Close Tickets", "UserPermission.27");
    public static final UserPermission QUICK_MAINTENANCE = new UserPermission("Quick Maintenance", "Quick_Maintenance");
    public static final UserPermission[] permissions = new UserPermission[]{VIEW_ALL_OPEN_TICKETS, CREATE_TICKET, VOID_TICKET, VIEW_BACK_OFFICE, AUTHORIZE_TICKETS, SPLIT_TICKET, SETTLE_TICKET, REOPEN_TICKET, PAY_OUT, DRAWER_ASSIGNMENT, DRAWER_PULL, VIEW_EXPLORERS, VIEW_REPORTS, SHUT_DOWN, ADD_DISCOUNT, REFUND, PERFORM_MANAGER_TASK, PERFORM_ADMINISTRATIVE_TASK, MANAGE_TABLE_LAYOUT, TABLE_BOOKING, MODIFY_PRINTED_TICKET, TRANSFER_TICKET, KITCHEN_DISPLAY, ALL_FUNCTIONS, HOLD_TICKET, VIEW_ALL_CLOSE_TICKETS, QUICK_MAINTENANCE};

    public UserPermission() {
    }

    public UserPermission(String name, String resourceBundlePropertyName) {
        super(name);
        this.resourceBundlePropertyName = resourceBundlePropertyName;
    }

    public UserPermission(String name, String resourceBundlePropertyName, boolean visibleWithoutPermission) {
        super(name);
        this.resourceBundlePropertyName = resourceBundlePropertyName;
        this.visibleWithoutPermission = visibleWithoutPermission;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserPermission)) {
            return false;
        }
        UserPermission p = (UserPermission)obj;
        return this.getName().equalsIgnoreCase(p.getName());
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty((String)this.resourceBundlePropertyName)) {
            return this.getName();
        }
        return Messages.getString(this.resourceBundlePropertyName);
    }

    public boolean isVisibleWithoutPermission() {
        return this.visibleWithoutPermission;
    }

    public void setVisibleWithoutPermission(boolean visibleWithoutPermission) {
        this.visibleWithoutPermission = visibleWithoutPermission;
    }
}


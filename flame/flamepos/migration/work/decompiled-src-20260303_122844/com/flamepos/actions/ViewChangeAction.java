/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.actions;

import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.Icon;
import org.apache.commons.lang.StringUtils;

public abstract class ViewChangeAction
extends PosAction {
    private boolean visible = true;
    protected UserPermission requiredPermission;

    public ViewChangeAction() {
    }

    public ViewChangeAction(String name) {
        super(name);
    }

    public ViewChangeAction(Icon icon) {
        super(null, icon);
    }

    public ViewChangeAction(String name, Icon icon) {
        super(name, icon);
    }

    public ViewChangeAction(String name, UserPermission requiredPermission) {
        super(name);
        this.requiredPermission = requiredPermission;
    }

    public ViewChangeAction(Icon icon, UserPermission requiredPermission) {
        super(null, icon);
        this.requiredPermission = requiredPermission;
    }

    @Override
    public UserPermission getRequiredPermission() {
        return this.requiredPermission;
    }

    @Override
    public void setRequiredPermission(UserPermission requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        User user = Application.getCurrentUser();
        if (RootView.getInstance().getCurrentView().getViewName().equals("ORDER_VIEW") && !OrderView.getInstance().getTicketView().isAllowToLogOut()) {
            POSMessageDialog.showError(Messages.getString("ViewChangeAction.0"));
            return;
        }
        this.saveTicketIfNeeded();
        if (this.requiredPermission == null) {
            this.execute();
            return;
        }
        if (!user.hasPermission(this.requiredPermission)) {
            String password = PasswordEntryDialog.show(Application.getPosWindow(), Messages.getString("PosAction.0"));
            if (StringUtils.isEmpty((String)password)) {
                return;
            }
            User user2 = UserDAO.getInstance().findUserBySecretKey(password);
            if (user2 == null) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("PosAction.1"));
            } else if (!user2.hasPermission(this.requiredPermission)) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("PosAction.2"));
            } else {
                this.execute();
            }
            return;
        }
        this.execute();
    }

    private void saveTicketIfNeeded() {
        OrderView orderView = OrderView.getInstance();
        if (!orderView.isVisible()) {
            return;
        }
        Ticket currentTicket = orderView.getCurrentTicket();
        if (currentTicket == null) {
            return;
        }
        if (!currentTicket.getTicketItems().isEmpty()) {
            if (this.hasNewItem(currentTicket)) {
                if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), Messages.getString("ViewChangeAction.1"), Messages.getString("ViewChangeAction.2")) == 0) {
                    orderView.getTicketView().saveTicketIfNeeded();
                }
            } else {
                orderView.getTicketView().saveTicketIfNeeded();
            }
        }
    }

    private boolean hasNewItem(Ticket currentTicket) {
        for (TicketItem item : currentTicket.getTicketItems()) {
            if (item.getId() != null) continue;
            return true;
        }
        return false;
    }

    @Override
    public abstract void execute();

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.actions;

import com.floreantpos.Messages;
import com.floreantpos.actions.PosAction;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.Currency;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.print.DrawerpullReportService;
import com.floreantpos.print.PosPrintService;
import com.floreantpos.swing.UserListDialog;
import com.floreantpos.ui.dialog.MultiCurrencyAssignDrawerDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.CurrencyUtil;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DrawerAssignmentAction
extends PosAction {
    public DrawerAssignmentAction() {
        super(Messages.getString("DrawerAssignmentAction.0"), UserPermission.DRAWER_ASSIGNMENT);
        this.updateActionText();
    }

    public void updateActionText() {
        Terminal terminal = Application.getInstance().getTerminal();
        User assignedUser = terminal.getAssignedUser();
        if (assignedUser != null) {
            this.putValue("Name", Messages.getString("DrawerAssignmentAction.1"));
        } else {
            this.putValue("Name", Messages.getString("DrawerAssignmentAction.2"));
        }
    }

    @Override
    public void execute() {
        try {
            Terminal terminal = Application.getInstance().getTerminal();
            User assignedUser = terminal.getAssignedUser();
            if (assignedUser != null) {
                int option = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(), Messages.getString("DrawerAssignmentAction.3") + assignedUser.getFullName() + Messages.getString("DrawerAssignmentAction.4"), Messages.getString("DrawerAssignmentAction.5"));
                if (option != 0) {
                    return;
                }
                this.performDrawerClose(terminal);
            } else {
                this.performAssignment(terminal);
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    private void performAssignment(Terminal terminal) throws Exception {
        Transaction tx = null;
        try (Session session = null;){
            UserListDialog dialog = new UserListDialog();
            dialog.pack();
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            User user = dialog.getSelectedUser();
            if (!user.isClockedIn().booleanValue()) {
                POSMessageDialog.showError("Can't assign drawer. Selected user is not clocked in.");
                return;
            }
            double drawerBalance = 0.0;
            CashDrawer cashDrawer = null;
            if (TerminalConfig.isEnabledMultiCurrency()) {
                List<Currency> currencyList = CurrencyUtil.getAllCurrency();
                if (currencyList.size() > 1) {
                    MultiCurrencyAssignDrawerDialog multiCurrencyDialog = new MultiCurrencyAssignDrawerDialog(500.0, currencyList);
                    multiCurrencyDialog.pack();
                    multiCurrencyDialog.open();
                    if (multiCurrencyDialog.isCanceled()) {
                        return;
                    }
                    cashDrawer = multiCurrencyDialog.getCashDrawer();
                    drawerBalance = multiCurrencyDialog.getTotalAmount();
                }
            } else {
                drawerBalance = NumberSelectionDialog2.takeDoubleInput(Messages.getString("DrawerAssignmentAction.6"), Messages.getString("DrawerAssignmentAction.7"), 500.0);
            }
        }
    }

    private void performDrawerClose(Terminal terminal) throws Exception {
        CashDrawer cashDrawer;
        User user = terminal.getAssignedUser();
        DrawerPullReport report = DrawerpullReportService.buildDrawerPullReport();
        report.setAssignedUser(user);
        TerminalDAO dao = new TerminalDAO();
        dao.resetCashDrawer(report, terminal, user, 0.0);
        if (TerminalConfig.isEnabledMultiCurrency() && (cashDrawer = CashDrawerDAO.getInstance().findByTerminal(terminal)) != null) {
            Set<CurrencyBalance> currencyBalances = cashDrawer.getCurrencyBalanceList();
            if (currencyBalances != null) {
                for (CurrencyBalance currencyBalance : currencyBalances) {
                    currencyBalance.setBalance(0.0);
                }
            }
            CashDrawerDAO.getInstance().saveOrUpdate(cashDrawer);
        }
        PosPrintService.printDrawerPullReport(report, terminal);
        POSMessageDialog.showMessage(Messages.getString("DrawerAssignmentAction.10"));
        this.putValue("Name", Messages.getString("DrawerAssignmentAction.11"));
    }
}


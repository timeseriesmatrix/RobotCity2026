/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.TipsCashoutReport;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.GratuityDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.TipsCashoutReportDialog;
import com.floreantpos.ui.util.UiUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class ServerTipsAction
extends PosAction {
    public ServerTipsAction() {
        super(POSConstants.SERVER_TIPS, UserPermission.DRAWER_PULL);
    }

    @Override
    public void execute() {
        try {
            JPanel panel = new JPanel((LayoutManager)new MigLayout());
            List<User> users = UserDAO.getInstance().findAll();
            JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
            JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
            panel.add((Component)new JLabel(POSConstants.SELECT_USER + ":"), "grow");
            JComboBox userCombo = new JComboBox(new ListComboBoxModel(users));
            panel.add(userCombo, "grow, wrap");
            panel.add((Component)new JLabel(POSConstants.FROM + ":"), "grow");
            panel.add((Component)fromDatePicker, "wrap");
            panel.add((Component)new JLabel(POSConstants.TO_), "grow");
            panel.add((Component)toDatePicker);
            int option = JOptionPane.showOptionDialog(Application.getPosWindow(), panel, POSConstants.SELECT_CRIETERIA, 2, 3, null, null, null);
            if (option != 0) {
                return;
            }
            GratuityDAO gratuityDAO = new GratuityDAO();
            TipsCashoutReport report = gratuityDAO.createReport(fromDatePicker.getDate(), toDatePicker.getDate(), (User)userCombo.getSelectedItem());
            TipsCashoutReportDialog dialog = new TipsCashoutReportDialog(report);
            dialog.setSize(400, 600);
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}


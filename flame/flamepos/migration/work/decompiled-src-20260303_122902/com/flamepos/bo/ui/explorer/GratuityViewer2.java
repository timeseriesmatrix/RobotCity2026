/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.intellij.uiDesigner.core.GridConstraints
 *  com.intellij.uiDesigner.core.GridLayoutManager
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.GratuityDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.util.NumberUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

public class GratuityViewer2
extends TransparentPanel
implements ActionListener {
    private JComboBox cbUsers;
    private JButton btnGo;
    private JLabel lblUserId;
    private JLabel lblUserName;
    private JLabel lblTotalGratuity;
    private JTable tableGratuityViewer;
    private JButton btnPay;
    private JPanel contentPane;
    private GratuityTableModel gratuityTableModel;

    public GratuityViewer2() {
        this.$$$setupUI$$$();
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.findAll();
        this.cbUsers.setModel(new ListComboBoxModel(users));
        this.gratuityTableModel = new GratuityTableModel(null);
        this.tableGratuityViewer.setModel(this.gratuityTableModel);
        this.btnGo.addActionListener(this);
        this.btnPay.setEnabled(false);
        this.btnPay.addActionListener(this);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout());
        this.add(this.contentPane);
    }

    private void $$$setupUI$$$() {
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new GridLayoutManager(9, 3, new Insets(0, 0, 0, 0), -1, -1));
        JLabel label1 = new JLabel();
        label1.setText(POSConstants.SELECT_USER + ":");
        this.contentPane.add((Component)label1, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.cbUsers = new JComboBox();
        this.contentPane.add((Component)this.cbUsers, new GridConstraints(0, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(406, 22), null, 0, false));
        this.btnGo = new JButton();
        this.btnGo.setText(POSConstants.GO);
        this.contentPane.add((Component)this.btnGo, new GridConstraints(0, 2, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
        JSeparator separator1 = new JSeparator();
        this.contentPane.add((Component)separator1, new GridConstraints(1, 0, 1, 3, 1, 1, 4, 0, null, null, null, 0, false));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.USER_ID + ":");
        this.contentPane.add((Component)label2, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.lblUserId = new JLabel();
        this.contentPane.add((Component)this.lblUserId, new GridConstraints(2, 1, 1, 1, 8, 0, 0, 0, null, new Dimension(406, 14), null, 0, false));
        JLabel label3 = new JLabel();
        label3.setText(POSConstants.USER_NAME + ":");
        this.contentPane.add((Component)label3, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.lblUserName = new JLabel();
        this.contentPane.add((Component)this.lblUserName, new GridConstraints(3, 1, 1, 1, 8, 0, 0, 0, null, new Dimension(406, 14), null, 0, false));
        JLabel label4 = new JLabel();
        label4.setText(POSConstants.TOTAL_GRATUITY + ":");
        this.contentPane.add((Component)label4, new GridConstraints(4, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.lblTotalGratuity = new JLabel();
        this.contentPane.add((Component)this.lblTotalGratuity, new GridConstraints(4, 1, 1, 1, 8, 0, 0, 0, null, new Dimension(406, 14), null, 0, false));
        JLabel label5 = new JLabel();
        label5.setText(POSConstants.DETAILS);
        this.contentPane.add((Component)label5, new GridConstraints(5, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        JSeparator separator2 = new JSeparator();
        this.contentPane.add((Component)separator2, new GridConstraints(5, 1, 1, 2, 0, 1, 4, 0, null, null, null, 0, false));
        JScrollPane scrollPane1 = new JScrollPane();
        this.contentPane.add((Component)scrollPane1, new GridConstraints(6, 0, 1, 3, 0, 3, 5, 5, null, null, null, 0, false));
        this.tableGratuityViewer = new JTable();
        scrollPane1.setViewportView(this.tableGratuityViewer);
        JSeparator separator3 = new JSeparator();
        this.contentPane.add((Component)separator3, new GridConstraints(7, 0, 1, 3, 2, 1, 4, 0, null, null, null, 0, false));
        this.btnPay = new JButton();
        this.btnPay.setText(POSConstants.PAY);
        this.contentPane.add((Component)this.btnPay, new GridConstraints(8, 2, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }

    public void showGratuity(User user) {
        GratuityDAO dao = new GratuityDAO();
        List<Gratuity> gratuities = dao.findByUser(user);
        double totalGratuity = 0.0;
        for (Gratuity gratuity : gratuities) {
            totalGratuity += gratuity.getAmount().doubleValue();
        }
        this.lblUserId.setText(String.valueOf(user.getUserId()));
        this.lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        this.lblTotalGratuity.setText(NumberUtil.formatNumber(totalGratuity));
        this.gratuityTableModel.setRows(gratuities);
        if (gratuities.size() > 0) {
            this.btnPay.setEnabled(true);
        } else {
            this.btnPay.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        User user;
        String actionCommand = e.getActionCommand();
        if (POSConstants.GO.equalsIgnoreCase(actionCommand) && (user = (User)this.cbUsers.getSelectedItem()) != null) {
            this.showGratuity(user);
        }
        if (POSConstants.PAY.equalsIgnoreCase(actionCommand)) {
            try {
                List<Gratuity> rows = this.gratuityTableModel.getRows();
                if (rows != null) {
                    new GratuityDAO().payGratuities(rows);
                }
                this.btnPay.setEnabled(false);
                String actionMessage = POSConstants.PAY_TIPS;
                ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.PAY_TIPS, actionMessage);
            }
            catch (PosException ex) {
                BOMessageDialog.showError(this.contentPane, ex.getMessage(), ex);
            }
        }
    }

    private class GratuityTableModel
    extends ListTableModel {
        public GratuityTableModel(List<Gratuity> gratuities) {
            super(new String[]{POSConstants.FIRST_NAME, POSConstants.LAST_NAME, POSConstants.TICKET_ID, POSConstants.AMOUNT}, gratuities);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Gratuity gratuity = (Gratuity)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return gratuity.getOwner().getFirstName();
                }
                case 1: {
                    return gratuity.getOwner().getLastName();
                }
                case 2: {
                    return gratuity.getTicket().getId();
                }
                case 3: {
                    return NumberUtil.formatNumber(gratuity.getAmount());
                }
            }
            return null;
        }
    }
}


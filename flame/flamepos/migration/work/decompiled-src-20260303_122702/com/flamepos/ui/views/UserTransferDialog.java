/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.OrderInfoView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserTransferDialog
extends POSDialog {
    private OrderInfoView view;
    private JList list;
    private TitlePanel titlePanel;
    private static Log logger = LogFactory.getLog(UserTransferDialog.class);

    public UserTransferDialog(OrderInfoView view) {
        this.view = view;
        this.createUI();
    }

    private void createUI() {
        this.setTitle(Messages.getString("UserTransferDialog.0"));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.titlePanel = new TitlePanel();
        this.titlePanel.setTitle(Messages.getString("UserTransferDialog.1"));
        panel.add(this.titlePanel);
        this.add((Component)panel, "North");
        List<User> users = UserDAO.getInstance().findAll();
        DefaultListModel<User> model = new DefaultListModel<User>();
        this.list = new JList(model);
        this.list.setFixedCellHeight(PosUIManager.getSize(60));
        for (User user : users) {
            model.addElement(user);
        }
        PosScrollPane scrollPane = new PosScrollPane(this.list);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(scrollPane);
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.add((Component)new JSeparator(), "North");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        footerPanel.add(buttonPanel);
        this.getContentPane().add((Component)footerPanel, "South");
        PosButton btnOk = new PosButton();
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                User selectedUser = (User)UserTransferDialog.this.list.getSelectedValue();
                if (selectedUser == null) {
                    POSMessageDialog.showError(UserTransferDialog.this, "Please select user.");
                    return;
                }
                if (!selectedUser.isClockedIn().booleanValue()) {
                    POSMessageDialog.showError(UserTransferDialog.this, "Selected user is not clocked in.");
                    return;
                }
                List<Ticket> tickets = UserTransferDialog.this.view.getTickets();
                for (Ticket ticket : tickets) {
                    ticket.setOwner(selectedUser);
                    TicketDAO.getInstance().saveOrUpdate(ticket);
                }
                try {
                    UserTransferDialog.this.view.getReportPanel().removeAll();
                    UserTransferDialog.this.view.createReport();
                    UserTransferDialog.this.view.revalidate();
                    UserTransferDialog.this.view.repaint();
                    UserTransferDialog.this.dispose();
                    POSMessageDialog.showMessage(Messages.getString("UserTransferDialog.3"));
                }
                catch (Exception e1) {
                    POSMessageDialog.showError(Messages.getString("UserTransferDialog.4"));
                    logger.error((Object)e1);
                }
            }
        });
        btnOk.setText(Messages.getString("UserTransferDialog.5"));
        buttonPanel.add((Component)btnOk, "split 2, align center");
        PosButton btnCancel = new PosButton();
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                UserTransferDialog.this.dispose();
            }
        });
        btnCancel.setText(Messages.getString("UserTransferDialog.7"));
        buttonPanel.add(btnCancel);
    }
}


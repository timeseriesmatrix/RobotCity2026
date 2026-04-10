/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 *  org.jdesktop.swingx.JXCollapsiblePane
 */
package com.floreantpos.ui;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PaymentStatusFilter;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.ui.TicketListView;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXCollapsiblePane;

public class OrderFilterPanel
extends JXCollapsiblePane {
    private ITicketList ticketList;
    private TicketListView ticketLists;
    private POSToggleButton btnFilterByOpenStatus;
    private POSToggleButton btnFilterByPaidStatus;
    private POSToggleButton btnFilterByUnPaidStatus;

    public OrderFilterPanel(ITicketList ticketList) {
        this.ticketList = ticketList;
        this.ticketLists = (TicketListView)ticketList;
        this.setCollapsed(true);
        this.getContentPane().setLayout((LayoutManager)new MigLayout("fill", "fill, grow", ""));
        this.createPaymentStatusFilterPanel();
        this.createOrderTypeFilterPanel();
    }

    private void createPaymentStatusFilterPanel() {
        this.btnFilterByOpenStatus = new POSToggleButton(PaymentStatusFilter.OPEN.toString());
        this.btnFilterByPaidStatus = new POSToggleButton(PaymentStatusFilter.PAID.toString());
        this.btnFilterByUnPaidStatus = new POSToggleButton(PaymentStatusFilter.CLOSED.toString());
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(this.btnFilterByOpenStatus);
        paymentGroup.add(this.btnFilterByPaidStatus);
        paymentGroup.add(this.btnFilterByUnPaidStatus);
        PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
        switch (paymentStatusFilter) {
            case OPEN: {
                this.btnFilterByOpenStatus.setSelected(true);
                break;
            }
            case PAID: {
                this.btnFilterByPaidStatus.setSelected(true);
                break;
            }
            case CLOSED: {
                this.btnFilterByUnPaidStatus.setSelected(true);
            }
        }
        ActionListener psFilterHandler = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                if (actionCommand.equals("CLOSED") && !Application.getCurrentUser().hasPermission(UserPermission.VIEW_ALL_CLOSE_TICKETS)) {
                    String password = PasswordEntryDialog.show(Application.getPosWindow(), "Please enter privileged password");
                    if (StringUtils.isEmpty((String)password)) {
                        OrderFilterPanel.this.updateButton();
                        return;
                    }
                    User user2 = UserDAO.getInstance().findUserBySecretKey(password);
                    if (user2 == null) {
                        POSMessageDialog.showError(Application.getPosWindow(), "No user found with that secret key");
                        OrderFilterPanel.this.updateButton();
                        return;
                    }
                    if (!user2.hasPermission(UserPermission.VIEW_ALL_CLOSE_TICKETS)) {
                        POSMessageDialog.showError(Application.getPosWindow(), "No permission");
                        OrderFilterPanel.this.updateButton();
                        return;
                    }
                }
                String filter = actionCommand.replaceAll("\\s", "_");
                TerminalConfig.setPaymentStatusFilter(filter);
                OrderFilterPanel.this.ticketList.updateTicketList();
                OrderFilterPanel.this.ticketLists.updateButtonStatus();
            }
        };
        this.btnFilterByOpenStatus.addActionListener(psFilterHandler);
        this.btnFilterByPaidStatus.addActionListener(psFilterHandler);
        this.btnFilterByUnPaidStatus.addActionListener(psFilterHandler);
        JPanel filterByPaymentStatusPanel = new JPanel((LayoutManager)new MigLayout("", "fill, grow", ""));
        filterByPaymentStatusPanel.setBorder(new TitledBorder(Messages.getString("SwitchboardView.3")));
        filterByPaymentStatusPanel.add(this.btnFilterByOpenStatus);
        filterByPaymentStatusPanel.add(this.btnFilterByPaidStatus);
        filterByPaymentStatusPanel.add(this.btnFilterByUnPaidStatus);
        this.getContentPane().add(filterByPaymentStatusPanel);
    }

    private void createOrderTypeFilterPanel() {
        OrderTypeFilterButton btnFilterByOrderTypeALL = new OrderTypeFilterButton(POSConstants.ALL);
        JPanel filterByOrderPanel = new JPanel((LayoutManager)new MigLayout("", "fill, grow", ""));
        filterByOrderPanel.setBorder(new TitledBorder(Messages.getString("SwitchboardView.4")));
        ButtonGroup orderTypeGroup = new ButtonGroup();
        orderTypeGroup.add(btnFilterByOrderTypeALL);
        filterByOrderPanel.add(btnFilterByOrderTypeALL);
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        for (OrderType orderType : orderTypes) {
            OrderTypeFilterButton orderTypeFilterButton = new OrderTypeFilterButton(orderType.getName());
            orderTypeGroup.add(orderTypeFilterButton);
            filterByOrderPanel.add(orderTypeFilterButton);
        }
        this.getContentPane().add(filterByOrderPanel);
    }

    private void updateButton() {
        PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
        if (paymentStatusFilter.name().equals("OPEN")) {
            this.btnFilterByOpenStatus.setSelected(true);
        } else if (paymentStatusFilter.name().equals("PAID")) {
            this.btnFilterByPaidStatus.setSelected(true);
        } else if (paymentStatusFilter.name().equals("CLOSE")) {
            this.btnFilterByUnPaidStatus.setSelected(true);
        }
    }

    private class OrderTypeFilterButton
    extends POSToggleButton
    implements ActionListener {
        public OrderTypeFilterButton(String name) {
            String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
            if (orderTypeFilter.equals(name)) {
                this.setSelected(true);
            }
            this.setText(name);
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.setSelected(true);
            String actionCommand = e.getActionCommand();
            TerminalConfig.setOrderTypeFilter(actionCommand);
            OrderFilterPanel.this.ticketList.updateTicketList();
            OrderFilterPanel.this.ticketLists.updateButtonStatus();
        }
    }
}


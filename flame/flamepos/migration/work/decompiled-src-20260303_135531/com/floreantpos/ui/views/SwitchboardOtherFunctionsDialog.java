/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.demo.KitchenDisplayWindow;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.extension.TicketImportPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.ManagerDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.PayoutDialog;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.payment.AuthorizableTicketBrowser;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class SwitchboardOtherFunctionsDialog
extends POSDialog
implements ActionListener {
    private SwitchboardView switchboardView;
    private PosButton btnManager = new PosButton(POSConstants.MANAGER_BUTTON_TEXT);
    private PosButton btnAuthorize = new PosButton(POSConstants.AUTHORIZE_BUTTON_TEXT);
    private PosButton btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
    private PosButton btnPayout = new PosButton(POSConstants.PAYOUT_BUTTON_TEXT);
    private PosButton btnTableManage = new PosButton(POSConstants.TABLE_MANAGE_BUTTON_TEXT);
    private PosButton btnOnlineTickets = new PosButton(POSConstants.ONLINE_TICKET_BUTTON_TEXT);
    private FloorLayoutPlugin floorLayoutPlugin;
    private TicketImportPlugin ticketImportPlugin;

    public SwitchboardOtherFunctionsDialog(SwitchboardView switchboardView) {
        this.switchboardView = switchboardView;
        this.setTitle(Messages.getString("SwitchboardOtherFunctionsDialog.0"));
        this.setDefaultCloseOperation(2);
        this.setSize(800, 400);
        JPanel contentPane = new JPanel(new GridLayout(3, 0, 10, 10));
        contentPane.add(this.btnManager);
        contentPane.add(this.btnAuthorize);
        contentPane.add(this.btnKitchenDisplay);
        contentPane.add(this.btnPayout);
        this.floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);
        if (this.floorLayoutPlugin != null) {
            contentPane.add(this.btnTableManage);
        }
        this.ticketImportPlugin = (TicketImportPlugin)ExtensionManager.getPlugin(TicketImportPlugin.class);
        if (this.ticketImportPlugin != null) {
            contentPane.add(this.btnOnlineTickets);
        }
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setContentPane(contentPane);
        this.btnManager.addActionListener(this);
        this.btnAuthorize.addActionListener(this);
        this.btnKitchenDisplay.addActionListener(this);
        this.btnPayout.addActionListener(this);
        this.btnTableManage.addActionListener(this);
        this.btnOnlineTickets.addActionListener(this);
        this.setupPermission();
    }

    private void setupPermission() {
        Set<UserPermission> permissions;
        User user = Application.getCurrentUser();
        UserType userType = user.getType();
        if (userType != null && (permissions = userType.getPermissions()) != null) {
            for (UserPermission permission : permissions) {
                if (permission.equals(UserPermission.PAY_OUT)) {
                    this.btnPayout.setEnabled(true);
                    continue;
                }
                if (!permission.equals(UserPermission.PERFORM_MANAGER_TASK)) continue;
                this.btnManager.setEnabled(true);
            }
        }
    }

    private void doShowKitchenDisplay() {
        Window[] windows;
        for (Window window : windows = Window.getWindows()) {
            if (!(window instanceof KitchenDisplayWindow)) continue;
            window.toFront();
            return;
        }
        KitchenDisplayWindow window = new KitchenDisplayWindow();
        window.setVisible(true);
    }

    private void doShowTicketImportDialog() {
        TicketImportPlugin ticketImportPlugin = (TicketImportPlugin)ExtensionManager.getPlugin(TicketImportPlugin.class);
        if (ticketImportPlugin != null) {
            ticketImportPlugin.startService();
        }
    }

    private void doShowAuthorizeTicketDialog() {
        AuthorizableTicketBrowser dialog = new AuthorizableTicketBrowser(Application.getPosWindow());
        dialog.setDefaultCloseOperation(2);
        dialog.setSize(PosUIManager.getSize(800, 600));
        dialog.setLocationRelativeTo(Application.getPosWindow());
        dialog.setVisible(true);
    }

    private void doShowManagerWindow() {
        ManagerDialog dialog = new ManagerDialog();
        dialog.open();
        this.switchboardView.updateTicketList();
    }

    private void doPayout() {
        PayoutDialog dialog = new PayoutDialog();
        dialog.open();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        this.dispose();
        if (source == this.btnManager) {
            this.doShowManagerWindow();
        } else if (source == this.btnAuthorize) {
            this.doShowAuthorizeTicketDialog();
        } else if (source == this.btnKitchenDisplay) {
            this.doShowKitchenDisplay();
        } else if (source == this.btnPayout) {
            this.doPayout();
        } else if (source == this.btnTableManage) {
            if (this.floorLayoutPlugin != null) {
                this.floorLayoutPlugin.openTicketsAndTablesDisplay();
            }
        } else if (source == this.btnOnlineTickets) {
            this.doShowTicketImportDialog();
        }
    }
}


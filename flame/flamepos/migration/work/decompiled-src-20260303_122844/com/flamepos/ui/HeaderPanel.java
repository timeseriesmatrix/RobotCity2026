/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.actions.ClockInOutAction;
import com.floreantpos.actions.HomeScreenViewAction;
import com.floreantpos.actions.LogoutAction;
import com.floreantpos.actions.ShowOtherFunctionsAction;
import com.floreantpos.actions.ShutDownAction;
import com.floreantpos.actions.SwithboardViewAction;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.views.IView;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.TableMapView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.PosGuiUtil;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Timer;
import net.miginfocom.swing.MigLayout;

public class HeaderPanel
extends JPanel {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy, hh:mm:ss aaa");
    private JLabel statusLabel;
    private Timer clockTimer = new Timer(1000, new ClockTimerHandler());
    private Timer autoLogoffTimer;
    private String userString = Messages.getString("PosMessage.70");
    private String terminalString = Messages.getString("TERMINAL_LABEL");
    private JLabel logoffLabel;
    private PosButton btnHomeScreen;
    private POSToggleButton btnMaintainance;
    private PosButton btnOthers;
    private PosButton btnSwithboardView;
    private PosButton btnLogout;
    private PosButton btnClockOUt;
    private PosButton btnShutdown;
    private JPanel buttonPanel;
    private int btnSize;
    private QuickMaintenanceExplorer quickMaintenancePanel;

    public HeaderPanel() {
        super(new BorderLayout());
        this.setOpaque(true);
        this.setBackground(Color.white);
        this.buttonPanel = new JPanel((LayoutManager)new MigLayout("hidemode 3", "", ""));
        this.buttonPanel.setBackground(Color.white);
        JLabel logoLabel = new JLabel(IconFactory.getIcon("/ui_icons/", "header-logo.png"));
        this.add((Component)logoLabel, "West");
        TransparentPanel statusPanel = new TransparentPanel((LayoutManager)new MigLayout("hidemode 3, fill, ins 0, gap 0"));
        this.statusLabel = new JLabel();
        this.statusLabel.setFont(this.statusLabel.getFont().deriveFont(1));
        this.statusLabel.setHorizontalAlignment(0);
        this.statusLabel.setVerticalAlignment(3);
        statusPanel.add((Component)this.statusLabel, "grow");
        this.logoffLabel = new JLabel();
        this.logoffLabel.setFont(this.statusLabel.getFont().deriveFont(1));
        this.logoffLabel.setHorizontalAlignment(0);
        this.logoffLabel.setVerticalAlignment(1);
        statusPanel.add((Component)this.logoffLabel, "newline, growx");
        this.add((Component)statusPanel, "Center");
        this.btnSize = PosUIManager.getSize(60);
        this.quickMaintenancePanel = new QuickMaintenanceExplorer();
        this.btnHomeScreen = new PosButton(new HomeScreenViewAction(false, true));
        this.buttonPanel.add((Component)this.btnHomeScreen, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnMaintainance = new POSToggleButton();
        this.btnMaintainance.setIcon(IconFactory.getIcon("/ui_icons/", "quick_setting.png"));
        this.btnMaintainance.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                RootView.getInstance().setMaintenanceMode(HeaderPanel.this.btnMaintainance.isSelected());
                HeaderPanel.this.updateViewToMaintenanceMode(HeaderPanel.this.btnMaintainance.isSelected());
                IView view = RootView.getInstance().getCurrentView();
                if (view instanceof OrderView) {
                    OrderView.getInstance().getCategoryView().initialize();
                } else if (view instanceof TableMapView) {
                    TableMapView.getInstance().updateView();
                } else if (view instanceof SwitchboardView) {
                    SwitchboardView.getInstance().rendererOrderPanel();
                }
            }
        });
        this.buttonPanel.add((Component)this.btnMaintainance, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnMaintainance.setVisible(false);
        this.btnSwithboardView = new PosButton(new SwithboardViewAction(false, true));
        this.buttonPanel.add((Component)this.btnSwithboardView, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnSwithboardView.setVisible(false);
        this.btnOthers = new PosButton(new ShowOtherFunctionsAction(false, true));
        this.buttonPanel.add((Component)this.btnOthers, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnClockOUt = new PosButton(new ClockInOutAction(false, true));
        this.buttonPanel.add((Component)this.btnClockOUt, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnLogout = new PosButton(new LogoutAction(false, true));
        this.btnLogout.setToolTipText(Messages.getString("Logout"));
        this.buttonPanel.add((Component)this.btnLogout, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.btnShutdown = new PosButton(new ShutDownAction(false, true));
        this.btnShutdown.setIcon(IconFactory.getIcon("/ui_icons/", "shutdown.png"));
        this.btnShutdown.setToolTipText(Messages.getString("Shutdown"));
        this.buttonPanel.add((Component)this.btnShutdown, "w " + this.btnSize + "!, h " + this.btnSize + "!");
        this.buttonPanel.add(this.quickMaintenancePanel);
        this.quickMaintenancePanel.setVisible(false);
        this.add((Component)this.buttonPanel, "East");
        this.add((Component)new JSeparator(0), "South");
        this.clockTimer.start();
        if (TerminalConfig.isAutoLogoffEnable()) {
            this.autoLogoffTimer = new Timer(1000, new AutoLogoffHandler());
        }
    }

    private void showHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.userString + ": " + Application.getCurrentUser().getFirstName());
        sb.append(", ");
        sb.append(this.terminalString + ": " + Application.getInstance().getTerminal().getName());
        sb.append(", ");
        sb.append(dateFormat.format(Calendar.getInstance().getTime()));
        this.statusLabel.setText(sb.toString());
    }

    private void updateView() {
        User currentUser = Application.getCurrentUser();
        boolean hasPermission = TerminalConfig.isAllowedQuickMaintenance() && (currentUser.isAdministrator() || currentUser.hasPermission(UserPermission.QUICK_MAINTENANCE));
        this.btnMaintainance.setVisible(hasPermission);
        this.quickMaintenancePanel.setVisible(this.btnMaintainance.isSelected() && hasPermission);
    }

    private void startTimer() {
        this.clockTimer.start();
        if (this.autoLogoffTimer != null) {
            this.autoLogoffTimer.start();
        }
    }

    private void stopTimer() {
        this.clockTimer.stop();
        if (this.autoLogoffTimer != null) {
            this.autoLogoffTimer.stop();
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            this.updateView();
            this.startTimer();
        } else {
            this.stopTimer();
        }
    }

    public void updateViewToMaintenanceMode(boolean enable) {
        boolean selected = this.btnMaintainance.isSelected();
        this.btnHomeScreen.setVisible(!selected);
        this.btnOthers.setVisible(!selected);
        this.btnSwithboardView.setVisible(!selected);
        this.btnClockOUt.setVisible(!selected);
        this.btnLogout.setVisible(!selected);
        this.btnShutdown.setVisible(!selected);
        this.btnOthers.setVisible(!selected);
        this.quickMaintenancePanel.setVisible(selected);
    }

    public void updateOthersFunctionsView(boolean enable) {
        boolean selected = this.btnMaintainance.isSelected();
        this.btnHomeScreen.setVisible(!selected);
        this.btnOthers.setVisible(!selected);
        this.btnSwithboardView.setVisible(!selected);
        this.btnClockOUt.setVisible(!selected);
        this.btnLogout.setVisible(!selected);
        this.btnShutdown.setVisible(!selected);
        this.btnOthers.setVisible(!selected && enable);
        this.quickMaintenancePanel.setVisible(selected);
    }

    public void updateSwitchBoardView(boolean enable) {
        boolean selected = this.btnMaintainance.isSelected();
        this.btnHomeScreen.setVisible(!selected);
        this.btnOthers.setVisible(!selected);
        this.btnSwithboardView.setVisible(!selected);
        this.btnClockOUt.setVisible(!selected);
        this.btnLogout.setVisible(!selected);
        this.btnShutdown.setVisible(!selected);
        this.btnSwithboardView.setVisible(!selected && enable);
        this.quickMaintenancePanel.setVisible(selected);
    }

    public void updateHomeView(boolean enable) {
        this.btnHomeScreen.setVisible(enable);
    }

    class AutoLogoffHandler
    implements ActionListener,
    AWTEventListener {
        int countDown = TerminalConfig.getAutoLogoffTime();

        public AutoLogoffHandler() {
            Toolkit.getDefaultToolkit().addAWTEventListener(this, 56L);
        }

        @Override
        public void eventDispatched(AWTEvent event) {
            this.reset();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!HeaderPanel.this.isShowing()) {
                HeaderPanel.this.autoLogoffTimer.stop();
                return;
            }
            if (!TerminalConfig.isAutoLogoffEnable()) {
                return;
            }
            if (PosGuiUtil.isModalDialogShowing()) {
                this.reset();
                return;
            }
            --this.countDown;
            int min = this.countDown / 60;
            int sec = this.countDown % 60;
            HeaderPanel.this.logoffLabel.setText(Messages.getString("HeaderPanel.0") + min + ":" + sec);
            if (this.countDown == 0) {
                Application.getInstance().doAutoLogout();
            }
        }

        public void reset() {
            HeaderPanel.this.logoffLabel.setText("");
            this.countDown = TerminalConfig.getAutoLogoffTime();
            HeaderPanel.this.autoLogoffTimer.setInitialDelay(5000);
            HeaderPanel.this.autoLogoffTimer.restart();
        }
    }

    private class ClockTimerHandler
    implements ActionListener {
        private ClockTimerHandler() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!HeaderPanel.this.isShowing()) {
                HeaderPanel.this.clockTimer.stop();
                return;
            }
            HeaderPanel.this.showHeader();
        }
    }
}


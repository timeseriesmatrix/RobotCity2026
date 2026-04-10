/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.actions.ClockInOutAction;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.DatabaseConfigurationDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.util.ShiftException;
import com.floreantpos.util.UserNotFoundException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.LogFactory;

class LoginPasswordEntryView
extends JPanel {
    private PosButton btnConfigureDatabase;
    private PosButton btnShutdown;
    private JPanel buttonPanel;
    private JPanel bottomPanel;
    Action goAction = new AbstractAction(){

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (POSConstants.LOGIN.equals(command)) {
                LoginPasswordEntryView.this.doLogin();
            } else if (POSConstants.SHUTDOWN.equals(command)) {
                Application.getInstance().shutdownPOS();
            } else if ("DBCONFIG".equalsIgnoreCase(command)) {
                DatabaseConfigurationDialog.show(Application.getPosWindow());
            }
        }
    };
    private PosButton psbtnLogin;
    private JLabel lblTerminalId;
    private JToggleButton btnRegularMode = new JToggleButton("<html><center>REGULAR<br/>MODE</center></html>");
    private JToggleButton btnKitchenMode = new JToggleButton("<html><center>KITCHEN<br/>MODE</center></html>");

    LoginPasswordEntryView() {
        this.initComponents();
        this.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    private void initComponents() {
        this.buttonPanel = new JPanel();
        this.bottomPanel = new JPanel();
        this.btnShutdown = new PosButton();
        this.setPreferredSize(new Dimension(320, 593));
        this.setLayout(new BorderLayout());
        this.buttonPanel.setOpaque(false);
        this.buttonPanel.setPreferredSize(new Dimension(200, 180));
        this.buttonPanel.setLayout((LayoutManager)new MigLayout("", "[111px][111px][111px,grow]", "[60px][60px][60px][60px]"));
        this.lblTerminalId = new JLabel("TERMINAL ID:");
        this.lblTerminalId.setForeground(Color.BLACK);
        this.lblTerminalId.setFont(new Font("Dialog", 1, 18));
        this.lblTerminalId.setHorizontalAlignment(0);
        this.add((Component)this.lblTerminalId, "North");
        this.bottomPanel.setLayout((LayoutManager)new MigLayout("hidemode 3, fill"));
        this.bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.btnRegularMode);
        buttonGroup.add(this.btnKitchenMode);
        this.btnRegularMode.setFocusable(false);
        this.btnKitchenMode.setFocusable(false);
        ModeSelectionListener l = new ModeSelectionListener();
        this.btnRegularMode.addActionListener(l);
        this.btnKitchenMode.addActionListener(l);
        this.btnRegularMode.setSelected(TerminalConfig.isRegularMode());
        this.btnKitchenMode.setSelected(TerminalConfig.isKitchenMode());
        if (!this.btnRegularMode.isSelected() && !this.btnKitchenMode.isSelected()) {
            this.btnRegularMode.setSelected(true);
        }
        JPanel modePanel = new JPanel(new GridLayout(1, 0, 2, 2));
        modePanel.add(this.btnRegularMode);
        modePanel.add(this.btnKitchenMode);
        this.bottomPanel.add((Component)modePanel, "h 60!, grow, wrap");
        this.psbtnLogin = new PosButton();
        this.psbtnLogin.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LoginPasswordEntryView.this.doLogin();
            }
        });
        this.psbtnLogin.setText("LOGIN");
        this.bottomPanel.add((Component)this.psbtnLogin, "grow, wrap, gapbottom 20px");
        PosButton btnClockOUt = new PosButton(new ClockInOutAction(false, true));
        this.bottomPanel.add((Component)btnClockOUt, "grow, wrap, h 60!");
        if (TerminalConfig.isShowDbConfigureButton()) {
            this.btnConfigureDatabase = new PosButton();
            this.btnConfigureDatabase.setAction(this.goAction);
            this.btnConfigureDatabase.setText(POSConstants.CONFIGURE_DATABASE);
            this.btnConfigureDatabase.setFocusable(false);
            this.btnConfigureDatabase.setActionCommand("DBCONFIG");
            this.bottomPanel.add((Component)this.btnConfigureDatabase, "grow, wrap, h 60!");
        }
        this.btnShutdown.setAction(this.goAction);
        this.btnShutdown.setText(POSConstants.SHUTDOWN);
        this.btnShutdown.setFocusable(false);
        if (TerminalConfig.isFullscreenMode()) {
            if (this.btnConfigureDatabase != null) {
                this.btnConfigureDatabase.setVisible(false);
            }
            if (this.btnShutdown != null) {
                this.btnShutdown.setVisible(false);
            }
        }
        this.bottomPanel.add((Component)this.btnShutdown, "grow, wrap, h 60!");
        this.add((Component)this.bottomPanel, "South");
        this.lblTerminalId.setText("");
    }

    public synchronized void doLogin() {
        try {
            User user = PasswordEntryDialog.getUser(Application.getPosWindow(), Messages.getString("LoginPasswordEntryView.13"), Messages.getString("LoginPasswordEntryView.14"));
            if (user == null) {
                return;
            }
            Application application = Application.getInstance();
            application.doLogin(user);
        }
        catch (UserNotFoundException e) {
            LogFactory.getLog(Application.class).error((Object)e);
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("LoginPasswordEntryView.15"));
        }
        catch (ShiftException e) {
            LogFactory.getLog(Application.class).error((Object)e);
            MessageDialog.showError(e.getMessage());
        }
        catch (Exception e1) {
            LogFactory.getLog(Application.class).error((Object)e1);
            String message = e1.getMessage();
            if (message != null && message.contains("Cannot open connection")) {
                MessageDialog.showError(Messages.getString("LoginPasswordEntryView.17"), e1);
                DatabaseConfigurationDialog.show(Application.getPosWindow());
            }
            MessageDialog.showError(Messages.getString("LoginPasswordEntryView.18"), e1);
        }
    }

    public void setTerminalId(int terminalId) {
        this.lblTerminalId.setText(Messages.getString("LoginPasswordEntryView.19") + terminalId);
    }

    class ModeSelectionListener
    implements ActionListener {
        ModeSelectionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TerminalConfig.setRegularMode(LoginPasswordEntryView.this.btnRegularMode.isSelected());
            TerminalConfig.setKitchenMode(LoginPasswordEntryView.this.btnKitchenMode.isSelected());
        }
    }
}


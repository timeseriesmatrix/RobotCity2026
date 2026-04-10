/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.ui.views;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.actions.ClockInOutAction;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.DatabaseConfigurationDialog;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.main.Application;
import com.floreantpos.main.FlameTheme;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.User;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.OrderTypeLoginButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.ui.views.IView;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.ViewPanel;
import com.floreantpos.util.ShiftException;
import com.floreantpos.util.UserNotFoundException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.LogFactory;

public class LoginView
extends ViewPanel {
    public static final String VIEW_NAME = "LOGIN_VIEW";
    private boolean backOfficeLogin;
    private PosButton btnSwitchBoard;
    private PosButton btnKitchenDisplay;
    private PosButton btnDriverView;
    private PosButton btnConfigureDatabase;
    private PosButton btnBackOffice;
    private PosButton btnShutdown;
    private PosButton btnClockOUt;
    private JLabel lblTerminalId;
    private JPanel centerPanel = new JPanel((LayoutManager)new MigLayout("al center center", "sg", "100"));
    private static LoginView instance;
    private JPanel mainPanel;
    private JPanel panel1 = new JPanel((LayoutManager)new MigLayout("fill, ins 0, hidemode 3", "sg, fill", ""));
    private JPanel panel2 = new JPanel((LayoutManager)new MigLayout("fill, ins 0, hidemode 3", "sg, fill", ""));
    private int width;
    private int height;

    private LoginView() {
        this.setLayout(new BorderLayout(5, 5));
        this.width = PosUIManager.getSize(600);
        this.height = PosUIManager.getSize(100);
        this.centerPanel.setLayout((LayoutManager)new MigLayout("al center center", "sg fill", String.valueOf(this.height)));
        JLabel titleLabel = new JLabel(IconFactory.getIcon("/ui_icons/", "title.png"));
        titleLabel.setOpaque(false);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(FlameTheme.BACKGROUND_SECONDARY);
        panel.setPreferredSize(new Dimension(0, PosUIManager.getSize(120)));
        panel.add((Component)titleLabel, "Center");
        JSeparator separator = new JSeparator(0);
        separator.setForeground(FlameTheme.SEPARATOR_COLOR);
        panel.add((Component)separator, "South");
        this.add((Component)panel, "North");
        this.add((Component)this.createCenterPanel(), "Center");
    }

    private JPanel createCenterPanel() {
        OrderServiceExtension orderService;
        this.lblTerminalId = new JLabel(Messages.getString("LoginView.0"));
        this.lblTerminalId.setForeground(FlameTheme.TEXT_PRIMARY);
        this.lblTerminalId.setFont(new Font("Dialog", 1, PosUIManager.getFontSize(18)));
        this.lblTerminalId.setHorizontalAlignment(0);
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.add((Component)this.lblTerminalId, "North");
        this.btnSwitchBoard = new PosButton(POSConstants.ORDERS);
        this.btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
        this.btnDriverView = new PosButton("DRIVER VIEW");
        this.btnConfigureDatabase = new PosButton(POSConstants.CONFIGURE_DATABASE);
        this.btnBackOffice = new PosButton(POSConstants.BACK_OFFICE_BUTTON_TEXT);
        this.btnShutdown = new PosButton(POSConstants.SHUTDOWN);
        this.btnClockOUt = new PosButton(new ClockInOutAction(false, true));
        this.btnBackOffice.setVisible(false);
        this.btnSwitchBoard.setVisible(false);
        this.btnKitchenDisplay.setVisible(false);
        this.btnClockOUt.setVisible(false);
        JPanel panel3 = new JPanel(new GridLayout(1, 0, 5, 5));
        JPanel panel4 = new JPanel((LayoutManager)new MigLayout("fill, ins 0, hidemode 3", "sg, fill", ""));
        this.centerPanel.add((Component)this.panel1, "cell 0 0, wrap, w " + this.width + "px, h " + this.height + "px, grow");
        panel3.add(this.btnSwitchBoard);
        panel3.add(this.btnBackOffice);
        if (TerminalConfig.isShowKitchenBtnOnLoginScreen()) {
            panel3.add(this.btnKitchenDisplay);
        }
        if ((orderService = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class)) != null) {
            panel3.add(this.btnDriverView);
            this.btnDriverView.setVisible(false);
        }
        this.centerPanel.add((Component)panel3, "cell 0 2, wrap, w " + this.width + "px, h " + this.height + "px, grow");
        panel4.add((Component)this.btnClockOUt, "grow");
        panel4.add((Component)this.btnConfigureDatabase, "grow");
        panel4.add((Component)this.btnShutdown, "grow");
        this.centerPanel.add((Component)panel4, "cell 0 3, wrap, w " + this.width + "px, h " + this.height + "px, grow");
        if (TerminalConfig.isFullscreenMode()) {
            if (this.btnConfigureDatabase != null) {
                this.btnConfigureDatabase.setVisible(false);
            }
            if (this.btnShutdown != null) {
                this.btnShutdown.setVisible(false);
            }
        } else if (!TerminalConfig.isShowDbConfigureButton()) {
            this.btnConfigureDatabase.setVisible(false);
        }
        this.initActionHandlers();
        this.mainPanel.add((Component)this.centerPanel, "Center");
        return this.mainPanel;
    }

    public void initializeOrderButtonPanel() {
        this.panel1.removeAll();
        this.panel2.removeAll();
        List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
        int buttonCount = 0;
        for (OrderType orderType : orderTypes) {
            if (!orderType.isShowInLoginScreen().booleanValue()) continue;
            if (buttonCount < 3) {
                this.panel1.add((Component)new OrderTypeLoginButton(orderType), "grow");
            } else {
                this.panel2.add((Component)new OrderTypeLoginButton(orderType), "grow");
            }
            ++buttonCount;
        }
        if (buttonCount > 3) {
            this.centerPanel.add((Component)this.panel2, "cell 0 1, wrap,w " + this.width + "px, h " + this.height + "px, grow");
        }
        this.btnSwitchBoard.setVisible(true);
        this.btnKitchenDisplay.setVisible(true);
        this.btnBackOffice.setVisible(true);
        this.btnClockOUt.setVisible(true);
        this.btnDriverView.setVisible(true);
        this.centerPanel.repaint();
    }

    public void updateView() {
        this.mainPanel.repaint();
    }

    void initActionHandlers() {
        this.btnConfigureDatabase.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConfigurationDialog.show(Application.getPosWindow());
            }
        });
        this.btnBackOffice.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LoginView.this.setBackOfficeLogin(true);
                TerminalConfig.setDefaultView(SwitchboardView.VIEW_NAME);
                LoginView.this.doLogin();
            }
        });
        this.btnKitchenDisplay.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalConfig.setDefaultView("KD");
                LoginView.this.doLogin();
            }
        });
        this.btnDriverView.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                IView view = OrderServiceFactory.getOrderService().getDriverView();
                if (view == null) {
                    return;
                }
                RootView.getInstance().setAndShowHomeScreen(view);
            }
        });
        this.btnShutdown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().shutdownPOS();
            }
        });
        this.btnSwitchBoard.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalConfig.setDefaultView(SwitchboardView.VIEW_NAME);
                LoginView.this.doLogin();
            }
        });
    }

    public synchronized void doLogin() {
        try {
            User user = PasswordEntryDialog.getUser(Application.getPosWindow(), Messages.getString("LoginView.1"), Messages.getString("LoginView.2"));
            if (user == null) {
                this.setBackOfficeLogin(false);
                return;
            }
            Application application = Application.getInstance();
            application.doLogin(user);
        }
        catch (UserNotFoundException e) {
            LogFactory.getLog(Application.class).error((Object)e);
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("LoginView.3"));
        }
        catch (ShiftException e) {
            LogFactory.getLog(Application.class).error((Object)e);
            MessageDialog.showError(e.getMessage());
        }
        catch (Exception e1) {
            LogFactory.getLog(Application.class).error((Object)e1);
            String message = e1.getMessage();
            if (message != null && message.contains("Cannot open connection")) {
                MessageDialog.showError(Messages.getString("LoginView.4"), e1);
                DatabaseConfigurationDialog.show(Application.getPosWindow());
            }
            MessageDialog.showError(Messages.getString("LoginView.5"), e1);
        }
    }

    public void setTerminalId(int terminalId) {
        this.lblTerminalId.setText(Messages.getString("LoginView.6") + terminalId);
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    public static LoginView getInstance() {
        if (instance == null) {
            instance = new LoginView();
        }
        return instance;
    }

    public JPanel getCenterPanel() {
        return this.centerPanel;
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public boolean isBackOfficeLogin() {
        return this.backOfficeLogin;
    }

    public void setBackOfficeLogin(boolean backOfficeLogin) {
        this.backOfficeLogin = backOfficeLogin;
    }
}

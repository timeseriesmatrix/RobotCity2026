/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.demo.KitchenDisplayView;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.HeaderPanel;
import com.floreantpos.ui.views.CustomerView;
import com.floreantpos.ui.views.IView;
import com.floreantpos.ui.views.LoginView;
import com.floreantpos.ui.views.SwitchboardOtherFunctionsView;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.TableMapView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.payment.SettleTicketDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RootView
extends TransparentPanel {
    private CardLayout cards = new CardLayout();
    private HeaderPanel headerPanel = new HeaderPanel();
    private JPanel contentPanel = new JPanel(this.cards);
    private LoginView loginScreen;
    private SettleTicketDialog paymentView;
    private String currentViewName;
    private IView homeView;
    private Map<String, IView> views = new HashMap<String, IView>();
    private boolean maintenanceMode;
    private static RootView instance;

    private RootView() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(3, 3, 3, 3));
        this.initView();
    }

    private void initView() {
        this.headerPanel.setVisible(false);
        this.add((Component)this.headerPanel, "North");
        this.add(this.contentPanel);
        this.loginScreen = LoginView.getInstance();
        this.addView(this.loginScreen);
    }

    public void addView(IView iView) {
        this.views.put(iView.getViewName(), iView);
        this.contentPanel.add(iView.getViewName(), iView.getViewComponent());
    }

    public void showView(String viewName) {
        if ("LOGIN_VIEW".equals(viewName)) {
            this.headerPanel.setVisible(false);
        } else {
            this.headerPanel.setVisible(true);
        }
        this.currentViewName = viewName;
        this.cards.show(this.contentPanel, viewName);
        this.headerPanel.updateHomeView(!this.homeView.getViewName().equals(this.currentViewName));
        this.headerPanel.updateOthersFunctionsView(!this.currentViewName.equals("ALL FUNCTIONS"));
        this.headerPanel.updateSwitchBoardView(!this.currentViewName.equals(SwitchboardView.VIEW_NAME));
    }

    public void showView(IView view) {
        if (!this.views.containsKey(view.getViewName())) {
            this.addView(view);
        }
        this.currentViewName = view.getViewName();
        this.showView(this.currentViewName);
    }

    public boolean hasView(String viewName) {
        return this.views.containsKey(viewName);
    }

    public boolean hasView(IView view) {
        return this.views.containsKey(view.getViewName());
    }

    public OrderView getOrderView() {
        return (OrderView)this.views.get("ORDER_VIEW");
    }

    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    public void setMaintenanceMode(boolean b) {
        this.maintenanceMode = b;
    }

    public LoginView getLoginScreen() {
        return this.loginScreen;
    }

    public static synchronized RootView getInstance() {
        if (instance == null) {
            instance = new RootView();
        }
        return instance;
    }

    public SettleTicketDialog getPaymentView() {
        return this.paymentView;
    }

    public HeaderPanel getHeaderPanel() {
        return this.headerPanel;
    }

    public String getCurrentViewName() {
        return this.currentViewName;
    }

    public IView getCurrentView() {
        return this.views.get(this.currentViewName);
    }

    public void showDefaultView() {
        String defaultViewName = TerminalConfig.getDefaultView();
        if (defaultViewName.equals("ALL FUNCTIONS")) {
            this.setAndShowHomeScreen(SwitchboardOtherFunctionsView.getInstance());
        } else if (defaultViewName.equals("KD")) {
            if (!this.hasView(KitchenDisplayView.getInstance())) {
                this.addView(KitchenDisplayView.getInstance());
            }
            this.headerPanel.setVisible(false);
            this.setAndShowHomeScreen(KitchenDisplayView.getInstance());
        } else if (defaultViewName.equals(SwitchboardView.VIEW_NAME)) {
            if (this.loginScreen.isBackOfficeLogin()) {
                this.showBackOffice();
            }
            this.setAndShowHomeScreen(SwitchboardView.getInstance());
        } else {
            OrderType orderType = OrderTypeDAO.getInstance().findByName(defaultViewName);
            if (orderType.isShowTableSelection().booleanValue()) {
                TableMapView tableMapView = TableMapView.getInstance(orderType);
                tableMapView.updateView();
                this.setAndShowHomeScreen(tableMapView);
            } else if (orderType.isRequiredCustomerData().booleanValue()) {
                OrderServiceExtension orderServicePlugin = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
                if (orderServicePlugin != null) {
                    if (orderType.isDelivery().booleanValue()) {
                        this.setAndShowHomeScreen(orderServicePlugin.getDeliveryDispatchView(orderType));
                    } else {
                        CustomerView customerView = CustomerView.getInstance(orderType);
                        customerView.updateView();
                        this.setAndShowHomeScreen(customerView);
                    }
                } else {
                    CustomerView customerView = CustomerView.getInstance(orderType);
                    customerView.updateView();
                    this.setAndShowHomeScreen(customerView);
                }
            } else {
                try {
                    this.homeView = OrderView.getInstance();
                    OrderServiceFactory.getOrderService().createNewTicket(orderType, null, null);
                }
                catch (TicketAlreadyExistsException ticketAlreadyExistsException) {
                    // empty catch block
                }
            }
        }
    }

    public IView getHomeView() {
        return this.homeView;
    }

    public void setAndShowHomeScreen(IView homeScreen) {
        this.homeView = homeScreen;
        this.showHomeScreen();
    }

    public void showHomeScreen() {
        this.showView(this.getHomeView());
    }

    public void showBackOffice() {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        if (window == null) {
            window = new BackOfficeWindow();
        }
        window.setVisible(true);
        window.toFront();
        this.loginScreen.setBackOfficeLogin(false);
    }
}


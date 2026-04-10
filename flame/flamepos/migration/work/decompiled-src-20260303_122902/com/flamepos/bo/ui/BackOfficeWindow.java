/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  com.jidesoft.swing.JideTabbedPane
 */
package com.floreantpos.bo.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.actions.AboutAction;
import com.floreantpos.actions.UpdateAction;
import com.floreantpos.bo.actions.AttendanceHistoryAction;
import com.floreantpos.bo.actions.CategoryExplorerAction;
import com.floreantpos.bo.actions.ConfigureRestaurantAction;
import com.floreantpos.bo.actions.CookingInstructionExplorerAction;
import com.floreantpos.bo.actions.CouponExplorerAction;
import com.floreantpos.bo.actions.CreditCardReportAction;
import com.floreantpos.bo.actions.CurrencyExplorerAction;
import com.floreantpos.bo.actions.CustomPaymentReportAction;
import com.floreantpos.bo.actions.DataExportAction;
import com.floreantpos.bo.actions.DataImportAction;
import com.floreantpos.bo.actions.DrawerPullReportExplorerAction;
import com.floreantpos.bo.actions.EmployeeAttendanceAction;
import com.floreantpos.bo.actions.GroupExplorerAction;
import com.floreantpos.bo.actions.HourlyLaborReportAction;
import com.floreantpos.bo.actions.ItemExplorerAction;
import com.floreantpos.bo.actions.JournalReportAction;
import com.floreantpos.bo.actions.KeyStatisticsSalesReportAction;
import com.floreantpos.bo.actions.LanguageSelectionAction;
import com.floreantpos.bo.actions.MenuItemSizeExplorerAction;
import com.floreantpos.bo.actions.MenuUsageReportAction;
import com.floreantpos.bo.actions.ModifierExplorerAction;
import com.floreantpos.bo.actions.ModifierGroupExplorerAction;
import com.floreantpos.bo.actions.MultiplierExplorerAction;
import com.floreantpos.bo.actions.OpenTicketSummaryReportAction;
import com.floreantpos.bo.actions.OrdersTypeExplorerAction;
import com.floreantpos.bo.actions.PayrollReportAction;
import com.floreantpos.bo.actions.PizzaCrustExplorerAction;
import com.floreantpos.bo.actions.PizzaExplorerAction;
import com.floreantpos.bo.actions.PizzaItemExplorerAction;
import com.floreantpos.bo.actions.PizzaModifierExplorerAction;
import com.floreantpos.bo.actions.SalesAnalysisReportAction;
import com.floreantpos.bo.actions.SalesBalanceReportAction;
import com.floreantpos.bo.actions.SalesDetailReportAction;
import com.floreantpos.bo.actions.SalesExceptionReportAction;
import com.floreantpos.bo.actions.SalesReportAction;
import com.floreantpos.bo.actions.ServerProductivityReportAction;
import com.floreantpos.bo.actions.ShiftExplorerAction;
import com.floreantpos.bo.actions.TaxExplorerAction;
import com.floreantpos.bo.actions.TicketExplorerAction;
import com.floreantpos.bo.actions.UserExplorerAction;
import com.floreantpos.bo.actions.UserTypeExplorerAction;
import com.floreantpos.bo.actions.ViewGratuitiesAction;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.customPayment.CustomPaymentBrowserAction;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.table.ShowTableBrowserAction;
import com.jidesoft.swing.JideTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class BackOfficeWindow
extends JFrame {
    private static final String POSY = "bwy";
    private static final String POSX = "bwx";
    private static final String WINDOW_HEIGHT = "bwheight";
    private static final String WINDOW_WIDTH = "bwwidth";
    private JMenu floorPlanMenu;
    private static BackOfficeWindow instance;
    private JMenuBar menuBar;
    private JPanel jPanel1;
    private JideTabbedPane tabbedPane;

    public BackOfficeWindow() {
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.initComponents();
        this.createMenus();
        this.positionWindow();
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                BackOfficeWindow.this.close();
            }
        });
        this.setTitle(Application.getTitle() + "- " + POSConstants.BACK_OFFICE);
        this.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    private void positionWindow() {
        int width = AppConfig.getInt(WINDOW_WIDTH, 900);
        int height = AppConfig.getInt(WINDOW_HEIGHT, 650);
        this.setSize(width, height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - width >> 1;
        int y = screenSize.height - height >> 1;
        x = AppConfig.getInt(POSX, x);
        y = AppConfig.getInt(POSY, y);
        this.setLocation(x, y);
    }

    private void createMenus() {
        User user = Application.getCurrentUser();
        UserType newUserType = user.getType();
        Set<UserPermission> permissions = null;
        if (newUserType != null) {
            permissions = newUserType.getPermissions();
        }
        this.menuBar = new JMenuBar();
        if (newUserType == null) {
            this.createAdminMenu(this.menuBar);
            this.createExplorerMenu(this.menuBar);
            this.createReportMenu(this.menuBar);
            this.createFloorMenu(this.menuBar);
        } else {
            if (permissions != null && permissions.contains(UserPermission.PERFORM_ADMINISTRATIVE_TASK)) {
                this.createAdminMenu(this.menuBar);
            }
            if (permissions != null && permissions.contains(UserPermission.VIEW_EXPLORERS)) {
                this.createExplorerMenu(this.menuBar);
            }
            if (permissions != null && permissions.contains(UserPermission.VIEW_REPORTS)) {
                this.createReportMenu(this.menuBar);
            }
        }
        this.createFloorMenu(this.menuBar);
        for (FloreantPlugin plugin : ExtensionManager.getPlugins()) {
            plugin.initBackoffice();
        }
        JMenu helpMenu = new JMenu(Messages.getString("BackOfficeWindow.0"));
        helpMenu.add(new UpdateAction());
        helpMenu.add(new AboutAction());
        this.menuBar.add(helpMenu);
        this.setJMenuBar(this.menuBar);
    }

    private void createReportMenu(JMenuBar menuBar) {
        JMenu reportMenu = new JMenu(POSConstants.REPORTS);
        reportMenu.add(new SalesReportAction());
        reportMenu.add(new OpenTicketSummaryReportAction());
        reportMenu.add(new HourlyLaborReportAction());
        reportMenu.add(new PayrollReportAction());
        reportMenu.add(new EmployeeAttendanceAction());
        reportMenu.add(new KeyStatisticsSalesReportAction());
        reportMenu.add(new SalesAnalysisReportAction());
        reportMenu.add(new CreditCardReportAction());
        reportMenu.add(new CustomPaymentReportAction());
        reportMenu.add(new MenuUsageReportAction());
        reportMenu.add(new ServerProductivityReportAction());
        reportMenu.add(new JournalReportAction());
        reportMenu.add(new SalesBalanceReportAction());
        reportMenu.add(new SalesExceptionReportAction());
        reportMenu.add(new SalesDetailReportAction());
        menuBar.add(reportMenu);
    }

    private void createExplorerMenu(JMenuBar menuBar) {
        JMenu explorerMenu = new JMenu(POSConstants.EXPLORERS);
        menuBar.add(explorerMenu);
        JMenu subMenuPizza = new JMenu(Messages.getString("BackOfficeWindow.1"));
        if (TerminalConfig.isMultipleOrderSupported()) {
            explorerMenu.add(new OrdersTypeExplorerAction());
        }
        explorerMenu.add(new CategoryExplorerAction());
        explorerMenu.add(new GroupExplorerAction());
        explorerMenu.add(new ItemExplorerAction());
        explorerMenu.add(new ModifierGroupExplorerAction());
        explorerMenu.add(new ModifierExplorerAction());
        explorerMenu.add(new ShiftExplorerAction());
        explorerMenu.add(new CouponExplorerAction());
        explorerMenu.add(new CookingInstructionExplorerAction());
        explorerMenu.add(new TaxExplorerAction());
        explorerMenu.add(new CustomPaymentBrowserAction());
        explorerMenu.add(new DrawerPullReportExplorerAction());
        explorerMenu.add(new TicketExplorerAction());
        explorerMenu.add(new AttendanceHistoryAction());
        explorerMenu.add(new PizzaExplorerAction());
        subMenuPizza.add(new MenuItemSizeExplorerAction());
        subMenuPizza.add(new PizzaCrustExplorerAction());
        subMenuPizza.add(new PizzaItemExplorerAction());
        subMenuPizza.add(new PizzaModifierExplorerAction());
        explorerMenu.add(new MultiplierExplorerAction());
        OrderServiceExtension plugin = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        if (plugin == null) {
            return;
        }
        plugin.createCustomerMenu(explorerMenu);
    }

    private void createAdminMenu(JMenuBar menuBar) {
        JMenu adminMenu = new JMenu(POSConstants.ADMIN);
        adminMenu.add(new ConfigureRestaurantAction());
        adminMenu.add(new CurrencyExplorerAction());
        adminMenu.add(new UserExplorerAction());
        adminMenu.add(new UserTypeExplorerAction());
        adminMenu.add(new ViewGratuitiesAction());
        adminMenu.add(new DataExportAction());
        adminMenu.add(new DataImportAction());
        adminMenu.add(new LanguageSelectionAction());
        menuBar.add(adminMenu);
    }

    private void createFloorMenu(JMenuBar menuBar) {
        this.floorPlanMenu = new JMenu(Messages.getString("BackOfficeWindow.2"));
        this.floorPlanMenu.add(new ShowTableBrowserAction());
        menuBar.add(this.floorPlanMenu);
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.tabbedPane = new JideTabbedPane();
        this.tabbedPane.setTabShape(1);
        this.tabbedPane.setShowCloseButtonOnTab(true);
        this.tabbedPane.setTabInsets(new Insets(5, 5, 5, 5));
        Font font = new Font(this.tabbedPane.getFont().getName(), 0, PosUIManager.getDefaultFontSize());
        this.tabbedPane.setFont(font);
        this.getContentPane().setLayout(new BorderLayout(5, 0));
        this.setDefaultCloseOperation(3);
        this.jPanel1.setLayout(new BorderLayout(5, 0));
        this.jPanel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.jPanel1.add((Component)this.tabbedPane, "Center");
        this.getContentPane().add((Component)this.jPanel1, "Center");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                new BackOfficeWindow().setVisible(true);
            }
        });
    }

    public JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }

    private void saveSizeAndLocation() {
        AppConfig.putInt(WINDOW_WIDTH, this.getWidth());
        AppConfig.putInt(WINDOW_HEIGHT, this.getHeight());
        AppConfig.putInt(POSX, this.getX());
        AppConfig.putInt(POSY, this.getY());
    }

    public void close() {
        this.saveSizeAndLocation();
        this.dispose();
    }

    public static BackOfficeWindow getInstance() {
        if (instance == null) {
            instance = new BackOfficeWindow();
        }
        return instance;
    }

    public JMenuBar getBackOfficeMenuBar() {
        return this.menuBar;
    }

    public JMenu getFloorPlanMenu() {
        return this.floorPlanMenu;
    }

    public void setFloorPlanMenu(JMenu floorPlanMenu) {
        this.floorPlanMenu = floorPlanMenu;
    }
}


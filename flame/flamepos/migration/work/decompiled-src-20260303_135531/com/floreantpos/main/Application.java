/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  com.jgoodies.looks.plastic.PlasticTheme
 *  com.jgoodies.looks.plastic.PlasticXPLookAndFeel
 *  com.jgoodies.looks.plastic.theme.ExperienceBlue
 *  com.orocube.common.util.TerminalUtil
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang.math.RandomUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.main;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.config.AppProperties;
import com.floreantpos.config.CardConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.DatabaseConfigurationDialog;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.InginicoPlugin;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.main.Main;
import com.floreantpos.main.PosWindow;
import com.floreantpos.model.DeliveryConfiguration;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.PrinterConfiguration;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.DeliveryConfigurationDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.model.dao.PrinterConfigurationDAO;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.posserver.PosServer;
import com.floreantpos.services.PosWebService;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.ui.dialog.UpdateDialog;
import com.floreantpos.ui.views.LoginView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.DatabaseConnectionException;
import com.floreantpos.util.DatabaseUtil;
import com.floreantpos.util.GlobalConfigUtil;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
import com.floreantpos.util.UserNotFoundException;
import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.orocube.common.util.TerminalUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Application {
    private static Log logger = LogFactory.getLog(Application.class);
    private boolean developmentMode = false;
    private Terminal terminal;
    private PosWindow posWindow;
    private User currentUser;
    private RootView rootView;
    private List<OrderType> orderTypes;
    private Shift currentShift;
    public PrinterConfiguration printConfiguration;
    private Restaurant restaurant;
    private PosPrinters printers;
    private static String lengthUnit;
    private static Application instance;
    private static SimpleDateFormat dateFormat;
    private static ImageIcon applicationIcon;
    private boolean systemInitialized;
    private boolean headLess = false;
    public static final String VERSION;

    private Application() {
    }

    public void start() {
        this.setApplicationLook();
        applicationIcon = new ImageIcon(this.getClass().getResource("/icons/icon.png"));
        this.posWindow = new PosWindow();
        this.posWindow.setTitle(Application.getTitle());
        this.posWindow.setIconImage(applicationIcon.getImage());
        this.posWindow.setupSizeAndLocation();
        this.posWindow.setVisibleWelcomeHeader(true);
        if (TerminalConfig.isFullscreenMode()) {
            this.posWindow.enterFullScreenMode();
        }
        this.posWindow.setVisible(true);
        this.rootView = RootView.getInstance();
        this.posWindow.getContentPane().add(this.rootView);
        this.posWindow.setVisibleWelcomeHeader(false);
        this.rootView.addView(LoginView.getInstance());
        this.initializeSystem();
    }

    private void setApplicationLook() {
        try {
            PlasticXPLookAndFeel.setPlasticTheme((PlasticTheme)new ExperienceBlue());
            UIManager.setLookAndFeel((LookAndFeel)new PlasticXPLookAndFeel());
            FlameTheme.applyLookAndFeelDefaults();
            this.initializeFont();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initializeSystem() {
        if (this.isSystemInitialized()) {
            return;
        }
        try {
            this.posWindow.setGlassPaneVisible(true);
            DatabaseUtil.checkConnection(DatabaseUtil.initialize());
            DatabaseUtil.updateLegacyDatabase();
            this.initTerminal();
            this.initOrderTypes();
            this.initPrintConfig();
            this.refreshRestaurant();
            this.loadCurrency();
            this.loadGlobalConfig();
            this.loadPrinters();
            this.initLengthUnit();
            this.initPlugins();
            LoginView.getInstance().initializeOrderButtonPanel();
            this.setSystemInitialized(true);
        }
        catch (DatabaseConnectionException e) {
            e.printStackTrace();
            PosLog.error(this.getClass(), e);
            int option = JOptionPane.showConfirmDialog(Application.getPosWindow(), Messages.getString("Application.0"), Messages.getString("PosMessage.Error"), 0);
            if (option == 0) {
                DatabaseConfigurationDialog.show(Application.getPosWindow());
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
            logger.error((Object)e);
        }
        finally {
            Application.getPosWindow().setGlassPaneVisible(false);
        }
    }

    private void checkAvailableUpdates() {
        PosWebService service = new PosWebService();
        try {
            String[] availableNewVersions;
            String versionInfo = service.getAvailableNewVersions(TerminalUtil.getSystemUID(), VERSION);
            if (versionInfo == null || versionInfo.equals("UP_TO_DATE")) {
                return;
            }
            if (versionInfo.startsWith("[") && (availableNewVersions = (versionInfo = versionInfo.replace("[", "").replace(",]", "")).split(",")).length > 0) {
                UpdateDialog dialog = new UpdateDialog(availableNewVersions, false, false);
                dialog.pack();
                dialog.open();
            }
        }
        catch (Exception ex) {
            PosLog.error(this.getClass(), ex);
        }
    }

    private boolean hasUpdateScheduleToday() {
        String status = TerminalConfig.getCheckUpdateStatus();
        if (status.equals("Never")) {
            return false;
        }
        if (status.equals("Weekly")) {
            return DateUtil.isStartOfWeek(new Date());
        }
        if (status.equals("Monthly")) {
            return DateUtil.isStartOfMonth(new Date());
        }
        return true;
    }

    public void initializeSystemHeadless() {
        if (this.isSystemInitialized()) {
            return;
        }
        this.headLess = true;
        DatabaseUtil.initialize();
        this.initTerminal();
        this.initOrderTypes();
        this.initPrintConfig();
        this.refreshRestaurant();
        this.loadCurrency();
        this.loadPrinters();
        this.initLengthUnit();
        this.setSystemInitialized(true);
    }

    private void initOrderTypes() {
        OrderTypeDAO dao = OrderTypeDAO.getInstance();
        this.orderTypes = dao.findEnabledOrderTypes();
        try {
            if (!dao.containsOrderTypeObj()) {
                dao.updateMenuItemOrderType();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void initPlugins() {
        List<FloreantPlugin> plugins = ExtensionManager.getPlugins();
        for (FloreantPlugin floreantPlugin : plugins) {
            floreantPlugin.initUI();
        }
    }

    private void loadPrinters() {
        this.printers = PosPrinters.load();
        if (this.printers == null) {
            this.printers = new PosPrinters();
        }
    }

    private void initPrintConfig() {
        this.printConfiguration = PrinterConfigurationDAO.getInstance().get(PrinterConfiguration.ID);
        if (this.printConfiguration == null) {
            this.printConfiguration = new PrinterConfiguration();
        }
    }

    private void initTerminal() {
        String terminalKey = TerminalUtil.getSystemUID();
        Terminal terminal = TerminalDAO.getInstance().getByTerminalKey(terminalKey);
        if (terminal != null) {
            TerminalConfig.setTerminalId(terminal.getId());
            this.terminal = terminal;
            if (!this.headLess) {
                LoginView.getInstance().setTerminalId(terminal.getId());
            }
            return;
        }
        int terminalId = TerminalConfig.getTerminalId();
        if (terminalId == -1) {
            Random random = new Random();
            terminalId = random.nextInt(10000) + 1;
        }
        try {
            terminal = TerminalDAO.getInstance().get(new Integer(terminalId));
            if (terminal == null) {
                terminal = new Terminal();
                terminal.setId(terminalId);
                terminal.setTerminalKey(terminalKey);
                terminal.setName(String.valueOf("Terminal " + terminalId));
                TerminalDAO.getInstance().saveOrUpdate(terminal);
            } else if (StringUtils.isEmpty((String)terminal.getTerminalKey())) {
                terminal.setTerminalKey(terminalKey);
                TerminalDAO.getInstance().saveOrUpdate(terminal);
            }
        }
        catch (Exception e) {
            throw new DatabaseConnectionException();
        }
        TerminalConfig.setTerminalId(terminalId);
        if (!this.headLess) {
            LoginView.getInstance().setTerminalId(terminalId);
        }
        this.terminal = terminal;
    }

    public void refreshRestaurant() {
        try {
            PaymentGatewayPlugin paymentGateway;
            this.restaurant = RestaurantDAO.getRestaurant();
            if (this.restaurant.getUniqueId() == null || this.restaurant.getUniqueId() == 0) {
                this.restaurant.setUniqueId(RandomUtils.nextInt());
                RestaurantDAO.getInstance().saveOrUpdate(this.restaurant);
            }
            if (!this.headLess) {
                if (this.restaurant.isItemPriceIncludesTax().booleanValue()) {
                    this.posWindow.setStatus(Messages.getString("Application.41"));
                } else {
                    this.posWindow.setStatus(Messages.getString("Application.42"));
                }
            }
            if ((paymentGateway = CardConfig.getPaymentGateway()) instanceof InginicoPlugin) {
                new PosServer();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseConnectionException();
        }
    }

    private void loadCurrency() {
        CurrencyUtil.populateCurrency();
    }

    private void loadGlobalConfig() {
        GlobalConfigUtil.populateGlobalConfig();
    }

    public List<OrderType> getOrderTypes() {
        return this.orderTypes;
    }

    public static synchronized Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    public void shutdownPOS() {
        Component[] optionValues;
        JOptionPane optionPane = new JOptionPane(Messages.getString("Application.1"), 3, 1, Application.getApplicationIcon(), new String[]{Messages.getString("Application.5"), Messages.getString("Application.6")});
        for (Component object : optionValues = optionPane.getComponents()) {
            Component[] components;
            if (!(object instanceof JPanel)) continue;
            JPanel panel = (JPanel)object;
            for (Component component : components = panel.getComponents()) {
                if (!(component instanceof JButton)) continue;
                component.setPreferredSize(new Dimension(100, 80));
                JButton button = (JButton)component;
                button.setPreferredSize(PosUIManager.getSize(100, 50));
            }
        }
        JDialog dialog = optionPane.createDialog(Application.getPosWindow(), Messages.getString("Application.2"));
        dialog.setIconImage(Application.getApplicationIcon().getImage());
        int y = dialog.getLocation().y;
        dialog.setLocation(dialog.getLocation().x, y + 60);
        dialog.setVisible(true);
        String selectedValue = (String)optionPane.getValue();
        if (selectedValue.equals(Messages.getString("Application.3"))) {
            try {
                Main.restart();
            }
            catch (Exception exception) {}
        } else if (selectedValue.equals(Messages.getString("Application.5"))) {
            this.posWindow.saveSizeAndLocation();
            System.exit(0);
        }
    }

    public synchronized void doLogin(User user) {
        this.initializeSystem();
        if (user == null) {
            return;
        }
        this.initCurrentUser(user);
        RootView rootView = this.getRootView();
        if (!rootView.hasView("ORDER_VIEW")) {
            rootView.addView(OrderView.getInstance());
        }
        rootView.showDefaultView();
    }

    public void initCurrentUser(User user) {
        int option;
        Shift currentShift = ShiftUtil.getCurrentShift();
        this.setCurrentShift(currentShift);
        if (!user.isClockedIn().booleanValue() && (option = POSMessageDialog.showYesNoQuestionDialog(this.posWindow, Messages.getString("Application.43"), Messages.getString("Application.44"))) == 0) {
            Calendar currentTime = Calendar.getInstance();
            user.doClockIn(this.getTerminal(), currentShift, currentTime);
        }
        this.setCurrentUser(user);
    }

    public void doLogout() {
        BackOfficeWindow window = POSUtil.getBackOfficeWindow();
        if (window != null && window.isVisible()) {
            window.dispose();
        }
        this.currentShift = null;
        this.setCurrentUser(null);
        RootView.getInstance().showView(LoginView.getInstance());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doAutoLogout() {
        try {
            this.posWindow.setGlassPaneVisible(true);
            PasswordEntryDialog dialog2 = new PasswordEntryDialog();
            dialog2.setTitle(Messages.getString("Application.19"));
            dialog2.setDialogTitle(Messages.getString("Application.20"));
            dialog2.pack();
            dialog2.setLocationRelativeTo(Application.getPosWindow());
            dialog2.setAutoLogOffMode(true);
            dialog2.setVisible(true);
            if (dialog2.isCanceled()) {
                this.doLogout();
                return;
            }
            User user = dialog2.getUser();
            this.doAutoLogin(user);
        }
        catch (UserNotFoundException e) {
            LogFactory.getLog(Application.class).error((Object)e);
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("LoginPasswordEntryView.15"));
        }
        finally {
            this.posWindow.setGlassPaneVisible(false);
        }
    }

    public void doAutoLogin(User user) {
        this.setCurrentUser(user);
    }

    public static User getCurrentUser() {
        return Application.getInstance().currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public RootView getRootView() {
        return this.rootView;
    }

    public void setRootView(RootView rootView) {
        this.rootView = rootView;
    }

    public static PosWindow getPosWindow() {
        return Application.getInstance().posWindow;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public synchronized Terminal refreshAndGetTerminal() {
        TerminalDAO.getInstance().refresh(this.terminal);
        return this.terminal;
    }

    public static PosPrinters getPrinters() {
        return Application.getInstance().printers;
    }

    public OrderType getCurrentOrderType() {
        return this.orderTypes.get(0);
    }

    public static String getTitle() {
        return "Flame POS - Version " + VERSION;
    }

    public static ImageIcon getApplicationIcon() {
        return applicationIcon;
    }

    public static void setApplicationIcon(ImageIcon applicationIcon) {
        Application.applicationIcon = applicationIcon;
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public Shift getCurrentShift() {
        return this.currentShift;
    }

    public void setCurrentShift(Shift currentShift) {
        this.currentShift = currentShift;
    }

    public boolean isSystemInitialized() {
        return this.systemInitialized;
    }

    public void setSystemInitialized(boolean systemInitialized) {
        this.systemInitialized = systemInitialized;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public static File getWorkingDir() {
        File file = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.getParentFile();
    }

    public boolean isDevelopmentMode() {
        return this.developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public boolean isPriceIncludesTax() {
        Restaurant restaurant = this.getRestaurant();
        if (restaurant == null) {
            return false;
        }
        return POSUtil.getBoolean(restaurant.isItemPriceIncludesTax());
    }

    public String getLocation() {
        File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        return file.getParent();
    }

    private void initializeFont() {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value == null || !(value instanceof FontUIResource)) continue;
            FontUIResource f = (FontUIResource)value;
            String fontName = f.getFontName();
            Font font = new Font(fontName, f.getStyle(), PosUIManager.getDefaultFontSize());
            UIManager.put(key, new FontUIResource(font));
        }
    }

    private void initLengthUnit() {
        DeliveryConfiguration deliveryConfig = DeliveryConfigurationDAO.getInstance().get(1);
        if (deliveryConfig == null) {
            deliveryConfig = new DeliveryConfiguration();
            deliveryConfig.setUnitName("MILE");
            DeliveryConfigurationDAO.getInstance().saveOrUpdate(deliveryConfig);
        }
        lengthUnit = deliveryConfig.getUnitName();
    }

    public static String getLengthUnit() {
        return lengthUnit;
    }

    public void refreshOrderTypes() {
        OrderTypeDAO dao = OrderTypeDAO.getInstance();
        this.orderTypes = dao.findEnabledOrderTypes();
    }

    static {
        dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        VERSION = AppProperties.getVersion();
    }
}

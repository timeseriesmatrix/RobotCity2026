/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.views.LoginView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.apache.commons.lang.StringUtils;

public class PasswordEntryDialog
extends OkCancelOptionDialog
implements ActionListener {
    private JPasswordField tfPassword;
    private JLabel statusLabel;
    private PosButton btnClear;
    private PosButton btnClearAll;
    private boolean autoLogOffMode;
    private User user;
    Action loginAction = new AbstractAction(){

        @Override
        public void actionPerformed(ActionEvent e) {
            PasswordEntryDialog.this.tfPassword.setText(PasswordEntryDialog.this.getPasswordAsString() + e.getActionCommand());
            String secretKey = PasswordEntryDialog.this.getPasswordAsString();
            if (secretKey != null && secretKey.length() == TerminalConfig.getDefaultPassLen()) {
                PasswordEntryDialog.this.statusLabel.setText("");
                if (PasswordEntryDialog.this.checkLogin(secretKey)) {
                    PasswordEntryDialog.this.setCanceled(false);
                    PasswordEntryDialog.this.dispose();
                }
            }
        }
    };

    public PasswordEntryDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.init();
    }

    public PasswordEntryDialog(Frame parent) {
        super(parent, true);
        this.init();
    }

    private void init() {
        this.btnClear = new PosButton();
        this.btnClear.setText(Messages.getString("PasswordEntryDialog.11"));
        this.btnClearAll = new PosButton();
        this.btnClearAll.setText(Messages.getString("PasswordEntryDialog.12"));
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        this.getContentPanel().add(contentPane);
        JPanel inputPanel = this.createInputPanel();
        contentPane.add((Component)inputPanel, "North");
        JPanel keyboardPanel = this.createKeyboardPanel();
        contentPane.add(keyboardPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        this.tfPassword = new JPasswordField();
        this.tfPassword.setFont(this.tfPassword.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        this.tfPassword.setFocusable(true);
        this.tfPassword.requestFocus();
        this.tfPassword.setBackground(Color.WHITE);
        this.tfPassword.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String secretKey = PasswordEntryDialog.this.getPasswordAsString();
                if (secretKey != null && secretKey.length() == TerminalConfig.getDefaultPassLen()) {
                    PasswordEntryDialog.this.statusLabel.setText("");
                    if (PasswordEntryDialog.this.checkLogin(secretKey)) {
                        PasswordEntryDialog.this.setCanceled(false);
                        PasswordEntryDialog.this.dispose();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        inputPanel.add((Component)this.tfPassword, "North");
        this.statusLabel = new JLabel();
        this.statusLabel.setHorizontalAlignment(0);
        inputPanel.add(this.statusLabel);
        return inputPanel;
    }

    private JPanel createKeyboardPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        String[][] numbers = new String[][]{{"7", "8", "9"}, {"4", "5", "6"}, {"1", "2", "3"}, {"0"}};
        String[][] iconNames = new String[][]{{"7.png", "8.png", "9.png"}, {"4.png", "5.png", "6.png"}, {"1.png", "2.png", "3.png"}, {"0.png"}};
        Dimension size = PosUIManager.getSize_w120_h70();
        for (int i = 0; i < numbers.length; ++i) {
            for (int j = 0; j < numbers[i].length; ++j) {
                String buttonText = String.valueOf(numbers[i][j]);
                PosButton posButton = new PosButton();
                posButton.setAction(this.loginAction);
                ImageIcon icon = IconFactory.getIcon("/ui_icons/", iconNames[i][j]);
                if (icon != null) {
                    posButton.setIcon(icon);
                } else {
                    posButton.setText(buttonText);
                }
                posButton.setPreferredSize(size);
                posButton.setIconTextGap(0);
                posButton.setActionCommand(buttonText);
                buttonPanel.add(posButton);
            }
        }
        ImageIcon clearIcon = IconFactory.getIcon("/ui_icons/", "clear.png");
        this.btnClear.setIcon(clearIcon);
        this.btnClear.setIconTextGap(0);
        ImageIcon clearAllIcon = IconFactory.getIcon("/ui_icons/", "clear.png");
        this.btnClearAll.setIcon(clearAllIcon);
        this.btnClearAll.setIconTextGap(0);
        buttonPanel.add(this.btnClear);
        buttonPanel.add(this.btnClearAll);
        this.btnClear.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PasswordEntryDialog.this.doClear();
            }
        });
        this.btnClearAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PasswordEntryDialog.this.doClearAll();
            }
        });
        return buttonPanel;
    }

    @Override
    public void doOk() {
        if (this.checkLogin(this.getPasswordAsString())) {
            this.setCanceled(false);
            this.dispose();
        }
    }

    @Override
    public void doCancel() {
        this.user = null;
        this.setCanceled(true);
        this.dispose();
    }

    private void doClearAll() {
        this.statusLabel.setText("");
        this.tfPassword.setText("");
    }

    private void doClear() {
        this.statusLabel.setText("");
        String passwordAsString = this.getPasswordAsString();
        if (StringUtils.isNotEmpty((String)passwordAsString)) {
            passwordAsString = passwordAsString.substring(0, passwordAsString.length() - 1);
        }
        this.tfPassword.setText(passwordAsString);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
            this.doCancel();
        } else if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
            this.doOk();
        } else if (StringUtils.isNotEmpty((String)actionCommand)) {
            this.tfPassword.setText(this.getPasswordAsString() + actionCommand);
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitlePaneText(title);
        super.setTitle(title);
    }

    public void setDialogTitle(String title) {
        super.setTitle(title);
    }

    private String getPasswordAsString() {
        return new String(this.tfPassword.getPassword());
    }

    public static void main(String[] args) {
        PasswordEntryDialog dialog2 = new PasswordEntryDialog();
        dialog2.pack();
        dialog2.setVisible(true);
    }

    public static String show(Component parent, String title) {
        PasswordEntryDialog dialog2 = new PasswordEntryDialog();
        dialog2.setTitle(title);
        dialog2.pack();
        dialog2.setLocationRelativeTo(parent);
        dialog2.setVisible(true);
        if (dialog2.isCanceled()) {
            return null;
        }
        return dialog2.getPasswordAsString();
    }

    public static User getUser(Component parent, String title) {
        return PasswordEntryDialog.getUser(parent, title, title);
    }

    public static User getUser(Component parent, String windowTitle, String title) {
        PasswordEntryDialog dialog2 = new PasswordEntryDialog();
        dialog2.setTitle(title);
        dialog2.setDialogTitle(windowTitle);
        dialog2.pack();
        dialog2.setLocationRelativeTo(parent);
        dialog2.setVisible(true);
        if (dialog2.isCanceled()) {
            return null;
        }
        return dialog2.getUser();
    }

    private synchronized boolean checkLogin(String secretKey) {
        this.user = UserDAO.getInstance().findUserBySecretKey(secretKey);
        if (this.user == null) {
            this.statusLabel.setText(Messages.getString("PasswordEntryDialog.30"));
            return false;
        }
        if (LoginView.getInstance().isBackOfficeLogin()) {
            if (!this.user.hasPermission(UserPermission.VIEW_BACK_OFFICE)) {
                this.statusLabel.setText("user has no permission to access this view");
                return false;
            }
            return true;
        }
        if (this.isAutoLogOffMode()) {
            String viewName = RootView.getInstance().getCurrentViewName();
            if (viewName.equals("TABLE_MAP")) {
                if (!this.user.hasPermission(UserPermission.CREATE_TICKET)) {
                    this.statusLabel.setText("user has no permission to access this view");
                    return false;
                }
            } else if (viewName.equals("ALL FUNCTIONS")) {
                if (!this.user.hasPermission(UserPermission.ALL_FUNCTIONS)) {
                    this.statusLabel.setText("user has no permission to access this view");
                    return false;
                }
            } else if (viewName.equals("ORDER_VIEW")) {
                if (!(OrderView.getInstance().getCurrentTicket().getOwner().getUserId().equals(this.user.getUserId()) || this.user.hasPermission(UserPermission.CREATE_TICKET) && (this.user.hasPermission(UserPermission.PERFORM_ADMINISTRATIVE_TASK) || this.user.hasPermission(UserPermission.PERFORM_MANAGER_TASK)))) {
                    this.statusLabel.setText("user has no permission to access this view");
                    return false;
                }
            } else if (viewName.equals("KD") && !this.user.hasPermission(UserPermission.KITCHEN_DISPLAY)) {
                this.statusLabel.setText("user has no permission to access this view");
                return false;
            }
        } else {
            List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
            if (orderTypes != null) {
                for (OrderType orderType : orderTypes) {
                    if (!TerminalConfig.getDefaultView().equals(orderType.getName()) || this.user.hasPermission(UserPermission.CREATE_TICKET)) continue;
                    this.statusLabel.setText("user has no permission to access this view");
                    return false;
                }
            }
            if (TerminalConfig.getDefaultView().equals("KD") && !this.user.hasPermission(UserPermission.KITCHEN_DISPLAY)) {
                this.statusLabel.setText("user has no permission to access this view");
                return false;
            }
        }
        this.setAutoLogOffMode(false);
        return true;
    }

    public User getUser() {
        return this.user;
    }

    public boolean isAutoLogOffMode() {
        return this.autoLogOffMode;
    }

    public void setAutoLogOffMode(boolean autoLogOffMode) {
        this.autoLogOffMode = autoLogOffMode;
    }
}


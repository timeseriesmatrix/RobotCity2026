/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.actions.DrawerAssignmentAction;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.main.Application;
import com.floreantpos.model.Terminal;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Window;
import java.net.URLEncoder;

public class POSUtil {
    public static Window getFocusedWindow() {
        Window[] windows;
        for (Window window : windows = Window.getWindows()) {
            if (!window.hasFocus()) continue;
            return window;
        }
        return null;
    }

    public static BackOfficeWindow getBackOfficeWindow() {
        Window[] windows;
        for (Window window : windows = Window.getWindows()) {
            if (!(window instanceof BackOfficeWindow)) continue;
            return (BackOfficeWindow)window;
        }
        return null;
    }

    public static boolean isBlankOrNull(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }

    public static String escapePropertyKey(String propertyKey) {
        return propertyKey.replaceAll("\\s+", "_");
    }

    public static boolean getBoolean(String b) {
        if (b == null) {
            return false;
        }
        return Boolean.valueOf(b);
    }

    public static boolean getBoolean(Boolean b) {
        if (b == null) {
            return false;
        }
        return b;
    }

    public static boolean getBoolean(Boolean b, boolean defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static double getDouble(Double d) {
        if (d == null) {
            return 0.0;
        }
        return d;
    }

    public static int getInteger(Integer d) {
        if (d == null) {
            return 0;
        }
        return d;
    }

    public static int parseInteger(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception x) {
            return 0;
        }
    }

    public static int parseInteger(String s, String parseErrorMessage) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception x) {
            throw new PosException(parseErrorMessage);
        }
    }

    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        }
        catch (Exception x) {
            return 0.0;
        }
    }

    public static double parseDouble(String s, String parseErrorMessage, boolean mandatory) {
        try {
            return Double.parseDouble(s);
        }
        catch (Exception x) {
            if (mandatory) {
                throw new PosException(parseErrorMessage);
            }
            return 0.0;
        }
    }

    public static String encodeURLString(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (Exception x) {
            return s;
        }
    }

    public static boolean isValidPassword(char[] password) {
        for (char c : password) {
            if (Character.isDigit(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean checkDrawerAssignment() {
        Terminal terminal = Application.getInstance().getTerminal();
        if (!terminal.isCashDrawerAssigned()) {
            int option = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(), Messages.getString("SwitchboardView.15") + Messages.getString("SwitchboardView.16"), Messages.getString("SwitchboardView.17"));
            if (option == 0) {
                try {
                    DrawerAssignmentAction action = new DrawerAssignmentAction();
                    action.execute();
                    if (!terminal.isCashDrawerAssigned()) {
                        POSUtil.showUnableToAcceptPayment();
                        return false;
                    }
                    return true;
                }
                catch (Exception e) {
                    return false;
                }
            }
            POSUtil.showUnableToAcceptPayment();
            return false;
        }
        return true;
    }

    private static void showUnableToAcceptPayment() {
        POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SwitchboardView.18"));
    }
}


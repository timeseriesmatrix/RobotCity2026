/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

public class MessageDialog {
    private static Logger logger = Logger.getLogger(Application.class);

    public static void showError(String errorMessage) {
        JOptionPane.showMessageDialog(Application.getPosWindow(), errorMessage, Messages.getString("MessageDialog.0"), 0);
    }

    public static void showError(String errorMessage, Throwable t) {
        logger.error((Object)errorMessage, t);
        JOptionPane.showMessageDialog(Application.getPosWindow(), errorMessage, Messages.getString("MessageDialog.0"), 0);
    }

    public static void showError(Throwable t) {
        logger.error((Object)t);
        JOptionPane.showMessageDialog(Application.getPosWindow(), Messages.getString("GenericErrorMessage"), Messages.getString("MessageDialog.0"), 0);
    }
}


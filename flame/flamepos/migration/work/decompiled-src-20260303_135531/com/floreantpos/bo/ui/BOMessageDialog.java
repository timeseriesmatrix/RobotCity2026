/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.floreantpos.bo.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

public class BOMessageDialog {
    private static Logger logger = Logger.getLogger(Application.class);

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, POSConstants.MDS_POS, 0, null);
    }

    public static void showError(Component parent, String message, Throwable x) {
        PosLog.error(parent.getClass(), x.getMessage());
        JOptionPane.showMessageDialog(parent, message, POSConstants.MDS_POS, 0, null);
    }

    public static void showError(String errorMessage) {
        JOptionPane.showMessageDialog(POSUtil.getFocusedWindow(), errorMessage, Messages.getString("BOMessageDialog.0"), 0);
    }

    public static void showError(String errorMessage, Throwable t) {
        logger.error((Object)errorMessage, t);
        JOptionPane.showMessageDialog(POSUtil.getFocusedWindow(), errorMessage, Messages.getString("BOMessageDialog.1"), 0);
    }

    public static void showError(Throwable t) {
        logger.error((Object)Messages.getString("BOMessageDialog.2"), t);
        JOptionPane.showMessageDialog(POSUtil.getFocusedWindow(), Messages.getString("BOMessageDialog.3"), Messages.getString("BOMessageDialog.4"), 0);
    }
}


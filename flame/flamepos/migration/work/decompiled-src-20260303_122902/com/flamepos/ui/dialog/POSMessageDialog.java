/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

public class POSMessageDialog {
    private static Logger logger = Logger.getLogger(Application.class);

    private static void showDialog(Component parent, String message, int messageType, int optionType) {
        Component[] options;
        JOptionPane optionPane = new JOptionPane(message, messageType, optionType);
        for (Component object : options = optionPane.getComponents()) {
            Component[] components;
            if (!(object instanceof JPanel)) continue;
            JPanel panel = (JPanel)object;
            for (Component component : components = panel.getComponents()) {
                if (!(component instanceof JButton)) continue;
                component.setPreferredSize(new Dimension(component.getPreferredSize().width, 60));
            }
        }
        JDialog dialog = optionPane.createDialog(parent, POSConstants.MDS_POS);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void showMessage(String message) {
        POSMessageDialog.showDialog(Application.getPosWindow(), message, 1, -1);
    }

    public static void showMessage(Component parent, String message) {
        POSMessageDialog.showDialog(parent, message, 1, -1);
    }

    public static void showError(String message) {
        POSMessageDialog.showDialog(Application.getPosWindow(), message, 0, -1);
    }

    public static void showError(Component parent, String message) {
        POSMessageDialog.showDialog(parent, message, 0, -1);
    }

    public static void showError(Component parent, String message, Throwable x) {
        logger.error((Object)message, x);
        POSMessageDialog.showDialog(parent, message, 0, -1);
    }

    public static int showYesNoQuestionDialog(Component parent, String message, String title) {
        Component[] options;
        JOptionPane optionPane = new JOptionPane(message, 3, 0);
        for (Component object : options = optionPane.getComponents()) {
            Component[] components;
            if (!(object instanceof JPanel)) continue;
            JPanel panel = (JPanel)object;
            for (Component component : components = panel.getComponents()) {
                if (!(component instanceof JButton)) continue;
                component.setPreferredSize(new Dimension(component.getPreferredSize().width, 60));
            }
        }
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setVisible(true);
        Object selectedValue = optionPane.getValue();
        if (selectedValue == null) {
            return -1;
        }
        return (Integer)selectedValue;
    }

    public static int showYesNoQuestionDialog(Component parent, String message, String title, String yesButtonText, String noButtonText) {
        Component[] options;
        Object[] buttonText = new String[]{yesButtonText, noButtonText};
        JOptionPane optionPane = new JOptionPane(message, 3, 0, null, buttonText);
        for (Component object : options = optionPane.getComponents()) {
            Component[] components;
            if (!(object instanceof JPanel)) continue;
            JPanel panel = (JPanel)object;
            for (Component component : components = panel.getComponents()) {
                if (!(component instanceof JButton)) continue;
                component.setPreferredSize(new Dimension(component.getPreferredSize().width, 60));
            }
        }
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setVisible(true);
        String selectedValue = (String)optionPane.getValue();
        if (selectedValue.equals(noButtonText)) {
            return -1;
        }
        return 0;
    }
}


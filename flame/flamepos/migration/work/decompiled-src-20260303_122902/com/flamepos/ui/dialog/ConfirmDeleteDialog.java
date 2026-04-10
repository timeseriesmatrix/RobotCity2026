/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import java.awt.Component;
import javax.swing.JOptionPane;

public class ConfirmDeleteDialog {
    public static final int YES = 0;
    public static final int NO = 1;
    public static final int CANCEL = 2;
    static final int OK = 0;
    static final int CLOSED = -1;

    public static int showMessage(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, 0, 3);
    }
}


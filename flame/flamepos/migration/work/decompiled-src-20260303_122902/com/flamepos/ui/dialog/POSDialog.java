/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class POSDialog
extends JDialog {
    protected boolean canceled = true;

    public POSDialog() throws HeadlessException {
        super((Frame)Application.getPosWindow(), true);
        this.initUI();
    }

    public POSDialog(Frame owner, boolean modal) {
        super(owner, modal);
        this.initUI();
        this.setIconImage(Application.getPosWindow().getIconImage());
    }

    public POSDialog(Window owner, String title) {
        this(owner, title, true);
        this.initUI();
    }

    public POSDialog(Window owner, String title, boolean modal) {
        super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
        this.initUI();
    }

    protected void initUI() {
    }

    public void open() {
        this.canceled = true;
        if (this.isUndecorated()) {
            Window owner = this.getOwner();
            if (owner instanceof JFrame) {
                JFrame frame = (JFrame)owner;
                this.setLocationRelativeTo(frame.getContentPane());
            } else {
                this.setLocationRelativeTo(owner);
            }
        } else {
            this.setLocationRelativeTo(this.getOwner());
        }
        this.setVisible(true);
    }

    public void openFullScreen() {
        this.canceled = true;
        this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        if (TerminalConfig.isFullscreenMode()) {
            this.setUndecorated(true);
        }
        this.setVisible(true);
    }

    public void openUndecoratedFullScreen() {
        this.canceled = true;
        this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        this.setUndecorated(true);
        this.setVisible(true);
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}


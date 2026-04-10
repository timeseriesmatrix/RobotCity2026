/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.config.ui.TerminalConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TitledView;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class TerminalSetupDialog
extends JDialog {
    private final PosButton psbtnSave = new PosButton();
    private TerminalConfigurationView terminalConfigurationView;
    private final Action saveAction = new SwingAction();
    private final Action closeAction = new SwingAction_1();

    public TerminalSetupDialog() {
        super((Frame)Application.getPosWindow(), true);
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "South");
        panel.setLayout((LayoutManager)new MigLayout("", "[64px][71px][][][][][][][][][][][][]", "[][41px]"));
        JSeparator separator = new JSeparator();
        panel.add((Component)separator, "cell 0 0 14 1,growx");
        this.psbtnSave.setAction(this.saveAction);
        this.psbtnSave.setMargin(new Insets(10, 20, 10, 20));
        this.psbtnSave.setText(Messages.getString("TerminalSetupDialog.4"));
        panel.add((Component)this.psbtnSave, "cell 12 1,alignx left,aligny top");
        PosButton psbtnClose = new PosButton();
        psbtnClose.setAction(this.closeAction);
        psbtnClose.setMargin(new Insets(10, 20, 10, 20));
        psbtnClose.setText(Messages.getString("TerminalSetupDialog.6"));
        panel.add((Component)psbtnClose, "cell 13 1,alignx left,aligny top");
        TitledView titledView = new TitledView();
        titledView.setTitle(Messages.getString("TerminalSetupDialog.0"));
        this.getContentPane().add((Component)titledView, "North");
        this.terminalConfigurationView = new TerminalConfigurationView();
        this.getContentPane().add((Component)this.terminalConfigurationView, "Center");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    TerminalSetupDialog dialog = new TerminalSetupDialog();
                    dialog.setDefaultCloseOperation(2);
                    dialog.setVisible(true);
                }
                catch (Exception e) {
                    PosLog.error(this.getClass(), e);
                }
            }
        });
    }

    private class SwingAction_1
    extends AbstractAction {
        public SwingAction_1() {
            this.putValue("Name", "CloseAction");
            this.putValue("ShortDescription", Messages.getString("TerminalSetupDialog.12"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TerminalSetupDialog.this.dispose();
        }
    }

    private class SwingAction
    extends AbstractAction {
        public SwingAction() {
            this.putValue("Name", "SaveAction");
            this.putValue("ShortDescription", Messages.getString("TerminalSetupDialog.10"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (TerminalSetupDialog.this.terminalConfigurationView.canSave()) {
                TerminalSetupDialog.this.terminalConfigurationView.save();
            }
        }
    }
}


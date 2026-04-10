/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.swing;

import com.floreantpos.main.Application;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;

public class PosOptionPane
extends OkCancelOptionDialog {
    private JTextArea taInputText;
    private String inputText;
    private QwertyKeyPad qwertyKeyPad;

    private PosOptionPane() {
        super(POSUtil.getFocusedWindow());
        this.init();
    }

    private PosOptionPane(Window parent) {
        super(parent);
        this.init();
    }

    private void init() {
        this.setResizable(false);
        JPanel contentPane = this.getContentPanel();
        MigLayout layout = new MigLayout("inset 0");
        contentPane.setLayout((LayoutManager)layout);
        Dimension size = PosUIManager.getSize(0, 100);
        this.taInputText = new JTextArea();
        this.taInputText.setFont(this.taInputText.getFont().deriveFont(1, PosUIManager.getFontSize(16)));
        this.taInputText.setFocusable(true);
        this.taInputText.requestFocus();
        this.taInputText.setLineWrap(true);
        this.taInputText.setBackground(Color.WHITE);
        this.taInputText.setPreferredSize(size);
        this.qwertyKeyPad = new QwertyKeyPad();
        JScrollPane scrollPane = new JScrollPane(this.taInputText);
        contentPane.add((Component)scrollPane, "spanx, grow");
        contentPane.add((Component)((Object)this.qwertyKeyPad), "spanx ,grow");
    }

    @Override
    public void doOk() {
        String s = this.taInputText.getText();
        if (s.isEmpty()) {
            POSMessageDialog.showError(Application.getPosWindow(), "Please enter value");
            return;
        }
        this.setValue(this.taInputText.getText());
        this.setCanceled(false);
        this.dispose();
    }

    public String getValue() {
        return this.inputText;
    }

    public void setValue(String value) {
        this.inputText = value;
    }

    public static String showInputDialog(String title) {
        PosOptionPane dialog = new PosOptionPane();
        dialog.setTitlePaneText(title);
        dialog.setTitle(title);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return null;
        }
        return dialog.getValue();
    }

    public static String showInputDialog(Window parent, String title) {
        PosOptionPane dialog = new PosOptionPane(parent);
        dialog.setTitlePaneText(title);
        dialog.setTitle(title);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return null;
        }
        return dialog.getValue();
    }
}


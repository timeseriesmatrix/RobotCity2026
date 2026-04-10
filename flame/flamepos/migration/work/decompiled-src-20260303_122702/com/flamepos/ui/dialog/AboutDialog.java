/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.orocube.common.util.TerminalUtil
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.POSUtil;
import com.orocube.common.util.TerminalUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class AboutDialog
extends POSDialog {
    public AboutDialog() {
        super((Window)POSUtil.getBackOfficeWindow(), Messages.getString("AboutDialog.0"));
        this.setIconImage(Application.getApplicationIcon().getImage());
    }

    @Override
    protected void initUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel logoLabel = new JLabel(IconFactory.getIcon("/icons/", "fp_logo128x128.png"));
        contentPanel.add((Component)logoLabel, "West");
        JLabel l = new JLabel("<html><center><h1>Floreant POS</h1><br/><h2>Version " + Application.VERSION + "</h2></center></html>");
        contentPanel.add(l);
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        JButton btnOk = new JButton(Messages.getString("AboutDialog.5"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.dispose();
            }
        });
        JTextField tfTerminalKey = new JTextField();
        tfTerminalKey.setHorizontalAlignment(0);
        tfTerminalKey.setEditable(false);
        tfTerminalKey.setText(TerminalUtil.getSystemUID());
        tfTerminalKey.setBorder(null);
        tfTerminalKey.setFont(tfTerminalKey.getFont().deriveFont(1, 18.0f));
        buttonPanel.add((Component)new JSeparator(), "growx");
        buttonPanel.add((Component)tfTerminalKey, "newline,growx");
        buttonPanel.add((Component)new JSeparator(), "newline, growx");
        buttonPanel.add((Component)btnOk, "newline,center");
        contentPanel.add((Component)buttonPanel, "South");
        this.add(contentPanel);
    }
}


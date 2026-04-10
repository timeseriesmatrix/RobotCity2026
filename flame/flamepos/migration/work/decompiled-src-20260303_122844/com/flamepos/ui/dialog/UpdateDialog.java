/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class UpdateDialog
extends POSDialog {
    String[] versions;
    boolean up_to_date = false;
    boolean showTerminalKey = false;
    private JComboBox cbCheckUpdateStatus;

    public UpdateDialog(String[] versions, boolean up_to_date, boolean showTerminalKey) {
        super((Window)POSUtil.getBackOfficeWindow(), "Update");
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.setResizable(false);
        this.versions = versions;
        this.up_to_date = up_to_date;
        this.showTerminalKey = showTerminalKey;
        this.initComponent();
        this.cbCheckUpdateStatus.setSelectedItem(TerminalConfig.getCheckUpdateStatus());
    }

    protected void initComponent() {
        JPanel panel = new JPanel((LayoutManager)new MigLayout("fillx"));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        JLabel logoLabel = new JLabel(IconFactory.getIcon("/icons/", "fp_logo128x128.png"));
        panel.add((Component)logoLabel, "cell 0 0 0 2");
        JLabel l = new JLabel("<html><h1>Floreant POS</h1><h4>Current Version " + Application.VERSION + "</h4></html>");
        panel.add((Component)l, "cell 0 2");
        String version = "";
        if (this.up_to_date) {
            version = "<h2>The software is up to date.</h2> ";
        } else if (this.versions == null || this.versions.length == 0) {
            version = "<h4><a href='#'>Check for updates</a></h4>";
        } else if (this.versions.length > 0) {
            version = "<h2>Update Available</h2> ";
            for (String i : this.versions) {
                version = version + "<h4>" + i + "<a href='#'>  Download</a></h4>";
            }
        }
        JLabel lblVersion = new JLabel("<html>" + version + "</html>");
        panel.add((Component)lblVersion, "cell 1 0,right");
        lblVersion.addMouseListener(new MouseListener(){

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                UpdateDialog.this.setCursor(Cursor.getPredefinedCursor(0));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                UpdateDialog.this.setCursor(Cursor.getPredefinedCursor(12));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                String link = TerminalConfig.getPosDownloadUrl();
                try {
                    UpdateDialog.this.openBrowser(link);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        });
        this.cbCheckUpdateStatus = new JComboBox();
        this.cbCheckUpdateStatus.addItem("Daily");
        this.cbCheckUpdateStatus.addItem("Weekly");
        this.cbCheckUpdateStatus.addItem("Monthly");
        this.cbCheckUpdateStatus.addItem("Never");
        panel.add((Component)new JLabel("Check for Update"), "split 2,cell 1 2,aligny top,right");
        panel.add((Component)this.cbCheckUpdateStatus, "growx,aligny top,right");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        PosButton btnOk = new PosButton(Messages.getString("AboutDialog.5"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String status = (String)UpdateDialog.this.cbCheckUpdateStatus.getSelectedItem();
                TerminalConfig.setCheckUpdateStatus(status);
                UpdateDialog.this.dispose();
            }
        });
        buttonPanel.add((Component)new JSeparator(), "newline, grow");
        buttonPanel.add((Component)btnOk, "newline,center");
        this.add((Component)buttonPanel, "South");
        this.add(panel);
    }

    private void openBrowser(String link) throws Exception {
        URI uri = new URI(link);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }
    }
}


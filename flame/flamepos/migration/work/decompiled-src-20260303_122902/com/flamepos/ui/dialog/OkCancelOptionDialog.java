/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public abstract class OkCancelOptionDialog
extends POSDialog {
    private PosButton btnOk;
    private PosButton btnCancel;
    private TitlePanel titlePanel;
    private TransparentPanel contentPanel;

    public OkCancelOptionDialog() {
        super((Window)POSUtil.getBackOfficeWindow(), "");
        this.init();
        this.titlePanel.setTitle("");
    }

    public OkCancelOptionDialog(String title) {
        super((Window)Application.getPosWindow(), title);
        this.init();
        this.titlePanel.setTitle(title);
    }

    public OkCancelOptionDialog(Window owner) {
        super(owner, "");
        this.init();
    }

    public OkCancelOptionDialog(Frame owner, boolean model) {
        super(owner, model);
        this.init();
    }

    public OkCancelOptionDialog(Window owner, String title) {
        super(owner, title);
        this.init();
        this.titlePanel.setTitle(title);
    }

    private void init() {
        this.setLayout(new BorderLayout(10, 10));
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.setDefaultCloseOperation(0);
        this.titlePanel = new TitlePanel();
        this.add((Component)this.titlePanel, "North");
        this.contentPanel = new TransparentPanel();
        this.contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add((Component)this.contentPanel, "Center");
        JSeparator jSeparator1 = new JSeparator();
        TransparentPanel buttonPanel = new TransparentPanel();
        this.btnOk = new PosButton();
        this.btnCancel = new PosButton();
        TransparentPanel southPanel = new TransparentPanel();
        southPanel.setLayout(new BorderLayout());
        buttonPanel.setLayout(new FlowLayout());
        Dimension btnSize = PosUIManager.getSize(100, 70);
        this.btnOk.setPreferredSize(btnSize);
        this.btnOk.setText(POSConstants.OK.toUpperCase());
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OkCancelOptionDialog.this.doOk();
            }
        });
        buttonPanel.add(this.btnOk);
        this.btnCancel.setPreferredSize(btnSize);
        this.btnCancel.setText(POSConstants.CANCEL.toUpperCase());
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OkCancelOptionDialog.this.doCancel();
            }
        });
        buttonPanel.add(this.btnCancel);
        southPanel.add((Component)jSeparator1, "North");
        southPanel.add((Component)buttonPanel, "Center");
        this.contentPanel.setLayout(new BorderLayout());
        this.add((Component)southPanel, "South");
    }

    public void setTitlePaneText(String title) {
        this.titlePanel.setTitle(title);
    }

    public void setOkButtonText(String text) {
        this.btnOk.setText(text);
    }

    public JPanel getContentPanel() {
        return this.contentPanel;
    }

    public abstract void doOk();

    public void doCancel() {
        this.setCanceled(true);
        this.dispose();
    }
}


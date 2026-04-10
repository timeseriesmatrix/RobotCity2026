/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JSeparator;

public class BeanEditorDialog
extends JDialog
implements WindowListener {
    protected BeanEditor beanEditor;
    private boolean canceled = false;
    private TransparentPanel beanEditorContainer;
    private PosButton btnCancel;
    private PosButton btnOk;
    private TransparentPanel jPanel1;
    private TransparentPanel buttonPanel;
    private JSeparator jSeparator1;
    private TitlePanel titlePanel;

    public BeanEditorDialog() {
        this((BeanEditor)null);
    }

    public BeanEditorDialog(BeanEditor beanEditor) {
        super(POSUtil.getFocusedWindow(), Dialog.ModalityType.APPLICATION_MODAL);
        this.initComponents();
        this.setBeanEditor(beanEditor);
        this.addWindowListener(this);
    }

    public BeanEditorDialog(Frame owner, BeanEditor beanEditor) {
        super(owner, true);
        this.initComponents();
        this.setBeanEditor(beanEditor);
        this.addWindowListener(this);
    }

    public BeanEditorDialog(Dialog owner, BeanEditor beanEditor) {
        super(owner, true);
        this.initComponents();
        this.setBeanEditor(beanEditor);
        this.addWindowListener(this);
    }

    private void initComponents() {
        this.titlePanel = new TitlePanel();
        this.jPanel1 = new TransparentPanel();
        this.jSeparator1 = new JSeparator();
        this.buttonPanel = new TransparentPanel();
        this.btnOk = new PosButton();
        this.btnCancel = new PosButton();
        this.beanEditorContainer = new TransparentPanel();
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.setDefaultCloseOperation(0);
        this.getContentPane().add((Component)this.titlePanel, "North");
        this.jPanel1.setLayout(new BorderLayout());
        this.jPanel1.add((Component)this.jSeparator1, "North");
        this.buttonPanel.setLayout(new FlowLayout());
        this.btnOk.setPreferredSize(new Dimension(100, 60));
        this.btnOk.setText(POSConstants.OK.toUpperCase());
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                BeanEditorDialog.this.performOk(evt);
            }
        });
        this.buttonPanel.add(this.btnOk);
        this.btnCancel.setPreferredSize(new Dimension(100, 60));
        this.btnCancel.setText(POSConstants.CANCEL.toUpperCase());
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                BeanEditorDialog.this.performCancel(evt);
            }
        });
        this.buttonPanel.add(this.btnCancel);
        this.jPanel1.add((Component)this.buttonPanel, "Center");
        this.getContentPane().add((Component)this.jPanel1, "South");
        this.beanEditorContainer.setLayout(new BorderLayout());
        this.getContentPane().add((Component)this.beanEditorContainer, "Center");
    }

    @Override
    public void dispose() {
        if (this.beanEditor != null) {
            this.beanEditor = null;
        }
        super.dispose();
    }

    private void performCancel(ActionEvent evt) {
        this.canceled = true;
        this.dispose();
    }

    private void performOk(ActionEvent evt) {
        if (this.beanEditor == null) {
            return;
        }
        boolean saved = this.beanEditor.save();
        if (saved) {
            this.dispose();
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.titlePanel.setTitle(title);
    }

    public void open() {
        this.canceled = false;
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
        super.setVisible(true);
    }

    public void open(int w, int h) {
        this.canceled = false;
        this.setSize(w, h);
        this.setLocationRelativeTo(this.getOwner());
        super.setVisible(true);
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public Frame getParentFrame() {
        return (Frame)this.getOwner();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.performCancel(null);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    public BeanEditor getBeanEditor() {
        return this.beanEditor;
    }

    public Object getBean() {
        if (this.beanEditor != null) {
            return this.beanEditor.getBean();
        }
        return null;
    }

    public void setBean(Object bean) {
        if (this.beanEditor != null) {
            this.beanEditor.setBean(bean);
        }
    }

    public void setBeanEditor(BeanEditor beanEditor) {
        if (this.beanEditor != beanEditor) {
            this.beanEditor = beanEditor;
            this.beanEditorContainer.removeAll();
            if (beanEditor != null) {
                beanEditor.setEditorDialog(this);
                this.beanEditorContainer.add(beanEditor);
                this.setTitle(beanEditor.getDisplayText());
            }
            this.beanEditorContainer.revalidate();
        }
    }

    public TransparentPanel getButtonPanel() {
        return this.buttonPanel;
    }
}


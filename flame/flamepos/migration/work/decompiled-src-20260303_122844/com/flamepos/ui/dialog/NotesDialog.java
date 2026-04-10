/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.main.Application;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.views.NoteView;
import java.awt.Component;
import javax.swing.BorderFactory;

public class NotesDialog
extends OkCancelOptionDialog {
    private NoteView noteView;

    public NotesDialog() {
        this.initComponents();
    }

    private void initComponents() {
        this.noteView = new NoteView();
        this.setDefaultCloseOperation(2);
        this.noteView.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.getContentPane().add((Component)this.noteView, "Center");
        this.pack();
    }

    @Override
    public void doOk() {
        this.setCanceled(false);
        this.dispose();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(Application.getTitle());
        this.setTitlePaneText(title);
    }

    public String getNote() {
        return this.noteView.getNote();
    }
}


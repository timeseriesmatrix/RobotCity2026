/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui;

import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import java.awt.Frame;
import java.awt.LayoutManager;

public abstract class BeanEditor<E>
extends TransparentPanel {
    protected E bean;
    protected BeanEditorDialog editorDialog;

    public BeanEditor(LayoutManager layout) {
        super(layout);
    }

    public BeanEditor() {
    }

    public void createNew() {
    }

    public void clearFields() {
    }

    public void edit() {
    }

    public boolean delete() {
        return false;
    }

    public void setFieldsEnable(boolean enable) {
    }

    public abstract boolean save();

    public void cancel() {
    }

    protected abstract void updateView();

    protected abstract boolean updateModel() throws IllegalModelStateException;

    public abstract String getDisplayText();

    public E getBean() {
        return this.bean;
    }

    public void setBean(E bean) {
        this.setBean(bean, true);
    }

    public void setBean(E bean, boolean updateView) {
        this.bean = bean;
        if (bean == null) {
            this.clearFields();
        } else if (updateView) {
            this.updateView();
        }
    }

    public Frame getParentFrame() {
        return (Frame)this.editorDialog.getOwner();
    }

    public BeanEditorDialog getEditorDialog() {
        return this.editorDialog;
    }

    public void setEditorDialog(BeanEditorDialog editorDialog) {
        this.editorDialog = editorDialog;
    }
}


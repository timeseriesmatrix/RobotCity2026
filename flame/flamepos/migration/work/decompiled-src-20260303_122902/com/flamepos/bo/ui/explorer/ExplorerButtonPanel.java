/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.swing.TransparentPanel;
import javax.swing.JButton;

public class ExplorerButtonPanel
extends TransparentPanel {
    private JButton editButton;
    private JButton addButton;
    private JButton deleteButton;

    public ExplorerButtonPanel() {
        this.initComponents();
    }

    private void initComponents() {
        this.editButton = new JButton();
        this.addButton = new JButton();
        this.deleteButton = new JButton();
        this.editButton.setText(POSConstants.EDIT);
        this.addButton.setText(POSConstants.ADD);
        this.deleteButton.setText(POSConstants.DELETE);
        this.add(this.addButton);
        this.add(this.editButton);
        this.add(this.deleteButton);
    }

    public JButton getAddButton() {
        return this.addButton;
    }

    public JButton getDeleteButton() {
        return this.deleteButton;
    }

    public JButton getEditButton() {
        return this.editButton;
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.actions.ActionCommand;
import com.floreantpos.actions.PosAction;
import com.floreantpos.swing.POSButtonUI;
import com.floreantpos.swing.PosUIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class PosButton
extends JButton {
    public static Border border = new LineBorder(Color.BLACK, 1);
    static Insets margin = new Insets(1, 5, 1, 5);
    static POSButtonUI ui = new POSButtonUI();

    public PosButton() {
        this("");
    }

    public PosButton(String text) {
        super(text);
        this.setFocusable(false);
        this.setFocusPainted(false);
        this.setMargin(margin);
    }

    public PosButton(String text, Action action) {
        super(action);
        this.setText(text);
        this.setFocusable(false);
        this.setFocusPainted(false);
        this.setMargin(margin);
    }

    public PosButton(Action a) {
        super(a);
        this.setFocusable(false);
        this.setFocusPainted(false);
        this.setMargin(margin);
    }

    public PosButton(ActionCommand command) {
        this(command.toString());
        this.setActionCommand(command.name());
    }

    public PosButton(String text, ActionCommand command) {
        this(text);
        this.setActionCommand(command.name());
    }

    public PosButton(ActionCommand command, ActionListener listener) {
        this(command.toString());
        this.setActionCommand(command.name());
        this.addActionListener(listener);
    }

    public PosButton(ImageIcon imageIcon) {
        super(imageIcon);
        this.setFocusable(false);
        this.setFocusPainted(false);
    }

    @Override
    public String getUIClassID() {
        return "PosButtonUI";
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = null;
        if (this.isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        if (ui != null) {
            size = ui.getPreferredSize(this);
        }
        if (size == null) {
            size = new Dimension(PosUIManager.getSize(80, 60));
        } else {
            int width = size.width < 80 ? 80 : size.width;
            int height = size.height < 60 ? 60 : size.height;
            size.setSize(PosUIManager.getSize(width, height));
        }
        return size;
    }

    @Override
    public void setAction(Action a) {
        super.setAction(a);
        if (a instanceof PosAction) {
            PosAction action = (PosAction)a;
            this.setVisible(action.isVisible());
        }
    }

    static {
        UIManager.put("PosButtonUI", "com.floreantpos.swing.POSButtonUI");
    }
}


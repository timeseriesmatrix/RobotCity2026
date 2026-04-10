/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JPasswordField;
import javax.swing.text.Document;

public class POSPasswordField
extends JPasswordField
implements FocusListener {
    public POSPasswordField() {
        this.addFocusListener(this);
    }

    public POSPasswordField(String text) {
        super(text);
        this.addFocusListener(this);
    }

    public POSPasswordField(int columns) {
        super(columns);
        this.addFocusListener(this);
    }

    public POSPasswordField(String text, int columns) {
        super(text, columns);
        this.addFocusListener(this);
    }

    public POSPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
        this.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.text.Document;

public class POSTextField
extends JTextField
implements FocusListener {
    public POSTextField() {
        super(10);
        this.addFocusListener(this);
    }

    public POSTextField(String text) {
        super(text);
        this.addFocusListener(this);
    }

    public POSTextField(int columns) {
        super(columns);
        this.addFocusListener(this);
    }

    public POSTextField(String text, int columns) {
        super(text, columns);
        this.addFocusListener(this);
    }

    public POSTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
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


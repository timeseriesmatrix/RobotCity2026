/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.text.Document;
import org.apache.commons.lang.StringUtils;

public class FocusedTextField
extends JTextField
implements FocusListener {
    public FocusedTextField() {
        this.init();
    }

    public FocusedTextField(String text) {
        super(text);
        this.init();
    }

    public FocusedTextField(int columns) {
        super(columns);
        this.init();
    }

    public FocusedTextField(String text, int columns) {
        super(text, columns);
        this.init();
    }

    public FocusedTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        this.init();
    }

    private void init() {
        this.installFocusHandler();
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty((String)this.getText());
    }

    private void installFocusHandler() {
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


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DoubleDocument
extends PlainDocument {
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        String value = this.getText(0, this.getLength());
        value = value + str;
        try {
            Double.parseDouble(value);
        }
        catch (Exception x) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        super.insertString(offs, str, a);
    }
}


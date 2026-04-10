/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FocusedTextField;

public class FixedLengthTextField
extends FocusedTextField {
    private FixedLengthDocument fixedLengthDocument;

    public FixedLengthTextField() {
        this(30);
    }

    public FixedLengthTextField(int length) {
        super(length);
        this.fixedLengthDocument = new FixedLengthDocument(length);
        this.setDocument(this.fixedLengthDocument);
    }

    public int getLength() {
        return this.fixedLengthDocument.getLength();
    }

    public void setLength(int length) {
        this.fixedLengthDocument.setMaximumLength(length);
    }
}


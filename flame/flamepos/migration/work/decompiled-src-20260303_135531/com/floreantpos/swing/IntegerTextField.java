/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.FocusedTextField;
import com.floreantpos.swing.IntegerDocument;

public class IntegerTextField
extends FocusedTextField {
    public IntegerTextField() {
        this.setDocument(new IntegerDocument());
    }

    public IntegerTextField(int columns) {
        super(columns);
        this.setDocument(new IntegerDocument());
    }

    public int getInteger() {
        try {
            return Integer.parseInt(this.getText());
        }
        catch (Exception e) {
            return 0;
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.DoubleDocument;
import com.floreantpos.swing.FocusedTextField;

public class DoubleTextField
extends FocusedTextField {
    public DoubleTextField() {
        this.setDocument(new DoubleDocument());
    }

    public DoubleTextField(int columns) {
        super(columns);
        this.setDocument(new DoubleDocument());
    }

    public double getDouble() {
        try {
            return Double.parseDouble(this.getText());
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }

    public double getDoubleOrZero() {
        try {
            return Double.parseDouble(this.getText());
        }
        catch (Exception e) {
            return 0.0;
        }
    }
}


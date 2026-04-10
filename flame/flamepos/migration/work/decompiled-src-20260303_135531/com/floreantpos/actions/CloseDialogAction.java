/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class CloseDialogAction
extends AbstractAction {
    private JDialog dialog;

    public CloseDialogAction(JDialog dialog) {
        super(POSConstants.CLOSE.toUpperCase());
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.dialog.dispose();
    }
}


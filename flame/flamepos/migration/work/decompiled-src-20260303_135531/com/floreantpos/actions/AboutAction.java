/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.Messages;
import com.floreantpos.ui.dialog.AboutDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class AboutAction
extends AbstractAction {
    public AboutAction() {
        super(Messages.getString("AboutAction.0"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutDialog dialog = new AboutDialog();
        dialog.pack();
        dialog.open();
    }
}


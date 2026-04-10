/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.explorer.CurrencyDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class CurrencyExplorerAction
extends AbstractAction {
    public CurrencyExplorerAction() {
        super(Messages.getString("CurrencyExplorerAction.0"));
    }

    public CurrencyExplorerAction(String name) {
        super(name);
    }

    public CurrencyExplorerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CurrencyDialog dialog = new CurrencyDialog();
        dialog.setTitle(Messages.getString("CurrencyExplorerAction.1"));
        dialog.setSize(800, 600);
        dialog.open();
    }
}


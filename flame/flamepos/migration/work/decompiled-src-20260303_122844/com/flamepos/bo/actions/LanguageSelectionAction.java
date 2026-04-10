/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.LanguageSelectionDialog;
import com.floreantpos.util.POSUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class LanguageSelectionAction
extends AbstractAction {
    public LanguageSelectionAction() {
        super("Language");
    }

    public LanguageSelectionAction(String name) {
        super(name);
    }

    public LanguageSelectionAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog();
        dialog.setTitle("Language Selection");
        dialog.setDefaultCloseOperation(2);
        dialog.setSize(PosUIManager.getSize(600, 400));
        dialog.setLocationRelativeTo(POSUtil.getFocusedWindow());
        dialog.setVisible(true);
    }
}


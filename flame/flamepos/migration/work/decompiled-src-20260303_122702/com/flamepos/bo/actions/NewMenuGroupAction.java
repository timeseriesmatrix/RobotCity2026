/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.util.POSUtil;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class NewMenuGroupAction
extends AbstractAction {
    public NewMenuGroupAction() {
    }

    public NewMenuGroupAction(String name) {
        super(name);
    }

    public NewMenuGroupAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuGroupForm editor = new MenuGroupForm();
        BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
        dialog.open();
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.model.MenuItemForm;
import com.floreantpos.util.POSUtil;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class NewMenuItemAction
extends AbstractAction {
    public NewMenuItemAction() {
    }

    public NewMenuItemAction(String name) {
        super(name);
    }

    public NewMenuItemAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            MenuItemForm editor = new MenuItemForm();
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
        }
        catch (Exception x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.config.ui.ConfigurationDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class ConfigureRestaurantAction
extends AbstractAction {
    public ConfigureRestaurantAction() {
        super(POSConstants.CONFIGURATION);
    }

    public ConfigureRestaurantAction(String name) {
        super(name);
    }

    public ConfigureRestaurantAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ConfigurationDialog dialog = new ConfigurationDialog();
        dialog.setSize(1024, 700);
        dialog.open();
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.PosAction;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.model.UserPermission;

public class ManageTableLayoutAction
extends PosAction {
    FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);

    public ManageTableLayoutAction() {
        super(POSConstants.TABLE_MANAGE_BUTTON_TEXT, UserPermission.MANAGE_TABLE_LAYOUT);
        if (this.floorLayoutPlugin == null) {
            this.setVisible(false);
        }
    }

    @Override
    public void execute() {
        if (this.floorLayoutPlugin != null) {
            this.floorLayoutPlugin.openTicketsAndTablesDisplay();
        }
    }
}


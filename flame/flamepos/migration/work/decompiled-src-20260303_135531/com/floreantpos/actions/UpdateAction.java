/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.orocube.common.util.TerminalUtil
 */
package com.floreantpos.actions;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.services.PosWebService;
import com.floreantpos.ui.dialog.UpdateDialog;
import com.orocube.common.util.TerminalUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class UpdateAction
extends AbstractAction {
    public UpdateAction() {
        super("Update");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.openUpdateDialog();
    }

    private void openUpdateDialog() {
        PosWebService service = new PosWebService();
        try {
            boolean up_to_date = false;
            String versionInfo = service.getAvailableNewVersions(TerminalUtil.getSystemUID(), Application.VERSION.substring(0, 3));
            String[] availableNewVersions = null;
            if (versionInfo != null) {
                if (versionInfo.equals("UP_TO_DATE")) {
                    up_to_date = true;
                } else if (versionInfo.startsWith("[")) {
                    versionInfo = versionInfo.replace("[", "").replace(",]", "");
                    availableNewVersions = versionInfo.split(",");
                }
            }
            UpdateDialog dialog = new UpdateDialog(availableNewVersions, up_to_date, true);
            dialog.setTitle(Messages.getString("UpdateAction.7"));
            dialog.pack();
            dialog.open();
        }
        catch (Exception ex) {
            PosLog.error(this.getClass(), ex);
        }
    }
}


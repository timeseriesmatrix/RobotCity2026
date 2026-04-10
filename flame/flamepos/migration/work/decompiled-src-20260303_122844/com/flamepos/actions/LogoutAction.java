/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.actions.ViewChangeAction;
import com.floreantpos.main.Application;
import com.floreantpos.main.PosWindow;
import java.awt.Window;

public class LogoutAction
extends ViewChangeAction {
    public LogoutAction() {
        super(Messages.getString("Logout"));
    }

    public LogoutAction(boolean showText, boolean showIcon) {
        if (showText) {
            this.putValue("Name", Messages.getString("Logout"));
        }
        if (showIcon) {
            this.putValue("SmallIcon", IconFactory.getIcon("/ui_icons/", "logout.png"));
        }
    }

    @Override
    public void execute() {
        Window[] windows;
        for (Window window : windows = Window.getWindows()) {
            if (window instanceof PosWindow) continue;
            window.setVisible(false);
            window.dispose();
        }
        Application.getInstance().doLogout();
    }
}


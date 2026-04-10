/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.AbstractFloreantPlugin
 */
package com.floreantpos.extension;

import com.floreantpos.extension.AbstractFloreantPlugin;
import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

public abstract class InventoryPlugin
extends AbstractFloreantPlugin {
    public abstract AbstractAction[] getActions();

    public abstract void addRecepieView(JTabbedPane var1);
}


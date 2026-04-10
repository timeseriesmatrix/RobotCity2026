/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 */
package com.floreantpos.extension;

import com.floreantpos.extension.FloreantPlugin;

public interface TicketImportPlugin
extends FloreantPlugin {
    public void startService();

    public void stopService();

    public void importTicket();
}


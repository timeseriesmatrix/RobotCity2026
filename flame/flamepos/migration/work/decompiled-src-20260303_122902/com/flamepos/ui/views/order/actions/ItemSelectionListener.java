/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.actions;

import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;

public interface ItemSelectionListener {
    public void itemSelected(MenuItem var1);

    public void itemSelectionFinished(MenuGroup var1);
}


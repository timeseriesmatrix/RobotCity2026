/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.TicketItemModifier;

public interface ModifierSelectionListener {
    public void modifierSelected(MenuModifier var1, Multiplier var2);

    public void modifierRemoved(TicketItemModifier var1);

    public void clearModifiers(MenuModifierGroup var1);

    public void modifierGroupSelectionDone(MenuModifierGroup var1);

    public void finishModifierSelection();
}


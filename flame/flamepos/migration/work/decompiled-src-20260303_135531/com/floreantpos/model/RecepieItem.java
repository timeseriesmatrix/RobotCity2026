/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.Recepie;
import com.floreantpos.model.base.BaseRecepieItem;

public class RecepieItem
extends BaseRecepieItem {
    private static final long serialVersionUID = 1L;

    public RecepieItem() {
    }

    public RecepieItem(Integer id) {
        super(id);
    }

    public RecepieItem(Integer id, Recepie recepie) {
        super(id, recepie);
    }
}


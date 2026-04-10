/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.base.BaseRecepie;
import java.util.ArrayList;
import java.util.List;

public class Recepie
extends BaseRecepie {
    private static final long serialVersionUID = 1L;

    public Recepie() {
    }

    public Recepie(Integer id) {
        super(id);
    }

    public void addRecepieItem(RecepieItem recepieItem) {
        List<RecepieItem> recepieItems = this.getRecepieItems();
        if (recepieItems == null) {
            recepieItems = new ArrayList<RecepieItem>(3);
            this.setRecepieItems(recepieItems);
        }
        recepieItem.setRecepie(this);
        recepieItems.add(recepieItem);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BasePizzaCrust;
import org.apache.commons.lang.StringUtils;

public class PizzaCrust
extends BasePizzaCrust {
    private static final long serialVersionUID = 1L;

    public PizzaCrust() {
    }

    public PizzaCrust(Integer id) {
        super(id);
    }

    @Override
    public String getTranslatedName() {
        String translatedName = super.getTranslatedName();
        if (StringUtils.isEmpty((String)translatedName)) {
            return this.getName();
        }
        return translatedName;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}


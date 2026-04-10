/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseShift;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="shift")
public class Shift
extends BaseShift {
    private static final long serialVersionUID = 1L;

    public Shift() {
    }

    public Shift(Integer id) {
        super(id);
    }

    public Shift(Integer id, String name) {
        super(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Shift)) {
            return false;
        }
        return this.getName().equalsIgnoreCase(((Shift)obj).getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}


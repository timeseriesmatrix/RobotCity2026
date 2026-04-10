/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.PackagingDimension;
import com.floreantpos.model.base.BasePackagingUnit;
import org.apache.commons.lang.StringUtils;

public class PackagingUnit
extends BasePackagingUnit {
    private static final long serialVersionUID = 1L;

    public PackagingUnit() {
    }

    public PackagingUnit(Integer id) {
        super(id);
    }

    public void setPackagingDimension(PackagingDimension dimension) {
        this.setDimension(dimension.name());
    }

    public PackagingDimension getPackagingDimension() {
        String dimension2 = this.getDimension();
        if (StringUtils.isEmpty((String)dimension2)) {
            return null;
        }
        return PackagingDimension.valueOf(dimension2);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}


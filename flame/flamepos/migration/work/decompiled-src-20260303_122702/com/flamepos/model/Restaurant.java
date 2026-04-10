/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseRestaurant;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="restaurant")
public class Restaurant
extends BaseRestaurant {
    private static final long serialVersionUID = 1L;

    public Restaurant() {
    }

    public Restaurant(Integer id) {
        super(id);
    }

    @Override
    public String getCurrencyName() {
        String currencyName = super.getCurrencyName();
        if (StringUtils.isEmpty((String)currencyName)) {
            return "Sample Currency";
        }
        return currencyName;
    }

    @Override
    public String getCurrencySymbol() {
        String currencySymbol = super.getCurrencySymbol();
        if (StringUtils.isEmpty((String)currencySymbol)) {
            currencySymbol = "$";
        }
        return currencySymbol;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.ShopTableType;
import com.floreantpos.model.dao.BaseShopTableTypeDAO;
import org.hibernate.criterion.Order;

public class ShopTableTypeDAO
extends BaseShopTableTypeDAO {
    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)ShopTableType.PROP_ID);
    }
}


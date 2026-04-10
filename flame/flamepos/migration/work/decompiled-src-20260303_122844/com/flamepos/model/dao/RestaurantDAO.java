/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.BaseRestaurantDAO;
import java.util.List;

public class RestaurantDAO
extends BaseRestaurantDAO {
    public static Restaurant getRestaurant() {
        Restaurant restaurant = RestaurantDAO.getInstance().get(1);
        if (restaurant == null) {
            List<Restaurant> list = RestaurantDAO.getInstance().findAll();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
            restaurant = new Restaurant(1);
            RestaurantDAO.getInstance().save(restaurant);
            return restaurant;
        }
        return restaurant;
    }
}


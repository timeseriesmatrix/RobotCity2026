/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseShopFloorTemplate;
import com.floreantpos.util.POSUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ShopFloorTemplate
extends BaseShopFloorTemplate {
    private static final long serialVersionUID = 1L;

    public ShopFloorTemplate() {
    }

    public ShopFloorTemplate(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        String displayName = super.getName();
        if (this.isDefaultFloor().booleanValue()) {
            displayName = displayName + " -Default";
        }
        return displayName;
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (this.getProperties() == null) {
            return null;
        }
        String string = this.getProperties().get(key);
        if (StringUtils.isEmpty((String)string)) {
            return defaultValue;
        }
        return string;
    }

    public void removeProperty(String propertyName) {
        Map<String, String> properties = this.getProperties();
        if (properties == null) {
            return;
        }
        properties.remove(propertyName);
    }

    public boolean isPropertyValueTrue(String propertyName) {
        String property = this.getProperty(propertyName);
        return POSUtil.getBoolean(property);
    }
}


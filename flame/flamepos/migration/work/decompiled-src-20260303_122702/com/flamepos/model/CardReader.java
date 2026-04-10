/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import org.apache.commons.lang.StringUtils;

public enum CardReader {
    PAX("PAX"),
    SWIPE(Messages.getString("CardReader.0")),
    MANUAL(Messages.getString("CardReader.1")),
    EXTERNAL_TERMINAL(Messages.getString("CardReader.2"));

    private String type;

    private CardReader(String typeString) {
        this.type = typeString;
    }

    public String getType() {
        return this.type;
    }

    public static CardReader fromString(String name) {
        if (StringUtils.isEmpty((String)name)) {
            return null;
        }
        return CardReader.valueOf(name);
    }

    public String toString() {
        return this.type;
    }
}


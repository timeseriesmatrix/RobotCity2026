/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui;

public enum Command {
    NEW,
    EDIT,
    DELETE,
    SAVE,
    CANCEL,
    NEW_TRANSACTION,
    UNKNOWN;


    public static Command fromString(String s) {
        Command[] values;
        for (Command command : values = Command.values()) {
            if (!command.name().equalsIgnoreCase(s)) continue;
            return command;
        }
        return UNKNOWN;
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.actions;

public enum ActionCommand {
    AUTHORIZE,
    AUTHORIZE_ALL,
    EDIT_TIPS,
    VOID_TRANS,
    CLOSE,
    OK;


    public String toString() {
        return this.name().replaceAll("_", " ");
    }
}


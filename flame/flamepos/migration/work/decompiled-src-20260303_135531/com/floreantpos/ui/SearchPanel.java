/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui;

import com.floreantpos.bo.ui.ModelBrowser;
import javax.swing.JPanel;

public abstract class SearchPanel<E>
extends JPanel {
    protected ModelBrowser<E> modelBrowser;

    public ModelBrowser<E> getModelBrowser() {
        return this.modelBrowser;
    }

    public void setModelBrowser(ModelBrowser<E> modelBrowser) {
        this.modelBrowser = modelBrowser;
    }

    public void refreshSearchPanel() {
    }
}


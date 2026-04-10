/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.demo;

import com.floreantpos.Messages;
import com.floreantpos.demo.KitchenDisplayView;
import com.floreantpos.main.Application;
import com.floreantpos.model.KitchenTicket;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class KitchenDisplayWindow
extends JFrame {
    KitchenDisplayView view = new KitchenDisplayView(false);

    public KitchenDisplayWindow() {
        this.setTitle(Messages.getString("KitchenDisplayWindow.0"));
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.add(this.view);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(2);
    }

    public void addTicket(KitchenTicket ticket) {
        this.view.addTicket(ticket);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.view.setVisible(b);
    }

    @Override
    public void dispose() {
        this.view.cleanup();
        super.dispose();
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.demo;

import com.floreantpos.POSConstants;
import com.floreantpos.demo.KitchenTicketView;
import com.floreantpos.main.Application;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class KitchenTicketListPanel
extends JPanel {
    private static int previousBlockIndex = -1;
    private static int currentBlockIndex = 0;
    private static int nextBlockIndex;
    private static int horizontalPanelCount;
    private PosButton btnNext = new PosButton();
    private PosButton btnPrev = new PosButton();
    private Set<KitchenTicket> existingTickets = new HashSet<KitchenTicket>();

    public KitchenTicketListPanel() {
        this.updatePanelCount();
        super.setLayout((LayoutManager)new MigLayout("filly, wrap " + horizontalPanelCount, "sg, fill", ""));
    }

    public boolean addTicket(KitchenTicket ticket) {
        if (this.existingTickets.contains(ticket)) {
            return false;
        }
        this.existingTickets.add(ticket);
        this.updateButton();
        if (nextBlockIndex < this.existingTickets.size()) {
            return false;
        }
        super.add((Component)new KitchenTicketView(ticket), "growy");
        return true;
    }

    protected void rendererKitchenTickets() {
        this.updatePanelCount();
        for (int i = currentBlockIndex; i < nextBlockIndex && i != this.existingTickets.size(); ++i) {
            KitchenTicket item = (KitchenTicket)this.existingTickets.toArray()[i];
            super.add((Component)new KitchenTicketView(item), "growy");
        }
        this.updateButton();
    }

    private void updateButton() {
        if (previousBlockIndex >= 0 && currentBlockIndex != 0) {
            this.btnPrev.setEnabled(true);
        } else {
            this.btnPrev.setEnabled(false);
        }
        if (nextBlockIndex < this.existingTickets.size()) {
            this.btnNext.setEnabled(true);
        } else {
            this.btnNext.setEnabled(false);
        }
    }

    public JPanel getPaginationPanel() {
        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.btnPrev.setText(POSConstants.CAPITAL_PREV);
        this.btnPrev.setPreferredSize(PosUIManager.getSize(80, 30));
        southPanel.add((Component)this.btnPrev, "West");
        this.btnNext.setPreferredSize(PosUIManager.getSize(80, 30));
        this.btnNext.setText(POSConstants.CAPITAL_NEXT);
        southPanel.add((Component)this.btnNext, "East");
        ScrollAction action = new ScrollAction();
        this.btnPrev.addActionListener(action);
        this.btnNext.addActionListener(action);
        return southPanel;
    }

    private void scrollDown() {
        currentBlockIndex = nextBlockIndex;
        super.removeAll();
        this.rendererKitchenTickets();
        this.repaint();
    }

    private void scrollUp() {
        currentBlockIndex = previousBlockIndex;
        super.removeAll();
        this.rendererKitchenTickets();
        this.repaint();
    }

    protected int getCount(int containerSize, int itemSize) {
        int panelCount = containerSize / itemSize;
        return panelCount;
    }

    private void updatePanelCount() {
        Dimension size = Application.getInstance().getRootView().getSize();
        horizontalPanelCount = this.getCount(size.width, 330);
        int verticalPanelCount = this.getCount(size.height, 280);
        int totalItem = horizontalPanelCount * verticalPanelCount;
        previousBlockIndex = currentBlockIndex - totalItem;
        nextBlockIndex = currentBlockIndex + totalItem;
    }

    @Override
    public void remove(Component comp) {
        if (comp instanceof KitchenTicketView) {
            KitchenTicketView view = (KitchenTicketView)comp;
            this.existingTickets.remove(view.getTicket());
        }
        super.remove(comp);
        super.removeAll();
        this.rendererKitchenTickets();
    }

    @Override
    public void removeAll() {
        Component[] components;
        this.existingTickets.clear();
        for (Component component : components = this.getComponents()) {
            if (!(component instanceof KitchenTicketView)) continue;
            KitchenTicketView kitchenTicketView = (KitchenTicketView)component;
            kitchenTicketView.stopTimer();
        }
        super.removeAll();
    }

    private class ScrollAction
    implements ActionListener {
        private ScrollAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == KitchenTicketListPanel.this.btnPrev) {
                KitchenTicketListPanel.this.scrollUp();
            } else if (source == KitchenTicketListPanel.this.btnNext) {
                KitchenTicketListPanel.this.scrollDown();
            }
        }
    }
}


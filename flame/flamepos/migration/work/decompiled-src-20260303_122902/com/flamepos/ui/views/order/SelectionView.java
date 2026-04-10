/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXTitledSeparator
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.swing.PosButton;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTitledSeparator;

public abstract class SelectionView
extends JPanel
implements ComponentListener {
    private static final int HORIZONTAL_GAP = 5;
    private static final int VERTICAL_GAP = 5;
    protected List items;
    private Dimension buttonSize;
    protected CardLayout cardLayout = new CardLayout();
    private JPanel cardLayoutContainer = new JPanel(this.cardLayout);
    protected JPanel buttonPanelContainer = new JPanel(new BorderLayout());
    protected TitledBorder border;
    protected JPanel actionButtonPanel = new JPanel((LayoutManager)new MigLayout("fill,hidemode 3, ins 2", "sg, fill", ""));
    protected PosButton btnNext;
    protected PosButton btnPrev;
    private boolean initialized = true;
    private String title;

    public SelectionView(String title, int buttonWidth, int buttonHeight) {
        this.title = title;
        this.buttonSize = new Dimension(buttonWidth, buttonHeight);
        this.border = new TitledBorder(title);
        this.border.setTitleJustification(2);
        this.setBorder(new CompoundBorder(this.border, new EmptyBorder(2, 2, 2, 2)));
        this.setLayout(new BorderLayout(5, 5));
        this.buttonPanelContainer.add(this.cardLayoutContainer);
        this.add(this.buttonPanelContainer);
        this.btnPrev = new PosButton();
        this.btnPrev.setText(POSConstants.CAPITAL_PREV);
        this.actionButtonPanel.add((Component)this.btnPrev, "grow, align center");
        this.btnNext = new PosButton();
        this.btnNext.setText(POSConstants.CAPITAL_NEXT);
        this.actionButtonPanel.add((Component)this.btnNext, "grow, align center");
        this.add((Component)this.actionButtonPanel, "South");
        ScrollAction action = new ScrollAction();
        this.btnPrev.addActionListener(action);
        this.btnNext.addActionListener(action);
        this.addComponentListener(this);
        this.btnNext.setVisible(false);
        this.btnPrev.setVisible(false);
    }

    public SelectionView(String title) {
        this(title, 120, TerminalConfig.getMenuItemButtonHeight());
    }

    public void setTitle(String title) {
        this.border.setTitle(title);
    }

    public void setItems(List items) {
        this.items = items;
        this.renderItems();
    }

    public List getItems() {
        return this.items;
    }

    public Dimension getButtonSize() {
        return this.buttonSize;
    }

    public void setButtonSize(Dimension buttonSize) {
        this.buttonSize = buttonSize;
    }

    protected abstract AbstractButton createItemButton(Object var1);

    public void reset() {
        this.cardLayoutContainer.removeAll();
    }

    protected int getHorizontalButtonCount() {
        Dimension size = this.buttonPanelContainer.getSize();
        Dimension itemButtonSize = this.getButtonSize();
        return this.getButtonCount(size.width, itemButtonSize.width);
    }

    protected int getFitableButtonCount() {
        Dimension size = this.buttonPanelContainer.getSize();
        Dimension itemButtonSize = this.getButtonSize();
        int horizontalButtonCount = this.getButtonCount(size.width, itemButtonSize.width);
        int verticalButtonCount = this.getButtonCount(size.height, itemButtonSize.height);
        int totalItem = horizontalButtonCount * verticalButtonCount;
        return totalItem;
    }

    protected void renderItems() {
        this.reset();
        if (this.items == null || this.items.size() == 0) {
            this.revalidate();
            this.repaint();
            return;
        }
        Dimension itemButtonSize = this.getButtonSize();
        int totalItem = this.getFitableButtonCount();
        try {
            ButtonPanel buttonPanel = null;
            for (int i = 0; i < this.items.size(); ++i) {
                Object item;
                AbstractButton itemButton;
                if (i % totalItem == 0) {
                    buttonPanel = new ButtonPanel("buttonpanel-" + i);
                    buttonPanel.setLayout(this.createButtonPanelLayout());
                    this.cardLayoutContainer.add((Component)buttonPanel, buttonPanel.getName());
                }
                if ((itemButton = this.createItemButton(item = this.items.get(i))) == null) continue;
                itemButton.setPreferredSize(itemButtonSize);
                buttonPanel.add(itemButton);
            }
        }
        catch (Exception e) {
            this.initialized = false;
        }
        this.cardLayout.first(this.cardLayoutContainer);
        if (this.cardLayoutContainer.getComponentCount() > 1) {
            this.btnPrev.setVisible(true);
            this.btnNext.setVisible(true);
        } else {
            this.btnPrev.setVisible(false);
            this.btnNext.setVisible(false);
        }
        this.revalidate();
        this.repaint();
    }

    protected LayoutManager createButtonPanelLayout() {
        return new FlowLayout(1);
    }

    public ButtonPanel getActivePanel() {
        Component[] components;
        for (Component component : components = this.cardLayoutContainer.getComponents()) {
            if (!(component instanceof ButtonPanel) || !component.isVisible()) continue;
            return (ButtonPanel)component;
        }
        return null;
    }

    public void addButton(AbstractButton button) {
        button.setPreferredSize(this.buttonSize);
        button.setText("<html><body><center>" + button.getText() + "</center></body></html>");
        this.cardLayoutContainer.add(button);
    }

    public void addSeparator(String text) {
        this.cardLayoutContainer.add((Component)new JXTitledSeparator(text, 0), "alignx 50%, newline, span, growx, height 30!");
    }

    private void scrollDown() {
        this.cardLayout.next(this.cardLayoutContainer);
    }

    private void scrollUp() {
        this.cardLayout.previous(this.cardLayoutContainer);
    }

    public JPanel getButtonsPanel() {
        return this.cardLayoutContainer;
    }

    public AbstractButton getFirstItemButton() {
        int componentCount = this.cardLayoutContainer.getComponentCount();
        if (componentCount == 0) {
            return null;
        }
        ButtonPanel buttonPanel = (ButtonPanel)this.cardLayoutContainer.getComponent(0);
        if (buttonPanel.getComponentCount() == 0) {
            return null;
        }
        return (AbstractButton)buttonPanel.getComponent(0);
    }

    protected int getButtonCount(int containerSize, int itemSize) {
        int buttonCount = containerSize / (itemSize + 5);
        return buttonCount;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int totalItem = this.getFitableButtonCount();
        if (totalItem == this.cardLayoutContainer.getComponentCount()) {
            return;
        }
        this.renderItems();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private class ButtonPanel
    extends JPanel {
        public ButtonPanel(String name) {
            this.setName(name);
            this.setBorder(null);
        }
    }

    private class ScrollAction
    implements ActionListener {
        private ScrollAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == SelectionView.this.btnPrev) {
                SelectionView.this.scrollUp();
            } else if (source == SelectionView.this.btnNext) {
                SelectionView.this.scrollDown();
            }
        }
    }
}


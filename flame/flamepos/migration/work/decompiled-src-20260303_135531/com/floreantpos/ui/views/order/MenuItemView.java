/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.SelectionView;
import com.floreantpos.ui.views.order.actions.ItemSelectionListener;
import com.floreantpos.util.CurrencyUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MenuItemView
extends SelectionView {
    public static final String VIEW_NAME = "ITEM_VIEW";
    private static final int ITEM_GAP = 5;
    private static final int MIN_COLUMNS = 4;
    private Vector<ItemSelectionListener> listenerList = new Vector();
    private MenuGroup menuGroup;
    private Map<Integer, ItemButton> menuItemButtonMap = new HashMap<Integer, ItemButton>();
    private boolean showPrice;
    private boolean showStockCount;
    private JScrollPane itemScrollPane;
    private JPanel itemGridWrapper;

    public MenuItemView() {
        super(POSConstants.ITEMS, PosUIManager.getSize(120), PosUIManager.getSize(80));
        this.remove(this.buttonPanelContainer);
        this.remove(this.actionButtonPanel);
        this.itemGridWrapper = new JPanel(new BorderLayout());
        this.itemGridWrapper.add(this.getButtonsPanel(), "North");
        this.itemScrollPane = new JScrollPane(this.itemGridWrapper);
        this.itemScrollPane.setBorder(null);
        this.itemScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.itemScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.itemScrollPane.getVerticalScrollBar().setUnitIncrement(PosUIManager.getSize(30));
        this.add(this.itemScrollPane);
        this.btnPrev.setVisible(false);
        this.btnNext.setVisible(false);
    }

    public MenuGroup getMenuGroup() {
        return this.menuGroup;
    }

    public void setMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
        this.menuItemButtonMap.clear();
        if (menuGroup == null) {
            this.setItems(null);
            return;
        }
        OrderType orderType = OrderView.getInstance().getCurrentTicket().getOrderType();
        this.showPrice = orderType.isShowPriceOnButton();
        this.showStockCount = orderType.isShowStockCountOnButton();
        MenuItemDAO dao = new MenuItemDAO();
        try {
            List<MenuItem> items = new ArrayList<MenuItem>();
            if (menuGroup.getId() != null) {
                items = dao.findByParent(Application.getInstance().getTerminal(), menuGroup, orderType, false);
            }
            if (RootView.getInstance().isMaintenanceMode()) {
                MenuItem newMenuItem = new MenuItem(null, "", 0.0, 0.0);
                newMenuItem.setParent(menuGroup);
                items.add(newMenuItem);
            }
            this.setItems(items);
        }
        catch (PosException e) {
            PosLog.error(this.getClass(), e);
        }
    }

    @Override
    protected AbstractButton createItemButton(Object item) {
        MenuItem menuItem = (MenuItem)item;
        ItemButton itemButton = new ItemButton(menuItem);
        this.menuItemButtonMap.put(menuItem.getId(), itemButton);
        this.filterByStockAmount(menuItem, itemButton);
        this.setInitialized(true);
        return itemButton;
    }

    @Override
    public void reset() {
        this.getButtonsPanel().removeAll();
    }

    @Override
    protected void renderItems() {
        this.reset();
        if (this.items == null || this.items.size() == 0) {
            this.revalidate();
            this.repaint();
            return;
        }
        int columnCount = this.getScrollableColumnCount();
        int squareItemSize = this.getSquareItemSize(columnCount);
        this.getButtonsPanel().setLayout(this.createScrollableLayout(columnCount));
        Dimension itemButtonSize = new Dimension(squareItemSize, squareItemSize);
        int renderedCount = 0;
        try {
            for (Object item : this.items) {
                AbstractButton itemButton = this.createItemButton(item);
                if (itemButton == null) continue;
                itemButton.setPreferredSize(itemButtonSize);
                itemButton.setMinimumSize(itemButtonSize);
                itemButton.setMaximumSize(itemButtonSize);
                this.getButtonsPanel().add(itemButton);
                ++renderedCount;
            }
            while (renderedCount % columnCount != 0) {
                JPanel blank = new JPanel();
                blank.setOpaque(false);
                blank.setPreferredSize(itemButtonSize);
                this.getButtonsPanel().add(blank);
                ++renderedCount;
            }
            int rowCount = renderedCount <= 0 ? 1 : renderedCount / columnCount;
            int contentWidth = columnCount * squareItemSize + (columnCount - 1) * ITEM_GAP;
            int contentHeight = rowCount * squareItemSize + (rowCount - 1) * ITEM_GAP;
            this.getButtonsPanel().setPreferredSize(new Dimension(contentWidth, contentHeight));
        }
        catch (Exception e) {
            this.setInitialized(false);
        }
        this.btnPrev.setVisible(false);
        this.btnNext.setVisible(false);
        this.revalidate();
        this.repaint();
    }

    private LayoutManager createScrollableLayout(int columnCount) {
        return new GridLayout(0, columnCount, ITEM_GAP, ITEM_GAP);
    }

    private int getScrollableColumnCount() {
        int viewportWidth = this.itemScrollPane != null ? this.itemScrollPane.getViewport().getWidth() : 0;
        if (viewportWidth <= 0) {
            viewportWidth = this.getWidth();
        }
        if (viewportWidth <= 0) {
            return MIN_COLUMNS;
        }
        int targetItemSize = Math.max(1, this.getButtonSize().width);
        int fitCount = (viewportWidth + ITEM_GAP) / (targetItemSize + ITEM_GAP);
        return Math.max(MIN_COLUMNS, Math.max(1, fitCount));
    }

    private int getSquareItemSize(int columnCount) {
        int viewportWidth = this.itemScrollPane != null ? this.itemScrollPane.getViewport().getWidth() : 0;
        if (viewportWidth <= 0) {
            viewportWidth = this.getWidth();
        }
        if (viewportWidth <= 0) {
            viewportWidth = this.getButtonSize().width * columnCount + (columnCount - 1) * ITEM_GAP;
        }
        int availableWidth = viewportWidth - (columnCount - 1) * ITEM_GAP;
        return Math.max(1, availableWidth / columnCount);
    }

    public void updateView(MenuItem menuItem) {
        this.setMenuGroup(menuItem.getParent());
    }

    public void addItemSelectionListener(ItemSelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeItemSelectionListener(ItemSelectionListener listener) {
        this.listenerList.remove(listener);
    }

    private void fireItemSelected(MenuItem foodItem) {
        for (ItemSelectionListener listener : this.listenerList) {
            listener.itemSelected(foodItem);
        }
    }

    public void selectItem(MenuItem menuItem) {
    }

    private void filterItemsByOrderType(List<MenuItem> items) {
        String orderType = OrderView.getInstance().getTicketView().getTicket().getOrderType().toString();
        Iterator<MenuItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            MenuItem menuItem = iterator.next();
            List<OrderType> orderTypeList = menuItem.getOrderTypeList();
            if (orderTypeList == null || orderTypeList.size() == 0 || orderTypeList.contains(orderType)) continue;
            iterator.remove();
        }
    }

    private void filterByStockAmount(MenuItem menuItem, ItemButton itemButton) {
        if (menuItem.isDisableWhenStockAmountIsZero().booleanValue() && menuItem.getStockAmount() <= 0.0) {
            itemButton.setEnabled(false);
        }
    }

    public void disableItemButton(MenuItem item) {
        ItemButton itemButton = this.menuItemButtonMap.get(item.getId());
        itemButton.setEnabled(false);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.renderItems();
    }

    public class ItemButton
    extends PosButton
    implements ActionListener,
    MouseListener {
        private int BUTTON_SIZE = 100;
        MenuItem foodItem;

        ItemButton(MenuItem menuItem) {
            this.setFocusable(false);
            this.setVerticalTextPosition(3);
            this.setHorizontalTextPosition(0);
            this.BUTTON_SIZE = PosUIManager.getSize(100);
            this.updateView(menuItem);
            this.setPreferredSize(new Dimension(this.BUTTON_SIZE, this.BUTTON_SIZE));
            this.addActionListener(this);
            this.addMouseListener(this);
        }

        private void updateView(MenuItem menuItem) {
            Color textColor;
            this.foodItem = menuItem;
            if (menuItem.getImage() != null) {
                int w = this.BUTTON_SIZE - PosUIManager.getSize(10);
                int h = this.BUTTON_SIZE - PosUIManager.getSize(10);
                if (menuItem.isShowImageOnly().booleanValue()) {
                    this.setIcon(menuItem.getScaledImage(w, h));
                } else {
                    w = PosUIManager.getSize(80);
                    h = PosUIManager.getSize(40);
                    this.setIcon(menuItem.getScaledImage(w, h));
                    this.setText("<html><body><center><span style='font-size:7px;margin:0px;'>" + menuItem.getDisplayName() + "</span></center></body></html>");
                }
            } else if (menuItem.getId() == null) {
                this.setIcon(IconFactory.getIcon("/ui_icons/", "add+user.png"));
            } else {
                this.setText("<html><body><center>" + menuItem.getName() + (!MenuItemView.this.showPrice ? "" : "<br><h4><span style='color:white;background-color:green;margin:1;'>&nbsp;&nbsp;&nbsp;" + CurrencyUtil.getCurrencySymbol() + menuItem.getPrice() + "&nbsp;" + "&nbsp;" + "&nbsp;" + "</span>") + "&nbsp;" + (!MenuItemView.this.showStockCount ? "" : "<span style='color:white;background-color:red;margin:1;'>&nbsp;&nbsp;&nbsp;" + menuItem.getStockAmount() + "&nbsp;" + "&nbsp;" + "&nbsp;" + "</span>") + "</h4>" + "</center></body></html>");
            }
            Color buttonColor = menuItem.getButtonColor();
            if (buttonColor != null) {
                this.setBackground(buttonColor);
            }
            if ((textColor = menuItem.getTextColor()) != null) {
                this.setForeground(textColor);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (OrderView.getInstance().isVisible() && RootView.getInstance().isMaintenanceMode()) {
                this.foodItem = MenuItemDAO.getInstance().initialize(this.foodItem);
                QuickMaintenanceExplorer.quickMaintain(this.foodItem);
            }
            MenuItemView.this.fireItemSelected(this.foodItem);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}

/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.SelectionView;
import com.floreantpos.ui.views.order.actions.GroupSelectionListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class GroupView
extends SelectionView {
    private Vector<GroupSelectionListener> listenerList = new Vector();
    private MenuCategory menuCategory;
    public static final String VIEW_NAME = "GROUP_VIEW";
    private ButtonGroup buttonGroup;

    public GroupView() {
        super(POSConstants.GROUPS, PosUIManager.getSize(100), PosUIManager.getSize(60));
        this.remove(this.actionButtonPanel);
        this.btnPrev.setText("<");
        this.btnNext.setText(">");
        this.add((Component)this.btnPrev, "West");
        this.add((Component)this.btnNext, "East");
    }

    public MenuCategory getMenuCategory() {
        return this.menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
        if (menuCategory == null) {
            this.setItems(null);
            this.fireGroupSelected(null);
            return;
        }
        try {
            MenuGroupDAO dao = new MenuGroupDAO();
            OrderType orderType = OrderView.getInstance().getCurrentTicket().getOrderType();
            ArrayList<MenuGroup> groups = new ArrayList<MenuGroup>();
            List<MenuGroup> groupList = dao.findEnabledByParent(menuCategory);
            if (groupList != null) {
                groups.addAll(groupList);
            }
            if (RootView.getInstance().isMaintenanceMode()) {
                MenuGroup newMenuGroup = new MenuGroup(null, "");
                newMenuGroup.setParent(menuCategory);
                groups.add(newMenuGroup);
            } else {
                Iterator iterator = groups.iterator();
                while (iterator.hasNext()) {
                    MenuGroup menuGroup = (MenuGroup)iterator.next();
                    if (dao.hasChildren(null, menuGroup, orderType)) continue;
                    iterator.remove();
                }
            }
            this.setItems(groups);
            if (!RootView.getInstance().isMaintenanceMode() && groups.size() <= 1) {
                this.setVisible(false);
            } else {
                this.setVisible(true);
            }
            if (groups.size() > 0) {
                MenuGroup menuGroup = (MenuGroup)groups.get(0);
                GroupButton groupButton = (GroupButton)this.getFirstItemButton();
                if (groupButton != null) {
                    groupButton.setSelected(true);
                    this.fireGroupSelected(menuGroup);
                }
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showError(e);
        }
    }

    @Override
    protected void renderItems() {
        this.buttonGroup = new ButtonGroup();
        super.renderItems();
    }

    public void updateView(MenuGroup menuGroup) {
        this.setMenuCategory(menuGroup.getParent());
    }

    @Override
    protected int getFitableButtonCount() {
        Dimension size = this.buttonPanelContainer.getSize();
        Dimension itemButtonSize = this.getButtonSize();
        int horizontalButtonCount = this.getButtonCount(size.width, itemButtonSize.width);
        return horizontalButtonCount;
    }

    @Override
    protected LayoutManager createButtonPanelLayout() {
        return new GridLayout(1, 0, 5, 0);
    }

    public void addGroupSelectionListener(GroupSelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeGroupSelectionListener(GroupSelectionListener listener) {
        this.listenerList.remove(listener);
    }

    private void fireGroupSelected(MenuGroup foodGroup) {
        for (GroupSelectionListener listener : this.listenerList) {
            listener.groupSelected(foodGroup);
        }
    }

    @Override
    protected AbstractButton createItemButton(Object item) {
        MenuGroup menuGroup = (MenuGroup)item;
        GroupButton button = new GroupButton(menuGroup);
        this.buttonGroup.add(button);
        return button;
    }

    private class GroupButton
    extends POSToggleButton
    implements ActionListener {
        MenuGroup menuGroup;

        GroupButton(MenuGroup foodGroup) {
            this.updateView(foodGroup);
            this.addActionListener(this);
        }

        public MenuGroup getMenuGroup() {
            return this.menuGroup;
        }

        public void updateView(MenuGroup foodGroup) {
            this.menuGroup = foodGroup;
            if (this.menuGroup.getId() == null) {
                this.setIcon(IconFactory.getIcon("/ui_icons/", "add+user.png"));
            } else {
                this.setText("<html><body><center>" + foodGroup.getDisplayName() + "</center></body></html>");
            }
            if (this.menuGroup.getButtonColorCode() != null) {
                this.setBackground(this.menuGroup.getButtonColor());
            }
            if (this.menuGroup.getTextColorCode() != null) {
                this.setForeground(this.menuGroup.getTextColor());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (OrderView.getInstance().isVisible() && RootView.getInstance().isMaintenanceMode()) {
                QuickMaintenanceExplorer.quickMaintain(this.menuGroup);
            }
            GroupView.this.fireGroupSelected(this.menuGroup);
        }
    }
}


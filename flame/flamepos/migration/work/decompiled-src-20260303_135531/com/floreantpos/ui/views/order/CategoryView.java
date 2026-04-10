/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.views.order.MenuItemView;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.SelectionView;
import com.floreantpos.ui.views.order.actions.CategorySelectionListener;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import org.apache.log4j.Logger;

public class CategoryView
extends SelectionView
implements ActionListener {
    private Vector<CategorySelectionListener> listenerList = new Vector();
    private ButtonGroup categoryButtonGroup;
    private Map<String, CategoryButton> buttonMap = new HashMap<String, CategoryButton>();
    private MenuCategory selectedCategory;
    public static final String VIEW_NAME = "CATEGORY_VIEW";
    private static Logger logger = Logger.getLogger(MenuItemView.class);

    public CategoryView() {
        super(POSConstants.CATEGORIES, PosUIManager.getSize(100), PosUIManager.getSize(80));
        this.categoryButtonGroup = new ButtonGroup();
        this.setPreferredSize(new Dimension(PosUIManager.getSize(120, 100)));
    }

    public void initialize() {
        this.reset();
        MenuCategoryDAO categoryDAO = new MenuCategoryDAO();
        List<MenuCategory> categories = categoryDAO.findAllEnable();
        boolean maintenanceMode = RootView.getInstance().isMaintenanceMode();
        if (categories.size() == 0 && !maintenanceMode) {
            return;
        }
        OrderType orderType = OrderView.getInstance().getCurrentTicket().getOrderType();
        MenuGroupDAO menuGroupDAO = MenuGroupDAO.getInstance();
        if (maintenanceMode) {
            categories.add(new MenuCategory(null, ""));
        } else {
            Iterator<MenuCategory> iterator = categories.iterator();
            while (iterator.hasNext()) {
                MenuCategory menuCategory = iterator.next();
                if (menuCategory.getId() == null) continue;
                List<MenuGroup> menuGroups = menuCategory.getMenuGroups();
                Iterator<MenuGroup> iterator2 = menuGroups.iterator();
                while (iterator2.hasNext()) {
                    MenuGroup menuGroup = iterator2.next();
                    if (menuGroupDAO.hasChildren(null, menuGroup, orderType)) continue;
                    iterator2.remove();
                }
                if (menuGroups != null && menuGroups.size() != 0) continue;
                iterator.remove();
            }
        }
        this.setItems(categories);
        CategoryButton categoryButton = null;
        categoryButton = maintenanceMode && !categories.isEmpty() && this.selectedCategory != null ? this.buttonMap.get(String.valueOf(this.selectedCategory.getId())) : (CategoryButton)this.getFirstItemButton();
        if (categoryButton != null) {
            categoryButton.setSelected(true);
            this.fireCategorySelected(categoryButton.foodCategory);
        } else {
            this.fireCategorySelected(null);
        }
        if (!maintenanceMode && categories.size() <= 1) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }

    @Override
    protected AbstractButton createItemButton(Object item) {
        MenuCategory menuCategory = (MenuCategory)item;
        CategoryButton button = new CategoryButton(this, menuCategory);
        this.categoryButtonGroup.add(button);
        this.buttonMap.put(String.valueOf(menuCategory.getId()), button);
        return button;
    }

    public void updateView(MenuCategory menuCategory) {
        this.selectedCategory = menuCategory;
        this.initialize();
    }

    @Override
    protected LayoutManager createButtonPanelLayout() {
        return new GridLayout(0, 1, 2, 5);
    }

    public void addCategorySelectionListener(CategorySelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeCategorySelectionListener(CategorySelectionListener listener) {
        this.listenerList.remove(listener);
    }

    private void fireCategorySelected(MenuCategory foodCategory) {
        this.selectedCategory = foodCategory;
        for (CategorySelectionListener listener : this.listenerList) {
            listener.categorySelected(foodCategory);
        }
    }

    public void setSelectedCategory(MenuCategory category) {
        CategoryButton button = this.buttonMap.get(String.valueOf(category.getId()));
        if (button != null) {
            button.setSelected(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CategoryButton button = (CategoryButton)e.getSource();
        if (button.isSelected()) {
            if (OrderView.getInstance().isVisible() && RootView.getInstance().isMaintenanceMode()) {
                QuickMaintenanceExplorer.quickMaintain(button.foodCategory);
                if (button.foodCategory.getId() == null) {
                    return;
                }
            }
            this.fireCategorySelected(button.foodCategory);
        }
    }

    public void cleanup() {
        Collection<CategoryButton> buttons = this.buttonMap.values();
        for (CategoryButton button : buttons) {
            button.removeActionListener(this);
        }
        this.buttonMap.clear();
        logger.debug((Object)Messages.getString("CategoryView.4"));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        CategoryButton categoryButton;
        int totalItem = this.getFitableButtonCount();
        if (totalItem == this.buttonPanelContainer.getComponentCount()) {
            return;
        }
        this.renderItems();
        if (!this.isInitialized() && (categoryButton = (CategoryButton)this.getFirstItemButton()) != null) {
            categoryButton.setSelected(true);
            this.fireCategorySelected(categoryButton.foodCategory);
        }
    }

    private static class CategoryButton
    extends POSToggleButton {
        MenuCategory foodCategory;

        CategoryButton(CategoryView view, MenuCategory menuCategory) {
            this.updateView(menuCategory);
            this.addActionListener(view);
        }

        public void updateView(MenuCategory menuCategory) {
            this.foodCategory = menuCategory;
            if (this.foodCategory.getId() == null) {
                this.setIcon(IconFactory.getIcon("/ui_icons/", "add+user.png"));
            } else {
                this.setText("<html><body><center>" + menuCategory.getDisplayName() + "</center></body></html>");
            }
            if (menuCategory.getButtonColor() != null) {
                this.setBackground(menuCategory.getButtonColor());
            }
            if (menuCategory.getTextColor() != null) {
                this.setForeground(menuCategory.getTextColor());
            }
        }
    }
}


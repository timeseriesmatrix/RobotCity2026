/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.Discount;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.Shift;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.base.BaseMenuItem;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="menu-item")
public class MenuItem
extends BaseMenuItem {
    private static final long serialVersionUID = 1L;
    private Color buttonColor;
    private Color textColor;
    private ImageIcon image;

    public MenuItem() {
    }

    public MenuItem(Integer id) {
        super(id);
    }

    public MenuItem(Integer id, String name, Double buyPrice, Double price) {
        super(id, name, buyPrice, price);
    }

    @XmlTransient
    public ImageIcon getImage() {
        if (this.image != null) {
            return this.image;
        }
        int width = 100;
        int height = 100;
        byte[] imageData = this.getImageData();
        if (imageData != null) {
            this.image = new ImageIcon(imageData);
            this.image = new ImageIcon(this.image.getImage().getScaledInstance(width, height, 4));
        }
        return this.image;
    }

    public void setImage(ImageIcon icon) {
    }

    @Override
    public String getTranslatedName() {
        String translatedName = super.getTranslatedName();
        if (StringUtils.isEmpty((String)translatedName)) {
            return this.getName();
        }
        return translatedName;
    }

    @Override
    public Integer getSortOrder() {
        return this.sortOrder == null ? 9999 : this.sortOrder;
    }

    @XmlTransient
    public Color getButtonColor() {
        if (this.buttonColor != null) {
            return this.buttonColor;
        }
        if (this.getButtonColorCode() == null) {
            return null;
        }
        this.buttonColor = new Color(this.getButtonColorCode());
        return this.buttonColor;
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
    }

    @XmlTransient
    public Color getTextColor() {
        if (this.textColor != null) {
            return this.textColor;
        }
        if (this.getTextColorCode() == null) {
            return null;
        }
        this.textColor = new Color(this.getTextColorCode());
        return this.textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @XmlTransient
    public String getDisplayName() {
        if (TerminalConfig.isUseTranslatedName() && StringUtils.isNotEmpty((String)this.getTranslatedName())) {
            return this.getTranslatedName();
        }
        return super.getName();
    }

    public double getPrice(Shift currentShift) {
        List<MenuItemShift> shifts = this.getShifts();
        double price = super.getPrice();
        if (currentShift == null) {
            return price;
        }
        if (shifts == null || shifts.size() == 0) {
            return price;
        }
        for (MenuItemShift shift : shifts) {
            if (!shift.getShift().equals(currentShift)) continue;
            return shift.getShiftPrice();
        }
        return price;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getUniqueId() {
        return ("menu_item_" + this.getName() + "_" + this.getId()).replaceAll("\\s+", "_");
    }

    public TicketItem convertToTicketItem() {
        return this.convertToTicketItem(null, 0.0);
    }

    public TicketItem convertToTicketItem(OrderType orderType, double itemQuantity) {
        Recepie recepie;
        TicketItem ticketItem = new TicketItem();
        ticketItem.setItemId(this.getId());
        ticketItem.setMenuItem(this);
        ticketItem.setPizzaType(this.isPizzaType());
        ticketItem.setFractionalUnit(this.isFractionalUnit());
        if (this.isFractionalUnit().booleanValue()) {
            ticketItem.setItemQuantity(itemQuantity);
            ticketItem.setItemUnitName(this.getUnitName());
        } else {
            ticketItem.setItemCount(1);
        }
        ticketItem.setName(this.getDisplayName());
        ticketItem.setGroupName(this.getParent().getDisplayName());
        ticketItem.setCategoryName(this.getParent().getParent().getDisplayName());
        ticketItem.setUnitPrice(this.getPriceByOrderType(orderType));
        ticketItem.setTaxRate(this.getTaxByOrderType(orderType));
        ticketItem.setHasModifiers(this.hasModifiers());
        if (this.getParent().getParent().isBeverage().booleanValue()) {
            ticketItem.setBeverage(true);
            ticketItem.setShouldPrintToKitchen(false);
        } else {
            ticketItem.setBeverage(false);
            ticketItem.setShouldPrintToKitchen(true);
        }
        ticketItem.setPrinterGroup(this.getPrinterGroup());
        List<Discount> discountList = this.getDiscounts();
        if (this.getDiscounts() != null) {
            for (Discount discount : discountList) {
                if (!discount.isAutoApply().booleanValue()) continue;
                TicketItemDiscount ticketItemDiscount = MenuItem.convertToTicketItemDiscount(discount, ticketItem);
                ticketItem.addTodiscounts(ticketItemDiscount);
            }
        }
        if ((recepie = this.getRecepie()) != null) {
            List<RecepieItem> recepieItems = recepie.getRecepieItems();
            for (RecepieItem recepieItem : recepieItems) {
                InventoryItem inventoryItem = recepieItem.getInventoryItem();
                Double recepieUnits = inventoryItem.getTotalRecepieUnits();
                recepieUnits = recepieUnits - 1.0;
            }
        }
        return ticketItem;
    }

    public static TicketItemDiscount convertToTicketItemDiscount(Discount discount, TicketItem ticketItem) {
        TicketItemDiscount ticketItemDiscount = new TicketItemDiscount();
        ticketItemDiscount.setDiscountId(discount.getId());
        ticketItemDiscount.setAutoApply(discount.isAutoApply());
        ticketItemDiscount.setName(discount.getName());
        ticketItemDiscount.setType(discount.getType());
        ticketItemDiscount.setMinimumQuantity(discount.getMinimunBuy());
        ticketItemDiscount.setValue(discount.getValue());
        ticketItemDiscount.setTicketItem(ticketItem);
        return ticketItemDiscount;
    }

    public boolean hasModifiers() {
        return this.getMenuItemModiferGroups() != null && this.getMenuItemModiferGroups().size() > 0;
    }

    public boolean hasMandatoryModifiers() {
        List<MenuItemModifierGroup> modiferGroups = this.getMenuItemModiferGroups();
        if (modiferGroups == null || modiferGroups.size() == 0) {
            return false;
        }
        for (MenuItemModifierGroup menuItemModifierGroup : modiferGroups) {
            if (menuItemModifierGroup.getMinQuantity() <= 0) continue;
            return true;
        }
        return false;
    }

    public ImageIcon getScaledImage(int width, int height) {
        ImageIcon icon = new ImageIcon(this.getImageData());
        Image scaledInstance = icon.getImage().getScaledInstance(width, height, 4);
        return new ImageIcon(scaledInstance);
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (this.getProperties() == null) {
            return null;
        }
        String string = this.getProperties().get(key);
        if (StringUtils.isEmpty((String)string)) {
            return defaultValue;
        }
        return string;
    }

    public void removeProperty(String typeProperty, String taxProperty) {
        Map<String, String> properties = this.getProperties();
        if (properties == null) {
            return;
        }
        properties.remove(typeProperty);
        properties.remove(taxProperty);
    }

    public boolean isPropertyValueTrue(String propertyName) {
        String property = this.getProperty(propertyName);
        return POSUtil.getBoolean(property);
    }

    public void setPriceByOrderType(String type, double price) {
        this.addProperty(this.getStringWithUnderScore(type, "_PRICE"), String.valueOf(price));
    }

    public void setTaxByOrderType(String type, double price) {
        this.addProperty(this.getStringWithUnderScore(type, "_TAX"), String.valueOf(price));
    }

    public double getPriceByOrderType(OrderType type) {
        double defaultPrice = this.getPrice(Application.getInstance().getCurrentShift());
        if (type == null) {
            return defaultPrice;
        }
        String priceProp = this.getProperty(this.getStringWithUnderScore(type.name(), "_PRICE"));
        if (priceProp == null) {
            return defaultPrice;
        }
        try {
            return Double.parseDouble(priceProp);
        }
        catch (Exception e) {
            return defaultPrice;
        }
    }

    public double getPriceByOrderType(String typeName) {
        double defaultPrice = this.getPrice(Application.getInstance().getCurrentShift());
        if (typeName == null) {
            return defaultPrice;
        }
        String priceProp = this.getProperty(this.getStringWithUnderScore(typeName, "_PRICE"));
        if (priceProp == null) {
            return defaultPrice;
        }
        try {
            return Double.parseDouble(priceProp);
        }
        catch (Exception e) {
            return defaultPrice;
        }
    }

    public double getTaxByOrderType(OrderType type) {
        if (this.getTax() == null) {
            return 0.0;
        }
        double defaultTax = this.getTax().getRate();
        if (type == null) {
            return defaultTax;
        }
        String taxProp = this.getProperty(this.getStringWithUnderScore(type.name(), "_TAX"));
        if (taxProp == null) {
            return defaultTax;
        }
        try {
            return Double.parseDouble(taxProp);
        }
        catch (Exception e) {
            return defaultTax;
        }
    }

    public double getTaxByOrderType(String typeName) {
        if (this.getTax() == null) {
            return 0.0;
        }
        double defaultTax = this.getTax().getRate();
        if (typeName == null) {
            return defaultTax;
        }
        String taxProp = this.getProperty(this.getStringWithUnderScore(typeName, "_TAX"));
        if (taxProp == null) {
            return defaultTax;
        }
        try {
            return Double.parseDouble(taxProp);
        }
        catch (Exception e) {
            return defaultTax;
        }
    }

    public String getStringWithUnderScore(String orderType, String additionalString) {
        orderType = orderType.replaceAll(" ", "_");
        return orderType + additionalString;
    }

    public String getStringWithOutUnderScore(String orderType, String regex) {
        orderType = orderType.replaceAll(regex, "");
        orderType = orderType.replaceAll("_", " ");
        return orderType;
    }

    public String replaceString(String orderType, String regex, String replacement) {
        orderType = orderType.replaceAll(regex, replacement);
        return orderType;
    }

    public Set<MenuItemSize> getSizes() {
        HashSet<MenuItemSize> sizes = new HashSet<MenuItemSize>();
        List<PizzaPrice> priceList = this.getPizzaPriceList();
        if (priceList != null) {
            for (PizzaPrice pizzaPrice : priceList) {
                sizes.add(pizzaPrice.getSize());
            }
        }
        return sizes;
    }

    public Set<PizzaCrust> getCrustsForSize(MenuItemSize size) {
        HashSet<PizzaCrust> crusts = new HashSet<PizzaCrust>();
        List<PizzaPrice> priceList = this.getPizzaPriceList();
        if (priceList != null) {
            for (PizzaPrice pizzaPrice : priceList) {
                if (!size.equals(pizzaPrice.getSize())) continue;
                crusts.add(pizzaPrice.getCrust());
            }
        }
        return crusts;
    }

    public Set<PizzaPrice> getAvailablePrices(MenuItemSize size) {
        HashSet<PizzaPrice> prices = new HashSet<PizzaPrice>();
        List<PizzaPrice> priceList = this.getPizzaPriceList();
        if (priceList != null) {
            for (PizzaPrice pizzaPrice : priceList) {
                if (!size.equals(pizzaPrice.getSize())) continue;
                prices.add(pizzaPrice);
            }
        }
        return prices;
    }

    public MenuItem clone(MenuItem source) throws Exception {
        MenuItem menuItem = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(source);
        out.flush();
        out.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        menuItem = (MenuItem)in.readObject();
        in.close();
        return menuItem;
    }
}


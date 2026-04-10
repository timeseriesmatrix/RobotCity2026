/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util.datamigrate;

import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.util.DatabaseUtil;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

public class DbMigrator {
    public static void main(String[] args) throws Exception {
        DatabaseUtil.initialize();
        JAXBContext jaxbContext = JAXBContext.newInstance(MenuItem.class);
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty("jaxb.formatted.output", Boolean.TRUE);
        m.setProperty("jaxb.fragment", Boolean.TRUE);
        List<MenuItem> list = MenuItemDAO.getInstance().findAll();
        MenuList list2 = new MenuList();
        for (MenuItem menuItem : list) {
            MenuItem menuItem2 = MenuItemDAO.getInstance().initialize(menuItem);
            list2.add(menuItem2);
            m.marshal((Object)menuItem2, System.out);
        }
        Unmarshaller um = jaxbContext.createUnmarshaller();
    }

    @XmlRootElement(name="menu-items")
    static class MenuList
    extends ArrayList {
        MenuList() {
        }
    }
}


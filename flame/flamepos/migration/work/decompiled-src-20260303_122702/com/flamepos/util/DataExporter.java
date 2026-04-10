/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao.MenuModifierGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataExporter {
    public static void main(String[] args) throws Exception {
        _RootDAO.initialize();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("default-data.obj"));
        DataExporter.write(MenuModifierGroupDAO.getInstance().findAll(), out);
        DataExporter.write(MenuModifierDAO.getInstance().findAll(), out);
        DataExporter.write(MenuCategoryDAO.getInstance().findAll(), out);
        DataExporter.write(MenuGroupDAO.getInstance().findAll(), out);
        DataExporter.write(MenuItemDAO.getInstance().findAll(), out);
        out.close();
    }

    private static void write(List list, ObjectOutputStream out) throws Exception {
        out.writeObject(list);
    }
}


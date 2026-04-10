/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.bo.actions;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.dao.GenericDAO;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.MenuItemModifierGroupDAO;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao.MenuModifierGroupDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.datamigrate.Elements;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataExportAction
extends AbstractAction {
    public DataExportAction() {
        super(Messages.getString("DataExportAction.0"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Session session = null;
        Transaction transaction = null;
        FileWriter fileWriter = null;
        GenericDAO dao = new GenericDAO();
        try {
            JFileChooser fileChooser = DataExportAction.getFileChooser();
            int option = fileChooser.showSaveDialog(POSUtil.getBackOfficeWindow());
            if (option != 0) {
                return;
            }
            File file = fileChooser.getSelectedFile();
            if (file.exists() && (option = JOptionPane.showConfirmDialog(POSUtil.getFocusedWindow(), Messages.getString("DataExportAction.1") + file.getName() + "?", Messages.getString("DataExportAction.3"), 0)) != 0) {
                return;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(Elements.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty("jaxb.formatted.output", Boolean.TRUE);
            m.setProperty("jaxb.fragment", Boolean.TRUE);
            StringWriter writer = new StringWriter();
            session = dao.createNewSession();
            transaction = session.beginTransaction();
            Elements elements = new Elements();
            elements.setTaxes(TaxDAO.getInstance().findAll(session));
            elements.setMenuCategories(MenuCategoryDAO.getInstance().findAll(session));
            elements.setMenuGroups(MenuGroupDAO.getInstance().findAll(session));
            elements.setMenuModifiers(MenuModifierDAO.getInstance().findAll(session));
            elements.setMenuModifierGroups(MenuModifierGroupDAO.getInstance().findAll(session));
            elements.setMenuItems(MenuItemDAO.getInstance().findAll(session));
            elements.setMenuItemModifierGroups(MenuItemModifierGroupDAO.getInstance().findAll(session));
            m.marshal((Object)elements, writer);
            transaction.commit();
            fileWriter = new FileWriter(file);
            fileWriter.write(writer.toString());
            fileWriter.close();
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("DataExportAction.4"));
            IOUtils.closeQuietly((Writer)fileWriter);
            dao.closeSession(session);
        }
        catch (Exception e1) {
            transaction.rollback();
            PosLog.error(this.getClass(), e1);
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), e1.getMessage());
        }
        finally {
            IOUtils.closeQuietly(fileWriter);
            dao.closeSession(session);
        }
    }

    public static JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(0);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectedFile(new File("floreantpos-menu-items.xml"));
        fileChooser.setFileFilter(new FileFilter(){

            @Override
            public String getDescription() {
                return "XML File";
            }

            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".xml");
            }
        });
        return fileChooser;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fixMenuItemModifierGroups() {
        MenuItemModifierGroupDAO menuItemModifierGroupDAO = MenuItemModifierGroupDAO.getInstance();
        Session session = menuItemModifierGroupDAO.createNewSession();
        Transaction transaction = session.beginTransaction();
        try {
            List menuItems = MenuItemDAO.getInstance().findAll(session);
            for (MenuItem menuItem : menuItems) {
                List<MenuItemModifierGroup> modiferGroups = menuItem.getMenuItemModiferGroups();
                for (MenuItemModifierGroup menuItemModifierGroup : modiferGroups) {
                    menuItemModifierGroupDAO.saveOrUpdate(menuItemModifierGroup, session);
                }
            }
            transaction.commit();
        }
        catch (Exception x) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        finally {
            menuItemModifierGroupDAO.closeSession(session);
        }
    }
}


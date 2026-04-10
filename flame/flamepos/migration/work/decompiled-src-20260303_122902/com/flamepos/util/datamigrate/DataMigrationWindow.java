/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.classic.Session
 */
package com.floreantpos.util.datamigrate;

import com.floreantpos.Database;
import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao._RootDAO;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.util.datamigrate.DbPanel;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

public class DataMigrationWindow
extends JFrame {
    private Database sourceDatabase;
    private Database destDatabase;
    private DbPanel sourceDbPanel = new DbPanel();
    private DbPanel destDbPanel = new DbPanel();
    private String sourceConnectString;
    private String destConnectString;

    public DataMigrationWindow() {
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("DataMigrationWindow.0"));
        this.getContentPane().add((Component)titlePanel, "North");
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "South");
        JPanel centerPanel = new JPanel();
        this.getContentPane().add((Component)centerPanel, "Center");
        centerPanel.setLayout(new GridLayout(1, 0, 10, 10));
        this.sourceDbPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("DataMigrationWindow.1")));
        this.destDbPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("DataMigrationWindow.2")));
        centerPanel.add(this.sourceDbPanel);
        centerPanel.add(this.destDbPanel);
        JButton btnMigrate = new JButton(Messages.getString("DataMigrationWindow.3"));
        btnMigrate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DataMigrationWindow.this.migrate();
            }
        });
        panel.add(btnMigrate);
    }

    protected void migrate() {
        this.sourceDatabase = this.sourceDbPanel.getDatabase();
        this.destDatabase = this.destDbPanel.getDatabase();
        this.sourceConnectString = this.sourceDbPanel.getConnectString();
        this.destConnectString = this.destDbPanel.getConnectString();
        Configuration sourceConfiguration = _RootDAO.getNewConfiguration(null);
        sourceConfiguration = sourceConfiguration.setProperty("hibernate.dialect", this.sourceDatabase.getHibernateDialect());
        sourceConfiguration = sourceConfiguration.setProperty("hibernate.connection.driver_class", this.sourceDatabase.getHibernateConnectionDriverClass());
        sourceConfiguration = sourceConfiguration.setProperty("hibernate.connection.url", this.sourceConnectString);
        sourceConfiguration = sourceConfiguration.setProperty("hibernate.connection.username", this.sourceDbPanel.getUser());
        sourceConfiguration = sourceConfiguration.setProperty("hibernate.connection.password", this.sourceDbPanel.getPass());
        Configuration destConfiguration = _RootDAO.getNewConfiguration(null);
        destConfiguration = destConfiguration.setProperty("hibernate.dialect", this.destDatabase.getHibernateDialect());
        destConfiguration = destConfiguration.setProperty("hibernate.connection.driver_class", this.destDatabase.getHibernateConnectionDriverClass());
        destConfiguration = destConfiguration.setProperty("hibernate.connection.url", this.destConnectString);
        destConfiguration = destConfiguration.setProperty("hibernate.connection.username", this.destDbPanel.getUser());
        destConfiguration = destConfiguration.setProperty("hibernate.connection.password", this.destDbPanel.getPass());
        SessionFactory sourceSessionFactory = sourceConfiguration.buildSessionFactory();
        SessionFactory destSessionFactory = destConfiguration.buildSessionFactory();
        Session sourceSession = sourceSessionFactory.openSession();
        Session destSession = destSessionFactory.openSession();
        Transaction transaction = destSession.beginTransaction();
        List categories = MenuCategoryDAO.getInstance().findAll((org.hibernate.Session)sourceSession);
        for (MenuCategory menuCategory : categories) {
            MenuCategory m = new MenuCategory();
            m.setName(menuCategory.getName());
            m.setTranslatedName(menuCategory.getTranslatedName());
            m.setBeverage(menuCategory.isBeverage());
            m.setVisible(menuCategory.isVisible());
            PosLog.info(DataMigrationWindow.class, "" + menuCategory);
            MenuCategoryDAO.getInstance().save(m, (org.hibernate.Session)destSession);
        }
        transaction.commit();
        destSession.close();
        PosLog.info(this.getClass(), "done");
    }

    public static void main(String[] args) {
        DataMigrationWindow window = new DataMigrationWindow();
        window.setSize(800, 600);
        window.setDefaultCloseOperation(3);
        window.setVisible(true);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PrinterConfiguration;
import com.floreantpos.model.dao.PrinterConfigurationDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePrinterConfigurationDAO
extends _RootDAO {
    public static PrinterConfigurationDAO instance;

    public static PrinterConfigurationDAO getInstance() {
        if (null == instance) {
            instance = new PrinterConfigurationDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PrinterConfiguration.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PrinterConfiguration cast(Object object) {
        return (PrinterConfiguration)object;
    }

    public PrinterConfiguration get(Integer key) {
        return (PrinterConfiguration)this.get(this.getReferenceClass(), key);
    }

    public PrinterConfiguration get(Integer key, Session s) {
        return (PrinterConfiguration)this.get(this.getReferenceClass(), key, s);
    }

    public PrinterConfiguration load(Integer key) {
        return (PrinterConfiguration)this.load(this.getReferenceClass(), key);
    }

    public PrinterConfiguration load(Integer key, Session s) {
        return (PrinterConfiguration)this.load(this.getReferenceClass(), key, s);
    }

    public PrinterConfiguration loadInitialize(Integer key, Session s) {
        PrinterConfiguration obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PrinterConfiguration> findAll() {
        return super.findAll();
    }

    @Override
    public List<PrinterConfiguration> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PrinterConfiguration> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PrinterConfiguration printerConfiguration) {
        return (Integer)super.save(printerConfiguration);
    }

    public Integer save(PrinterConfiguration printerConfiguration, Session s) {
        return (Integer)this.save((Object)printerConfiguration, s);
    }

    public void saveOrUpdate(PrinterConfiguration printerConfiguration) {
        this.saveOrUpdate((Object)printerConfiguration);
    }

    public void saveOrUpdate(PrinterConfiguration printerConfiguration, Session s) {
        this.saveOrUpdate((Object)printerConfiguration, s);
    }

    public void update(PrinterConfiguration printerConfiguration) {
        this.update((Object)printerConfiguration);
    }

    public void update(PrinterConfiguration printerConfiguration, Session s) {
        this.update((Object)printerConfiguration, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PrinterConfiguration printerConfiguration) {
        this.delete((Object)printerConfiguration);
    }

    public void delete(PrinterConfiguration printerConfiguration, Session s) {
        this.delete((Object)printerConfiguration, s);
    }

    public void refresh(PrinterConfiguration printerConfiguration, Session s) {
        this.refresh((Object)printerConfiguration, s);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 */
package com.floreantpos.model;

import com.floreantpos.PosLog;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.base.BaseShopFloor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Set;
import org.hibernate.Hibernate;

public class ShopFloor
extends BaseShopFloor {
    private static final long serialVersionUID = 1L;
    private byte[] imageData;

    public ShopFloor() {
    }

    public ShopFloor(Integer id) {
        super(id);
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    @Override
    public void setImage(Blob image) {
        try {
            this.imageData = this.toByteArray(image);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    @Override
    public Blob getImage() {
        return Hibernate.createBlob((byte[])this.imageData);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] toByteArray(Blob fromBlob) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] byArray = this.toByteArrayImpl(fromBlob, baos);
            return byArray;
        }
        finally {
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] toByteArrayImpl(Blob fromBlob, ByteArrayOutputStream baos) throws SQLException, IOException {
        byte[] buf = new byte[4000];
        InputStream is = fromBlob.getBinaryStream();
        try {
            int dataSize;
            while ((dataSize = is.read(buf)) != -1) {
                baos.write(buf, 0, dataSize);
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
        return baos.toByteArray();
    }

    public boolean hasTableWithNumber(String number) {
        Set<ShopTable> tables = this.getTables();
        if (tables == null) {
            return false;
        }
        for (ShopTable shopTable : tables) {
            if (!shopTable.getTableNumber().equals(number)) continue;
            return true;
        }
        return false;
    }
}


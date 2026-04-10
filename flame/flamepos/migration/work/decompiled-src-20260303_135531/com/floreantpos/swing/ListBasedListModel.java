/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;

public class ListBasedListModel<E>
extends AbstractListModel<E> {
    private List<E> dataList;

    public ListBasedListModel() {
    }

    public ListBasedListModel(List list) {
        this.dataList = list;
    }

    @Override
    public int getSize() {
        if (this.dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    @Override
    public E getElementAt(int index) {
        if (this.dataList == null) {
            return null;
        }
        return this.dataList.get(index);
    }

    public void addElement(E element) {
        this.ensureListNotNull();
        this.dataList.add(element);
    }

    public List<E> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    public Iterator<E> iterator() {
        this.ensureListNotNull();
        return this.dataList.iterator();
    }

    private void ensureListNotNull() {
        if (this.dataList == null) {
            this.dataList = new ArrayList();
        }
    }

    public void clearAll() {
        Iterator<E> iterator = this.dataList.iterator();
        while (iterator.hasNext()) {
            E value = iterator.next();
            if (value == null) continue;
            iterator.remove();
        }
    }

    public void clearItem(E item) {
        Iterator<E> iterator = this.dataList.iterator();
        while (iterator.hasNext()) {
            E value = iterator.next();
            if (value != item) continue;
            iterator.remove();
        }
    }
}


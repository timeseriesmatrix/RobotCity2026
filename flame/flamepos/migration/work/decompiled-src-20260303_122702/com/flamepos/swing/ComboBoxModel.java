/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ComboBoxModel
extends AbstractListModel
implements MutableComboBoxModel,
Serializable,
ListDataListener {
    private List dataList;
    private Object selectedObject;

    public ComboBoxModel() {
        this(new ArrayList());
    }

    public ComboBoxModel(List list) {
        this.setDataList(list);
        if (this.getSize() > 0) {
            this.selectedObject = this.getElementAt(0);
        }
    }

    public void setDataList(List list) {
        int size = 0;
        if (list != null) {
            size = list.size();
        }
        this.dataList = list;
        this.fireContentsChanged(this, 0, size);
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if (this.selectedObject != null && !this.selectedObject.equals(anObject) || this.selectedObject == null && anObject != null) {
            this.selectedObject = anObject;
            this.fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return this.selectedObject;
    }

    @Override
    public int getSize() {
        if (this.dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    @Override
    public Object getElementAt(int index) {
        if (this.dataList != null) {
            if (index >= 0 && index < this.dataList.size()) {
                return this.dataList.get(index);
            }
            return null;
        }
        return null;
    }

    public int getIndexOf(Object anObject) {
        return this.dataList.indexOf(anObject);
    }

    public void addElement(Object anObject) {
        this.dataList.add(anObject);
        this.fireIntervalAdded(this, this.dataList.size() - 1, this.dataList.size() - 1);
        if (this.dataList.size() == 1 && this.selectedObject == null && anObject != null) {
            this.setSelectedItem(anObject);
        }
    }

    public void insertElementAt(Object anObject, int index) {
        if (this.dataList == null) {
            this.dataList = new ArrayList();
        }
        this.dataList.add(index, anObject);
        this.fireIntervalAdded(this, index, index);
    }

    @Override
    public void removeElementAt(int index) {
        if (this.dataList == null) {
            return;
        }
        if (this.getElementAt(index) == this.selectedObject) {
            if (index == 0) {
                this.setSelectedItem(this.getSize() == 1 ? null : this.getElementAt(index + 1));
            } else {
                this.setSelectedItem(this.getElementAt(index - 1));
            }
        }
        this.dataList.remove(index);
        this.fireIntervalRemoved(this, index, index);
    }

    @Override
    public void removeElement(Object anObject) {
        if (this.dataList == null) {
            return;
        }
        int index = this.dataList.indexOf(anObject);
        if (index != -1) {
            this.removeElementAt(index);
        }
    }

    public void removeAllElements() {
        if (this.dataList == null) {
            return;
        }
        if (this.dataList.size() > 0) {
            int firstIndex = 0;
            int lastIndex = this.dataList.size() - 1;
            this.dataList.clear();
            this.selectedObject = null;
            this.fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
            this.selectedObject = null;
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();
        this.fireIntervalAdded(this, index0, index1);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();
        this.fireIntervalRemoved(this, index0, index1);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();
        this.fireContentsChanged(this, index0, index1);
    }

    public List getDataList() {
        return this.dataList;
    }
}


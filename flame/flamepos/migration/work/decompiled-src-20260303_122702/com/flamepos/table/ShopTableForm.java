/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.StaleObjectStateException
 */
package com.floreantpos.table;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.ShopTableType;
import com.floreantpos.model.TableBookingInfo;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.ShopTableTypeDAO;
import com.floreantpos.model.dao.TableBookingInfoDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.hibernate.StaleObjectStateException;

public class ShopTableForm
extends BeanEditor<ShopTable> {
    private FixedLengthTextField tfTableDescription;
    private IntegerTextField tfTableCapacity;
    private IntegerTextField tfTableNo;
    private FixedLengthTextField tfTableName;
    private CheckBoxList tableTypeCBoxList;
    private JPanel statusPanel;
    private JRadioButton rbFree;
    private JRadioButton rbDisable;
    private JButton btnCapacityOne;
    private JButton btnCapacityTwo;
    private JButton btnCapacityFour;
    private JButton btnCapacitySix;
    private JButton btnCapacityEight;
    private JButton btnCapacityTen;
    private JButton btnCreateType;
    private int newTable;
    private boolean dupTableEnable;
    private boolean dupTableDisable;
    private String dupName;
    private Integer dupCapacity;
    private String dupDescription;
    private List<ShopTableType> dupCheckValues;
    private int selectedTable;
    private boolean duplicate;

    public ShopTableForm() {
        this.setPreferredSize(new Dimension(600, 800));
        this.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][][][][][][][]"));
        this.setBorder(BorderFactory.createTitledBorder(Messages.getString("ShopTableForm.19")));
        this.tableTypeCBoxList = new CheckBoxList();
        this.tableTypeCBoxList.setModel(ShopTableTypeDAO.getInstance().findAll());
        JScrollPane tableTypeCheckBoxList = new JScrollPane(this.tableTypeCBoxList);
        tableTypeCheckBoxList.setPreferredSize(new Dimension(0, 350));
        JLabel lblName = new JLabel(Messages.getString("ShopTableForm.0"));
        this.add((Component)lblName, "cell 0 0,alignx trailing,aligny center");
        this.tfTableNo = new IntegerTextField(6);
        this.add((Component)this.tfTableNo, "cell 1 0,aligny top");
        this.tfTableName = new FixedLengthTextField();
        JLabel lblAddress = new JLabel(Messages.getString("ShopTableForm.2"));
        this.add((Component)lblAddress, "cell 0 2,alignx trailing");
        this.tfTableDescription = new FixedLengthTextField();
        this.add((Component)this.tfTableDescription, "cell 1 2,growx");
        JLabel lblCitytown = new JLabel(Messages.getString("ShopTableForm.3"));
        this.add((Component)lblCitytown, "cell 0 3,alignx trailing");
        this.tfTableCapacity = new IntegerTextField(6);
        this.add((Component)this.tfTableCapacity, "flowx,grow,cell 1 3");
        ActionListener action = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == ShopTableForm.this.btnCapacityOne) {
                    ShopTableForm.this.tfTableCapacity.setText("1");
                } else if (e.getSource() == ShopTableForm.this.btnCapacityTwo) {
                    ShopTableForm.this.tfTableCapacity.setText("2");
                } else if (e.getSource() == ShopTableForm.this.btnCapacityFour) {
                    ShopTableForm.this.tfTableCapacity.setText("4");
                } else if (e.getSource() == ShopTableForm.this.btnCapacitySix) {
                    ShopTableForm.this.tfTableCapacity.setText("6");
                } else if (e.getSource() == ShopTableForm.this.btnCapacityEight) {
                    ShopTableForm.this.tfTableCapacity.setText("8");
                } else if (e.getSource() == ShopTableForm.this.btnCapacityTen) {
                    ShopTableForm.this.tfTableCapacity.setText("10");
                }
            }
        };
        this.btnCapacityOne = new PosButton("1");
        this.btnCapacityOne.setPreferredSize(new Dimension(52, 52));
        this.btnCapacityTwo = new PosButton("2");
        this.btnCapacityTwo.setPreferredSize(new Dimension(52, 52));
        this.btnCapacityFour = new PosButton("4");
        this.btnCapacityFour.setPreferredSize(new Dimension(52, 52));
        this.btnCapacitySix = new PosButton("6");
        this.btnCapacitySix.setPreferredSize(new Dimension(52, 52));
        this.btnCapacityEight = new PosButton("8");
        this.btnCapacityEight.setPreferredSize(new Dimension(52, 52));
        this.btnCapacityTen = new PosButton("10");
        this.btnCapacityTen.setPreferredSize(new Dimension(52, 52));
        this.btnCapacityOne.addActionListener(action);
        this.btnCapacityTwo.addActionListener(action);
        this.btnCapacityFour.addActionListener(action);
        this.btnCapacitySix.addActionListener(action);
        this.btnCapacityEight.addActionListener(action);
        this.btnCapacityTen.addActionListener(action);
        this.add((Component)this.btnCapacityOne, "cell 1 3");
        this.add((Component)this.btnCapacityTwo, "cell 1 3");
        this.add((Component)this.btnCapacityFour, "cell 1 3");
        this.add((Component)this.btnCapacitySix, "cell 1 3");
        this.add((Component)this.btnCapacityEight, "cell 1 3");
        this.add((Component)this.btnCapacityTen, "cell 1 3");
        this.statusPanel = new JPanel();
        this.statusPanel.setBorder(new TitledBorder(null, Messages.getString("ShopTableForm.4"), 4, 2, null, null));
        this.add((Component)this.statusPanel, "cell 1 4,grow");
        this.statusPanel.setLayout(new FlowLayout(0, 5, 5));
        this.rbFree = new JRadioButton(Messages.getString("ShopTableForm.5"));
        this.statusPanel.add(this.rbFree);
        this.rbDisable = new JRadioButton(Messages.getString("ShopTableForm.9"));
        this.statusPanel.add(this.rbDisable);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.rbFree);
        buttonGroup.add(this.rbDisable);
        this.add((Component)new JLabel(), "grow,span");
        final FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);
        if (floorLayoutPlugin != null) {
            this.btnCreateType = new JButton(Messages.getString("ShopTableForm.40"));
            this.add((Component)new JLabel(Messages.getString("ShopTableForm.10")), "cell 0 5");
            this.add((Component)tableTypeCheckBoxList, "cell 1 5,wrap,grow");
            this.add((Component)this.btnCreateType, "cell 1 6");
            this.btnCreateType.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), floorLayoutPlugin.getBeanEditor());
                    dialog.open();
                    ShopTableForm.this.tableTypeCBoxList.setModel(ShopTableTypeDAO.getInstance().findAll());
                }
            });
        }
    }

    @Override
    public void createNew() {
        ShopTable bean2 = new ShopTable();
        this.setBean(bean2);
        int nxtTableNumber = ShopTableDAO.getInstance().getNextTableNumber();
        if (nxtTableNumber == 0) {
            nxtTableNumber = 1;
        }
        for (int i = 1; i <= nxtTableNumber + 1; ++i) {
            ShopTable shopTable = ShopTableDAO.getInstance().get(i);
            if (shopTable != null) continue;
            this.tfTableNo.setText(String.valueOf(i));
            break;
        }
        this.tfTableCapacity.setText("4");
        this.tfTableDescription.setText("");
        this.tfTableName.setText("");
        this.setBorder(BorderFactory.createTitledBorder(Messages.getString("ShopTableForm.18")));
    }

    @Override
    public void cancel() {
        this.setBorder(BorderFactory.createTitledBorder(Messages.getString("ShopTableForm.46")));
    }

    @Override
    public void clearFields() {
        this.tfTableNo.setText("");
        this.tfTableCapacity.setText("");
        this.tfTableDescription.setText("");
        this.tfTableName.setText("");
        this.tableTypeCBoxList.unCheckAll();
        this.rbFree.setSelected(false);
        this.rbDisable.setSelected(false);
    }

    @Override
    public boolean delete() {
        try {
            ShopTable bean2 = (ShopTable)this.getBean();
            if (bean2 == null) {
                return false;
            }
            int option = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getBackOfficeWindow(), Messages.getString("ShopTableForm.14"), Messages.getString("ShopTableForm.15"));
            if (option != 0) {
                return false;
            }
            List<TableBookingInfo> bookingList = TableBookingInfoDAO.getInstance().findAll();
            block2: for (TableBookingInfo info : bookingList) {
                List<ShopTable> tableList = info.getTables();
                for (ShopTable shopTable : tableList) {
                    if (!shopTable.getId().equals(bean2.getId())) continue;
                    tableList.remove(shopTable);
                    info.setTables(tableList);
                    TableBookingInfoDAO.getInstance().saveOrUpdate(info);
                    continue block2;
                }
            }
            ShopTableDAO.getInstance().delete(bean2);
            this.tfTableCapacity.setText("");
            this.tfTableDescription.setText("");
            this.tfTableName.setText("");
            this.tfTableNo.setText("");
            this.tableTypeCBoxList.unCheckAll();
            return true;
        }
        catch (Exception e) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteAllTables() {
        List<ShopTable> list = ShopTableDAO.getInstance().findAll();
        if (list.isEmpty()) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), Messages.getString("ShopTableForm.51"));
            return false;
        }
        int option = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getBackOfficeWindow(), Messages.getString("ShopTableForm.20"), Messages.getString("ShopTableForm.21"));
        if (option != 0) {
            return false;
        }
        List<TableBookingInfo> bookingList = TableBookingInfoDAO.getInstance().findAll();
        for (TableBookingInfo info : bookingList) {
            info.setTables(null);
            TableBookingInfoDAO.getInstance().saveOrUpdate(info);
        }
        for (ShopTable table : list) {
            table.setFloor(null);
            table.setTypes(null);
            ShopTableDAO.getInstance().delete(table);
        }
        this.tfTableNo.setText("");
        this.tfTableCapacity.setText("");
        this.tfTableDescription.setText("");
        this.tfTableName.setText("");
        this.tableTypeCBoxList.unCheckAll();
        return true;
    }

    public void setFieldsEditable(boolean editable) {
        this.tfTableName.setEditable(editable);
        this.tfTableDescription.setEditable(editable);
        this.tfTableCapacity.setEditable(editable);
    }

    @Override
    public void setFieldsEnable(boolean enable) {
        this.tableTypeCBoxList.setEnabled(enable);
        this.tableTypeCBoxList.clearSelection();
        this.tfTableNo.setEnabled(enable);
        this.tfTableName.setEnabled(enable);
        this.tfTableDescription.setEnabled(enable);
        this.tfTableCapacity.setEnabled(enable);
        this.btnCapacityOne.setEnabled(enable);
        this.btnCapacityTwo.setEnabled(enable);
        this.btnCapacityFour.setEnabled(enable);
        this.btnCapacitySix.setEnabled(enable);
        this.btnCapacityEight.setEnabled(enable);
        this.btnCapacityTen.setEnabled(enable);
        if (this.btnCreateType != null) {
            this.btnCreateType.setEnabled(enable);
        }
        this.rbFree.setEnabled(enable);
        this.rbDisable.setEnabled(enable);
    }

    public void setOnlyStatusEnable() {
        this.tfTableNo.setEditable(false);
        this.tfTableName.setEditable(false);
        this.tfTableDescription.setEditable(false);
        this.tfTableCapacity.setEditable(false);
        this.btnCapacityOne.setVisible(false);
        this.btnCapacityTwo.setVisible(false);
        this.btnCapacityFour.setVisible(false);
        this.btnCapacitySix.setVisible(false);
        this.btnCapacityEight.setVisible(false);
        this.btnCapacityTen.setVisible(false);
        this.tableTypeCBoxList.setEnabled(false);
        if (this.btnCreateType != null) {
            this.btnCreateType.setVisible(false);
        }
        this.rbFree.setEnabled(true);
        this.rbDisable.setEnabled(true);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            ShopTable table = (ShopTable)this.getBean();
            ShopTableDAO.getInstance().saveOrUpdate(table);
            this.updateView();
            return true;
        }
        catch (IllegalModelStateException table) {
        }
        catch (StaleObjectStateException e) {
            BOMessageDialog.showError(this, Messages.getString("ShopTableForm.16"));
        }
        return false;
    }

    @Override
    protected void updateView() {
        ShopTable table = (ShopTable)this.getBean();
        if (table == null) {
            return;
        }
        this.tableTypeCBoxList.setModel(ShopTableTypeDAO.getInstance().findAll());
        this.tableTypeCBoxList.selectItems(table.getTypes());
        this.tfTableNo.setText(String.valueOf(table.getTableNumber()));
        this.tfTableName.setText(table.getName());
        this.tfTableDescription.setText(table.getDescription());
        this.tfTableCapacity.setText(String.valueOf(table.getCapacity()));
        this.rbFree.setSelected(true);
        this.rbDisable.setSelected(table.isDisable());
        if (table.getTableNumber() != null) {
            this.selectedTable = table.getTableNumber();
        }
        if (!this.isDuplicateOn()) {
            List checkValues = this.tableTypeCBoxList.getCheckedValues();
            this.dupCheckValues = checkValues;
            this.dupCapacity = table.getCapacity();
            this.dupDescription = table.getDescription();
            this.dupName = table.getName();
            this.dupTableEnable = this.rbFree.isSelected();
            this.dupTableDisable = this.rbDisable.isSelected();
        }
        this.setBorder(BorderFactory.createTitledBorder(Messages.getString("ShopTableForm.56")));
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        ShopTable table = (ShopTable)this.getBean();
        if (table == null) {
            table = new ShopTable();
            this.setBean(table, false);
        }
        if (!this.isDuplicateOn() && this.tfTableNo.getInteger() == 0) {
            POSMessageDialog.showError(null, Messages.getString("ShopTableForm.57"));
            return false;
        }
        ShopTable tableTocheck = ShopTableDAO.getInstance().get(this.tfTableNo.getInteger());
        if (tableTocheck != null && this.selectedTable != tableTocheck.getId()) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), Messages.getString("ShopTableForm.58"));
            return false;
        }
        if (this.isDuplicateOn()) {
            int nxtTableNumber = ShopTableDAO.getInstance().getNextTableNumber();
            for (int i = 1; i <= nxtTableNumber; ++i) {
                ShopTable shopTable = ShopTableDAO.getInstance().get(i);
                if (shopTable != null) continue;
                table.setId(i);
                break;
            }
            if (table.getId() == null) {
                table.setId(nxtTableNumber + 1);
            }
            table.setTypes(this.dupCheckValues);
            table.setCapacity(this.dupCapacity);
            table.setDescription(this.dupDescription);
            table.setName(this.dupName);
            table.setFree(this.dupTableEnable);
            table.setDisable(this.dupTableDisable);
            this.setDuplicate(false);
        } else {
            table.setId(this.tfTableNo.getInteger());
            table.setName(this.tfTableName.getText());
            table.setDescription(this.tfTableDescription.getText());
            table.setCapacity(this.tfTableCapacity.getInteger());
            List<ShopTableType> checkValues = this.tableTypeCBoxList.getCheckedValues();
            table.setTypes(checkValues);
            if (this.rbFree.isSelected()) {
                table.setFree(true);
                table.setServing(false);
                table.setBooked(false);
                table.setDirty(false);
                table.setDisable(false);
            } else if (this.rbDisable.isSelected()) {
                table.setFree(false);
                table.setServing(false);
                table.setBooked(false);
                table.setDirty(false);
                table.setDisable(true);
            }
        }
        this.setNewTable(table.getId());
        return true;
    }

    @Override
    public void edit() {
        this.setBorder(BorderFactory.createTitledBorder(Messages.getString("ShopTableForm.17")));
    }

    @Override
    public String getDisplayText() {
        return Messages.getString("ShopTableForm.18");
    }

    public void setTableTypeCBoxListEnable(boolean enable) {
        this.tableTypeCBoxList.setEnabled(enable);
    }

    public boolean isDuplicateOn() {
        return this.duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getNewTable() {
        return this.newTable;
    }

    public void setNewTable(int newTable) {
        this.newTable = newTable;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.DateChoserDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

public class AttendanceHistoryExplorer
extends TransparentPanel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM,dd  hh:mm a");
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JButton btnGo = new JButton(POSConstants.GO);
    private JButton btnAdd = new JButton(Messages.getString("AttendanceHistoryExplorer.0"));
    private JButton btnEdit = new JButton(Messages.getString("AttendanceHistoryExplorer.1"));
    private JButton btnDelete = new JButton(Messages.getString("AttendanceHistoryExplorer.2"));
    private JButton btnPrint = new JButton(Messages.getString("AttendanceHistoryExplorer.3"));
    private JXTable table = new JXTable((TableModel)new AttendenceHistoryTableModel(AttendenceHistoryDAO.getInstance().findAll()));
    private JComboBox cbUserType;

    public AttendanceHistoryExplorer() {
        super(new BorderLayout());
        this.add(new JScrollPane((Component)this.table));
        this.table.getSelectionModel().setSelectionMode(0);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        this.cbUserType = new JComboBox();
        UserDAO dao = new UserDAO();
        List<User> userTypes = dao.findAll();
        Vector<Object> list = new Vector<Object>();
        list.add(POSConstants.ALL);
        list.addAll(userTypes);
        this.cbUserType.setModel(new DefaultComboBoxModel(list));
        topPanel.add((Component)new JLabel(POSConstants.START_DATE), "grow");
        topPanel.add((Component)this.fromDatePicker);
        topPanel.add((Component)new JLabel(POSConstants.END_DATE), "grow");
        topPanel.add((Component)this.toDatePicker);
        topPanel.add(new JLabel(POSConstants.USER + ":"));
        topPanel.add(this.cbUserType);
        topPanel.add((Component)this.btnGo, "skip 1, al right");
        this.add((Component)topPanel, "North");
        JPanel bottomPanel = new JPanel(new FlowLayout(1));
        bottomPanel.add(this.btnAdd);
        bottomPanel.add(this.btnEdit);
        bottomPanel.add(this.btnDelete);
        this.add((Component)bottomPanel, "South");
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    AttendanceHistoryExplorer.this.viewReport();
                }
                catch (Exception e1) {
                    BOMessageDialog.showError(AttendanceHistoryExplorer.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
        this.btnEdit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = AttendanceHistoryExplorer.this.table.getSelectedRow();
                if (selectedRow < 0) {
                    BOMessageDialog.showError(AttendanceHistoryExplorer.this, Messages.getString("AttendanceHistoryExplorer.4"));
                    return;
                }
                AttendenceHistoryTableModel model = (AttendenceHistoryTableModel)AttendanceHistoryExplorer.this.table.getModel();
                AttendenceHistory history = (AttendenceHistory)model.getRowData(selectedRow);
                DateChoserDialog dialog = new DateChoserDialog(history, Messages.getString("AttendanceHistoryExplorer.5"));
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                if (dialog.getAttendenceHistory() != null) {
                    history = dialog.getAttendenceHistory();
                }
                AttendenceHistoryDAO dao = new AttendenceHistoryDAO();
                dao.saveOrUpdate(history);
                model.updateItem(selectedRow);
            }
        });
        this.btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DateChoserDialog dialog = new DateChoserDialog(Messages.getString("AttendanceHistoryExplorer.6"));
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                AttendenceHistory history = null;
                if (dialog.getAttendenceHistory() != null) {
                    history = dialog.getAttendenceHistory();
                }
                AttendenceHistoryDAO dao = new AttendenceHistoryDAO();
                dao.saveOrUpdate(history);
                AttendenceHistoryTableModel model = (AttendenceHistoryTableModel)AttendanceHistoryExplorer.this.table.getModel();
                model.addItem(history);
            }
        });
        this.btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = AttendanceHistoryExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = AttendanceHistoryExplorer.this.table.convertRowIndexToModel(index);
                    AttendenceHistoryTableModel model = (AttendenceHistoryTableModel)AttendanceHistoryExplorer.this.table.getModel();
                    AttendenceHistory history = (AttendenceHistory)model.getRowData(index);
                    if (POSMessageDialog.showYesNoQuestionDialog(AttendanceHistoryExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    AttendenceHistoryDAO dao = new AttendenceHistoryDAO();
                    dao.delete(history);
                    model.deleteItem(index);
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
    }

    private void viewReport() {
        try {
            Date fromDate = this.fromDatePicker.getDate();
            Date toDate = this.toDatePicker.getDate();
            if (fromDate.after(toDate)) {
                POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(fromDate);
            calendar.set(1, calendar2.get(1));
            calendar.set(2, calendar2.get(2));
            calendar.set(5, calendar2.get(5));
            calendar.set(10, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            fromDate = calendar.getTime();
            calendar.clear();
            calendar2.setTime(toDate);
            calendar.set(1, calendar2.get(1));
            calendar.set(2, calendar2.get(2));
            calendar.set(5, calendar2.get(5));
            calendar.set(10, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            toDate = calendar.getTime();
            User user = null;
            if (!this.cbUserType.getSelectedItem().equals(POSConstants.ALL)) {
                user = (User)this.cbUserType.getSelectedItem();
            }
            AttendenceHistoryDAO dao = new AttendenceHistoryDAO();
            List<AttendenceHistory> historyList = dao.findHistory(fromDate, toDate, user);
            AttendenceHistoryTableModel model = (AttendenceHistoryTableModel)this.table.getModel();
            model.setRows(historyList);
        }
        catch (Exception e) {
            BOMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    class AttendenceHistoryTableModel
    extends ListTableModel {
        String[] columnNames = new String[]{"EMP ID", "EMP NAME", "CLOCK IN TIME", "CLOCK OUT TIME", "CLOCKED OUT", "SHIFT ID", "TERMINAL ID"};

        AttendenceHistoryTableModel(List<AttendenceHistory> list) {
            this.setRows(list);
            this.setColumnNames(this.columnNames);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AttendenceHistory history = (AttendenceHistory)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return history.getUser().getUserId();
                }
                case 1: {
                    return history.getUser().getFirstName() + " " + history.getUser().getLastName();
                }
                case 2: {
                    Date date = history.getClockInTime();
                    if (date != null) {
                        return AttendanceHistoryExplorer.this.dateFormat.format(date);
                    }
                    return "";
                }
                case 3: {
                    Date date2 = history.getClockOutTime();
                    if (date2 != null) {
                        return AttendanceHistoryExplorer.this.dateFormat.format(date2);
                    }
                    return "";
                }
                case 4: {
                    return history.isClockedOut();
                }
                case 5: {
                    if (history.getShift() == null) {
                        return "";
                    }
                    return history.getShift().getId();
                }
                case 6: {
                    return history.getTerminal().getId();
                }
            }
            return null;
        }
    }
}


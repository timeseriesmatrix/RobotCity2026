/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.swing.ListTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JournalReportModel {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private List<JournalReportData> reportDatas = new ArrayList<JournalReportData>();
    private JournalReportTableModel tableModel;

    public JournalReportTableModel getTableModel() {
        if (this.tableModel == null) {
            this.tableModel = new JournalReportTableModel(this.reportDatas);
        }
        return this.tableModel;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getReportTime() {
        return this.reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void addReportData(JournalReportData data) {
        this.reportDatas.add(data);
    }

    public static class JournalReportTableModel
    extends ListTableModel {
        public JournalReportTableModel(List<JournalReportData> datas) {
            super(new String[]{"refId", "time", "action", "user", "comment"}, datas);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            JournalReportData data = (JournalReportData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(data.getRefId());
                }
                case 1: {
                    return data.getTime();
                }
                case 2: {
                    return data.getAction();
                }
                case 3: {
                    return data.getUserInfo();
                }
                case 4: {
                    return data.getComments();
                }
            }
            return null;
        }
    }

    public static class JournalReportData {
        private Integer refId;
        private Date time;
        private String action;
        private String userInfo;
        private String comments;

        public String getAction() {
            return this.action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getComments() {
            return this.comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public Integer getRefId() {
            return this.refId;
        }

        public void setRefId(Integer refId) {
            this.refId = refId;
        }

        public Date getTime() {
            return this.time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getUserInfo() {
            return this.userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }
    }
}


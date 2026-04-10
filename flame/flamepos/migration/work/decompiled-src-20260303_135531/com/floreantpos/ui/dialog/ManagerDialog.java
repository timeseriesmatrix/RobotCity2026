/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.logging.LogFactory
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.TipsCashoutReport;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.GratuityDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.GlassPane;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.CashDropDialog;
import com.floreantpos.ui.dialog.DrawerPullReportDialog;
import com.floreantpos.ui.dialog.OpenTicketsListDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.TipsCashoutReportDialog;
import com.floreantpos.ui.util.UiUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;

public class ManagerDialog
extends JDialog {
    private GlassPane glassPane;
    private PosButton btnDrawerPullReport;
    private PosButton btnCashDrops;
    private PosButton btnFinish;
    private PosButton btnOpenTickets;
    private PosButton btnShowTips;
    private TitlePanel titlePanel1;
    private TransparentPanel transparentPanel1;
    private TransparentPanel transparentPanel2;
    private TransparentPanel transparentPanel3;
    private TransparentPanel transparentPanel4;
    private PosButton btnDrawerKick;

    public ManagerDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.initComponents();
        this.setIconImage(Application.getPosWindow().getIconImage());
        this.setTitle(Application.getTitle() + ": " + Messages.getString("ManagerDialog.2"));
        this.glassPane = new GlassPane();
        this.setGlassPane(this.glassPane);
    }

    public void setGlassPaneVisible(boolean b) {
        this.glassPane.setVisible(b);
    }

    private void initComponents() {
        this.titlePanel1 = new TitlePanel();
        this.transparentPanel4 = new TransparentPanel();
        this.transparentPanel2 = new TransparentPanel();
        this.transparentPanel3 = new TransparentPanel();
        this.btnShowTips = new PosButton();
        this.btnDrawerPullReport = new PosButton();
        this.btnOpenTickets = new PosButton();
        this.btnCashDrops = new PosButton();
        this.transparentPanel1 = new TransparentPanel();
        this.btnFinish = new PosButton();
        this.setDefaultCloseOperation(2);
        this.titlePanel1.setTitle(POSConstants.MANAGER_FUNCTION);
        this.getContentPane().add((Component)this.titlePanel1, "North");
        this.transparentPanel4.setLayout(new BorderLayout());
        this.transparentPanel4.setOpaque(true);
        this.transparentPanel2.setLayout(new GridBagLayout());
        this.transparentPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.transparentPanel3.setLayout(new GridLayout(3, 2, 5, 5));
        this.btnShowTips.setText(POSConstants.SERVER_TIPS);
        this.btnShowTips.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ManagerDialog.this.doShowServerTips(evt);
            }
        });
        this.transparentPanel3.add(this.btnShowTips);
        this.btnDrawerPullReport.setText(POSConstants.DRAWER_PULL_BUTTON_TEXT);
        this.btnDrawerPullReport.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ManagerDialog.this.btnDrawerPullReportActionPerformed(evt);
            }
        });
        this.transparentPanel3.add(this.btnDrawerPullReport);
        this.btnOpenTickets.setText(POSConstants.OPEN_TICKETS);
        this.btnOpenTickets.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ManagerDialog.this.doShowOpenTickets();
            }
        });
        this.transparentPanel3.add(this.btnOpenTickets);
        this.btnCashDrops.setText(POSConstants.DRAWER_BLEED);
        this.btnCashDrops.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ManagerDialog.this.doShowCashDrops();
            }
        });
        this.transparentPanel3.add(this.btnCashDrops);
        GridBagConstraints gbc_transparentPanel3 = new GridBagConstraints();
        gbc_transparentPanel3.insets = new Insets(0, 0, 5, 0);
        gbc_transparentPanel3.gridx = 0;
        gbc_transparentPanel3.gridy = 0;
        this.transparentPanel2.add((Component)this.transparentPanel3, gbc_transparentPanel3);
        this.btnDrawerKick = new PosButton();
        this.btnDrawerKick.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerDialog.this.doDrawerKick();
            }
        });
        this.btnDrawerKick.setText(Messages.getString("ManagerDialog.1"));
        this.transparentPanel3.add(this.btnDrawerKick);
        this.transparentPanel4.add((Component)this.transparentPanel2, "Center");
        this.transparentPanel1.setLayout(new FlowLayout(2));
        this.btnFinish.setText(POSConstants.FINISH);
        this.btnFinish.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ManagerDialog.this.doCloseDialog();
            }
        });
        this.transparentPanel1.add(this.btnFinish);
        this.transparentPanel4.add((Component)this.transparentPanel1, "South");
        this.getContentPane().add((Component)this.transparentPanel4, "Center");
        this.pack();
    }

    protected void doDrawerKick() {
        try {
            File file = new File(Application.getInstance().getLocation(), "drawer-kick.bat");
            if (file.exists()) {
                Runtime.getRuntime().exec(file.getAbsolutePath());
            }
        }
        catch (Exception e) {
            LogFactory.getLog(ManagerDialog.class).error((Object)e);
        }
    }

    private void doCloseDialog() {
        this.dispose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doShowCashDrops() {
        try {
            this.setGlassPaneVisible(true);
            CashDropDialog dialog = new CashDropDialog();
            dialog.initDate();
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
        finally {
            this.setGlassPaneVisible(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doShowOpenTickets() {
        try {
            this.setGlassPaneVisible(true);
            OpenTicketsListDialog dialog = new OpenTicketsListDialog();
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
        finally {
            this.setGlassPaneVisible(false);
        }
    }

    private void btnDrawerPullReportActionPerformed(ActionEvent evt) {
        this.doShowDrawerPullReport();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doShowDrawerPullReport() {
        try {
            this.setGlassPaneVisible(true);
            DrawerPullReportDialog dialog = new DrawerPullReportDialog(this, true);
            dialog.setTitle(POSConstants.DRAWER_PULL_BUTTON_TEXT);
            dialog.initialize();
            dialog.setSize(470, 500);
            dialog.setResizable(false);
            dialog.setDefaultCloseOperation(2);
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
        finally {
            this.setGlassPaneVisible(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doShowServerTips(ActionEvent evt) {
        try {
            this.setGlassPaneVisible(true);
            JPanel panel = new JPanel((LayoutManager)new MigLayout());
            List<User> users = UserDAO.getInstance().findAll();
            JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
            JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
            panel.add((Component)new JLabel(POSConstants.SELECT_USER + ":"), "grow");
            JComboBox userCombo = new JComboBox(new ListComboBoxModel(users));
            panel.add(userCombo, "grow, wrap");
            panel.add((Component)new JLabel(POSConstants.FROM + ":"), "grow");
            panel.add((Component)fromDatePicker, "wrap");
            panel.add((Component)new JLabel(POSConstants.TO_), "grow");
            panel.add((Component)toDatePicker);
            int option = JOptionPane.showOptionDialog(this, panel, POSConstants.SELECT_CRIETERIA, 2, 3, null, null, null);
            if (option != 0) {
                return;
            }
            GratuityDAO gratuityDAO = new GratuityDAO();
            TipsCashoutReport report = gratuityDAO.createReport(fromDatePicker.getDate(), toDatePicker.getDate(), (User)userCombo.getSelectedItem());
            TipsCashoutReportDialog dialog = new TipsCashoutReportDialog(report);
            dialog.setSize(400, 600);
            dialog.open();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
        finally {
            this.setGlassPaneVisible(false);
        }
    }

    public void open() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 800;
        int height = 600;
        int x = (screenSize.width - 800) / 2;
        int y = (screenSize.height - 600) / 2;
        this.setSize(width, height);
        this.setLocation(x, y);
        this.setDefaultCloseOperation(2);
        this.setVisible(true);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.PosLog;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Main;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class LanguageSelectionDialog
extends POSDialog {
    private JComboBox cbLang;
    private List<Locale> posLocaleList;

    public LanguageSelectionDialog() {
        super((Window)POSUtil.getBackOfficeWindow(), "");
        this.init();
        this.updateModel();
    }

    public LanguageSelectionDialog(JFrame parent) {
        this.init();
        this.updateModel();
    }

    private void init() {
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle("Language Selection");
        this.add((Component)titlePanel, "North");
        this.posLocaleList = new ArrayList<Locale>();
        int length = this.countProperties();
        JPanel centerPanel = new JPanel((LayoutManager)new MigLayout("fillx,aligny center", "[][]", ""));
        JLabel lblLanguage = new JLabel("Select Language:");
        this.cbLang = new JComboBox();
        for (Locale locale : this.posLocaleList) {
            this.cbLang.addItem(locale.getDisplayName());
        }
        centerPanel.add((Component)lblLanguage, "cell 0 0, alignx right");
        centerPanel.add((Component)this.cbLang, "cell 1 0");
        this.add((Component)centerPanel, "Center");
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("al center", "sg, fill", ""));
        PosButton btnSave = new PosButton("Save");
        buttonPanel.add((Component)btnSave, "grow");
        btnSave.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageSelectionDialog.this.doSave();
            }
        });
        PosButton btnCancel = new PosButton("Cancel");
        buttonPanel.add((Component)btnCancel, "grow");
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageSelectionDialog.this.dispose();
            }
        });
        buttonPanel.add(btnCancel);
        this.add((Component)buttonPanel, "South");
    }

    private void updateModel() {
        if (TerminalConfig.getDefaultLocale() != null) {
            Locale getLocale = TerminalConfig.getDefaultLocale();
            this.cbLang.setSelectedItem(getLocale.getDisplayName());
        }
    }

    public void doSave() {
        if (this.cbLang.getSelectedItem() != null) {
            String selectedLang = (String)this.cbLang.getSelectedItem();
            String savedLocal = "";
            for (Locale locale : this.posLocaleList) {
                if (!locale.getDisplayName().equals(selectedLang)) continue;
                savedLocal = savedLocal + locale;
            }
            TerminalConfig.setDefaultLocale(savedLocal);
            POSMessageDialog.showMessage(this, "Language set as default language. Application will restart.");
            this.dispose();
            try {
                Main.restart();
            }
            catch (Exception e) {
                PosLog.error(this.getClass(), e);
            }
        }
    }

    private boolean save() {
        return false;
    }

    private int countProperties() {
        boolean count = false;
        File[] files = null;
        try {
            files = new File("i18n").listFiles();
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String languageName = "";
                String countryName = "";
                int lf = fileName.indexOf("_") + 1;
                int ll = fileName.lastIndexOf("_");
                if (lf != 0 && ll != -1 && ll > lf) {
                    languageName = fileName.substring(lf, ll);
                }
                int start = fileName.lastIndexOf("_") + 1;
                int end = fileName.lastIndexOf(".");
                if (start != 0 && end != -1 && end > start) {
                    countryName = fileName.substring(start, end);
                }
                if (StringUtils.isEmpty((String)languageName) && !StringUtils.isEmpty((String)countryName)) {
                    languageName = countryName;
                    this.posLocaleList.add(new Locale(languageName));
                    continue;
                }
                if (StringUtils.isEmpty((String)languageName)) {
                    this.posLocaleList.add(new Locale("en"));
                    continue;
                }
                if (StringUtils.isEmpty((String)countryName)) {
                    this.posLocaleList.add(new Locale(languageName));
                    continue;
                }
                this.posLocaleList.add(new Locale(languageName, countryName));
            }
            return files.length;
        }
        return 0;
    }
}


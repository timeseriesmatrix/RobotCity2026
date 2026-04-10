/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.model.GlobalConfig;
import com.floreantpos.model.dao.GlobalConfigDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.util.GlobalConfigUtil;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class OtherConfigurationView
extends ConfigurationView {
    public static final String CONFIG_TAB_OTHER = "Others";
    private FixedLengthTextField tfMapApiKey;

    public OtherConfigurationView() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[][]", "[]"));
        this.tfMapApiKey = new FixedLengthTextField();
        this.tfMapApiKey.setLength(220);
        contentPanel.add(new JLabel(Messages.getString("OtherConfigurationView.0")));
        contentPanel.add(this.tfMapApiKey);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    @Override
    public boolean save() throws Exception {
        if (!this.isInitialized()) {
            return true;
        }
        GlobalConfig globalConfig = GlobalConfigUtil.get("map api key");
        if (globalConfig == null) {
            globalConfig = new GlobalConfig();
            globalConfig.setKey("map api key");
        }
        globalConfig.setValue(this.tfMapApiKey.getText());
        GlobalConfigDAO.getInstance().saveOrUpdate(globalConfig);
        GlobalConfigUtil.populateGlobalConfig();
        return true;
    }

    @Override
    public void initialize() throws Exception {
        String map_api_key = GlobalConfigUtil.getValue("map api key");
        if (StringUtils.isEmpty((String)map_api_key)) {
            map_api_key = "AIzaSyDc-5LFTSC-bB9kQcZkM74LHUxwndRy_XM";
        }
        this.tfMapApiKey.setText(map_api_key);
        this.setInitialized(true);
    }

    @Override
    public String getName() {
        return CONFIG_TAB_OTHER;
    }
}


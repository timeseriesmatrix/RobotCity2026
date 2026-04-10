/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.actions.DrawerAssignmentAction;
import com.floreantpos.actions.DrawerBleedAction;
import com.floreantpos.actions.DrawerKickAction;
import com.floreantpos.actions.DrawerPullAction;
import com.floreantpos.actions.ManageTableLayoutAction;
import com.floreantpos.actions.PayoutAction;
import com.floreantpos.actions.PosAction;
import com.floreantpos.actions.ServerTipsAction;
import com.floreantpos.actions.ShowBackofficeAction;
import com.floreantpos.actions.ShowKitchenDisplayAction;
import com.floreantpos.actions.ShowOnlineTicketManagementAction;
import com.floreantpos.actions.ShowTransactionsAuthorizationsAction;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.ViewPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

public class SwitchboardOtherFunctionsView
extends ViewPanel {
    public static final String VIEW_NAME = "ALL FUNCTIONS";
    private static SwitchboardOtherFunctionsView instance;
    private JPanel contentPanel;
    private DrawerAssignmentAction drawerAction;

    public SwitchboardOtherFunctionsView() {
        this.setLayout(new BorderLayout(5, 5));
        PosButton btnBack = new PosButton(Messages.getString("SwitchboardOtherFunctionsView.1"));
        btnBack.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                RootView.getInstance().showDefaultView();
            }
        });
        this.add((Component)btnBack, "South");
        this.contentPanel = new JPanel((LayoutManager)new MigLayout("hidemode 3,align 50% 50%, wrap 6", "sg fill", ""));
        ArrayList<PosAction> actions = new ArrayList<PosAction>();
        actions.add(new ShowBackofficeAction());
        this.drawerAction = new DrawerAssignmentAction();
        actions.add(this.drawerAction);
        actions.add(new DrawerPullAction());
        actions.add(new DrawerBleedAction());
        actions.add(new DrawerKickAction());
        actions.add(new PayoutAction());
        actions.add(new ServerTipsAction());
        actions.add(new ShowTransactionsAuthorizationsAction());
        actions.add(new ShowKitchenDisplayAction());
        actions.add(new ManageTableLayoutAction());
        actions.add(new ShowOnlineTicketManagementAction());
        List<FloreantPlugin> plugins = ExtensionManager.getPlugins();
        if (plugins != null) {
            for (FloreantPlugin plugin : plugins) {
                List posActions = plugin.getSpecialFunctionActions();
                if (posActions == null) continue;
                for (AbstractAction action : posActions) {
                    actions.add((PosAction)action);
                }
            }
        }
        Dimension size = PosUIManager.getSize(150, 150);
        for (PosAction action : actions) {
            if (action instanceof DrawerAssignmentAction && !Application.getInstance().getTerminal().isHasCashDrawer().booleanValue()) continue;
            PosButton button = new PosButton(action);
            this.contentPanel.add((Component)button, "w " + size.width + "!, h " + size.height + "!");
        }
        JScrollPane scrollPane = new JScrollPane(this.contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    public static SwitchboardOtherFunctionsView getInstance() {
        if (instance == null) {
            instance = new SwitchboardOtherFunctionsView();
        }
        instance.updateView();
        return instance;
    }

    private void updateView() {
        this.drawerAction.updateActionText();
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    public JPanel getContentPanel() {
        return this.contentPanel;
    }
}


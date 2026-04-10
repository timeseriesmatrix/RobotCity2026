/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXCollapsiblePane
 */
package com.floreantpos.ui;

import com.floreantpos.POSConstants;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.ui.PosFilterListener;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXCollapsiblePane;

public class PosFilterPanel
extends JXCollapsiblePane {
    private List<String> filters;
    private PosFilterListener listener;

    public PosFilterPanel(List<String> filters) {
        this.filters = filters;
        this.getContentPane().setLayout((LayoutManager)new MigLayout("inset 0", "grow", ""));
        this.setCollapsed(true);
        this.createFilterPanel();
    }

    public void addFilterListener(PosFilterListener listener) {
        this.listener = listener;
    }

    private void createFilterPanel() {
        JPanel panel = new JPanel((LayoutManager)new MigLayout("inset 0 0 5 0", "sg, fill", ""));
        ButtonGroup group = new ButtonGroup();
        for (String filter : this.filters) {
            FilterButton btnFilter = new FilterButton(filter);
            if (filter.equals(POSConstants.ALL)) {
                btnFilter.setSelected(true);
            }
            group.add(btnFilter);
            panel.add(btnFilter);
        }
        this.getContentPane().add(panel);
    }

    private class FilterButton
    extends POSToggleButton
    implements ActionListener {
        public FilterButton(String name) {
            this.setText(name);
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.setSelected(true);
            String actionCommand = e.getActionCommand();
            PosFilterPanel.this.listener.filterSelected(actionCommand);
        }
    }
}


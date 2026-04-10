/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.swing.JRViewer
 *  net.sf.jasperreports.swing.JRViewerPanel
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.swing.JRViewerPanel;

public class TicketReceiptView
extends JPanel {
    JRViewer jrViewer;

    public TicketReceiptView(JasperPrint jasperPrint) {
        this.setLayout(new BorderLayout());
        this.jrViewer = new JRViewer(jasperPrint);
        this.add((Component)this.jrViewer);
    }

    public Component getReportPanel() {
        Component[] components;
        for (Component component : components = this.jrViewer.getComponents()) {
            Component[] components2;
            if (!(component instanceof JRViewerPanel)) continue;
            for (Component component2 : components2 = ((JRViewerPanel)component).getComponents()) {
                if (!(component2 instanceof JScrollPane)) continue;
                JScrollPane scrollPane = (JScrollPane)component2;
                return scrollPane.getViewport().getView();
            }
        }
        throw new RuntimeException(Messages.getString("TicketReceiptView.0"));
    }
}


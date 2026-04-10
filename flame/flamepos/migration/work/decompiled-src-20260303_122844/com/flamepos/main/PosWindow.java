/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXStatusBar
 *  org.jdesktop.swingx.JXStatusBar$Constraint$ResizeBehavior
 */
package com.floreantpos.main;

import com.floreantpos.IconFactory;
import com.floreantpos.actions.ShutDownAction;
import com.floreantpos.config.AppConfig;
import com.floreantpos.main.Application;
import com.floreantpos.swing.GlassPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.jdesktop.swingx.JXStatusBar;

public class PosWindow
extends JFrame
implements WindowListener {
    private static final String EXTENDEDSTATE = "extendedstate";
    private static final String WLOCY = "wlocy";
    private static final String WLOCX = "wlocx";
    private static final String WHEIGHT = "wheight";
    private static final String WWIDTH = "wwidth";
    private GlassPane glassPane;
    private JXStatusBar statusBar;
    private JLabel statusLabel;
    private JPanel welcomeHeaderPanel;

    public PosWindow() {
        this.setIconImage(Application.getApplicationIcon().getImage());
        this.addWindowListener(this);
        this.glassPane = new GlassPane();
        this.glassPane.setOpacity(0.6f);
        this.setGlassPane(this.glassPane);
        this.statusBar = new JXStatusBar();
        this.statusLabel = new JLabel("");
        this.statusBar.add((Component)this.statusLabel, (Object)JXStatusBar.Constraint.ResizeBehavior.FILL);
        JPanel statusBarContainer = new JPanel(new BorderLayout());
        statusBarContainer.add((Component)new JSeparator(0), "North");
        statusBarContainer.add((Component)this.statusBar);
        this.getContentPane().add((Component)statusBarContainer, "South");
    }

    public void setVisibleWelcomeHeader(boolean visible) {
        if (!visible) {
            this.getContentPane().remove(this.welcomeHeaderPanel);
            this.welcomeHeaderPanel = null;
            return;
        }
        JLabel titleLabel = new JLabel(IconFactory.getIcon("/ui_icons/", "title.png"));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);
        this.welcomeHeaderPanel = new JPanel(new BorderLayout());
        this.welcomeHeaderPanel.add((Component)titleLabel, "Center");
        this.welcomeHeaderPanel.add((Component)new JSeparator(0), "South");
        this.add((Component)this.welcomeHeaderPanel, "North");
    }

    public void setStatus(String status) {
        this.statusLabel.setText(status);
    }

    public void setupSizeAndLocation() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(AppConfig.getInt(WWIDTH, (int)screenSize.getWidth()), AppConfig.getInt(WHEIGHT, (int)screenSize.getHeight()));
        this.setLocation(AppConfig.getInt(WLOCX, screenSize.width - this.getWidth() >> 1), AppConfig.getInt(WLOCY, screenSize.height - this.getHeight() >> 1));
        this.setMinimumSize(new Dimension(1024, 724));
        this.setDefaultCloseOperation(0);
        int extendedState = AppConfig.getInt(EXTENDEDSTATE, -1);
        if (extendedState != -1) {
            this.setExtendedState(extendedState);
        }
    }

    public void enterFullScreenMode() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize);
        this.setExtendedState(6);
        this.setUndecorated(true);
        this.setLocation(0, 0);
    }

    public void leaveFullScreenMode() {
        GraphicsDevice window = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        this.setUndecorated(false);
        window.setFullScreenWindow(null);
    }

    public void saveSizeAndLocation() {
        int width = this.getWidth();
        int height = this.getHeight();
        AppConfig.putInt(WWIDTH, width);
        AppConfig.putInt(WHEIGHT, height);
        Point locationOnScreen = this.getLocationOnScreen();
        AppConfig.putInt(WLOCX, locationOnScreen.x);
        AppConfig.putInt(WLOCY, locationOnScreen.y);
        AppConfig.putInt(EXTENDEDSTATE, this.getExtendedState());
    }

    public void setGlassPaneVisible(boolean b) {
        this.glassPane.setVisible(b);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (Application.getCurrentUser() != null) {
            new ShutDownAction().actionPerformed(null);
        } else {
            Application.getInstance().shutdownPOS();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}


/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

public class POSFileChooser
extends JFileChooser {
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");

    public POSFileChooser() {
        this.setDialogTitle("FloreantPOS");
        this.setAcceptAllFileFilterUsed(false);
        this.setFileFilter(this.filter);
        this.setAccessory(new ImagePreview(this));
        this.setFileView(new FileView(){

            @Override
            public Icon getIcon(File f) {
                return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
        });
    }

    public class ImagePreview
    extends JComponent
    implements PropertyChangeListener {
        ImageIcon thumbnail = null;
        File file = null;

        public ImagePreview(JFileChooser fc) {
            this.setPreferredSize(new Dimension(300, 200));
            fc.addPropertyChangeListener(this);
        }

        public void loadImage() {
            if (this.file == null) {
                this.thumbnail = null;
                return;
            }
            ImageIcon tmpIcon = new ImageIcon(this.file.getPath());
            if (tmpIcon != null) {
                this.thumbnail = tmpIcon.getIconWidth() > 90 ? new ImageIcon(tmpIcon.getImage().getScaledInstance(280, -1, 1)) : tmpIcon;
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            boolean update = false;
            String prop = e.getPropertyName();
            if ("directoryChanged".equals(prop)) {
                this.file = null;
                update = true;
            } else if ("SelectedFileChangedProperty".equals(prop)) {
                this.file = (File)e.getNewValue();
                update = true;
            }
            if (update) {
                this.thumbnail = null;
                if (this.isShowing()) {
                    this.loadImage();
                    this.repaint();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (this.thumbnail == null) {
                this.loadImage();
            }
            if (this.thumbnail != null) {
                int x = this.getWidth() / 2 - this.thumbnail.getIconWidth() / 2;
                int y = this.getHeight() / 2 - this.thumbnail.getIconHeight() / 2;
                if (y < 0) {
                    y = 0;
                }
                if (x < 5) {
                    x = 5;
                }
                this.thumbnail.paintIcon(this, g, x, y);
            }
        }
    }
}


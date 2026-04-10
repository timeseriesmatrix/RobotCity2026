/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos;

import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.ImageIcon;

public class IconFactory {
    private static HashMap<String, ImageIcon> iconCache = new HashMap();

    public static ImageIcon getIcon(String iconName) {
        ImageIcon icon = iconCache.get(iconName);
        if (icon == null) {
            try {
                icon = new ImageIcon(IconFactory.class.getResource("/ui_icons/" + iconName));
                iconCache.put(iconName, icon);
            }
            catch (Exception x) {
                return IconFactory.getDefaultIcon(iconName);
            }
        }
        return icon;
    }

    private static ImageIcon getDefaultIcon(String iconName) {
        ImageIcon icon = iconCache.get(iconName);
        if (icon == null) {
            try {
                icon = new ImageIcon(IconFactory.class.getResource("/images/" + iconName));
                iconCache.put(iconName, icon);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return icon;
    }

    public static ImageIcon getIcon(String path, String iconName) {
        ImageIcon icon = iconCache.get(iconName);
        if (icon == null) {
            try {
                icon = new ImageIcon(IconFactory.class.getResource(path + iconName));
                iconCache.put(iconName, icon);
            }
            catch (Exception x) {
                return IconFactory.getIcon(iconName);
            }
        }
        return icon;
    }

    public static ImageIcon getIcon(String path, String iconName, Dimension size) {
        ImageIcon icon = iconCache.get(iconName);
        if (icon == null) {
            try {
                icon = new ImageIcon(IconFactory.class.getResource(path + iconName));
                icon = new ImageIcon(icon.getImage().getScaledInstance(size.width, size.height, 4));
                iconCache.put(iconName, icon);
            }
            catch (Exception x) {
                return IconFactory.getIcon(iconName);
            }
        }
        return icon;
    }
}


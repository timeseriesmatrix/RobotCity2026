package com.floreantpos.main;

import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public final class FlameTheme {
    public static final Color BACKGROUND_PRIMARY = new Color(245, 243, 238);
    public static final Color BACKGROUND_SECONDARY = new Color(255, 255, 255);
    public static final Color BACKGROUND_TERTIARY = new Color(248, 241, 232);
    public static final Color BUTTON_BACKGROUND = new Color(255, 255, 255);
    public static final Color BUTTON_PRESSED = new Color(245, 229, 214);
    public static final Color TEXT_PRIMARY = new Color(39, 49, 66);
    public static final Color TEXT_MUTED = new Color(109, 121, 142);
    public static final Color ACCENT_COLOR = new Color(220, 70, 28);
    public static final Color WARM_ACCENT = new Color(242, 141, 46);
    public static final Color BORDER_COLOR = new Color(214, 222, 234);
    public static final Color SEPARATOR_COLOR = new Color(224, 231, 242);

    private FlameTheme() {
    }

    public static void applyLookAndFeelDefaults() {
        UIManager.put("control", BACKGROUND_SECONDARY);
        UIManager.put("info", BACKGROUND_SECONDARY);
        UIManager.put("Panel.background", BACKGROUND_PRIMARY);
        UIManager.put("Viewport.background", BACKGROUND_PRIMARY);
        UIManager.put("ScrollPane.background", BACKGROUND_PRIMARY);
        UIManager.put("Separator.foreground", SEPARATOR_COLOR);
        UIManager.put("Separator.background", BACKGROUND_PRIMARY);
        UIManager.put("TitledBorder.titleColor", ACCENT_COLOR);
        UIManager.put("Label.foreground", TEXT_PRIMARY);

        UIManager.put("Button.background", BUTTON_BACKGROUND);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Button.disabledText", TEXT_MUTED);
        UIManager.put("Button.select", BUTTON_PRESSED);
        UIManager.put("Button.border", new LineBorder(BORDER_COLOR, 1));

        UIManager.put("ToggleButton.background", BUTTON_BACKGROUND);
        UIManager.put("ToggleButton.foreground", TEXT_PRIMARY);
        UIManager.put("ToggleButton.select", BUTTON_PRESSED);
        UIManager.put("ToggleButton.border", new LineBorder(BORDER_COLOR, 1));

        UIManager.put("TextField.background", BACKGROUND_TERTIARY);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("PasswordField.background", BACKGROUND_TERTIARY);
        UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", TEXT_PRIMARY);
        UIManager.put("TextArea.background", BACKGROUND_TERTIARY);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("TextPane.background", BACKGROUND_TERTIARY);
        UIManager.put("TextPane.foreground", TEXT_PRIMARY);
        UIManager.put("EditorPane.background", BACKGROUND_TERTIARY);
        UIManager.put("EditorPane.foreground", TEXT_PRIMARY);

        UIManager.put("ComboBox.background", BACKGROUND_TERTIARY);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", new Color(255, 242, 230));
        UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);
        UIManager.put("List.background", BACKGROUND_SECONDARY);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", new Color(255, 242, 230));
        UIManager.put("List.selectionForeground", TEXT_PRIMARY);

        UIManager.put("Table.background", BACKGROUND_SECONDARY);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("Table.selectionBackground", new Color(255, 242, 230));
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TableHeader.background", BACKGROUND_TERTIARY);
        UIManager.put("TableHeader.foreground", TEXT_PRIMARY);

        UIManager.put("MenuBar.background", BACKGROUND_SECONDARY);
        UIManager.put("MenuBar.foreground", TEXT_PRIMARY);
        UIManager.put("Menu.background", BACKGROUND_SECONDARY);
        UIManager.put("Menu.foreground", TEXT_PRIMARY);
        UIManager.put("MenuItem.background", BACKGROUND_SECONDARY);
        UIManager.put("MenuItem.foreground", TEXT_PRIMARY);
        UIManager.put("PopupMenu.background", BACKGROUND_SECONDARY);

        UIManager.put("OptionPane.background", BACKGROUND_PRIMARY);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);

        UIManager.put("ToolTip.background", new Color(255, 249, 241));
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);

        UIManager.put("Plastic.brightenStop", BORDER_COLOR);
        UIManager.put("Plastic.ltBrightenStop", new Color(255, 246, 236));
    }

    public static void styleButton(AbstractButton button) {
        if (button == null) {
            return;
        }
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(BUTTON_BACKGROUND);
        button.setBorder(new LineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
    }

    public static void stylePanel(JComponent component, Color color) {
        if (component == null) {
            return;
        }
        component.setOpaque(true);
        component.setBackground(color);
    }
}

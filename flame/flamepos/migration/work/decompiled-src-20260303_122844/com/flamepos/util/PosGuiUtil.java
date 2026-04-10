/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.POSConstants;
import com.floreantpos.customer.CustomerSelectorDialog;
import com.floreantpos.customer.CustomerSelectorFactory;
import com.floreantpos.main.Application;
import com.floreantpos.model.Customer;
import com.floreantpos.model.OrderType;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Dialog;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

public class PosGuiUtil {
    private static JFileChooser fileChooser = new JFileChooser();

    public static int captureGuestNumber() {
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setTitle(POSConstants.ENTER_NUMBER_OF_GUEST);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return -1;
        }
        int numberOfGuests = (int)dialog.getValue();
        if (numberOfGuests == 0) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.GUEST_NUMBER_CANNOT_BE_0);
            return -1;
        }
        return numberOfGuests;
    }

    public static Customer captureCustomer(OrderType orderType) {
        CustomerSelectorDialog dialog = CustomerSelectorFactory.createCustomerSelectorDialog(orderType);
        dialog.setCreateNewTicket(true);
        dialog.updateView(true);
        dialog.openUndecoratedFullScreen();
        if (dialog.isCanceled()) {
            return null;
        }
        return dialog.getSelectedCustomer();
    }

    public static Double parseDouble(JTextComponent textComponent) {
        String text = textComponent.getText();
        try {
            return Double.parseDouble(text);
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    public static boolean isModalDialogShowing() {
        Window[] windows = Window.getWindows();
        if (windows != null) {
            for (Window w : windows) {
                if (!w.isShowing() || !(w instanceof Dialog) || !((Dialog)w).isModal()) continue;
                return true;
            }
        }
        return false;
    }

    public static void setColumnWidth(JTable table, int columnNumber, int width) {
        TableColumn column = table.getColumnModel().getColumn(columnNumber);
        column.setPreferredWidth(width);
        column.setWidth(width);
    }

    public static TitledBorder createTitledBorder(String title) {
        return new TitledBorder(null, title, 2, 2);
    }

    public static BufferedImage selectImageFile() throws Exception {
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(0);
        int option = fileChooser.showOpenDialog(null);
        if (option == 0) {
            File imageFile = fileChooser.getSelectedFile();
            BufferedImage image = ImageIO.read(imageFile);
            return PosGuiUtil.scale(image, 100, 100);
        }
        return null;
    }

    public static BufferedImage scale(BufferedImage img, int targetWidth, int targetHeight) {
        int type = img.getTransparency() == 1 ? 1 : 2;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int prevW = w;
        int prevH = h;
        do {
            if (w > targetWidth) {
                int n = w = (w /= 2) < targetWidth ? targetWidth : w;
            }
            if (h > targetHeight) {
                int n = h = (h /= 2) < targetHeight ? targetHeight : h;
            }
            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);
        if (g2 != null) {
            g2.dispose();
        }
        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage((Image)ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }
        return ret;
    }
}


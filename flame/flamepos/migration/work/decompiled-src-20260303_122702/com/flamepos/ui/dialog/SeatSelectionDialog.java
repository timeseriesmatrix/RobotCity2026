/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.model.ShopTable;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import net.miginfocom.swing.MigLayout;

public class SeatSelectionDialog
extends POSDialog
implements ActionListener {
    private TitlePanel titlePanel;
    private Integer selectedSeat;
    private List<Integer> seats = new ArrayList<Integer>();
    private List<Integer> orderSeats;

    public SeatSelectionDialog(List<Integer> tableNumbers, List<Integer> orderSeats) {
        List<ShopTable> tables = ShopTableDAO.getInstance().getByNumbers(tableNumbers);
        int seatNumber = 1;
        for (ShopTable shopTable : tables) {
            for (int i = 0; i < shopTable.getCapacity(); ++i) {
                this.seats.add(seatNumber);
                ++seatNumber;
            }
        }
        this.orderSeats = orderSeats;
        if (orderSeats != null) {
            Integer i = 0;
            while (i < orderSeats.size()) {
                int orderSeatNumber = orderSeats.get(i);
                if (orderSeatNumber != 0 && !this.seats.contains(orderSeatNumber)) {
                    this.seats.add(orderSeatNumber);
                }
                Integer n = i;
                Integer n2 = i = Integer.valueOf(i + 1);
            }
        }
        this.init();
    }

    private void init() {
        this.setResizable(false);
        Container contentPane = this.getContentPane();
        MigLayout layout = new MigLayout("fillx,wrap 3", "[60px,fill][60px,fill][60px,fill]", "[][][][][]");
        contentPane.setLayout((LayoutManager)layout);
        this.titlePanel = new TitlePanel();
        contentPane.add((Component)this.titlePanel, "spanx ,growy,height 60,wrap");
        for (Integer seat : this.seats) {
            PosButton posButton = new PosButton();
            posButton.setFocusable(false);
            posButton.setFont(posButton.getFont().deriveFont(1, 24.0f));
            posButton.setText(String.valueOf(seat));
            if (this.orderSeats != null && this.orderSeats.contains(seat)) {
                posButton.setBackground(Color.GREEN);
            }
            posButton.addActionListener(this);
            String constraints = "grow, height 55";
            contentPane.add((Component)posButton, constraints);
        }
        PosButton btnShared = new PosButton("Shared");
        btnShared.setFocusable(false);
        if (this.orderSeats != null && this.orderSeats.contains(0)) {
            btnShared.setBackground(Color.GREEN);
        }
        btnShared.addActionListener(this);
        contentPane.add((Component)btnShared, "grow, height 55");
        PosButton btnCustom = new PosButton("Custom");
        btnCustom.setFocusable(false);
        btnCustom.addActionListener(this);
        contentPane.add((Component)btnCustom, "grow, height 55");
        PosButton btnCancel = new PosButton("Cancel");
        btnCancel.setFocusable(false);
        btnCancel.addActionListener(this);
        contentPane.add((Component)btnCancel, "newline, span,grow, height 55");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        boolean canceled = false;
        if ("Shared".equalsIgnoreCase(actionCommand)) {
            this.selectedSeat = 0;
        } else if ("Custom".equalsIgnoreCase(actionCommand)) {
            this.selectedSeat = -1;
        } else if ("Cancel".equalsIgnoreCase(actionCommand)) {
            canceled = true;
        } else {
            this.selectedSeat = Integer.valueOf(actionCommand);
        }
        this.setCanceled(canceled);
        this.dispose();
    }

    public Integer getSeatNumber() {
        return this.selectedSeat;
    }

    @Override
    public void setTitle(String title) {
        this.titlePanel.setTitle(title);
        super.setTitle(title);
    }

    public void setDialogTitle(String title) {
        super.setTitle(title);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.dao.CookingInstructionDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.NewCookongInstructionDialog;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class SelectCookongInstructionDialog
extends POSDialog
implements ActionListener {
    private CookingInstruction cookingInstruction;
    private JComboBox cbCookingInstructions;
    private PosButton btnNew;
    private PosButton btnOk;
    private PosButton btnCancel;

    @Override
    protected void initUI() {
        this.setLayout((LayoutManager)new MigLayout());
        CookingInstructionDAO dao = new CookingInstructionDAO();
        List<CookingInstruction> cookingInstructions = dao.findAll();
        DefaultComboBoxModel<Object> cbModel = new DefaultComboBoxModel<Object>(cookingInstructions.toArray());
        this.cbCookingInstructions = new JComboBox<Object>(cbModel);
        this.cbCookingInstructions.setFont(this.cbCookingInstructions.getFont().deriveFont(16));
        this.btnNew = new PosButton(POSConstants.NEW);
        this.btnOk = new PosButton(POSConstants.OK);
        this.btnCancel = new PosButton(POSConstants.CANCEL);
        this.add((Component)this.cbCookingInstructions, "wrap, span, grow, h 30");
        this.add((Component)new JSeparator(), "wrap, span, grow");
        this.add((Component)this.btnNew, "al right,width 120, height 30");
        this.add((Component)this.btnOk, "al right,width 120, height 30");
        this.add((Component)this.btnCancel, "width 120, height 30");
        this.btnNew.addActionListener(this);
        this.btnOk.addActionListener(this);
        this.btnCancel.addActionListener(this);
    }

    private void doOk() {
        this.cookingInstruction = (CookingInstruction)this.cbCookingInstructions.getSelectedItem();
        this.setCanceled(false);
        this.dispose();
    }

    private void doCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    private void doCreateNew() {
        NewCookongInstructionDialog dialog = new NewCookongInstructionDialog();
        dialog.pack();
        dialog.open();
        if (!dialog.isCanceled()) {
            this.cookingInstruction = dialog.getCookingInstruction();
            DefaultComboBoxModel model = (DefaultComboBoxModel)this.cbCookingInstructions.getModel();
            model.addElement(this.cookingInstruction);
            model.setSelectedItem(this.cookingInstruction);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
            this.doOk();
        } else if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
            this.doCancel();
        } else if (POSConstants.NEW.equalsIgnoreCase(actionCommand)) {
            this.doCreateNew();
        }
    }

    public CookingInstruction getCookingInstruction() {
        return this.cookingInstruction;
    }

    public CookingInstruction getSelectedCookingInstruction() {
        return this.cookingInstruction;
    }
}


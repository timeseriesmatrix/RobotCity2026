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
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.NoteView;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class NewCookongInstructionDialog
extends POSDialog
implements ActionListener {
    private CookingInstruction cookingInstruction;
    private NoteView noteView;
    private PosButton btnOk;
    private PosButton btnCancel;

    @Override
    protected void initUI() {
        this.setLayout((LayoutManager)new MigLayout());
        this.noteView = new NoteView();
        this.btnOk = new PosButton(POSConstants.OK);
        this.btnCancel = new PosButton(POSConstants.CANCEL);
        this.add((Component)this.noteView, "wrap, span, grow");
        this.add((Component)new JSeparator(), "wrap, span, grow");
        this.add((Component)this.btnOk, "al right,width 120, height 50");
        this.add((Component)this.btnCancel, "width 120, height 50");
        this.btnOk.addActionListener(this);
        this.btnCancel.addActionListener(this);
    }

    public String getText() {
        return this.noteView.getNote();
    }

    private void doOk() {
        if (this.cookingInstruction == null) {
            this.cookingInstruction = new CookingInstruction();
        }
        this.cookingInstruction.setDescription(this.getText());
        CookingInstructionDAO dao = new CookingInstructionDAO();
        dao.save(this.cookingInstruction);
        this.setCanceled(false);
        this.dispose();
    }

    private void doCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
            this.doOk();
        } else if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
            this.doCancel();
        }
    }

    public CookingInstruction getCookingInstruction() {
        return this.cookingInstruction;
    }
}


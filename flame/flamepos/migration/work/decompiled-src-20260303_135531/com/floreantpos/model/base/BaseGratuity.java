/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Gratuity;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import java.io.Serializable;

public abstract class BaseGratuity
implements Comparable,
Serializable {
    public static String REF = "Gratuity";
    public static String PROP_REFUNDED = "refunded";
    public static String PROP_OWNER = "owner";
    public static String PROP_PAID = "paid";
    public static String PROP_TICKET = "ticket";
    public static String PROP_AMOUNT = "amount";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Double amount;
    protected Boolean paid;
    protected Boolean refunded;
    private Ticket ticket;
    private User owner;
    private Terminal terminal;

    public BaseGratuity() {
        this.initialize();
    }

    public BaseGratuity(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Double getAmount() {
        return this.amount == null ? Double.valueOf(0.0) : this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean isPaid() {
        return this.paid == null ? Boolean.FALSE : this.paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean isRefunded() {
        return this.refunded == null ? Boolean.FALSE : this.refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Gratuity)) {
            return false;
        }
        Gratuity gratuity = (Gratuity)obj;
        if (null == this.getId() || null == gratuity.getId()) {
            return this == obj;
        }
        return this.getId().equals(gratuity.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}


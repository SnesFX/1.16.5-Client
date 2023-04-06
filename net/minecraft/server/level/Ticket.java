/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import java.util.Comparator;
import java.util.Objects;
import net.minecraft.server.level.TicketType;

public final class Ticket<T>
implements Comparable<Ticket<?>> {
    private final TicketType<T> type;
    private final int ticketLevel;
    private final T key;
    private long createdTick;

    protected Ticket(TicketType<T> ticketType, int n, T t) {
        this.type = ticketType;
        this.ticketLevel = n;
        this.key = t;
    }

    @Override
    public int compareTo(Ticket<?> ticket) {
        int n = Integer.compare(this.ticketLevel, ticket.ticketLevel);
        if (n != 0) {
            return n;
        }
        int n2 = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(ticket.type));
        if (n2 != 0) {
            return n2;
        }
        return this.type.getComparator().compare(this.key, ticket.key);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket ticket = (Ticket)object;
        return this.ticketLevel == ticket.ticketLevel && Objects.equals(this.type, ticket.type) && Objects.equals(this.key, ticket.key);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.ticketLevel, this.key);
    }

    public String toString() {
        return "Ticket[" + this.type + " " + this.ticketLevel + " (" + this.key + ")] at " + this.createdTick;
    }

    public TicketType<T> getType() {
        return this.type;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    protected void setCreatedTick(long l) {
        this.createdTick = l;
    }

    protected boolean timedOut(long l) {
        long l2 = this.type.timeout();
        return l2 != 0L && l - this.createdTick > l2;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((Ticket)object);
    }
}


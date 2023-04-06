/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.Collection;
import java.util.EnumSet;

public abstract class Goal {
    private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    public abstract boolean canUse();

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public boolean isInterruptable() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
    }

    public void setFlags(EnumSet<Flag> enumSet) {
        this.flags.clear();
        this.flags.addAll(enumSet);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public EnumSet<Flag> getFlags() {
        return this.flags;
    }

    public static enum Flag {
        MOVE,
        LOOK,
        JUMP,
        TARGET;
        
    }

}


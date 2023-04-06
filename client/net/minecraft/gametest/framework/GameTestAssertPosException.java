/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;

public class GameTestAssertPosException
extends GameTestAssertException {
    private final BlockPos absolutePos;
    private final BlockPos relativePos;
    private final long tick;

    @Override
    public String getMessage() {
        String string = "" + this.absolutePos.getX() + "," + this.absolutePos.getY() + "," + this.absolutePos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
        return super.getMessage() + " at " + string + " (t=" + this.tick + ")";
    }

    @Nullable
    public String getMessageToShowAtBlock() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos getAbsolutePos() {
        return this.absolutePos;
    }
}


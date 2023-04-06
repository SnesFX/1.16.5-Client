/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface Coordinates {
    public Vec3 getPosition(CommandSourceStack var1);

    public Vec2 getRotation(CommandSourceStack var1);

    default public BlockPos getBlockPos(CommandSourceStack commandSourceStack) {
        return new BlockPos(this.getPosition(commandSourceStack));
    }

    public boolean isXRelative();

    public boolean isYRelative();

    public boolean isZRelative();
}


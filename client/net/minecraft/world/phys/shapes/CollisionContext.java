/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionContext {
    public static CollisionContext empty() {
        return EntityCollisionContext.EMPTY;
    }

    public static CollisionContext of(Entity entity) {
        return new EntityCollisionContext(entity);
    }

    public boolean isDescending();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHoldingItem(Item var1);

    public boolean canStandOnFluid(FluidState var1, FlowingFluid var2);
}


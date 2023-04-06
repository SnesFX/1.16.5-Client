/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TheEndPortalBlockEntity
extends BlockEntity {
    public TheEndPortalBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public TheEndPortalBlockEntity() {
        this(BlockEntityType.END_PORTAL);
    }

    public boolean shouldRenderFace(Direction direction) {
        return direction == Direction.UP;
    }
}


/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.phys.AABB;

public class EndCrystalItem
extends Item {
    public EndCrystalItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        double d;
        BlockPos blockPos;
        double d2;
        Level level = useOnContext.getLevel();
        BlockState blockState = level.getBlockState(blockPos = useOnContext.getClickedPos());
        if (!blockState.is(Blocks.OBSIDIAN) && !blockState.is(Blocks.BEDROCK)) {
            return InteractionResult.FAIL;
        }
        BlockPos blockPos2 = blockPos.above();
        if (!level.isEmptyBlock(blockPos2)) {
            return InteractionResult.FAIL;
        }
        double d3 = blockPos2.getX();
        List<Entity> list = level.getEntities(null, new AABB(d3, d = (double)blockPos2.getY(), d2 = (double)blockPos2.getZ(), d3 + 1.0, d + 2.0, d2 + 1.0));
        if (!list.isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel) {
            EndCrystal endCrystal = new EndCrystal(level, d3 + 0.5, d, d2 + 0.5);
            endCrystal.setShowBottom(false);
            level.addFreshEntity(endCrystal);
            EndDragonFight endDragonFight = ((ServerLevel)level).dragonFight();
            if (endDragonFight != null) {
                endDragonFight.tryRespawn();
            }
        }
        useOnContext.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}


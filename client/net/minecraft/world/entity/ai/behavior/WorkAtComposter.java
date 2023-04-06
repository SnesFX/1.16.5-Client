/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class WorkAtComposter
extends WorkAtPoi {
    private static final List<Item> COMPOSTABLE_ITEMS = ImmutableList.of((Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS);

    @Override
    protected void useWorkstation(ServerLevel serverLevel, Villager villager) {
        Optional<GlobalPos> optional = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return;
        }
        GlobalPos globalPos = optional.get();
        BlockState blockState = serverLevel.getBlockState(globalPos.pos());
        if (blockState.is(Blocks.COMPOSTER)) {
            this.makeBread(villager);
            this.compostItems(serverLevel, villager, globalPos, blockState);
        }
    }

    private void compostItems(ServerLevel serverLevel, Villager villager, GlobalPos globalPos, BlockState blockState) {
        BlockPos blockPos = globalPos.pos();
        if (blockState.getValue(ComposterBlock.LEVEL) == 8) {
            blockState = ComposterBlock.extractProduce(blockState, serverLevel, blockPos);
        }
        int n = 20;
        int n2 = 10;
        int[] arrn = new int[COMPOSTABLE_ITEMS.size()];
        SimpleContainer simpleContainer = villager.getInventory();
        int n3 = simpleContainer.getContainerSize();
        BlockState blockState2 = blockState;
        for (int i = n3 - 1; i >= 0 && n > 0; --i) {
            int n4;
            ItemStack itemStack = simpleContainer.getItem(i);
            int n5 = COMPOSTABLE_ITEMS.indexOf(itemStack.getItem());
            if (n5 == -1) continue;
            int n6 = itemStack.getCount();
            arrn[n5] = n4 = arrn[n5] + n6;
            int n7 = Math.min(Math.min(n4 - 10, n), n6);
            if (n7 <= 0) continue;
            n -= n7;
            for (int j = 0; j < n7; ++j) {
                if ((blockState2 = ComposterBlock.insertItem(blockState2, serverLevel, itemStack, blockPos)).getValue(ComposterBlock.LEVEL) != 7) continue;
                this.spawnComposterFillEffects(serverLevel, blockState, blockPos, blockState2);
                return;
            }
        }
        this.spawnComposterFillEffects(serverLevel, blockState, blockPos, blockState2);
    }

    private void spawnComposterFillEffects(ServerLevel serverLevel, BlockState blockState, BlockPos blockPos, BlockState blockState2) {
        serverLevel.levelEvent(1500, blockPos, blockState2 != blockState ? 1 : 0);
    }

    private void makeBread(Villager villager) {
        SimpleContainer simpleContainer = villager.getInventory();
        if (simpleContainer.countItem(Items.BREAD) > 36) {
            return;
        }
        int n = simpleContainer.countItem(Items.WHEAT);
        int n2 = 3;
        int n3 = 3;
        int n4 = Math.min(3, n / 3);
        if (n4 == 0) {
            return;
        }
        int n5 = n4 * 3;
        simpleContainer.removeItemType(Items.WHEAT, n5);
        ItemStack itemStack = simpleContainer.addItem(new ItemStack(Items.BREAD, n4));
        if (!itemStack.isEmpty()) {
            villager.spawnAtLocation(itemStack, 0.5f);
        }
    }
}


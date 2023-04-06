/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class ShowTradesToPlayer
extends Behavior<Villager> {
    @Nullable
    private ItemStack playerItemStack;
    private final List<ItemStack> displayItems = Lists.newArrayList();
    private int cycleCounter;
    private int displayIndex;
    private int lookTime;

    public ShowTradesToPlayer(int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT)), n, n2);
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        Brain<Villager> brain = villager.getBrain();
        if (!brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && villager.isAlive() && livingEntity.isAlive() && !villager.isBaby() && villager.distanceToSqr(livingEntity) <= 17.0;
    }

    @Override
    public boolean canStillUse(ServerLevel serverLevel, Villager villager, long l) {
        return this.checkExtraStartConditions(serverLevel, villager) && this.lookTime > 0 && villager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void start(ServerLevel serverLevel, Villager villager, long l) {
        super.start(serverLevel, villager, l);
        this.lookAtTarget(villager);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    @Override
    public void tick(ServerLevel serverLevel, Villager villager, long l) {
        LivingEntity livingEntity = this.lookAtTarget(villager);
        this.findItemsToDisplay(livingEntity, villager);
        if (!this.displayItems.isEmpty()) {
            this.displayCyclingItems(villager);
        } else {
            villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.lookTime = Math.min(this.lookTime, 40);
        }
        --this.lookTime;
    }

    @Override
    public void stop(ServerLevel serverLevel, Villager villager, long l) {
        super.stop(serverLevel, villager, l);
        villager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.playerItemStack = null;
    }

    private void findItemsToDisplay(LivingEntity livingEntity, Villager villager) {
        boolean bl = false;
        ItemStack itemStack = livingEntity.getMainHandItem();
        if (this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, itemStack)) {
            this.playerItemStack = itemStack;
            bl = true;
            this.displayItems.clear();
        }
        if (bl && !this.playerItemStack.isEmpty()) {
            this.updateDisplayItems(villager);
            if (!this.displayItems.isEmpty()) {
                this.lookTime = 900;
                this.displayFirstItem(villager);
            }
        }
    }

    private void displayFirstItem(Villager villager) {
        villager.setItemSlot(EquipmentSlot.MAINHAND, this.displayItems.get(0));
    }

    private void updateDisplayItems(Villager villager) {
        for (MerchantOffer merchantOffer : villager.getOffers()) {
            if (merchantOffer.isOutOfStock() || !this.playerItemStackMatchesCostOfOffer(merchantOffer)) continue;
            this.displayItems.add(merchantOffer.getResult());
        }
    }

    private boolean playerItemStackMatchesCostOfOffer(MerchantOffer merchantOffer) {
        return ItemStack.isSame(this.playerItemStack, merchantOffer.getCostA()) || ItemStack.isSame(this.playerItemStack, merchantOffer.getCostB());
    }

    private LivingEntity lookAtTarget(Villager villager) {
        Brain<Villager> brain = villager.getBrain();
        LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingEntity, true));
        return livingEntity;
    }

    private void displayCyclingItems(Villager villager) {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
            ++this.displayIndex;
            this.cycleCounter = 0;
            if (this.displayIndex > this.displayItems.size() - 1) {
                this.displayIndex = 0;
            }
            villager.setItemSlot(EquipmentSlot.MAINHAND, this.displayItems.get(this.displayIndex));
        }
    }

    @Override
    public /* synthetic */ boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        return this.canStillUse(serverLevel, (Villager)livingEntity, l);
    }

    @Override
    public /* synthetic */ void stop(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.stop(serverLevel, (Villager)livingEntity, l);
    }

    @Override
    public /* synthetic */ void tick(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.tick(serverLevel, (Villager)livingEntity, l);
    }

    @Override
    public /* synthetic */ void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.start(serverLevel, (Villager)livingEntity, l);
    }
}


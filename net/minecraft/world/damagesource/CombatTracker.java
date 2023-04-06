/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CombatTracker {
    private final List<CombatEntry> entries = Lists.newArrayList();
    private final LivingEntity mob;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    private String nextLocation;

    public CombatTracker(LivingEntity livingEntity) {
        this.mob = livingEntity;
    }

    public void prepareForDamage() {
        this.resetPreparedStatus();
        Optional<BlockPos> optional = this.mob.getLastClimbablePos();
        if (optional.isPresent()) {
            BlockState blockState = this.mob.level.getBlockState(optional.get());
            this.nextLocation = blockState.is(Blocks.LADDER) || blockState.is(BlockTags.TRAPDOORS) ? "ladder" : (blockState.is(Blocks.VINE) ? "vines" : (blockState.is(Blocks.WEEPING_VINES) || blockState.is(Blocks.WEEPING_VINES_PLANT) ? "weeping_vines" : (blockState.is(Blocks.TWISTING_VINES) || blockState.is(Blocks.TWISTING_VINES_PLANT) ? "twisting_vines" : (blockState.is(Blocks.SCAFFOLDING) ? "scaffolding" : "other_climbable"))));
        } else if (this.mob.isInWater()) {
            this.nextLocation = "water";
        }
    }

    public void recordDamage(DamageSource damageSource, float f, float f2) {
        this.recheckStatus();
        this.prepareForDamage();
        CombatEntry combatEntry = new CombatEntry(damageSource, this.mob.tickCount, f, f2, this.nextLocation, this.mob.fallDistance);
        this.entries.add(combatEntry);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (combatEntry.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
            this.inCombat = true;
            this.combatEndTime = this.combatStartTime = this.mob.tickCount;
            this.mob.onEnterCombat();
        }
    }

    public Component getDeathMessage() {
        Component component;
        if (this.entries.isEmpty()) {
            return new TranslatableComponent("death.attack.generic", this.mob.getDisplayName());
        }
        CombatEntry combatEntry = this.getMostSignificantFall();
        CombatEntry combatEntry2 = this.entries.get(this.entries.size() - 1);
        Component component2 = combatEntry2.getAttackerName();
        Entity entity = combatEntry2.getSource().getEntity();
        if (combatEntry != null && combatEntry2.getSource() == DamageSource.FALL) {
            Component component3 = combatEntry.getAttackerName();
            if (combatEntry.getSource() == DamageSource.FALL || combatEntry.getSource() == DamageSource.OUT_OF_WORLD) {
                component = new TranslatableComponent("death.fell.accident." + this.getFallLocation(combatEntry), this.mob.getDisplayName());
            } else if (!(component3 == null || component2 != null && component3.equals(component2))) {
                ItemStack itemStack;
                Entity entity2 = combatEntry.getSource().getEntity();
                ItemStack itemStack2 = itemStack = entity2 instanceof LivingEntity ? ((LivingEntity)entity2).getMainHandItem() : ItemStack.EMPTY;
                component = !itemStack.isEmpty() && itemStack.hasCustomHoverName() ? new TranslatableComponent("death.fell.assist.item", this.mob.getDisplayName(), component3, itemStack.getDisplayName()) : new TranslatableComponent("death.fell.assist", this.mob.getDisplayName(), component3);
            } else if (component2 != null) {
                ItemStack itemStack;
                ItemStack itemStack3 = itemStack = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandItem() : ItemStack.EMPTY;
                component = !itemStack.isEmpty() && itemStack.hasCustomHoverName() ? new TranslatableComponent("death.fell.finish.item", this.mob.getDisplayName(), component2, itemStack.getDisplayName()) : new TranslatableComponent("death.fell.finish", this.mob.getDisplayName(), component2);
            } else {
                component = new TranslatableComponent("death.fell.killer", this.mob.getDisplayName());
            }
        } else {
            component = combatEntry2.getSource().getLocalizedDeathMessage(this.mob);
        }
        return component;
    }

    @Nullable
    public LivingEntity getKiller() {
        LivingEntity livingEntity = null;
        Player player = null;
        float f = 0.0f;
        float f2 = 0.0f;
        for (CombatEntry combatEntry : this.entries) {
            if (combatEntry.getSource().getEntity() instanceof Player && (player == null || combatEntry.getDamage() > f2)) {
                f2 = combatEntry.getDamage();
                player = (Player)combatEntry.getSource().getEntity();
            }
            if (!(combatEntry.getSource().getEntity() instanceof LivingEntity) || livingEntity != null && !(combatEntry.getDamage() > f)) continue;
            f = combatEntry.getDamage();
            livingEntity = (LivingEntity)combatEntry.getSource().getEntity();
        }
        if (player != null && f2 >= f / 3.0f) {
            return player;
        }
        return livingEntity;
    }

    @Nullable
    private CombatEntry getMostSignificantFall() {
        CombatEntry combatEntry = null;
        CombatEntry combatEntry2 = null;
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i = 0; i < this.entries.size(); ++i) {
            CombatEntry combatEntry3;
            CombatEntry combatEntry4 = this.entries.get(i);
            CombatEntry combatEntry5 = combatEntry3 = i > 0 ? this.entries.get(i - 1) : null;
            if ((combatEntry4.getSource() == DamageSource.FALL || combatEntry4.getSource() == DamageSource.OUT_OF_WORLD) && combatEntry4.getFallDistance() > 0.0f && (combatEntry == null || combatEntry4.getFallDistance() > f2)) {
                combatEntry = i > 0 ? combatEntry3 : combatEntry4;
                f2 = combatEntry4.getFallDistance();
            }
            if (combatEntry4.getLocation() == null || combatEntry2 != null && !(combatEntry4.getDamage() > f)) continue;
            combatEntry2 = combatEntry4;
            f = combatEntry4.getDamage();
        }
        if (f2 > 5.0f && combatEntry != null) {
            return combatEntry;
        }
        if (f > 5.0f && combatEntry2 != null) {
            return combatEntry2;
        }
        return null;
    }

    private String getFallLocation(CombatEntry combatEntry) {
        return combatEntry.getLocation() == null ? "generic" : combatEntry.getLocation();
    }

    public int getCombatDuration() {
        if (this.inCombat) {
            return this.mob.tickCount - this.combatStartTime;
        }
        return this.combatEndTime - this.combatStartTime;
    }

    private void resetPreparedStatus() {
        this.nextLocation = null;
    }

    public void recheckStatus() {
        int n;
        int n2 = n = this.inCombat ? 300 : 100;
        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > n)) {
            boolean bl = this.inCombat;
            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if (bl) {
                this.mob.onLeaveCombat();
            }
            this.entries.clear();
        }
    }

    public LivingEntity getMob() {
        return this.mob;
    }
}


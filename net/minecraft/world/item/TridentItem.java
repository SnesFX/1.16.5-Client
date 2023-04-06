/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TridentItem
extends Item
implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public TridentItem(Item.Properties properties) {
        super(properties);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.put((Object)Attributes.ATTACK_DAMAGE, (Object)new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0, AttributeModifier.Operation.ADDITION));
        builder.put((Object)Attributes.ATTACK_SPEED, (Object)new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9000000953674316, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player2 = (Player)livingEntity;
        int n2 = this.getUseDuration(itemStack) - n;
        if (n2 < 10) {
            return;
        }
        int n3 = EnchantmentHelper.getRiptide(itemStack);
        if (n3 > 0 && !player2.isInWaterOrRain()) {
            return;
        }
        if (!level.isClientSide) {
            itemStack.hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(livingEntity.getUsedItemHand()));
            if (n3 == 0) {
                ThrownTrident thrownTrident = new ThrownTrident(level, player2, itemStack);
                thrownTrident.shootFromRotation(player2, player2.xRot, player2.yRot, 0.0f, 2.5f + (float)n3 * 0.5f, 1.0f);
                if (player2.abilities.instabuild) {
                    thrownTrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                level.addFreshEntity(thrownTrident);
                level.playSound(null, thrownTrident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (!player2.abilities.instabuild) {
                    player2.inventory.removeItem(itemStack);
                }
            }
        }
        player2.awardStat(Stats.ITEM_USED.get(this));
        if (n3 > 0) {
            float f = player2.yRot;
            float f2 = player2.xRot;
            float f3 = -Mth.sin(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f4 = -Mth.sin(f2 * 0.017453292f);
            float f5 = Mth.cos(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
            float f7 = 3.0f * ((1.0f + (float)n3) / 4.0f);
            player2.push(f3 *= f7 / f6, f4 *= f7 / f6, f5 *= f7 / f6);
            player2.startAutoSpinAttack(20);
            if (player2.isOnGround()) {
                float f8 = 1.1999999f;
                player2.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
            }
            SoundEvent soundEvent = n3 >= 3 ? SoundEvents.TRIDENT_RIPTIDE_3 : (n3 == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1);
            level.playSound(null, player2, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (EnchantmentHelper.getRiptide(itemStack) > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemStack);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity3) {
        itemStack.hurtAndBreak(1, livingEntity3, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
        if ((double)blockState.getDestroySpeed(level, blockPos) != 0.0) {
            itemStack.hurtAndBreak(2, livingEntity2, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}


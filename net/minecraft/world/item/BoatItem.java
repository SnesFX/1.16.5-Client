/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem
extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Boat.Type type;

    public BoatItem(Boat.Type type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        Object object;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = BoatItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (((HitResult)blockHitResult).getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        }
        Vec3 vec3 = player.getViewVector(1.0f);
        double d = 5.0;
        List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
        if (!list.isEmpty()) {
            object = player.getEyePosition(1.0f);
            for (Entity entity : list) {
                AABB aABB = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (!aABB.contains((Vec3)object)) continue;
                return InteractionResultHolder.pass(itemStack);
            }
        }
        if (((HitResult)blockHitResult).getType() == HitResult.Type.BLOCK) {
            object = new Boat(level, blockHitResult.getLocation().x, blockHitResult.getLocation().y, blockHitResult.getLocation().z);
            ((Boat)object).setType(this.type);
            ((Boat)object).yRot = player.yRot;
            if (!level.noCollision((Entity)object, ((Entity)object).getBoundingBox().inflate(-0.1))) {
                return InteractionResultHolder.fail(itemStack);
            }
            if (!level.isClientSide) {
                level.addFreshEntity((Entity)object);
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                }
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemStack);
    }
}


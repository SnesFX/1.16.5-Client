/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DyeItem
extends Item {
    private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
    private final DyeColor dyeColor;

    public DyeItem(DyeColor dyeColor, Item.Properties properties) {
        super(properties);
        this.dyeColor = dyeColor;
        ITEM_BY_COLOR.put(dyeColor, this);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        Sheep sheep;
        if (livingEntity instanceof Sheep && (sheep = (Sheep)livingEntity).isAlive() && !sheep.isSheared() && sheep.getColor() != this.dyeColor) {
            if (!player.level.isClientSide) {
                sheep.setColor(this.dyeColor);
                itemStack.shrink(1);
            }
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public static DyeItem byColor(DyeColor dyeColor) {
        return ITEM_BY_COLOR.get(dyeColor);
    }
}


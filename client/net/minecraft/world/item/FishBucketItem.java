/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

public class FishBucketItem
extends BucketItem {
    private final EntityType<?> type;

    public FishBucketItem(EntityType<?> entityType, Fluid fluid, Item.Properties properties) {
        super(fluid, properties);
        this.type = entityType;
    }

    @Override
    public void checkExtraContent(Level level, ItemStack itemStack, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            this.spawn((ServerLevel)level, itemStack, blockPos);
        }
    }

    @Override
    protected void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos) {
        levelAccessor.playSound(player, blockPos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawn(ServerLevel serverLevel, ItemStack itemStack, BlockPos blockPos) {
        Entity entity = this.type.spawn(serverLevel, itemStack, null, blockPos, MobSpawnType.BUCKET, true, false);
        if (entity != null) {
            ((AbstractFish)entity).setFromBucket(true);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        CompoundTag compoundTag;
        if (this.type == EntityType.TROPICAL_FISH && (compoundTag = itemStack.getTag()) != null && compoundTag.contains("BucketVariantTag", 3)) {
            int n = compoundTag.getInt("BucketVariantTag");
            ChatFormatting[] arrchatFormatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String string = "color.minecraft." + TropicalFish.getBaseColor(n);
            String string2 = "color.minecraft." + TropicalFish.getPatternColor(n);
            for (int i = 0; i < TropicalFish.COMMON_VARIANTS.length; ++i) {
                if (n != TropicalFish.COMMON_VARIANTS[i]) continue;
                list.add(new TranslatableComponent(TropicalFish.getPredefinedName(i)).withStyle(arrchatFormatting));
                return;
            }
            list.add(new TranslatableComponent(TropicalFish.getFishTypeName(n)).withStyle(arrchatFormatting));
            TranslatableComponent translatableComponent = new TranslatableComponent(string);
            if (!string.equals(string2)) {
                translatableComponent.append(", ").append(new TranslatableComponent(string2));
            }
            translatableComponent.withStyle(arrchatFormatting);
            list.add(translatableComponent);
        }
    }
}


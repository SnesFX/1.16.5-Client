/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompassItem
extends Item
implements Vanishable {
    private static final Logger LOGGER = LogManager.getLogger();

    public CompassItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean isLodestoneCompass(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && (compoundTag.contains("LodestoneDimension") || compoundTag.contains("LodestonePos"));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return CompassItem.isLodestoneCompass(itemStack) || super.isFoil(itemStack);
    }

    public static Optional<ResourceKey<Level>> getLodestoneDimension(CompoundTag compoundTag) {
        return Level.RESOURCE_KEY_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("LodestoneDimension")).result();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int n, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        if (CompassItem.isLodestoneCompass(itemStack)) {
            CompoundTag compoundTag = itemStack.getOrCreateTag();
            if (compoundTag.contains("LodestoneTracked") && !compoundTag.getBoolean("LodestoneTracked")) {
                return;
            }
            Optional<ResourceKey<Level>> optional = CompassItem.getLodestoneDimension(compoundTag);
            if (optional.isPresent() && optional.get() == level.dimension() && compoundTag.contains("LodestonePos") && !((ServerLevel)level).getPoiManager().existsAtPosition(PoiType.LODESTONE, NbtUtils.readBlockPos(compoundTag.getCompound("LodestonePos")))) {
                compoundTag.remove("LodestonePos");
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockPos blockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        if (level.getBlockState(blockPos).is(Blocks.LODESTONE)) {
            boolean bl;
            level.playSound(null, blockPos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0f, 1.0f);
            Player player = useOnContext.getPlayer();
            ItemStack itemStack = useOnContext.getItemInHand();
            boolean bl2 = bl = !player.abilities.instabuild && itemStack.getCount() == 1;
            if (bl) {
                this.addLodestoneTags(level.dimension(), blockPos, itemStack.getOrCreateTag());
            } else {
                ItemStack itemStack2 = new ItemStack(Items.COMPASS, 1);
                CompoundTag compoundTag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
                itemStack2.setTag(compoundTag);
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                }
                this.addLodestoneTags(level.dimension(), blockPos, compoundTag);
                if (!player.inventory.add(itemStack2)) {
                    player.drop(itemStack2, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(useOnContext);
    }

    private void addLodestoneTags(ResourceKey<Level> resourceKey, BlockPos blockPos, CompoundTag compoundTag) {
        compoundTag.put("LodestonePos", NbtUtils.writeBlockPos(blockPos));
        Level.RESOURCE_KEY_CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, resourceKey).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("LodestoneDimension", (Tag)tag));
        compoundTag.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return CompassItem.isLodestoneCompass(itemStack) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(itemStack);
    }
}


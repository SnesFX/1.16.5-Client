/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.color.item;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemColors {
    private final IdMapper<ItemColor> itemColors = new IdMapper(32);

    public static ItemColors createDefault(BlockColors blockColors) {
        ItemColors itemColors = new ItemColors();
        itemColors.register((itemStack, n) -> n > 0 ? -1 : ((DyeableLeatherItem)((Object)itemStack.getItem())).getColor(itemStack), Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
        itemColors.register((itemStack, n) -> GrassColor.get(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        itemColors.register((itemStack, n) -> {
            int[] arrn;
            if (n != 1) {
                return -1;
            }
            CompoundTag compoundTag = itemStack.getTagElement("Explosion");
            int[] arrn2 = arrn = compoundTag != null && compoundTag.contains("Colors", 11) ? compoundTag.getIntArray("Colors") : null;
            if (arrn == null || arrn.length == 0) {
                return 9079434;
            }
            if (arrn.length == 1) {
                return arrn[0];
            }
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            for (int n5 : arrn) {
                n2 += (n5 & 0xFF0000) >> 16;
                n3 += (n5 & 0xFF00) >> 8;
                n4 += (n5 & 0xFF) >> 0;
            }
            return (n2 /= arrn.length) << 16 | (n3 /= arrn.length) << 8 | (n4 /= arrn.length);
        }, Items.FIREWORK_STAR);
        itemColors.register((itemStack, n) -> n > 0 ? -1 : PotionUtils.getColor(itemStack), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        for (SpawnEggItem spawnEggItem : SpawnEggItem.eggs()) {
            itemColors.register((itemStack, n) -> spawnEggItem.getColor(n), spawnEggItem);
        }
        itemColors.register((itemStack, n) -> {
            BlockState blockState = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockState, null, null, n);
        }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
        itemColors.register((itemStack, n) -> n == 0 ? PotionUtils.getColor(itemStack) : -1, Items.TIPPED_ARROW);
        itemColors.register((itemStack, n) -> n == 0 ? -1 : MapItem.getColor(itemStack), Items.FILLED_MAP);
        return itemColors;
    }

    public int getColor(ItemStack itemStack, int n) {
        ItemColor itemColor = this.itemColors.byId(Registry.ITEM.getId(itemStack.getItem()));
        return itemColor == null ? -1 : itemColor.getColor(itemStack, n);
    }

    public void register(ItemColor itemColor, ItemLike ... arritemLike) {
        for (ItemLike itemLike : arritemLike) {
            this.itemColors.addMapping(itemColor, Item.getId(itemLike.asItem()));
        }
    }
}


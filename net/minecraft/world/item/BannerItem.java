/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.Validate;

public class BannerItem
extends StandingAndWallBlockItem {
    public BannerItem(Block block, Block block2, Item.Properties properties) {
        super(block, block2, properties);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)block);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)block2);
    }

    public static void appendHoverTextFromBannerBlockEntityTag(ItemStack itemStack, List<Component> list) {
        CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
        if (compoundTag == null || !compoundTag.contains("Patterns")) {
            return;
        }
        ListTag listTag = compoundTag.getList("Patterns", 10);
        for (int i = 0; i < listTag.size() && i < 6; ++i) {
            CompoundTag compoundTag2 = listTag.getCompound(i);
            DyeColor dyeColor = DyeColor.byId(compoundTag2.getInt("Color"));
            BannerPattern bannerPattern = BannerPattern.byHash(compoundTag2.getString("Pattern"));
            if (bannerPattern == null) continue;
            list.add(new TranslatableComponent("block.minecraft.banner." + bannerPattern.getFilename() + '.' + dyeColor.getName()).withStyle(ChatFormatting.GRAY));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(itemStack, list);
    }
}


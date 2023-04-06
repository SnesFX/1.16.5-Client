/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadItem
extends StandingAndWallBlockItem {
    public PlayerHeadItem(Block block, Block block2, Item.Properties properties) {
        super(block, block2, properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.getItem() == Items.PLAYER_HEAD && itemStack.hasTag()) {
            CompoundTag compoundTag;
            String string = null;
            CompoundTag compoundTag2 = itemStack.getTag();
            if (compoundTag2.contains("SkullOwner", 8)) {
                string = compoundTag2.getString("SkullOwner");
            } else if (compoundTag2.contains("SkullOwner", 10) && (compoundTag = compoundTag2.getCompound("SkullOwner")).contains("Name", 8)) {
                string = compoundTag.getString("Name");
            }
            if (string != null) {
                return new TranslatableComponent(this.getDescriptionId() + ".named", string);
            }
        }
        return super.getName(itemStack);
    }

    @Override
    public boolean verifyTagAfterLoad(CompoundTag compoundTag) {
        super.verifyTagAfterLoad(compoundTag);
        if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)compoundTag.getString("SkullOwner"))) {
            GameProfile gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
            gameProfile = SkullBlockEntity.updateGameprofile(gameProfile);
            compoundTag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));
            return true;
        }
        return false;
    }
}


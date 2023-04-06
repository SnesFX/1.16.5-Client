/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.trading;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantOffers
extends ArrayList<MerchantOffer> {
    public MerchantOffers() {
    }

    public MerchantOffers(CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList("Recipes", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            this.add(new MerchantOffer(listTag.getCompound(i)));
        }
    }

    @Nullable
    public MerchantOffer getRecipeFor(ItemStack itemStack, ItemStack itemStack2, int n) {
        if (n > 0 && n < this.size()) {
            MerchantOffer merchantOffer = (MerchantOffer)this.get(n);
            if (merchantOffer.satisfiedBy(itemStack, itemStack2)) {
                return merchantOffer;
            }
            return null;
        }
        for (int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantOffer = (MerchantOffer)this.get(i);
            if (!merchantOffer.satisfiedBy(itemStack, itemStack2)) continue;
            return merchantOffer;
        }
        return null;
    }

    public void writeToStream(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte((byte)(this.size() & 0xFF));
        for (int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantOffer = (MerchantOffer)this.get(i);
            friendlyByteBuf.writeItem(merchantOffer.getBaseCostA());
            friendlyByteBuf.writeItem(merchantOffer.getResult());
            ItemStack itemStack = merchantOffer.getCostB();
            friendlyByteBuf.writeBoolean(!itemStack.isEmpty());
            if (!itemStack.isEmpty()) {
                friendlyByteBuf.writeItem(itemStack);
            }
            friendlyByteBuf.writeBoolean(merchantOffer.isOutOfStock());
            friendlyByteBuf.writeInt(merchantOffer.getUses());
            friendlyByteBuf.writeInt(merchantOffer.getMaxUses());
            friendlyByteBuf.writeInt(merchantOffer.getXp());
            friendlyByteBuf.writeInt(merchantOffer.getSpecialPriceDiff());
            friendlyByteBuf.writeFloat(merchantOffer.getPriceMultiplier());
            friendlyByteBuf.writeInt(merchantOffer.getDemand());
        }
    }

    public static MerchantOffers createFromStream(FriendlyByteBuf friendlyByteBuf) {
        MerchantOffers merchantOffers = new MerchantOffers();
        int n = friendlyByteBuf.readByte() & 0xFF;
        for (int i = 0; i < n; ++i) {
            ItemStack itemStack = friendlyByteBuf.readItem();
            ItemStack itemStack2 = friendlyByteBuf.readItem();
            ItemStack itemStack3 = ItemStack.EMPTY;
            if (friendlyByteBuf.readBoolean()) {
                itemStack3 = friendlyByteBuf.readItem();
            }
            boolean bl = friendlyByteBuf.readBoolean();
            int n2 = friendlyByteBuf.readInt();
            int n3 = friendlyByteBuf.readInt();
            int n4 = friendlyByteBuf.readInt();
            int n5 = friendlyByteBuf.readInt();
            float f = friendlyByteBuf.readFloat();
            int n6 = friendlyByteBuf.readInt();
            MerchantOffer merchantOffer = new MerchantOffer(itemStack, itemStack3, itemStack2, n2, n3, n4, f, n6);
            if (bl) {
                merchantOffer.setToOutOfStock();
            }
            merchantOffer.setSpecialPriceDiff(n5);
            merchantOffers.add(merchantOffer);
        }
        return merchantOffers;
    }

    public CompoundTag createTag() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantOffer = (MerchantOffer)this.get(i);
            listTag.add(merchantOffer.createTag());
        }
        compoundTag.put("Recipes", listTag);
        return compoundTag;
    }
}


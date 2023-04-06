/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentSerializer
implements ArgumentSerializer<IntegerArgumentType> {
    @Override
    public void serializeToNetwork(IntegerArgumentType integerArgumentType, FriendlyByteBuf friendlyByteBuf) {
        boolean bl = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
        boolean bl2 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
        friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(bl, bl2));
        if (bl) {
            friendlyByteBuf.writeInt(integerArgumentType.getMinimum());
        }
        if (bl2) {
            friendlyByteBuf.writeInt(integerArgumentType.getMaximum());
        }
    }

    @Override
    public IntegerArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        byte by = friendlyByteBuf.readByte();
        int n = BrigadierArgumentSerializers.numberHasMin(by) ? friendlyByteBuf.readInt() : Integer.MIN_VALUE;
        int n2 = BrigadierArgumentSerializers.numberHasMax(by) ? friendlyByteBuf.readInt() : Integer.MAX_VALUE;
        return IntegerArgumentType.integer((int)n, (int)n2);
    }

    @Override
    public void serializeToJson(IntegerArgumentType integerArgumentType, JsonObject jsonObject) {
        if (integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)integerArgumentType.getMinimum());
        }
        if (integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)integerArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return this.deserializeFromNetwork(friendlyByteBuf);
    }
}


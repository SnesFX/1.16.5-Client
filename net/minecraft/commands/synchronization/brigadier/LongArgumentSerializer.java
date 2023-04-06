/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentSerializer
implements ArgumentSerializer<LongArgumentType> {
    @Override
    public void serializeToNetwork(LongArgumentType longArgumentType, FriendlyByteBuf friendlyByteBuf) {
        boolean bl = longArgumentType.getMinimum() != Long.MIN_VALUE;
        boolean bl2 = longArgumentType.getMaximum() != Long.MAX_VALUE;
        friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(bl, bl2));
        if (bl) {
            friendlyByteBuf.writeLong(longArgumentType.getMinimum());
        }
        if (bl2) {
            friendlyByteBuf.writeLong(longArgumentType.getMaximum());
        }
    }

    @Override
    public LongArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        byte by = friendlyByteBuf.readByte();
        long l = BrigadierArgumentSerializers.numberHasMin(by) ? friendlyByteBuf.readLong() : Long.MIN_VALUE;
        long l2 = BrigadierArgumentSerializers.numberHasMax(by) ? friendlyByteBuf.readLong() : Long.MAX_VALUE;
        return LongArgumentType.longArg((long)l, (long)l2);
    }

    @Override
    public void serializeToJson(LongArgumentType longArgumentType, JsonObject jsonObject) {
        if (longArgumentType.getMinimum() != Long.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)longArgumentType.getMinimum());
        }
        if (longArgumentType.getMaximum() != Long.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)longArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return this.deserializeFromNetwork(friendlyByteBuf);
    }
}


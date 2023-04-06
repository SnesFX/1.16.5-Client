/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentSerializer
implements ArgumentSerializer<DoubleArgumentType> {
    @Override
    public void serializeToNetwork(DoubleArgumentType doubleArgumentType, FriendlyByteBuf friendlyByteBuf) {
        boolean bl = doubleArgumentType.getMinimum() != -1.7976931348623157E308;
        boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
        friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(bl, bl2));
        if (bl) {
            friendlyByteBuf.writeDouble(doubleArgumentType.getMinimum());
        }
        if (bl2) {
            friendlyByteBuf.writeDouble(doubleArgumentType.getMaximum());
        }
    }

    @Override
    public DoubleArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        byte by = friendlyByteBuf.readByte();
        double d = BrigadierArgumentSerializers.numberHasMin(by) ? friendlyByteBuf.readDouble() : -1.7976931348623157E308;
        double d2 = BrigadierArgumentSerializers.numberHasMax(by) ? friendlyByteBuf.readDouble() : Double.MAX_VALUE;
        return DoubleArgumentType.doubleArg((double)d, (double)d2);
    }

    @Override
    public void serializeToJson(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
        if (doubleArgumentType.getMinimum() != -1.7976931348623157E308) {
            jsonObject.addProperty("min", (Number)doubleArgumentType.getMinimum());
        }
        if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)doubleArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return this.deserializeFromNetwork(friendlyByteBuf);
    }
}


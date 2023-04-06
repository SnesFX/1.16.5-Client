/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentSerializer
implements ArgumentSerializer<FloatArgumentType> {
    @Override
    public void serializeToNetwork(FloatArgumentType floatArgumentType, FriendlyByteBuf friendlyByteBuf) {
        boolean bl = floatArgumentType.getMinimum() != -3.4028235E38f;
        boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
        friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(bl, bl2));
        if (bl) {
            friendlyByteBuf.writeFloat(floatArgumentType.getMinimum());
        }
        if (bl2) {
            friendlyByteBuf.writeFloat(floatArgumentType.getMaximum());
        }
    }

    @Override
    public FloatArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        byte by = friendlyByteBuf.readByte();
        float f = BrigadierArgumentSerializers.numberHasMin(by) ? friendlyByteBuf.readFloat() : -3.4028235E38f;
        float f2 = BrigadierArgumentSerializers.numberHasMax(by) ? friendlyByteBuf.readFloat() : Float.MAX_VALUE;
        return FloatArgumentType.floatArg((float)f, (float)f2);
    }

    @Override
    public void serializeToJson(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
        if (floatArgumentType.getMinimum() != -3.4028235E38f) {
            jsonObject.addProperty("min", (Number)Float.valueOf(floatArgumentType.getMinimum()));
        }
        if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)Float.valueOf(floatArgumentType.getMaximum()));
        }
    }

    @Override
    public /* synthetic */ ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return this.deserializeFromNetwork(friendlyByteBuf);
    }
}


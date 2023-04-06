/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
    public void serializeToNetwork(T var1, FriendlyByteBuf var2);

    public T deserializeFromNetwork(FriendlyByteBuf var1);

    public void serializeToJson(T var1, JsonObject var2);
}


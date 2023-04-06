/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public interface ParticleOptions {
    public ParticleType<?> getType();

    public void writeToNetwork(FriendlyByteBuf var1);

    public String writeToString();

    @Deprecated
    public static interface Deserializer<T extends ParticleOptions> {
        public T fromCommand(ParticleType<T> var1, StringReader var2) throws CommandSyntaxException;

        public T fromNetwork(ParticleType<T> var1, FriendlyByteBuf var2);
    }

}


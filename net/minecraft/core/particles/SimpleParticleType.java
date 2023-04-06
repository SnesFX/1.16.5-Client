/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SimpleParticleType
extends ParticleType<SimpleParticleType>
implements ParticleOptions {
    private static final ParticleOptions.Deserializer<SimpleParticleType> DESERIALIZER = new ParticleOptions.Deserializer<SimpleParticleType>(){

        @Override
        public SimpleParticleType fromCommand(ParticleType<SimpleParticleType> particleType, StringReader stringReader) throws CommandSyntaxException {
            return (SimpleParticleType)particleType;
        }

        @Override
        public SimpleParticleType fromNetwork(ParticleType<SimpleParticleType> particleType, FriendlyByteBuf friendlyByteBuf) {
            return (SimpleParticleType)particleType;
        }

        @Override
        public /* synthetic */ ParticleOptions fromNetwork(ParticleType particleType, FriendlyByteBuf friendlyByteBuf) {
            return this.fromNetwork(particleType, friendlyByteBuf);
        }

        @Override
        public /* synthetic */ ParticleOptions fromCommand(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
            return this.fromCommand(particleType, stringReader);
        }
    };
    private final Codec<SimpleParticleType> codec = Codec.unit(this::getType);

    protected SimpleParticleType(boolean bl) {
        super(bl, DESERIALIZER);
    }

    public SimpleParticleType getType() {
        return this;
    }

    @Override
    public Codec<SimpleParticleType> codec() {
        return this.codec;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this).toString();
    }

    public /* synthetic */ ParticleType getType() {
        return this.getType();
    }

}


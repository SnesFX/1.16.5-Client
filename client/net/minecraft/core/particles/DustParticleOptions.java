/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DustParticleOptions
implements ParticleOptions {
    public static final DustParticleOptions REDSTONE = new DustParticleOptions(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Codec<DustParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("r").forGetter(dustParticleOptions -> Float.valueOf(dustParticleOptions.r)), (App)Codec.FLOAT.fieldOf("g").forGetter(dustParticleOptions -> Float.valueOf(dustParticleOptions.g)), (App)Codec.FLOAT.fieldOf("b").forGetter(dustParticleOptions -> Float.valueOf(dustParticleOptions.b)), (App)Codec.FLOAT.fieldOf("scale").forGetter(dustParticleOptions -> Float.valueOf(dustParticleOptions.scale))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> DustParticleOptions.new(arg_0, arg_1, arg_2, arg_3)));
    public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>(){

        @Override
        public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float f2 = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float f3 = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float f4 = (float)stringReader.readDouble();
            return new DustParticleOptions(f, f2, f3, f4);
        }

        @Override
        public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
            return new DustParticleOptions(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
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
    private final float r;
    private final float g;
    private final float b;
    private final float scale;

    public DustParticleOptions(float f, float f2, float f3, float f4) {
        this.r = f;
        this.g = f2;
        this.b = f3;
        this.scale = Mth.clamp(f4, 0.01f, 4.0f);
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(this.r);
        friendlyByteBuf.writeFloat(this.g);
        friendlyByteBuf.writeFloat(this.b);
        friendlyByteBuf.writeFloat(this.scale);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf(this.r), Float.valueOf(this.g), Float.valueOf(this.b), Float.valueOf(this.scale));
    }

    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public float getScale() {
        return this.scale;
    }

}


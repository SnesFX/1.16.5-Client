/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class NoneDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<NoneDecoratorConfiguration> CODEC = Codec.unit(() -> INSTANCE);
    public static final NoneDecoratorConfiguration INSTANCE = new NoneDecoratorConfiguration();
}


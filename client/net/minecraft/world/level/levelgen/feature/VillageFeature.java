/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class VillageFeature
extends JigsawFeature {
    public VillageFeature(Codec<JigsawConfiguration> codec) {
        super(codec, 0, true, true);
    }
}


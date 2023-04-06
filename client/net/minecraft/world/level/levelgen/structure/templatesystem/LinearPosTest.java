/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
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
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class LinearPosTest
extends PosRuleTest {
    public static final Codec<LinearPosTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(linearPosTest -> Float.valueOf(linearPosTest.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(linearPosTest -> Float.valueOf(linearPosTest.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter(linearPosTest -> linearPosTest.minDist), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter(linearPosTest -> linearPosTest.maxDist)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> LinearPosTest.new(arg_0, arg_1, arg_2, arg_3)));
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;

    public LinearPosTest(float f, float f2, int n, int n2) {
        if (n >= n2) {
            throw new IllegalArgumentException("Invalid range: [" + n + "," + n2 + "]");
        }
        this.minChance = f;
        this.maxChance = f2;
        this.minDist = n;
        this.maxDist = n2;
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        int n = blockPos2.distManhattan(blockPos3);
        float f = random.nextFloat();
        return (double)f <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp(n, this.minDist, this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.LINEAR_POS_TEST;
    }
}


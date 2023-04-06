/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
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
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class AxisAlignedLinearPosTest
extends PosRuleTest {
    public static final Codec<AxisAlignedLinearPosTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(axisAlignedLinearPosTest -> Float.valueOf(axisAlignedLinearPosTest.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf(0.0f)).forGetter(axisAlignedLinearPosTest -> Float.valueOf(axisAlignedLinearPosTest.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter(axisAlignedLinearPosTest -> axisAlignedLinearPosTest.minDist), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter(axisAlignedLinearPosTest -> axisAlignedLinearPosTest.maxDist), (App)Direction.Axis.CODEC.fieldOf("axis").orElse((Object)Direction.Axis.Y).forGetter(axisAlignedLinearPosTest -> axisAlignedLinearPosTest.axis)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> AxisAlignedLinearPosTest.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;
    private final Direction.Axis axis;

    public AxisAlignedLinearPosTest(float f, float f2, int n, int n2, Direction.Axis axis) {
        if (n >= n2) {
            throw new IllegalArgumentException("Invalid range: [" + n + "," + n2 + "]");
        }
        this.minChance = f;
        this.maxChance = f2;
        this.minDist = n;
        this.maxDist = n2;
        this.axis = axis;
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        float f = Math.abs((blockPos2.getX() - blockPos3.getX()) * direction.getStepX());
        float f2 = Math.abs((blockPos2.getY() - blockPos3.getY()) * direction.getStepY());
        float f3 = Math.abs((blockPos2.getZ() - blockPos3.getZ()) * direction.getStepZ());
        int n = (int)(f + f2 + f3);
        float f4 = random.nextFloat();
        return (double)f4 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp(n, this.minDist, this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS_TEST;
    }
}


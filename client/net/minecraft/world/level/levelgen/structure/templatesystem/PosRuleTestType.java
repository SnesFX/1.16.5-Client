/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LinearPosTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;

public interface PosRuleTestType<P extends PosRuleTest> {
    public static final PosRuleTestType<PosAlwaysTrueTest> ALWAYS_TRUE_TEST = PosRuleTestType.register("always_true", PosAlwaysTrueTest.CODEC);
    public static final PosRuleTestType<LinearPosTest> LINEAR_POS_TEST = PosRuleTestType.register("linear_pos", LinearPosTest.CODEC);
    public static final PosRuleTestType<AxisAlignedLinearPosTest> AXIS_ALIGNED_LINEAR_POS_TEST = PosRuleTestType.register("axis_aligned_linear_pos", AxisAlignedLinearPosTest.CODEC);

    public Codec<P> codec();

    public static <P extends PosRuleTest> PosRuleTestType<P> register(String string, Codec<P> codec) {
        return Registry.register(Registry.POS_RULE_TEST, string, () -> codec);
    }
}


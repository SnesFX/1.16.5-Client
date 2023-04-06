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
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class ProcessorRule {
    public static final Codec<ProcessorRule> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RuleTest.CODEC.fieldOf("input_predicate").forGetter(processorRule -> processorRule.inputPredicate), (App)RuleTest.CODEC.fieldOf("location_predicate").forGetter(processorRule -> processorRule.locPredicate), (App)PosRuleTest.CODEC.optionalFieldOf("position_predicate", (Object)PosAlwaysTrueTest.INSTANCE).forGetter(processorRule -> processorRule.posPredicate), (App)BlockState.CODEC.fieldOf("output_state").forGetter(processorRule -> processorRule.outputState), (App)CompoundTag.CODEC.optionalFieldOf("output_nbt").forGetter(processorRule -> Optional.ofNullable(processorRule.outputTag))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> ProcessorRule.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
    private final RuleTest inputPredicate;
    private final RuleTest locPredicate;
    private final PosRuleTest posPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag outputTag;

    public ProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, BlockState blockState) {
        this(ruleTest, ruleTest2, PosAlwaysTrueTest.INSTANCE, blockState, Optional.empty());
    }

    public ProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, PosRuleTest posRuleTest, BlockState blockState) {
        this(ruleTest, ruleTest2, posRuleTest, blockState, Optional.empty());
    }

    public ProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, PosRuleTest posRuleTest, BlockState blockState, Optional<CompoundTag> optional) {
        this.inputPredicate = ruleTest;
        this.locPredicate = ruleTest2;
        this.posPredicate = posRuleTest;
        this.outputState = blockState;
        this.outputTag = optional.orElse(null);
    }

    public boolean test(BlockState blockState, BlockState blockState2, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        return this.inputPredicate.test(blockState, random) && this.locPredicate.test(blockState2, random) && this.posPredicate.test(blockPos, blockPos2, blockPos3, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getOutputTag() {
        return this.outputTag;
    }
}


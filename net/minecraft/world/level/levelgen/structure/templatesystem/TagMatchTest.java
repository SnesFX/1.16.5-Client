/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class TagMatchTest
extends RuleTest {
    public static final Codec<TagMatchTest> CODEC = Tag.codec(() -> SerializationTags.getInstance().getBlocks()).fieldOf("tag").xmap(TagMatchTest::new, tagMatchTest -> tagMatchTest.tag).codec();
    private final Tag<Block> tag;

    public TagMatchTest(Tag<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState blockState, Random random) {
        return blockState.is(this.tag);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.TAG_TEST;
    }
}


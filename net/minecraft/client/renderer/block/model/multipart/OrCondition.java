/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class OrCondition
implements Condition {
    private final Iterable<? extends Condition> conditions;

    public OrCondition(Iterable<? extends Condition> iterable) {
        this.conditions = iterable;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> stateDefinition) {
        List list = Streams.stream(this.conditions).map(condition -> condition.getPredicate(stateDefinition)).collect(Collectors.toList());
        return blockState -> list.stream().anyMatch(predicate -> predicate.test(blockState));
    }
}


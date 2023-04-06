/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DirectionProperty
extends EnumProperty<Direction> {
    protected DirectionProperty(String string, Collection<Direction> collection) {
        super(string, Direction.class, collection);
    }

    public static DirectionProperty create(String string, Predicate<Direction> predicate) {
        return DirectionProperty.create(string, Arrays.stream(Direction.values()).filter(predicate).collect(Collectors.toList()));
    }

    public static DirectionProperty create(String string, Direction ... arrdirection) {
        return DirectionProperty.create(string, Lists.newArrayList((Object[])arrdirection));
    }

    public static DirectionProperty create(String string, Collection<Direction> collection) {
        return new DirectionProperty(string, collection);
    }
}


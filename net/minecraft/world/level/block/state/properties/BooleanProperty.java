/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;

public class BooleanProperty
extends Property<Boolean> {
    private final ImmutableSet<Boolean> values = ImmutableSet.of((Object)true, (Object)false);

    protected BooleanProperty(String string) {
        super(string, Boolean.class);
    }

    @Override
    public Collection<Boolean> getPossibleValues() {
        return this.values;
    }

    public static BooleanProperty create(String string) {
        return new BooleanProperty(string);
    }

    @Override
    public Optional<Boolean> getValue(String string) {
        if ("true".equals(string) || "false".equals(string)) {
            return Optional.of(Boolean.valueOf(string));
        }
        return Optional.empty();
    }

    @Override
    public String getName(Boolean bl) {
        return bl.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BooleanProperty && super.equals(object)) {
            BooleanProperty booleanProperty = (BooleanProperty)object;
            return this.values.equals(booleanProperty.values);
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }
}


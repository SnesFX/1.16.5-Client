/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeSupplier {
    private final Map<Attribute, AttributeInstance> instances;

    public AttributeSupplier(Map<Attribute, AttributeInstance> map) {
        this.instances = ImmutableMap.copyOf(map);
    }

    private AttributeInstance getAttributeInstance(Attribute attribute) {
        AttributeInstance attributeInstance = this.instances.get(attribute);
        if (attributeInstance == null) {
            throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getKey(attribute));
        }
        return attributeInstance;
    }

    public double getValue(Attribute attribute) {
        return this.getAttributeInstance(attribute).getValue();
    }

    public double getBaseValue(Attribute attribute) {
        return this.getAttributeInstance(attribute).getBaseValue();
    }

    public double getModifierValue(Attribute attribute, UUID uUID) {
        AttributeModifier attributeModifier = this.getAttributeInstance(attribute).getModifier(uUID);
        if (attributeModifier == null) {
            throw new IllegalArgumentException("Can't find modifier " + uUID + " on attribute " + Registry.ATTRIBUTE.getKey(attribute));
        }
        return attributeModifier.getAmount();
    }

    @Nullable
    public AttributeInstance createInstance(Consumer<AttributeInstance> consumer, Attribute attribute) {
        AttributeInstance attributeInstance = this.instances.get(attribute);
        if (attributeInstance == null) {
            return null;
        }
        AttributeInstance attributeInstance2 = new AttributeInstance(attribute, consumer);
        attributeInstance2.replaceFrom(attributeInstance);
        return attributeInstance2;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasAttribute(Attribute attribute) {
        return this.instances.containsKey(attribute);
    }

    public boolean hasModifier(Attribute attribute, UUID uUID) {
        AttributeInstance attributeInstance = this.instances.get(attribute);
        return attributeInstance != null && attributeInstance.getModifier(uUID) != null;
    }

    public static class Builder {
        private final Map<Attribute, AttributeInstance> builder = Maps.newHashMap();
        private boolean instanceFrozen;

        private AttributeInstance create(Attribute attribute) {
            AttributeInstance attributeInstance2 = new AttributeInstance(attribute, attributeInstance -> {
                if (this.instanceFrozen) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(attribute));
                }
            });
            this.builder.put(attribute, attributeInstance2);
            return attributeInstance2;
        }

        public Builder add(Attribute attribute) {
            this.create(attribute);
            return this;
        }

        public Builder add(Attribute attribute, double d) {
            AttributeInstance attributeInstance = this.create(attribute);
            attributeInstance.setBaseValue(d);
            return this;
        }

        public AttributeSupplier build() {
            this.instanceFrozen = true;
            return new AttributeSupplier(this.builder);
        }
    }

}


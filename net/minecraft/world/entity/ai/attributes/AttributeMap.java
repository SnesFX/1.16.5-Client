/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeMap {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Attribute, AttributeInstance> attributes = Maps.newHashMap();
    private final Set<AttributeInstance> dirtyAttributes = Sets.newHashSet();
    private final AttributeSupplier supplier;

    public AttributeMap(AttributeSupplier attributeSupplier) {
        this.supplier = attributeSupplier;
    }

    private void onAttributeModified(AttributeInstance attributeInstance) {
        if (attributeInstance.getAttribute().isClientSyncable()) {
            this.dirtyAttributes.add(attributeInstance);
        }
    }

    public Set<AttributeInstance> getDirtyAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeInstance> getSyncableAttributes() {
        return this.attributes.values().stream().filter(attributeInstance -> attributeInstance.getAttribute().isClientSyncable()).collect(Collectors.toList());
    }

    @Nullable
    public AttributeInstance getInstance(Attribute attribute2) {
        return this.attributes.computeIfAbsent(attribute2, attribute -> this.supplier.createInstance(this::onAttributeModified, (Attribute)attribute));
    }

    public boolean hasAttribute(Attribute attribute) {
        return this.attributes.get(attribute) != null || this.supplier.hasAttribute(attribute);
    }

    public boolean hasModifier(Attribute attribute, UUID uUID) {
        AttributeInstance attributeInstance = this.attributes.get(attribute);
        return attributeInstance != null ? attributeInstance.getModifier(uUID) != null : this.supplier.hasModifier(attribute, uUID);
    }

    public double getValue(Attribute attribute) {
        AttributeInstance attributeInstance = this.attributes.get(attribute);
        return attributeInstance != null ? attributeInstance.getValue() : this.supplier.getValue(attribute);
    }

    public double getBaseValue(Attribute attribute) {
        AttributeInstance attributeInstance = this.attributes.get(attribute);
        return attributeInstance != null ? attributeInstance.getBaseValue() : this.supplier.getBaseValue(attribute);
    }

    public double getModifierValue(Attribute attribute, UUID uUID) {
        AttributeInstance attributeInstance = this.attributes.get(attribute);
        return attributeInstance != null ? attributeInstance.getModifier(uUID).getAmount() : this.supplier.getModifierValue(attribute, uUID);
    }

    public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> multimap) {
        multimap.asMap().forEach((attribute, collection) -> {
            AttributeInstance attributeInstance = this.attributes.get(attribute);
            if (attributeInstance != null) {
                collection.forEach(attributeInstance::removeModifier);
            }
        });
    }

    public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> multimap) {
        multimap.forEach((attribute, attributeModifier) -> {
            AttributeInstance attributeInstance = this.getInstance((Attribute)attribute);
            if (attributeInstance != null) {
                attributeInstance.removeModifier((AttributeModifier)attributeModifier);
                attributeInstance.addTransientModifier((AttributeModifier)attributeModifier);
            }
        });
    }

    public void assignValues(AttributeMap attributeMap) {
        attributeMap.attributes.values().forEach(attributeInstance -> {
            AttributeInstance attributeInstance2 = this.getInstance(attributeInstance.getAttribute());
            if (attributeInstance2 != null) {
                attributeInstance2.replaceFrom((AttributeInstance)attributeInstance);
            }
        });
    }

    public ListTag save() {
        ListTag listTag = new ListTag();
        for (AttributeInstance attributeInstance : this.attributes.values()) {
            listTag.add(attributeInstance.save());
        }
        return listTag;
    }

    public void load(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            String string = compoundTag.getString("Name");
            Util.ifElse(Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(string)), attribute -> {
                AttributeInstance attributeInstance = this.getInstance((Attribute)attribute);
                if (attributeInstance != null) {
                    attributeInstance.load(compoundTag);
                }
            }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", (Object)string));
        }
    }
}


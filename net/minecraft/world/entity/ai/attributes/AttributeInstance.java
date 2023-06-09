/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeInstance {
    private final Attribute attribute;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap();
    private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet();
    private double baseValue;
    private boolean dirty = true;
    private double cachedValue;
    private final Consumer<AttributeInstance> onDirty;

    public AttributeInstance(Attribute attribute, Consumer<AttributeInstance> consumer) {
        this.attribute = attribute;
        this.onDirty = consumer;
        this.baseValue = attribute.getDefaultValue();
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double d) {
        if (d == this.baseValue) {
            return;
        }
        this.baseValue = d;
        this.setDirty();
    }

    public Set<AttributeModifier> getModifiers(AttributeModifier.Operation operation2) {
        return this.modifiersByOperation.computeIfAbsent(operation2, operation -> Sets.newHashSet());
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifierById.values());
    }

    @Nullable
    public AttributeModifier getModifier(UUID uUID) {
        return this.modifierById.get(uUID);
    }

    public boolean hasModifier(AttributeModifier attributeModifier) {
        return this.modifierById.get(attributeModifier.getId()) != null;
    }

    private void addModifier(AttributeModifier attributeModifier) {
        AttributeModifier attributeModifier2 = this.modifierById.putIfAbsent(attributeModifier.getId(), attributeModifier);
        if (attributeModifier2 != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        this.getModifiers(attributeModifier.getOperation()).add(attributeModifier);
        this.setDirty();
    }

    public void addTransientModifier(AttributeModifier attributeModifier) {
        this.addModifier(attributeModifier);
    }

    public void addPermanentModifier(AttributeModifier attributeModifier) {
        this.addModifier(attributeModifier);
        this.permanentModifiers.add(attributeModifier);
    }

    protected void setDirty() {
        this.dirty = true;
        this.onDirty.accept(this);
    }

    public void removeModifier(AttributeModifier attributeModifier) {
        this.getModifiers(attributeModifier.getOperation()).remove(attributeModifier);
        this.modifierById.remove(attributeModifier.getId());
        this.permanentModifiers.remove(attributeModifier);
        this.setDirty();
    }

    public void removeModifier(UUID uUID) {
        AttributeModifier attributeModifier = this.getModifier(uUID);
        if (attributeModifier != null) {
            this.removeModifier(attributeModifier);
        }
    }

    public boolean removePermanentModifier(UUID uUID) {
        AttributeModifier attributeModifier = this.getModifier(uUID);
        if (attributeModifier != null && this.permanentModifiers.contains(attributeModifier)) {
            this.removeModifier(attributeModifier);
            return true;
        }
        return false;
    }

    public void removeModifiers() {
        for (AttributeModifier attributeModifier : this.getModifiers()) {
            this.removeModifier(attributeModifier);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }
        return this.cachedValue;
    }

    private double calculateValue() {
        double d = this.getBaseValue();
        for (AttributeModifier attributeModifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
            d += attributeModifier.getAmount();
        }
        double d2 = d;
        for (AttributeModifier attributeModifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
            d2 += d * attributeModifier.getAmount();
        }
        for (AttributeModifier attributeModifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            d2 *= 1.0 + attributeModifier.getAmount();
        }
        return this.attribute.sanitizeValue(d2);
    }

    private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation operation) {
        return this.modifiersByOperation.getOrDefault((Object)operation, Collections.emptySet());
    }

    public void replaceFrom(AttributeInstance attributeInstance) {
        this.baseValue = attributeInstance.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll(attributeInstance.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.addAll(attributeInstance.permanentModifiers);
        this.modifiersByOperation.clear();
        attributeInstance.modifiersByOperation.forEach((operation, set) -> this.getModifiers((AttributeModifier.Operation)((Object)operation)).addAll((Collection<AttributeModifier>)set));
        this.setDirty();
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", Registry.ATTRIBUTE.getKey(this.attribute).toString());
        compoundTag.putDouble("Base", this.baseValue);
        if (!this.permanentModifiers.isEmpty()) {
            ListTag listTag = new ListTag();
            for (AttributeModifier attributeModifier : this.permanentModifiers) {
                listTag.add(attributeModifier.save());
            }
            compoundTag.put("Modifiers", listTag);
        }
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        this.baseValue = compoundTag.getDouble("Base");
        if (compoundTag.contains("Modifiers", 9)) {
            ListTag listTag = compoundTag.getList("Modifiers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                AttributeModifier attributeModifier = AttributeModifier.load(listTag.getCompound(i));
                if (attributeModifier == null) continue;
                this.modifierById.put(attributeModifier.getId(), attributeModifier);
                this.getModifiers(attributeModifier.getOperation()).add(attributeModifier);
                this.permanentModifiers.add(attributeModifier);
            }
        }
        this.setDirty();
    }
}


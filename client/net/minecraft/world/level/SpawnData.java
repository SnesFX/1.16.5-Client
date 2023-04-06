/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.WeighedRandom;

public class SpawnData
extends WeighedRandom.WeighedRandomItem {
    private final CompoundTag tag;

    public SpawnData() {
        super(1);
        this.tag = new CompoundTag();
        this.tag.putString("id", "minecraft:pig");
    }

    public SpawnData(CompoundTag compoundTag) {
        this(compoundTag.contains("Weight", 99) ? compoundTag.getInt("Weight") : 1, compoundTag.getCompound("Entity"));
    }

    public SpawnData(int n, CompoundTag compoundTag) {
        super(n);
        this.tag = compoundTag;
        ResourceLocation resourceLocation = ResourceLocation.tryParse(compoundTag.getString("id"));
        if (resourceLocation != null) {
            compoundTag.putString("id", resourceLocation.toString());
        } else {
            compoundTag.putString("id", "minecraft:pig");
        }
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Entity", this.tag);
        compoundTag.putInt("Weight", this.weight);
        return compoundTag;
    }

    public CompoundTag getTag() {
        return this.tag;
    }
}


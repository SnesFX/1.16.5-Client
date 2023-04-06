/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public enum MobCategory implements StringRepresentable
{
    MONSTER("monster", 70, false, false, 128),
    CREATURE("creature", 10, true, true, 128),
    AMBIENT("ambient", 15, true, false, 128),
    WATER_CREATURE("water_creature", 5, true, false, 128),
    WATER_AMBIENT("water_ambient", 20, true, false, 64),
    MISC("misc", -1, true, true, 128);
    
    public static final Codec<MobCategory> CODEC;
    private static final Map<String, MobCategory> BY_NAME;
    private final int max;
    private final boolean isFriendly;
    private final boolean isPersistent;
    private final String name;
    private final int noDespawnDistance = 32;
    private final int despawnDistance;

    private MobCategory(String string2, int n2, boolean bl, boolean bl2, int n3) {
        this.name = string2;
        this.max = n2;
        this.isFriendly = bl;
        this.isPersistent = bl2;
        this.despawnDistance = n3;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static MobCategory byName(String string) {
        return BY_NAME.get(string);
    }

    public int getMaxInstancesPerChunk() {
        return this.max;
    }

    public boolean isFriendly() {
        return this.isFriendly;
    }

    public boolean isPersistent() {
        return this.isPersistent;
    }

    public int getDespawnDistance() {
        return this.despawnDistance;
    }

    public int getNoDespawnDistance() {
        return 32;
    }

    static {
        CODEC = StringRepresentable.fromEnum(MobCategory::values, MobCategory::byName);
        BY_NAME = Arrays.stream(MobCategory.values()).collect(Collectors.toMap(MobCategory::getName, mobCategory -> mobCategory));
    }
}


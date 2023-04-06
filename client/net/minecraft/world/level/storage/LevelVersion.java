/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import net.minecraft.SharedConstants;

public class LevelVersion {
    private final int levelDataVersion;
    private final long lastPlayed;
    private final String minecraftVersionName;
    private final int minecraftVersion;
    private final boolean snapshot;

    public LevelVersion(int n, long l, String string, int n2, boolean bl) {
        this.levelDataVersion = n;
        this.lastPlayed = l;
        this.minecraftVersionName = string;
        this.minecraftVersion = n2;
        this.snapshot = bl;
    }

    public static LevelVersion parse(Dynamic<?> dynamic) {
        int n = dynamic.get("version").asInt(0);
        long l = dynamic.get("LastPlayed").asLong(0L);
        OptionalDynamic optionalDynamic = dynamic.get("Version");
        if (optionalDynamic.result().isPresent()) {
            return new LevelVersion(n, l, optionalDynamic.get("Name").asString(SharedConstants.getCurrentVersion().getName()), optionalDynamic.get("Id").asInt(SharedConstants.getCurrentVersion().getWorldVersion()), optionalDynamic.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable()));
        }
        return new LevelVersion(n, l, "", 0, false);
    }

    public int levelDataVersion() {
        return this.levelDataVersion;
    }

    public long lastPlayed() {
        return this.lastPlayed;
    }

    public String minecraftVersionName() {
        return this.minecraftVersionName;
    }

    public int minecraftVersion() {
        return this.minecraftVersion;
    }

    public boolean snapshot() {
        return this.snapshot;
    }
}


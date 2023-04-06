/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.ServerLevelData;

public interface WorldData {
    public DataPackConfig getDataPackConfig();

    public void setDataPackConfig(DataPackConfig var1);

    public boolean wasModded();

    public Set<String> getKnownServerBrands();

    public void setModdedInfo(String var1, boolean var2);

    default public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        crashReportCategory.setDetail("Known server brands", () -> String.join((CharSequence)", ", this.getKnownServerBrands()));
        crashReportCategory.setDetail("Level was modded", () -> Boolean.toString(this.wasModded()));
        crashReportCategory.setDetail("Level storage version", () -> {
            int n = this.getVersion();
            return String.format("0x%05X - %s", n, this.getStorageVersionName(n));
        });
    }

    default public String getStorageVersionName(int n) {
        switch (n) {
            case 19133: {
                return "Anvil";
            }
            case 19132: {
                return "McRegion";
            }
        }
        return "Unknown?";
    }

    @Nullable
    public CompoundTag getCustomBossEvents();

    public void setCustomBossEvents(@Nullable CompoundTag var1);

    public ServerLevelData overworldData();

    public LevelSettings getLevelSettings();

    public CompoundTag createTag(RegistryAccess var1, @Nullable CompoundTag var2);

    public boolean isHardcore();

    public int getVersion();

    public String getLevelName();

    public GameType getGameType();

    public void setGameType(GameType var1);

    public boolean getAllowCommands();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRules();

    public CompoundTag getLoadedPlayerTag();

    public CompoundTag endDragonFightData();

    public void setEndDragonFightData(CompoundTag var1);

    public WorldGenSettings worldGenSettings();

    public Lifecycle worldGenSettingsLifecycle();
}


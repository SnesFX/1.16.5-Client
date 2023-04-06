/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.timers.TimerQueue;

public interface ServerLevelData
extends WritableLevelData {
    public String getLevelName();

    public void setThundering(boolean var1);

    public int getRainTime();

    public void setRainTime(int var1);

    public void setThunderTime(int var1);

    public int getThunderTime();

    @Override
    default public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        WritableLevelData.super.fillCrashReportCategory(crashReportCategory);
        crashReportCategory.setDetail("Level name", this::getLevelName);
        crashReportCategory.setDetail("Level game mode", () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.getAllowCommands()));
        crashReportCategory.setDetail("Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()));
    }

    public int getClearWeatherTime();

    public void setClearWeatherTime(int var1);

    public int getWanderingTraderSpawnDelay();

    public void setWanderingTraderSpawnDelay(int var1);

    public int getWanderingTraderSpawnChance();

    public void setWanderingTraderSpawnChance(int var1);

    public void setWanderingTraderId(UUID var1);

    public GameType getGameType();

    public void setWorldBorder(WorldBorder.Settings var1);

    public WorldBorder.Settings getWorldBorder();

    public boolean isInitialized();

    public void setInitialized(boolean var1);

    public boolean getAllowCommands();

    public void setGameType(GameType var1);

    public TimerQueue<MinecraftServer> getScheduledEvents();

    public void setGameTime(long var1);

    public void setDayTime(long var1);
}


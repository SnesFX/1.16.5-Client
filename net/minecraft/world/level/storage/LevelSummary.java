/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelVersion;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelSettings settings;
    private final LevelVersion levelVersion;
    private final String levelId;
    private final boolean requiresConversion;
    private final boolean locked;
    private final File icon;
    @Nullable
    private Component info;

    public LevelSummary(LevelSettings levelSettings, LevelVersion levelVersion, String string, boolean bl, boolean bl2, File file) {
        this.settings = levelSettings;
        this.levelVersion = levelVersion;
        this.levelId = string;
        this.locked = bl2;
        this.icon = file;
        this.requiresConversion = bl;
    }

    public String getLevelId() {
        return this.levelId;
    }

    public String getLevelName() {
        return StringUtils.isEmpty((CharSequence)this.settings.levelName()) ? this.levelId : this.settings.levelName();
    }

    public File getIcon() {
        return this.icon;
    }

    public boolean isRequiresConversion() {
        return this.requiresConversion;
    }

    public long getLastPlayed() {
        return this.levelVersion.lastPlayed();
    }

    @Override
    public int compareTo(LevelSummary levelSummary) {
        if (this.levelVersion.lastPlayed() < levelSummary.levelVersion.lastPlayed()) {
            return 1;
        }
        if (this.levelVersion.lastPlayed() > levelSummary.levelVersion.lastPlayed()) {
            return -1;
        }
        return this.levelId.compareTo(levelSummary.levelId);
    }

    public GameType getGameMode() {
        return this.settings.gameType();
    }

    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    public boolean hasCheats() {
        return this.settings.allowCommands();
    }

    public MutableComponent getWorldVersionName() {
        if (StringUtil.isNullOrEmpty(this.levelVersion.minecraftVersionName())) {
            return new TranslatableComponent("selectWorld.versionUnknown");
        }
        return new TextComponent(this.levelVersion.minecraftVersionName());
    }

    public LevelVersion levelVersion() {
        return this.levelVersion;
    }

    public boolean markVersionInList() {
        return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.shouldBackup();
    }

    public boolean askToOpenWorld() {
        return this.levelVersion.minecraftVersion() > SharedConstants.getCurrentVersion().getWorldVersion();
    }

    public boolean shouldBackup() {
        return this.levelVersion.minecraftVersion() < SharedConstants.getCurrentVersion().getWorldVersion();
    }

    public boolean isLocked() {
        return this.locked;
    }

    public Component getInfo() {
        if (this.info == null) {
            this.info = this.createInfo();
        }
        return this.info;
    }

    private Component createInfo() {
        MutableComponent mutableComponent;
        if (this.isLocked()) {
            return new TranslatableComponent("selectWorld.locked").withStyle(ChatFormatting.RED);
        }
        if (this.isRequiresConversion()) {
            return new TranslatableComponent("selectWorld.conversion");
        }
        MutableComponent mutableComponent2 = mutableComponent = this.isHardcore() ? new TextComponent("").append(new TranslatableComponent("gameMode.hardcore").withStyle(ChatFormatting.DARK_RED)) : new TranslatableComponent("gameMode." + this.getGameMode().getName());
        if (this.hasCheats()) {
            mutableComponent.append(", ").append(new TranslatableComponent("selectWorld.cheats"));
        }
        MutableComponent mutableComponent3 = this.getWorldVersionName();
        MutableComponent mutableComponent4 = new TextComponent(", ").append(new TranslatableComponent("selectWorld.version")).append(" ");
        if (this.markVersionInList()) {
            mutableComponent4.append(mutableComponent3.withStyle(this.askToOpenWorld() ? ChatFormatting.RED : ChatFormatting.ITALIC));
        } else {
            mutableComponent4.append(mutableComponent3);
        }
        mutableComponent.append(mutableComponent4);
        return mutableComponent;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LevelSummary)object);
    }
}


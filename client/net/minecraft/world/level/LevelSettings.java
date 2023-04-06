/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.world.level;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public final class LevelSettings {
    private final String levelName;
    private final GameType gameType;
    private final boolean hardcore;
    private final Difficulty difficulty;
    private final boolean allowCommands;
    private final GameRules gameRules;
    private final DataPackConfig dataPackConfig;

    public LevelSettings(String string, GameType gameType, boolean bl, Difficulty difficulty, boolean bl2, GameRules gameRules, DataPackConfig dataPackConfig) {
        this.levelName = string;
        this.gameType = gameType;
        this.hardcore = bl;
        this.difficulty = difficulty;
        this.allowCommands = bl2;
        this.gameRules = gameRules;
        this.dataPackConfig = dataPackConfig;
    }

    public static LevelSettings parse(Dynamic<?> dynamic, DataPackConfig dataPackConfig) {
        GameType gameType;
        return new LevelSettings(dynamic.get("LevelName").asString(""), gameType, dynamic.get("hardcore").asBoolean(false), dynamic.get("Difficulty").asNumber().map(number -> Difficulty.byId(number.byteValue())).result().orElse(Difficulty.NORMAL), dynamic.get("allowCommands").asBoolean((gameType = GameType.byId(dynamic.get("GameType").asInt(0))) == GameType.CREATIVE), new GameRules((DynamicLike<?>)dynamic.get("GameRules")), dataPackConfig);
    }

    public String levelName() {
        return this.levelName;
    }

    public GameType gameType() {
        return this.gameType;
    }

    public boolean hardcore() {
        return this.hardcore;
    }

    public Difficulty difficulty() {
        return this.difficulty;
    }

    public boolean allowCommands() {
        return this.allowCommands;
    }

    public GameRules gameRules() {
        return this.gameRules;
    }

    public DataPackConfig getDataPackConfig() {
        return this.dataPackConfig;
    }

    public LevelSettings withGameType(GameType gameType) {
        return new LevelSettings(this.levelName, gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataPackConfig);
    }

    public LevelSettings withDifficulty(Difficulty difficulty) {
        return new LevelSettings(this.levelName, this.gameType, this.hardcore, difficulty, this.allowCommands, this.gameRules, this.dataPackConfig);
    }

    public LevelSettings withDataPackConfig(DataPackConfig dataPackConfig) {
        return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, dataPackConfig);
    }

    public LevelSettings copy() {
        return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataPackConfig);
    }
}


/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;

public class RealmsWorldOptions
extends ValueObject {
    public Boolean pvp;
    public Boolean spawnAnimals;
    public Boolean spawnMonsters;
    public Boolean spawnNPCs;
    public Integer spawnProtection;
    public Boolean commandBlocks;
    public Boolean forceGameMode;
    public Integer difficulty;
    public Integer gameMode;
    public String slotName;
    public long templateId;
    public String templateImage;
    public boolean adventureMap;
    public boolean empty;
    private static final String DEFAULT_TEMPLATE_IMAGE = null;

    public RealmsWorldOptions(Boolean bl, Boolean bl2, Boolean bl3, Boolean bl4, Integer n, Boolean bl5, Integer n2, Integer n3, Boolean bl6, String string) {
        this.pvp = bl;
        this.spawnAnimals = bl2;
        this.spawnMonsters = bl3;
        this.spawnNPCs = bl4;
        this.spawnProtection = n;
        this.commandBlocks = bl5;
        this.difficulty = n2;
        this.gameMode = n3;
        this.forceGameMode = bl6;
        this.slotName = string;
    }

    public static RealmsWorldOptions createDefaults() {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
    }

    public static RealmsWorldOptions createEmptyDefaults() {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.createDefaults();
        realmsWorldOptions.setEmpty(true);
        return realmsWorldOptions;
    }

    public void setEmpty(boolean bl) {
        this.empty = bl;
    }

    public static RealmsWorldOptions parse(JsonObject jsonObject) {
        RealmsWorldOptions realmsWorldOptions = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", jsonObject, true), JsonUtils.getBooleanOr("spawnAnimals", jsonObject, true), JsonUtils.getBooleanOr("spawnMonsters", jsonObject, true), JsonUtils.getBooleanOr("spawnNPCs", jsonObject, true), JsonUtils.getIntOr("spawnProtection", jsonObject, 0), JsonUtils.getBooleanOr("commandBlocks", jsonObject, false), JsonUtils.getIntOr("difficulty", jsonObject, 2), JsonUtils.getIntOr("gameMode", jsonObject, 0), JsonUtils.getBooleanOr("forceGameMode", jsonObject, false), JsonUtils.getStringOr("slotName", jsonObject, ""));
        realmsWorldOptions.templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, -1L);
        realmsWorldOptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, DEFAULT_TEMPLATE_IMAGE);
        realmsWorldOptions.adventureMap = JsonUtils.getBooleanOr("adventureMap", jsonObject, false);
        return realmsWorldOptions;
    }

    public String getSlotName(int n) {
        if (this.slotName == null || this.slotName.isEmpty()) {
            if (this.empty) {
                return I18n.get("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName(n);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int n) {
        return I18n.get("mco.configure.world.slot", n);
    }

    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        if (!this.pvp.booleanValue()) {
            jsonObject.addProperty("pvp", this.pvp);
        }
        if (!this.spawnAnimals.booleanValue()) {
            jsonObject.addProperty("spawnAnimals", this.spawnAnimals);
        }
        if (!this.spawnMonsters.booleanValue()) {
            jsonObject.addProperty("spawnMonsters", this.spawnMonsters);
        }
        if (!this.spawnNPCs.booleanValue()) {
            jsonObject.addProperty("spawnNPCs", this.spawnNPCs);
        }
        if (this.spawnProtection != 0) {
            jsonObject.addProperty("spawnProtection", (Number)this.spawnProtection);
        }
        if (this.commandBlocks.booleanValue()) {
            jsonObject.addProperty("commandBlocks", this.commandBlocks);
        }
        if (this.difficulty != 2) {
            jsonObject.addProperty("difficulty", (Number)this.difficulty);
        }
        if (this.gameMode != 0) {
            jsonObject.addProperty("gameMode", (Number)this.gameMode);
        }
        if (this.forceGameMode.booleanValue()) {
            jsonObject.addProperty("forceGameMode", this.forceGameMode);
        }
        if (!Objects.equals(this.slotName, "")) {
            jsonObject.addProperty("slotName", this.slotName);
        }
        return jsonObject.toString();
    }

    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }
}


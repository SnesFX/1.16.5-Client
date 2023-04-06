/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.internal.Streams
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.OptionalDynamic
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.OptionalDynamic;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, (Object)new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>(){};
    private final DataFixer dataFixer;
    private final PlayerList playerList;
    private final File file;
    private final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
    private final Set<Advancement> visible = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
    private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
    private ServerPlayer player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket = true;

    public PlayerAdvancements(DataFixer dataFixer, PlayerList playerList, ServerAdvancementManager serverAdvancementManager, File file, ServerPlayer serverPlayer) {
        this.dataFixer = dataFixer;
        this.playerList = playerList;
        this.file = file;
        this.player = serverPlayer;
        this.load(serverAdvancementManager);
    }

    public void setPlayer(ServerPlayer serverPlayer) {
        this.player = serverPlayer;
    }

    public void stopListening() {
        for (CriterionTrigger<?> criterionTrigger : CriteriaTriggers.all()) {
            criterionTrigger.removePlayerListeners(this);
        }
    }

    public void reload(ServerAdvancementManager serverAdvancementManager) {
        this.stopListening();
        this.advancements.clear();
        this.visible.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.load(serverAdvancementManager);
    }

    private void registerListeners(ServerAdvancementManager serverAdvancementManager) {
        for (Advancement advancement : serverAdvancementManager.getAllAdvancements()) {
            this.registerListeners(advancement);
        }
    }

    private void ensureAllVisible() {
        ArrayList arrayList = Lists.newArrayList();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
            if (!entry.getValue().isDone()) continue;
            arrayList.add(entry.getKey());
            this.progressChanged.add((Advancement)entry.getKey());
        }
        for (Map.Entry<Advancement, AdvancementProgress> entry : arrayList) {
            this.ensureVisibility((Advancement)((Object)entry));
        }
    }

    private void checkForAutomaticTriggers(ServerAdvancementManager serverAdvancementManager) {
        for (Advancement advancement : serverAdvancementManager.getAllAdvancements()) {
            if (!advancement.getCriteria().isEmpty()) continue;
            this.award(advancement, "");
            advancement.getRewards().grant(this.player);
        }
    }

    private void load(ServerAdvancementManager serverAdvancementManager) {
        if (this.file.isFile()) {
            try {
                try (JsonReader jsonReader = new JsonReader((Reader)new StringReader(Files.toString((File)this.file, (Charset)StandardCharsets.UTF_8)));){
                    jsonReader.setLenient(false);
                    Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Streams.parse((JsonReader)jsonReader));
                    if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                        dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                    }
                    dynamic = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.getType(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
                    dynamic = dynamic.remove("DataVersion");
                    Map map = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)dynamic.getValue());
                    if (map == null) {
                        throw new JsonParseException("Found null for advancements");
                    }
                    Stream<Map.Entry> stream = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                    for (Map.Entry entry : stream.collect(Collectors.toList())) {
                        Advancement advancement = serverAdvancementManager.getAdvancement((ResourceLocation)entry.getKey());
                        if (advancement == null) {
                            LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), (Object)this.file);
                            continue;
                        }
                        this.startProgress(advancement, (AdvancementProgress)entry.getValue());
                    }
                }
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.file, (Object)jsonParseException);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.file, (Object)iOException);
            }
        }
        this.checkForAutomaticTriggers(serverAdvancementManager);
        this.ensureAllVisible();
        this.registerListeners(serverAdvancementManager);
    }

    public void save() {
        Object object;
        HashMap hashMap = Maps.newHashMap();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
            object = entry.getValue();
            if (!((AdvancementProgress)object).hasProgress()) continue;
            hashMap.put(entry.getKey().getId(), object);
        }
        if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
        }
        JsonElement jsonElement = GSON.toJsonTree((Object)hashMap);
        jsonElement.getAsJsonObject().addProperty("DataVersion", (Number)SharedConstants.getCurrentVersion().getWorldVersion());
        try {
            Map.Entry<Advancement, AdvancementProgress> entry;
            entry = new FileOutputStream(this.file);
            object = null;
            try {
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)((Object)entry), Charsets.UTF_8.newEncoder());){
                    GSON.toJson(jsonElement, (Appendable)outputStreamWriter);
                }
            }
            catch (Throwable throwable) {
                object = throwable;
                throw throwable;
            }
            finally {
                if (entry != null) {
                    if (object != null) {
                        try {
                            ((OutputStream)((Object)entry)).close();
                        }
                        catch (Throwable throwable) {
                            ((Throwable)object).addSuppressed(throwable);
                        }
                    } else {
                        ((OutputStream)((Object)entry)).close();
                    }
                }
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.file, (Object)iOException);
        }
    }

    public boolean award(Advancement advancement, String string) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.grantProgress(string)) {
            this.unregisterListeners(advancement);
            this.progressChanged.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.getRewards().grant(this.player);
                if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
                    this.playerList.broadcastMessage(new TranslatableComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getName(), this.player.getDisplayName(), advancement.getChatComponent()), ChatType.SYSTEM, Util.NIL_UUID);
                }
            }
        }
        if (advancementProgress.isDone()) {
            this.ensureVisibility(advancement);
        }
        return bl;
    }

    public boolean revoke(Advancement advancement, String string) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        if (advancementProgress.revokeProgress(string)) {
            this.registerListeners(advancement);
            this.progressChanged.add(advancement);
            bl = true;
        }
        if (!advancementProgress.hasProgress()) {
            this.ensureVisibility(advancement);
        }
        return bl;
    }

    private void registerListeners(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        if (advancementProgress.isDone()) {
            return;
        }
        for (Map.Entry<String, Criterion> entry : advancement.getCriteria().entrySet()) {
            CriterionTrigger<CriterionTriggerInstance> criterionTrigger;
            CriterionTriggerInstance criterionTriggerInstance;
            CriterionProgress criterionProgress = advancementProgress.getCriterion(entry.getKey());
            if (criterionProgress == null || criterionProgress.isDone() || (criterionTriggerInstance = entry.getValue().getTrigger()) == null || (criterionTrigger = CriteriaTriggers.getCriterion(criterionTriggerInstance.getCriterion())) == null) continue;
            criterionTrigger.addPlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>(criterionTriggerInstance, advancement, entry.getKey()));
        }
    }

    private void unregisterListeners(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        for (Map.Entry<String, Criterion> entry : advancement.getCriteria().entrySet()) {
            CriterionTrigger<CriterionTriggerInstance> criterionTrigger;
            CriterionTriggerInstance criterionTriggerInstance;
            CriterionProgress criterionProgress = advancementProgress.getCriterion(entry.getKey());
            if (criterionProgress == null || !criterionProgress.isDone() && !advancementProgress.isDone() || (criterionTriggerInstance = entry.getValue().getTrigger()) == null || (criterionTrigger = CriteriaTriggers.getCriterion(criterionTriggerInstance.getCriterion())) == null) continue;
            criterionTrigger.removePlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>(criterionTriggerInstance, advancement, entry.getKey()));
        }
    }

    public void flushDirty(ServerPlayer serverPlayer) {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
            HashMap hashMap = Maps.newHashMap();
            LinkedHashSet linkedHashSet = Sets.newLinkedHashSet();
            LinkedHashSet linkedHashSet2 = Sets.newLinkedHashSet();
            for (Advancement advancement : this.progressChanged) {
                if (!this.visible.contains(advancement)) continue;
                hashMap.put(advancement.getId(), this.advancements.get(advancement));
            }
            for (Advancement advancement : this.visibilityChanged) {
                if (this.visible.contains(advancement)) {
                    linkedHashSet.add(advancement);
                    continue;
                }
                linkedHashSet2.add(advancement.getId());
            }
            if (this.isFirstPacket || !hashMap.isEmpty() || !linkedHashSet.isEmpty() || !linkedHashSet2.isEmpty()) {
                serverPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, linkedHashSet, linkedHashSet2, hashMap));
                this.visibilityChanged.clear();
                this.progressChanged.clear();
            }
        }
        this.isFirstPacket = false;
    }

    public void setSelectedTab(@Nullable Advancement advancement) {
        Advancement advancement2 = this.lastSelectedTab;
        this.lastSelectedTab = advancement != null && advancement.getParent() == null && advancement.getDisplay() != null ? advancement : null;
        if (advancement2 != this.lastSelectedTab) {
            this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
        }
    }

    public AdvancementProgress getOrStartProgress(Advancement advancement) {
        AdvancementProgress advancementProgress = this.advancements.get(advancement);
        if (advancementProgress == null) {
            advancementProgress = new AdvancementProgress();
            this.startProgress(advancement, advancementProgress);
        }
        return advancementProgress;
    }

    private void startProgress(Advancement advancement, AdvancementProgress advancementProgress) {
        advancementProgress.update(advancement.getCriteria(), advancement.getRequirements());
        this.advancements.put(advancement, advancementProgress);
    }

    private void ensureVisibility(Advancement advancement) {
        boolean bl = this.shouldBeVisible(advancement);
        boolean bl2 = this.visible.contains(advancement);
        if (bl && !bl2) {
            this.visible.add(advancement);
            this.visibilityChanged.add(advancement);
            if (this.advancements.containsKey(advancement)) {
                this.progressChanged.add(advancement);
            }
        } else if (!bl && bl2) {
            this.visible.remove(advancement);
            this.visibilityChanged.add(advancement);
        }
        if (bl != bl2 && advancement.getParent() != null) {
            this.ensureVisibility(advancement.getParent());
        }
        for (Advancement advancement2 : advancement.getChildren()) {
            this.ensureVisibility(advancement2);
        }
    }

    private boolean shouldBeVisible(Advancement advancement) {
        for (int n = 0; advancement != null && n <= 2; advancement = advancement.getParent(), ++n) {
            if (n == 0 && this.hasCompletedChildrenOrSelf(advancement)) {
                return true;
            }
            if (advancement.getDisplay() == null) {
                return false;
            }
            AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
            if (advancementProgress.isDone()) {
                return true;
            }
            if (!advancement.getDisplay().isHidden()) continue;
            return false;
        }
        return false;
    }

    private boolean hasCompletedChildrenOrSelf(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        if (advancementProgress.isDone()) {
            return true;
        }
        for (Advancement advancement2 : advancement.getChildren()) {
            if (!this.hasCompletedChildrenOrSelf(advancement2)) continue;
            return true;
        }
        return false;
    }

}


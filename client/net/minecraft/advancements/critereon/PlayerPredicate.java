/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public class PlayerPredicate {
    public static final PlayerPredicate ANY = new Builder().build();
    private final MinMaxBounds.Ints level;
    private final GameType gameType;
    private final Map<Stat<?>, MinMaxBounds.Ints> stats;
    private final Object2BooleanMap<ResourceLocation> recipes;
    private final Map<ResourceLocation, AdvancementPredicate> advancements;

    private static AdvancementPredicate advancementPredicateFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            boolean bl = jsonElement.getAsBoolean();
            return new AdvancementDonePredicate(bl);
        }
        Object2BooleanOpenHashMap object2BooleanOpenHashMap = new Object2BooleanOpenHashMap();
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "criterion data");
        jsonObject.entrySet().forEach(arg_0 -> PlayerPredicate.lambda$advancementPredicateFromJson$0((Object2BooleanMap)object2BooleanOpenHashMap, arg_0));
        return new AdvancementCriterionsPredicate((Object2BooleanMap<String>)object2BooleanOpenHashMap);
    }

    private PlayerPredicate(MinMaxBounds.Ints ints, GameType gameType, Map<Stat<?>, MinMaxBounds.Ints> map, Object2BooleanMap<ResourceLocation> object2BooleanMap, Map<ResourceLocation, AdvancementPredicate> map2) {
        this.level = ints;
        this.gameType = gameType;
        this.stats = map;
        this.recipes = object2BooleanMap;
        this.advancements = map2;
    }

    public boolean matches(Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof ServerPlayer)) {
            return false;
        }
        ServerPlayer serverPlayer = (ServerPlayer)entity;
        if (!this.level.matches(serverPlayer.experienceLevel)) {
            return false;
        }
        if (this.gameType != GameType.NOT_SET && this.gameType != serverPlayer.gameMode.getGameModeForPlayer()) {
            return false;
        }
        ServerStatsCounter serverStatsCounter = serverPlayer.getStats();
        for (Map.Entry<Stat<?>, MinMaxBounds.Ints> object : this.stats.entrySet()) {
            int serverAdvancementManager = serverStatsCounter.getValue(object.getKey());
            if (object.getValue().matches(serverAdvancementManager)) continue;
            return false;
        }
        ServerRecipeBook serverRecipeBook = serverPlayer.getRecipeBook();
        for (Object2BooleanMap.Entry entry : this.recipes.object2BooleanEntrySet()) {
            if (serverRecipeBook.contains((ResourceLocation)entry.getKey()) == entry.getBooleanValue()) continue;
            return false;
        }
        if (!this.advancements.isEmpty()) {
            PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
            ServerAdvancementManager serverAdvancementManager = serverPlayer.getServer().getAdvancements();
            for (Map.Entry<ResourceLocation, AdvancementPredicate> entry : this.advancements.entrySet()) {
                Advancement advancement = serverAdvancementManager.getAdvancement(entry.getKey());
                if (advancement != null && entry.getValue().test(playerAdvancements.getOrStartProgress(advancement))) continue;
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(@Nullable JsonElement jsonElement) {
        Object object;
        Object object2;
        Object object32;
        Object object4;
        JsonElement jsonElement22;
        Object object5;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "player");
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject.get("level"));
        String string = GsonHelper.getAsString(jsonObject, "gamemode", "");
        GameType gameType = GameType.byName(string, GameType.NOT_SET);
        HashMap hashMap = Maps.newHashMap();
        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "stats", null);
        if (jsonArray != null) {
            for (JsonElement jsonElement22 : jsonArray) {
                object4 = GsonHelper.convertToJsonObject(jsonElement22, "stats entry");
                object32 = new ResourceLocation(GsonHelper.getAsString((JsonObject)object4, "type"));
                object2 = Registry.STAT_TYPE.get((ResourceLocation)object32);
                if (object2 == null) {
                    throw new JsonParseException("Invalid stat type: " + object32);
                }
                Object object6 = new ResourceLocation(GsonHelper.getAsString((JsonObject)object4, "stat"));
                object = PlayerPredicate.getStat(object2, (ResourceLocation)object6);
                object5 = MinMaxBounds.Ints.fromJson(object4.get("value"));
                hashMap.put(object, object5);
            }
        }
        Iterator iterator = new Object2BooleanOpenHashMap();
        jsonElement22 = GsonHelper.getAsJsonObject(jsonObject, "recipes", new JsonObject());
        for (Object object32 : jsonElement22.entrySet()) {
            object2 = new ResourceLocation((String)object32.getKey());
            boolean bl = GsonHelper.convertToBoolean((JsonElement)object32.getValue(), "recipe present");
            iterator.put(object2, bl);
        }
        object4 = Maps.newHashMap();
        object32 = GsonHelper.getAsJsonObject(jsonObject, "advancements", new JsonObject());
        for (Object object6 : object32.entrySet()) {
            object = new ResourceLocation((String)object6.getKey());
            object5 = PlayerPredicate.advancementPredicateFromJson((JsonElement)object6.getValue());
            object4.put(object, object5);
        }
        return new PlayerPredicate(ints, gameType, hashMap, (Object2BooleanMap<ResourceLocation>)iterator, (Map<ResourceLocation, AdvancementPredicate>)object4);
    }

    private static <T> Stat<T> getStat(StatType<T> statType, ResourceLocation resourceLocation) {
        Registry<T> registry = statType.getRegistry();
        T t = registry.get(resourceLocation);
        if (t == null) {
            throw new JsonParseException("Unknown object " + resourceLocation + " for stat type " + Registry.STAT_TYPE.getKey(statType));
        }
        return statType.get(t);
    }

    private static <T> ResourceLocation getStatValueId(Stat<T> stat) {
        return stat.getType().getRegistry().getKey(stat.getValue());
    }

    public JsonElement serializeToJson() {
        JsonArray jsonArray;
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.level.serializeToJson());
        if (this.gameType != GameType.NOT_SET) {
            jsonObject.addProperty("gamemode", this.gameType.getName());
        }
        if (!this.stats.isEmpty()) {
            jsonArray = new JsonArray();
            this.stats.forEach((stat, ints) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registry.STAT_TYPE.getKey(stat.getType()).toString());
                jsonObject.addProperty("stat", PlayerPredicate.getStatValueId(stat).toString());
                jsonObject.add("value", ints.serializeToJson());
                jsonArray.add((JsonElement)jsonObject);
            });
            jsonObject.add("stats", (JsonElement)jsonArray);
        }
        if (!this.recipes.isEmpty()) {
            jsonArray = new JsonObject();
            this.recipes.forEach((arg_0, arg_1) -> PlayerPredicate.lambda$serializeToJson$2((JsonObject)jsonArray, arg_0, arg_1));
            jsonObject.add("recipes", (JsonElement)jsonArray);
        }
        if (!this.advancements.isEmpty()) {
            jsonArray = new JsonObject();
            this.advancements.forEach((arg_0, arg_1) -> PlayerPredicate.lambda$serializeToJson$3((JsonObject)jsonArray, arg_0, arg_1));
            jsonObject.add("advancements", (JsonElement)jsonArray);
        }
        return jsonObject;
    }

    private static /* synthetic */ void lambda$serializeToJson$3(JsonObject jsonObject, ResourceLocation resourceLocation, AdvancementPredicate advancementPredicate) {
        jsonObject.add(resourceLocation.toString(), advancementPredicate.toJson());
    }

    private static /* synthetic */ void lambda$serializeToJson$2(JsonObject jsonObject, ResourceLocation resourceLocation, Boolean bl) {
        jsonObject.addProperty(resourceLocation.toString(), bl);
    }

    private static /* synthetic */ void lambda$advancementPredicateFromJson$0(Object2BooleanMap object2BooleanMap, Map.Entry entry) {
        boolean bl = GsonHelper.convertToBoolean((JsonElement)entry.getValue(), "criterion test");
        object2BooleanMap.put(entry.getKey(), bl);
    }

    public static class Builder {
        private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
        private GameType gameType = GameType.NOT_SET;
        private final Map<Stat<?>, MinMaxBounds.Ints> stats = Maps.newHashMap();
        private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap();
        private final Map<ResourceLocation, AdvancementPredicate> advancements = Maps.newHashMap();

        public PlayerPredicate build() {
            return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements);
        }
    }

    static class AdvancementCriterionsPredicate
    implements AdvancementPredicate {
        private final Object2BooleanMap<String> criterions;

        public AdvancementCriterionsPredicate(Object2BooleanMap<String> object2BooleanMap) {
            this.criterions = object2BooleanMap;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            this.criterions.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).addProperty(arg_0, arg_1));
            return jsonObject;
        }

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            for (Object2BooleanMap.Entry entry : this.criterions.object2BooleanEntrySet()) {
                CriterionProgress criterionProgress = advancementProgress.getCriterion((String)entry.getKey());
                if (criterionProgress != null && criterionProgress.isDone() == entry.getBooleanValue()) continue;
                return false;
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((AdvancementProgress)object);
        }
    }

    static class AdvancementDonePredicate
    implements AdvancementPredicate {
        private final boolean state;

        public AdvancementDonePredicate(boolean bl) {
            this.state = bl;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Boolean.valueOf(this.state));
        }

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            return advancementProgress.isDone() == this.state;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((AdvancementProgress)object);
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }

}


/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<?> DEFAULT_FEATURE = StructureFeature.BURIED_TREASURE;
    public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
    private final StructureFeature<?> destination;
    private final MapDecoration.Type mapDecoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipKnownStructures;

    private ExplorationMapFunction(LootItemCondition[] arrlootItemCondition, StructureFeature<?> structureFeature, MapDecoration.Type type, byte by, int n, boolean bl) {
        super(arrlootItemCondition);
        this.destination = structureFeature;
        this.mapDecoration = type;
        this.zoom = by;
        this.searchRadius = n;
        this.skipKnownStructures = bl;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLORATION_MAP;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        BlockPos blockPos;
        ServerLevel serverLevel;
        if (itemStack.getItem() != Items.MAP) {
            return itemStack;
        }
        Vec3 vec3 = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        if (vec3 != null && (blockPos = (serverLevel = lootContext.getLevel()).findNearestMapFeature(this.destination, new BlockPos(vec3), this.searchRadius, this.skipKnownStructures)) != null) {
            ItemStack itemStack2 = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), this.zoom, true, true);
            MapItem.renderBiomePreviewMap(serverLevel, itemStack2);
            MapItemSavedData.addTargetDecoration(itemStack2, blockPos, "+", this.mapDecoration);
            itemStack2.setHoverName(new TranslatableComponent("filled_map." + this.destination.getFeatureName().toLowerCase(Locale.ROOT)));
            return itemStack2;
        }
        return itemStack;
    }

    public static Builder makeExplorationMap() {
        return new Builder();
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
        @Override
        public void serialize(JsonObject jsonObject, ExplorationMapFunction explorationMapFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, explorationMapFunction, jsonSerializationContext);
            if (!explorationMapFunction.destination.equals(DEFAULT_FEATURE)) {
                jsonObject.add("destination", jsonSerializationContext.serialize((Object)explorationMapFunction.destination.getFeatureName()));
            }
            if (explorationMapFunction.mapDecoration != DEFAULT_DECORATION) {
                jsonObject.add("decoration", jsonSerializationContext.serialize((Object)explorationMapFunction.mapDecoration.toString().toLowerCase(Locale.ROOT)));
            }
            if (explorationMapFunction.zoom != 2) {
                jsonObject.addProperty("zoom", (Number)explorationMapFunction.zoom);
            }
            if (explorationMapFunction.searchRadius != 50) {
                jsonObject.addProperty("search_radius", (Number)explorationMapFunction.searchRadius);
            }
            if (!explorationMapFunction.skipKnownStructures) {
                jsonObject.addProperty("skip_existing_chunks", Boolean.valueOf(explorationMapFunction.skipKnownStructures));
            }
        }

        @Override
        public ExplorationMapFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            StructureFeature<?> structureFeature = Serializer.readStructure(jsonObject);
            String string = jsonObject.has("decoration") ? GsonHelper.getAsString(jsonObject, "decoration") : "mansion";
            MapDecoration.Type type = DEFAULT_DECORATION;
            try {
                type = MapDecoration.Type.valueOf(string.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + (Object)((Object)DEFAULT_DECORATION), (Object)string);
            }
            byte by = GsonHelper.getAsByte(jsonObject, "zoom", (byte)2);
            int n = GsonHelper.getAsInt(jsonObject, "search_radius", 50);
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "skip_existing_chunks", true);
            return new ExplorationMapFunction(arrlootItemCondition, structureFeature, type, by, n, bl);
        }

        private static StructureFeature<?> readStructure(JsonObject jsonObject) {
            String string;
            StructureFeature structureFeature;
            if (jsonObject.has("destination") && (structureFeature = (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get((Object)(string = GsonHelper.getAsString(jsonObject, "destination")).toLowerCase(Locale.ROOT))) != null) {
                return structureFeature;
            }
            return DEFAULT_FEATURE;
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private StructureFeature<?> destination = DEFAULT_FEATURE;
        private MapDecoration.Type mapDecoration = DEFAULT_DECORATION;
        private byte zoom = (byte)2;
        private int searchRadius = 50;
        private boolean skipKnownStructures = true;

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder setDestination(StructureFeature<?> structureFeature) {
            this.destination = structureFeature;
            return this;
        }

        public Builder setMapDecoration(MapDecoration.Type type) {
            this.mapDecoration = type;
            return this;
        }

        public Builder setZoom(byte by) {
            this.zoom = by;
            return this;
        }

        public Builder setSkipKnownStructures(boolean bl) {
            this.skipKnownStructures = bl;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

}


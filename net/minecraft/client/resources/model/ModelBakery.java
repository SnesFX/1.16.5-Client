/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.tuple.Triple
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base_nopattern"));
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10).mapToObj(n -> new ResourceLocation("block/destroy_stage_" + n)).collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream().map(resourceLocation -> new ResourceLocation("textures/" + resourceLocation.getPath() + ".png")).collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    private static final Set<Material> UNREFERENCED_TEXTURES = Util.make(Sets.newHashSet(), hashSet -> {
        hashSet.add(WATER_FLOW);
        hashSet.add(LAVA_FLOW);
        hashSet.add(WATER_OVERLAY);
        hashSet.add(FIRE_0);
        hashSet.add(FIRE_1);
        hashSet.add(BellRenderer.BELL_RESOURCE_LOCATION);
        hashSet.add(ConduitRenderer.SHELL_TEXTURE);
        hashSet.add(ConduitRenderer.ACTIVE_SHELL_TEXTURE);
        hashSet.add(ConduitRenderer.WIND_TEXTURE);
        hashSet.add(ConduitRenderer.VERTICAL_WIND_TEXTURE);
        hashSet.add(ConduitRenderer.OPEN_EYE_TEXTURE);
        hashSet.add(ConduitRenderer.CLOSED_EYE_TEXTURE);
        hashSet.add(EnchantTableRenderer.BOOK_LOCATION);
        hashSet.add(BANNER_BASE);
        hashSet.add(SHIELD_BASE);
        hashSet.add(NO_PATTERN_SHIELD);
        for (ResourceLocation resourceLocation : DESTROY_STAGES) {
            hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, resourceLocation));
        }
        hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
        hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
        hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
        hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
        hashSet.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
        Sheets.getAllMaterials(hashSet::add);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelResourceLocation MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
    private static final String MISSING_MODEL_LOCATION_STRING = MISSING_MODEL_LOCATION.toString();
    @VisibleForTesting
    public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureAtlasSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureAtlasSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODELS = Maps.newHashMap((Map)ImmutableMap.of((Object)"missing", (Object)MISSING_MODEL_MESH));
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on((char)'=').limit(2);
    public static final BlockModel GENERATION_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), blockModel -> {
        blockModel.name = "generation marker";
    });
    public static final BlockModel BLOCK_ENTITY_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), blockModel -> {
        blockModel.name = "block entity marker";
    });
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder(Blocks.AIR).add(BooleanProperty.create("map")).create(Block::defaultBlockState, (arg_0, arg_1, arg_2) -> BlockState.new(arg_0, arg_1, arg_2));
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of((Object)new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION);
    private final ResourceManager resourceManager;
    @Nullable
    private AtlasSet atlasSet;
    private final BlockColors blockColors;
    private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
    private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
    private final Map<ResourceLocation, UnbakedModel> unbakedCache = Maps.newHashMap();
    private final Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache = Maps.newHashMap();
    private final Map<ResourceLocation, UnbakedModel> topLevelModels = Maps.newHashMap();
    private final Map<ResourceLocation, BakedModel> bakedTopLevelModels = Maps.newHashMap();
    private final Map<ResourceLocation, Pair<TextureAtlas, TextureAtlas.Preparations>> atlasPreparations;
    private int nextModelGroup = 1;
    private final Object2IntMap<BlockState> modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelBakery(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profilerFiller, int n) {
        this.resourceManager = resourceManager;
        this.blockColors = blockColors;
        profilerFiller.push("missing_model");
        try {
            this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
            this.loadTopLevel(MISSING_MODEL_LOCATION);
        }
        catch (IOException iOException) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException);
            throw new RuntimeException(iOException);
        }
        profilerFiller.popPush("static_definitions");
        STATIC_DEFINITIONS.forEach((resourceLocation, stateDefinition) -> stateDefinition.getPossibleStates().forEach(blockState -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(resourceLocation, blockState))));
        profilerFiller.popPush("blocks");
        for (Block set2 : Registry.BLOCK) {
            set2.getStateDefinition().getPossibleStates().forEach(blockState -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(blockState)));
        }
        profilerFiller.popPush("items");
        for (ResourceLocation resourceLocation2 : Registry.ITEM.keySet()) {
            this.loadTopLevel(new ModelResourceLocation(resourceLocation2, "inventory"));
        }
        profilerFiller.popPush("special");
        this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
        profilerFiller.popPush("textures");
        LinkedHashSet linkedHashSet = Sets.newLinkedHashSet();
        Set set = this.topLevelModels.values().stream().flatMap(unbakedModel -> unbakedModel.getMaterials(this::getModel, linkedHashSet).stream()).collect(Collectors.toSet());
        set.addAll(UNREFERENCED_TEXTURES);
        linkedHashSet.stream().filter(pair -> !((String)pair.getSecond()).equals(MISSING_MODEL_LOCATION_STRING)).forEach(pair -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
        Map<ResourceLocation, List<Material>> map = set.stream().collect(Collectors.groupingBy(Material::atlasLocation));
        profilerFiller.popPush("stitching");
        this.atlasPreparations = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, List<Material>> entry : map.entrySet()) {
            TextureAtlas textureAtlas = new TextureAtlas(entry.getKey());
            TextureAtlas.Preparations preparations = textureAtlas.prepareToStitch(this.resourceManager, entry.getValue().stream().map(Material::texture), profilerFiller, n);
            this.atlasPreparations.put(entry.getKey(), (Pair<TextureAtlas, TextureAtlas.Preparations>)Pair.of((Object)textureAtlas, (Object)preparations));
        }
        profilerFiller.pop();
    }

    public AtlasSet uploadTextures(TextureManager textureManager, ProfilerFiller profilerFiller) {
        profilerFiller.push("atlas");
        for (Pair<TextureAtlas, TextureAtlas.Preparations> pair : this.atlasPreparations.values()) {
            TextureAtlas textureAtlas = (TextureAtlas)pair.getFirst();
            TextureAtlas.Preparations preparations = (TextureAtlas.Preparations)pair.getSecond();
            textureAtlas.reload(preparations);
            textureManager.register(textureAtlas.location(), textureAtlas);
            textureManager.bind(textureAtlas.location());
            textureAtlas.updateFilter(preparations);
        }
        this.atlasSet = new AtlasSet(this.atlasPreparations.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
        profilerFiller.popPush("baking");
        this.topLevelModels.keySet().forEach(resourceLocation -> {
            BakedModel bakedModel = null;
            try {
                bakedModel = this.bake((ResourceLocation)resourceLocation, BlockModelRotation.X0_Y0);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, (Object)exception);
            }
            if (bakedModel != null) {
                this.bakedTopLevelModels.put((ResourceLocation)resourceLocation, bakedModel);
            }
        });
        profilerFiller.pop();
        return this.atlasSet;
    }

    private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> stateDefinition, String string) {
        HashMap hashMap = Maps.newHashMap();
        for (String string2 : COMMA_SPLITTER.split((CharSequence)string)) {
            Iterator iterator = EQUAL_SPLITTER.split((CharSequence)string2).iterator();
            if (!iterator.hasNext()) continue;
            String string3 = (String)iterator.next();
            Property<?> property = stateDefinition.getProperty(string3);
            if (property != null && iterator.hasNext()) {
                String string4 = (String)iterator.next();
                Object obj = ModelBakery.getValueHelper(property, string4);
                if (obj != null) {
                    hashMap.put(property, obj);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getPossibleValues());
            }
            if (string3.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
        }
        Block block = stateDefinition.getOwner();
        return blockState -> {
            if (blockState == null || block != blockState.getBlock()) {
                return false;
            }
            for (Map.Entry entry : hashMap.entrySet()) {
                if (Objects.equals(blockState.getValue((Property)entry.getKey()), entry.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    static <T extends Comparable<T>> T getValueHelper(Property<T> property, String string) {
        return property.getValue(string).orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UnbakedModel getModel(ResourceLocation resourceLocation) {
        if (this.unbakedCache.containsKey(resourceLocation)) {
            return this.unbakedCache.get(resourceLocation);
        }
        if (this.loadingStack.contains(resourceLocation)) {
            throw new IllegalStateException("Circular reference while loading " + resourceLocation);
        }
        this.loadingStack.add(resourceLocation);
        UnbakedModel unbakedModel = this.unbakedCache.get(MISSING_MODEL_LOCATION);
        while (!this.loadingStack.isEmpty()) {
            ResourceLocation resourceLocation2 = this.loadingStack.iterator().next();
            try {
                if (this.unbakedCache.containsKey(resourceLocation2)) continue;
                this.loadModel(resourceLocation2);
            }
            catch (BlockStateDefinitionException blockStateDefinitionException) {
                LOGGER.warn(blockStateDefinitionException.getMessage());
                this.unbakedCache.put(resourceLocation2, unbakedModel);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", (Object)resourceLocation2, (Object)resourceLocation, (Object)exception);
                this.unbakedCache.put(resourceLocation2, unbakedModel);
            }
            finally {
                this.loadingStack.remove(resourceLocation2);
            }
        }
        return this.unbakedCache.getOrDefault(resourceLocation, unbakedModel);
    }

    private void loadModel(ResourceLocation resourceLocation) throws Exception {
        if (!(resourceLocation instanceof ModelResourceLocation)) {
            this.cacheAndQueueDependencies(resourceLocation, this.loadBlockModel(resourceLocation));
            return;
        }
        ModelResourceLocation modelResourceLocation2 = (ModelResourceLocation)resourceLocation;
        if (Objects.equals(modelResourceLocation2.getVariant(), "inventory")) {
            ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath());
            BlockModel blockModel = this.loadBlockModel(resourceLocation2);
            this.cacheAndQueueDependencies(modelResourceLocation2, blockModel);
            this.unbakedCache.put(resourceLocation2, blockModel);
        } else {
            ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath());
            StateDefinition stateDefinition = Optional.ofNullable(STATIC_DEFINITIONS.get(resourceLocation3)).orElseGet(() -> Registry.BLOCK.get(resourceLocation3).getStateDefinition());
            this.context.setDefinition(stateDefinition);
            ImmutableList immutableList = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)stateDefinition.getOwner()));
            ImmutableList immutableList2 = stateDefinition.getPossibleStates();
            HashMap hashMap = Maps.newHashMap();
            immutableList2.forEach(blockState -> hashMap.put(BlockModelShaper.stateToModelLocation(resourceLocation3, blockState), blockState));
            HashMap hashMap2 = Maps.newHashMap();
            ResourceLocation resourceLocation4 = new ResourceLocation(resourceLocation.getNamespace(), "blockstates/" + resourceLocation.getPath() + ".json");
            UnbakedModel unbakedModel = this.unbakedCache.get(MISSING_MODEL_LOCATION);
            ModelGroupKey modelGroupKey2 = new ModelGroupKey((List<UnbakedModel>)ImmutableList.of((Object)unbakedModel), (List<Object>)ImmutableList.of());
            Pair pair = Pair.of((Object)unbakedModel, () -> modelGroupKey2);
            try {
                List list;
                try {
                    list = this.resourceManager.getResources(resourceLocation4).stream().map(resource -> {
                        try {
                            try (InputStream inputStream = resource.getInputStream();){
                                Pair pair = Pair.of((Object)resource.getSourceName(), (Object)BlockModelDefinition.fromStream(this.context, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                                return pair;
                            }
                        }
                        catch (Exception exception) {
                            throw new BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getLocation(), resource.getSourceName(), exception.getMessage()));
                        }
                    }).collect(Collectors.toList());
                }
                catch (IOException iOException) {
                    LOGGER.warn("Exception loading blockstate definition: {}: {}", (Object)resourceLocation4, (Object)iOException);
                    HashMap hashMap3 = Maps.newHashMap();
                    hashMap.forEach((modelResourceLocation, blockState) -> {
                        Pair pair2 = (Pair)hashMap2.get(blockState);
                        if (pair2 == null) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)resourceLocation4, modelResourceLocation);
                            pair2 = pair;
                        }
                        this.cacheAndQueueDependencies((ResourceLocation)modelResourceLocation, (UnbakedModel)pair2.getFirst());
                        try {
                            ModelGroupKey modelGroupKey2 = (ModelGroupKey)((Supplier)pair2.getSecond()).get();
                            hashMap3.computeIfAbsent(modelGroupKey2, modelGroupKey -> Sets.newIdentityHashSet()).add(blockState);
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", modelResourceLocation, (Object)exception);
                        }
                    });
                    hashMap3.forEach((modelGroupKey, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState blockState = (BlockState)iterator.next();
                            if (blockState.getRenderShape() == RenderShape.MODEL) continue;
                            iterator.remove();
                            this.modelGroups.put((Object)blockState, 0);
                        }
                        if (set.size() > 1) {
                            this.registerModelGroup((Iterable<BlockState>)set);
                        }
                    });
                    return;
                }
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    MultiPart multiPart;
                    Pair pair2 = (Pair)iterator.next();
                    BlockModelDefinition blockModelDefinition = (BlockModelDefinition)pair2.getSecond();
                    IdentityHashMap identityHashMap = Maps.newIdentityHashMap();
                    if (blockModelDefinition.isMultiPart()) {
                        multiPart = blockModelDefinition.getMultiPart();
                        immutableList2.forEach(arg_0 -> ModelBakery.lambda$loadModel$19(identityHashMap, multiPart, (List)immutableList, arg_0));
                    } else {
                        multiPart = null;
                    }
                    blockModelDefinition.getVariants().forEach((arg_0, arg_1) -> ModelBakery.lambda$loadModel$23(immutableList2, stateDefinition, identityHashMap, (List)immutableList, multiPart, pair, blockModelDefinition, resourceLocation4, pair2, arg_0, arg_1));
                    hashMap2.putAll(identityHashMap);
                }
            }
            catch (BlockStateDefinitionException blockStateDefinitionException) {
                throw blockStateDefinitionException;
            }
            catch (Exception exception) {
                throw new BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourceLocation4, exception));
            }
            finally {
                HashMap hashMap4 = Maps.newHashMap();
                hashMap.forEach((modelResourceLocation, blockState) -> {
                    Pair pair2 = (Pair)hashMap2.get(blockState);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)resourceLocation4, modelResourceLocation);
                        pair2 = pair;
                    }
                    this.cacheAndQueueDependencies((ResourceLocation)modelResourceLocation, (UnbakedModel)pair2.getFirst());
                    try {
                        ModelGroupKey modelGroupKey2 = (ModelGroupKey)((Supplier)pair2.getSecond()).get();
                        hashMap3.computeIfAbsent(modelGroupKey2, modelGroupKey -> Sets.newIdentityHashSet()).add(blockState);
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", modelResourceLocation, (Object)exception);
                    }
                });
                hashMap4.forEach((modelGroupKey, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState blockState = (BlockState)iterator.next();
                        if (blockState.getRenderShape() == RenderShape.MODEL) continue;
                        iterator.remove();
                        this.modelGroups.put((Object)blockState, 0);
                    }
                    if (set.size() > 1) {
                        this.registerModelGroup((Iterable<BlockState>)set);
                    }
                });
            }
        }
    }

    private void cacheAndQueueDependencies(ResourceLocation resourceLocation, UnbakedModel unbakedModel) {
        this.unbakedCache.put(resourceLocation, unbakedModel);
        this.loadingStack.addAll(unbakedModel.getDependencies());
    }

    private void loadTopLevel(ModelResourceLocation modelResourceLocation) {
        UnbakedModel unbakedModel = this.getModel(modelResourceLocation);
        this.unbakedCache.put(modelResourceLocation, unbakedModel);
        this.topLevelModels.put(modelResourceLocation, unbakedModel);
    }

    private void registerModelGroup(Iterable<BlockState> iterable) {
        int n = this.nextModelGroup++;
        iterable.forEach(blockState -> this.modelGroups.put(blockState, n));
    }

    @Nullable
    public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
        Object object;
        Triple triple = Triple.of((Object)resourceLocation, (Object)modelState.getRotation(), (Object)modelState.isUvLocked());
        if (this.bakedCache.containsKey((Object)triple)) {
            return this.bakedCache.get((Object)triple);
        }
        if (this.atlasSet == null) {
            throw new IllegalStateException("bake called too early");
        }
        UnbakedModel unbakedModel = this.getModel(resourceLocation);
        if (unbakedModel instanceof BlockModel && ((BlockModel)(object = (BlockModel)unbakedModel)).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.generateBlockModel(this.atlasSet::getSprite, (BlockModel)object).bake(this, (BlockModel)object, this.atlasSet::getSprite, modelState, resourceLocation, false);
        }
        object = unbakedModel.bake(this, this.atlasSet::getSprite, modelState, resourceLocation);
        this.bakedCache.put((Triple<ResourceLocation, Transformation, Boolean>)triple, (BakedModel)object);
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BlockModel loadBlockModel(ResourceLocation resourceLocation) throws IOException {
        Object object;
        Object object2;
        Reader reader;
        String string;
        Resource resource;
        block8 : {
            block7 : {
                BlockModel blockModel;
                reader = null;
                resource = null;
                try {
                    string = resourceLocation.getPath();
                    if (!"builtin/generated".equals(string)) break block7;
                    blockModel = GENERATION_MARKER;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);
                return blockModel;
            }
            if (!"builtin/entity".equals(string)) break block8;
            BlockModel blockModel = BLOCK_ENTITY_MARKER;
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(resource);
            return blockModel;
        }
        if (string.startsWith("builtin/")) {
            object = string.substring("builtin/".length());
            object2 = BUILTIN_MODELS.get(object);
            if (object2 == null) {
                throw new FileNotFoundException(resourceLocation.toString());
            }
            reader = new StringReader((String)object2);
        } else {
            resource = this.resourceManager.getResource(new ResourceLocation(resourceLocation.getNamespace(), "models/" + resourceLocation.getPath() + ".json"));
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        }
        object = BlockModel.fromStream(reader);
        ((BlockModel)object).name = resourceLocation.toString();
        object2 = object;
        IOUtils.closeQuietly((Reader)reader);
        IOUtils.closeQuietly((Closeable)resource);
        return object2;
    }

    public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }

    public Object2IntMap<BlockState> getModelGroups() {
        return this.modelGroups;
    }

    private static /* synthetic */ void lambda$loadModel$23(ImmutableList immutableList, StateDefinition stateDefinition, Map map, List list, MultiPart multiPart, Pair pair, BlockModelDefinition blockModelDefinition, ResourceLocation resourceLocation, Pair pair2, String string, MultiVariant multiVariant) {
        try {
            immutableList.stream().filter(ModelBakery.predicate(stateDefinition, string)).forEach(blockState -> {
                Pair pair2 = map.put(blockState, Pair.of((Object)multiVariant, () -> ModelGroupKey.create(blockState, multiVariant, list)));
                if (pair2 != null && pair2.getFirst() != multiPart) {
                    map.put(blockState, pair);
                    throw new RuntimeException("Overlapping definition with: " + (String)blockModelDefinition.getVariants().entrySet().stream().filter(entry -> entry.getValue() == pair2.getFirst()).findFirst().get().getKey());
                }
            });
        }
        catch (Exception exception) {
            LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", (Object)resourceLocation, pair2.getFirst(), (Object)string, (Object)exception.getMessage());
        }
    }

    private static /* synthetic */ void lambda$loadModel$19(Map map, MultiPart multiPart, List list, BlockState blockState) {
        map.put(blockState, Pair.of((Object)multiPart, () -> ModelGroupKey.create(blockState, multiPart, list)));
    }

    static class ModelGroupKey {
        private final List<UnbakedModel> models;
        private final List<Object> coloringValues;

        public ModelGroupKey(List<UnbakedModel> list, List<Object> list2) {
            this.models = list;
            this.coloringValues = list2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ModelGroupKey) {
                ModelGroupKey modelGroupKey = (ModelGroupKey)object;
                return Objects.equals(this.models, modelGroupKey.models) && Objects.equals(this.coloringValues, modelGroupKey.coloringValues);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.models.hashCode() + this.coloringValues.hashCode();
        }

        public static ModelGroupKey create(BlockState blockState, MultiPart multiPart, Collection<Property<?>> collection) {
            StateDefinition<Block, BlockState> stateDefinition = blockState.getBlock().getStateDefinition();
            List list = (List)multiPart.getSelectors().stream().filter(selector -> selector.getPredicate(stateDefinition).test(blockState)).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
            List<Object> list2 = ModelGroupKey.getColoringValues(blockState, collection);
            return new ModelGroupKey(list, list2);
        }

        public static ModelGroupKey create(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
            List<Object> list = ModelGroupKey.getColoringValues(blockState, collection);
            return new ModelGroupKey((List<UnbakedModel>)ImmutableList.of((Object)unbakedModel), list);
        }

        private static List<Object> getColoringValues(BlockState blockState, Collection<Property<?>> collection) {
            return (List)collection.stream().map(blockState::getValue).collect(ImmutableList.toImmutableList());
        }
    }

    static class BlockStateDefinitionException
    extends RuntimeException {
        public BlockStateDefinitionException(String string) {
            super(string);
        }
    }

}


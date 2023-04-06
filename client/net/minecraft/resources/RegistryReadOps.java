/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.Hash
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryReadOps<T>
extends DelegatingOps<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceAccess resources;
    private final RegistryAccess.RegistryHolder registryHolder;
    private final Map<ResourceKey<? extends Registry<?>>, ReadCache<?>> readCache;
    private final RegistryReadOps<JsonElement> jsonOps;

    public static <T> RegistryReadOps<T> create(DynamicOps<T> dynamicOps, ResourceManager resourceManager, RegistryAccess.RegistryHolder registryHolder) {
        return RegistryReadOps.create(dynamicOps, ResourceAccess.forResourceManager(resourceManager), registryHolder);
    }

    public static <T> RegistryReadOps<T> create(DynamicOps<T> dynamicOps, ResourceAccess resourceAccess, RegistryAccess.RegistryHolder registryHolder) {
        RegistryReadOps<T> registryReadOps = new RegistryReadOps<T>(dynamicOps, resourceAccess, registryHolder, Maps.newIdentityHashMap());
        RegistryAccess.load(registryHolder, registryReadOps);
        return registryReadOps;
    }

    private RegistryReadOps(DynamicOps<T> dynamicOps, ResourceAccess resourceAccess, RegistryAccess.RegistryHolder registryHolder, IdentityHashMap<ResourceKey<? extends Registry<?>>, ReadCache<?>> identityHashMap) {
        super(dynamicOps);
        this.resources = resourceAccess;
        this.registryHolder = registryHolder;
        this.readCache = identityHashMap;
        this.jsonOps = dynamicOps == JsonOps.INSTANCE ? this : new RegistryReadOps<T>((DynamicOps<T>)JsonOps.INSTANCE, resourceAccess, registryHolder, (IdentityHashMap<ResourceKey<Registry<?>>, ReadCache<?>>)identityHashMap);
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> decodeElement(T t, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, boolean bl) {
        Optional optional = this.registryHolder.registry(resourceKey);
        if (!optional.isPresent()) {
            return DataResult.error((String)("Unknown registry: " + resourceKey));
        }
        WritableRegistry writableRegistry = optional.get();
        DataResult dataResult = ResourceLocation.CODEC.decode(this.delegate, t);
        if (!dataResult.result().isPresent()) {
            if (!bl) {
                return DataResult.error((String)"Inline definitions not allowed here");
            }
            return codec.decode((DynamicOps)this, t).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Pair pair2 = (Pair)dataResult.result().get();
        ResourceLocation resourceLocation = (ResourceLocation)pair2.getFirst();
        return this.readAndRegisterElement(resourceKey, writableRegistry, codec, resourceLocation).map(supplier -> Pair.of((Object)supplier, (Object)pair2.getSecond()));
    }

    public <E> DataResult<MappedRegistry<E>> decodeElements(MappedRegistry<E> mappedRegistry2, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
        Collection<ResourceLocation> collection = this.resources.listResources(resourceKey);
        DataResult dataResult = DataResult.success(mappedRegistry2, (Lifecycle)Lifecycle.stable());
        String string = resourceKey.location().getPath() + "/";
        for (ResourceLocation resourceLocation : collection) {
            String string2 = resourceLocation.getPath();
            if (!string2.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)resourceLocation);
                continue;
            }
            if (!string2.startsWith(string)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)resourceLocation);
                continue;
            }
            String string3 = string2.substring(string.length(), string2.length() - ".json".length());
            ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getNamespace(), string3);
            dataResult = dataResult.flatMap(mappedRegistry -> this.readAndRegisterElement((ResourceKey<? extends Registry<E>>)resourceKey, (WritableRegistry<E>)mappedRegistry, codec, resourceLocation2).map(supplier -> mappedRegistry));
        }
        return dataResult.setPartial(mappedRegistry2);
    }

    private <E> DataResult<Supplier<E>> readAndRegisterElement(ResourceKey<? extends Registry<E>> resourceKey, WritableRegistry<E> writableRegistry, Codec<E> codec, ResourceLocation resourceLocation) {
        Pair pair2;
        ResourceKey resourceKey2 = ResourceKey.create(resourceKey, resourceLocation);
        ReadCache<E> readCache = this.readCache(resourceKey);
        DataResult dataResult = (DataResult)readCache.values.get(resourceKey2);
        if (dataResult != null) {
            return dataResult;
        }
        com.google.common.base.Supplier supplier = Suppliers.memoize(() -> {
            Object t = writableRegistry.get(resourceKey2);
            if (t == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + resourceKey2);
            }
            return t;
        });
        readCache.values.put(resourceKey2, DataResult.success((Object)supplier));
        DataResult dataResult2 = this.resources.parseElement((DynamicOps<JsonElement>)this.jsonOps, resourceKey, resourceKey2, codec);
        Optional optional = dataResult2.result();
        if (optional.isPresent()) {
            pair2 = (Pair)optional.get();
            writableRegistry.registerOrOverride((OptionalInt)pair2.getSecond(), resourceKey2, pair2.getFirst(), dataResult2.lifecycle());
        }
        pair2 = !optional.isPresent() && writableRegistry.get(resourceKey2) != null ? DataResult.success(() -> writableRegistry.get(resourceKey2), (Lifecycle)Lifecycle.stable()) : dataResult2.map(pair -> () -> writableRegistry.get(resourceKey2));
        readCache.values.put(resourceKey2, pair2);
        return pair2;
    }

    private <E> ReadCache<E> readCache(ResourceKey<? extends Registry<E>> resourceKey2) {
        return this.readCache.computeIfAbsent(resourceKey2, resourceKey -> new ReadCache());
    }

    protected <E> DataResult<Registry<E>> registry(ResourceKey<? extends Registry<E>> resourceKey) {
        return this.registryHolder.registry(resourceKey).map(writableRegistry -> DataResult.success((Object)writableRegistry, (Lifecycle)writableRegistry.elementsLifecycle())).orElseGet(() -> DataResult.error((String)("Unknown registry: " + resourceKey)));
    }

    public static interface ResourceAccess {
        public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> var1);

        public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4);

        public static ResourceAccess forResourceManager(final ResourceManager resourceManager) {
            return new ResourceAccess(){

                @Override
                public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> resourceKey) {
                    return resourceManager.listResources(resourceKey.location().getPath(), string -> string.endsWith(".json"));
                }

                /*
                 * Exception decompiling
                 */
                @Override
                public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> var1_1, ResourceKey<? extends Registry<E>> var2_2, ResourceKey<E> var3_3, Decoder<E> var4_4) {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[TRYBLOCK]
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:699)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:188)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:133)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
                    // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
                    // org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:778)
                    // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:886)
                    // org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:778)
                    // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:886)
                    // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
                    // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
                    // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                    // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                    // org.benf.cfr.reader.Main.main(Main.java:48)
                    throw new IllegalStateException("Decompilation failed");
                }

                public String toString() {
                    return "ResourceAccess[" + resourceManager + "]";
                }

                private static /* synthetic */ Pair lambda$parseElement$1(Object object) {
                    return Pair.of((Object)object, (Object)OptionalInt.empty());
                }
            };
        }

        public static final class MemoryMap
        implements ResourceAccess {
            private final Map<ResourceKey<?>, JsonElement> data = Maps.newIdentityHashMap();
            private final Object2IntMap<ResourceKey<?>> ids = new Object2IntOpenCustomHashMap(Util.identityStrategy());
            private final Map<ResourceKey<?>, Lifecycle> lifecycles = Maps.newIdentityHashMap();

            public <E> void add(RegistryAccess.RegistryHolder registryHolder, ResourceKey<E> resourceKey, Encoder<E> encoder, int n, E e, Lifecycle lifecycle) {
                DataResult dataResult = encoder.encodeStart(RegistryWriteOps.create(JsonOps.INSTANCE, registryHolder), e);
                Optional optional = dataResult.error();
                if (optional.isPresent()) {
                    LOGGER.error("Error adding element: {}", (Object)((DataResult.PartialResult)optional.get()).message());
                    return;
                }
                this.data.put(resourceKey, (JsonElement)dataResult.result().get());
                this.ids.put(resourceKey, n);
                this.lifecycles.put(resourceKey, lifecycle);
            }

            @Override
            public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> resourceKey) {
                return this.data.keySet().stream().filter(resourceKey2 -> resourceKey2.isFor(resourceKey)).map(resourceKey2 -> new ResourceLocation(resourceKey2.location().getNamespace(), resourceKey.location().getPath() + "/" + resourceKey2.location().getPath() + ".json")).collect(Collectors.toList());
            }

            @Override
            public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> dynamicOps, ResourceKey<? extends Registry<E>> resourceKey, ResourceKey<E> resourceKey2, Decoder<E> decoder) {
                JsonElement jsonElement = this.data.get(resourceKey2);
                if (jsonElement == null) {
                    return DataResult.error((String)("Unknown element: " + resourceKey2));
                }
                return decoder.parse(dynamicOps, (Object)jsonElement).setLifecycle(this.lifecycles.get(resourceKey2)).map(object -> Pair.of((Object)object, (Object)OptionalInt.of(this.ids.getInt((Object)resourceKey2))));
            }
        }

    }

    static final class ReadCache<E> {
        private final Map<ResourceKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();

        private ReadCache() {
        }
    }

}


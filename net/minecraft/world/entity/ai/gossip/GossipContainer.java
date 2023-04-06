/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.SerializableUUID;
import net.minecraft.world.entity.ai.gossip.GossipType;

public class GossipContainer {
    private final Map<UUID, EntityGossips> gossips = Maps.newHashMap();

    public void decay() {
        Iterator<EntityGossips> iterator = this.gossips.values().iterator();
        while (iterator.hasNext()) {
            EntityGossips entityGossips = iterator.next();
            entityGossips.decay();
            if (!entityGossips.isEmpty()) continue;
            iterator.remove();
        }
    }

    private Stream<GossipEntry> unpack() {
        return this.gossips.entrySet().stream().flatMap(entry -> ((EntityGossips)entry.getValue()).unpack((UUID)entry.getKey()));
    }

    private Collection<GossipEntry> selectGossipsForTransfer(Random random, int n) {
        List list = this.unpack().collect(Collectors.toList());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        int[] arrn = new int[list.size()];
        int n2 = 0;
        for (int i = 0; i < list.size(); ++i) {
            GossipEntry gossipEntry = (GossipEntry)list.get(i);
            arrn[i] = (n2 += Math.abs(gossipEntry.weightedValue())) - 1;
        }
        Set set = Sets.newIdentityHashSet();
        for (int i = 0; i < n; ++i) {
            int n3 = random.nextInt(n2);
            int n4 = Arrays.binarySearch(arrn, n3);
            set.add(list.get(n4 < 0 ? -n4 - 1 : n4));
        }
        return set;
    }

    private EntityGossips getOrCreate(UUID uUID2) {
        return this.gossips.computeIfAbsent(uUID2, uUID -> new EntityGossips());
    }

    public void transferFrom(GossipContainer gossipContainer, Random random, int n) {
        Collection<GossipEntry> collection = gossipContainer.selectGossipsForTransfer(random, n);
        collection.forEach(gossipEntry -> {
            int n = gossipEntry.value - gossipEntry.type.decayPerTransfer;
            if (n >= 2) {
                this.getOrCreate(gossipEntry.target).entries.mergeInt((Object)gossipEntry.type, n, (arg_0, arg_1) -> GossipContainer.mergeValuesForTransfer(arg_0, arg_1));
            }
        });
    }

    public int getReputation(UUID uUID, Predicate<GossipType> predicate) {
        EntityGossips entityGossips = this.gossips.get(uUID);
        return entityGossips != null ? entityGossips.weightedValue(predicate) : 0;
    }

    public void add(UUID uUID, GossipType gossipType, int n3) {
        EntityGossips entityGossips = this.getOrCreate(uUID);
        entityGossips.entries.mergeInt((Object)gossipType, n3, (n, n2) -> this.mergeValuesForAddition(gossipType, (int)n, (int)n2));
        entityGossips.makeSureValueIsntTooLowOrTooHigh(gossipType);
        if (entityGossips.isEmpty()) {
            this.gossips.remove(uUID);
        }
    }

    public <T> Dynamic<T> store(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createList(this.unpack().map(gossipEntry -> gossipEntry.store(dynamicOps)).map(Dynamic::getValue)));
    }

    public void update(Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::load).flatMap(dataResult -> Util.toStream(dataResult.result())).forEach(gossipEntry -> this.getOrCreate(gossipEntry.target).entries.put((Object)gossipEntry.type, gossipEntry.value));
    }

    private static int mergeValuesForTransfer(int n, int n2) {
        return Math.max(n, n2);
    }

    private int mergeValuesForAddition(GossipType gossipType, int n, int n2) {
        int n3 = n + n2;
        return n3 > gossipType.max ? Math.max(gossipType.max, n) : n3;
    }

    static class EntityGossips {
        private final Object2IntMap<GossipType> entries = new Object2IntOpenHashMap();

        private EntityGossips() {
        }

        public int weightedValue(Predicate<GossipType> predicate) {
            return this.entries.object2IntEntrySet().stream().filter(entry -> predicate.test((GossipType)((Object)entry.getKey()))).mapToInt(entry -> entry.getIntValue() * ((GossipType)entry.getKey()).weight).sum();
        }

        public Stream<GossipEntry> unpack(UUID uUID) {
            return this.entries.object2IntEntrySet().stream().map(entry -> new GossipEntry(uUID, (GossipType)((Object)((Object)entry.getKey())), entry.getIntValue()));
        }

        public void decay() {
            ObjectIterator objectIterator = this.entries.object2IntEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
                int n = entry.getIntValue() - ((GossipType)entry.getKey()).decayPerDay;
                if (n < 2) {
                    objectIterator.remove();
                    continue;
                }
                entry.setValue(n);
            }
        }

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }

        public void makeSureValueIsntTooLowOrTooHigh(GossipType gossipType) {
            int n = this.entries.getInt((Object)gossipType);
            if (n > gossipType.max) {
                this.entries.put((Object)gossipType, gossipType.max);
            }
            if (n < 2) {
                this.remove(gossipType);
            }
        }

        public void remove(GossipType gossipType) {
            this.entries.removeInt((Object)gossipType);
        }
    }

    static class GossipEntry {
        public final UUID target;
        public final GossipType type;
        public final int value;

        public GossipEntry(UUID uUID, GossipType gossipType, int n) {
            this.target = uUID;
            this.type = gossipType;
            this.value = n;
        }

        public int weightedValue() {
            return this.value * this.type.weight;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + (Object)((Object)this.type) + ", value=" + this.value + '}';
        }

        public <T> Dynamic<T> store(DynamicOps<T> dynamicOps) {
            return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Target"), SerializableUUID.CODEC.encodeStart(dynamicOps, (Object)this.target).result().orElseThrow(RuntimeException::new), (Object)dynamicOps.createString("Type"), (Object)dynamicOps.createString(this.type.id), (Object)dynamicOps.createString("Value"), (Object)dynamicOps.createInt(this.value))));
        }

        public static DataResult<GossipEntry> load(Dynamic<?> dynamic) {
            return DataResult.unbox((App)DataResult.instance().group((App)dynamic.get("Target").read(SerializableUUID.CODEC), (App)dynamic.get("Type").asString().map(GossipType::byId), (App)dynamic.get("Value").asNumber().map(Number::intValue)).apply((Applicative)DataResult.instance(), (arg_0, arg_1, arg_2) -> GossipEntry.new(arg_0, arg_1, arg_2)));
        }
    }

}


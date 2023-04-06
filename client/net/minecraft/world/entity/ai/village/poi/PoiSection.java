/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PoiSection {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PoiRecord> records = new Short2ObjectOpenHashMap();
    private final Map<PoiType, Set<PoiRecord>> byType = Maps.newHashMap();
    private final Runnable setDirty;
    private boolean isValid;

    public static Codec<PoiSection> codec(Runnable runnable) {
        return RecordCodecBuilder.create(instance -> instance.group((App)RecordCodecBuilder.point((Object)runnable), (App)Codec.BOOL.optionalFieldOf("Valid", (Object)false).forGetter(poiSection -> poiSection.isValid), (App)PoiRecord.codec(runnable).listOf().fieldOf("Records").forGetter(poiSection -> ImmutableList.copyOf((Collection)poiSection.records.values()))).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> PoiSection.new(arg_0, arg_1, arg_2))).orElseGet(Util.prefix("Failed to read POI section: ", ((Logger)LOGGER)::error), () -> new PoiSection(runnable, false, (List<PoiRecord>)ImmutableList.of()));
    }

    public PoiSection(Runnable runnable) {
        this(runnable, true, (List<PoiRecord>)ImmutableList.of());
    }

    private PoiSection(Runnable runnable, boolean bl, List<PoiRecord> list) {
        this.setDirty = runnable;
        this.isValid = bl;
        list.forEach(this::add);
    }

    public Stream<PoiRecord> getRecords(Predicate<PoiType> predicate, PoiManager.Occupancy occupancy) {
        return this.byType.entrySet().stream().filter(entry -> predicate.test((PoiType)entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter(occupancy.getTest());
    }

    public void add(BlockPos blockPos, PoiType poiType) {
        if (this.add(new PoiRecord(blockPos, poiType, this.setDirty))) {
            LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> poiType, () -> blockPos});
            this.setDirty.run();
        }
    }

    private boolean add(PoiRecord poiRecord) {
        BlockPos blockPos = poiRecord.getPos();
        PoiType poiType2 = poiRecord.getPoiType();
        short s = SectionPos.sectionRelativePos(blockPos);
        PoiRecord poiRecord2 = (PoiRecord)this.records.get(s);
        if (poiRecord2 != null) {
            if (poiType2.equals(poiRecord2.getPoiType())) {
                return false;
            }
            String string = "POI data mismatch: already registered at " + blockPos;
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw Util.pauseInIde(new IllegalStateException(string));
            }
            LOGGER.error(string);
        }
        this.records.put(s, (Object)poiRecord);
        this.byType.computeIfAbsent(poiType2, poiType -> Sets.newHashSet()).add(poiRecord);
        return true;
    }

    public void remove(BlockPos blockPos) {
        PoiRecord poiRecord = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(blockPos));
        if (poiRecord == null) {
            LOGGER.error("POI data mismatch: never registered at " + blockPos);
            return;
        }
        this.byType.get(poiRecord.getPoiType()).remove(poiRecord);
        Supplier[] arrsupplier = new Supplier[2];
        arrsupplier[0] = poiRecord::getPoiType;
        arrsupplier[1] = poiRecord::getPos;
        LOGGER.debug("Removed POI of type {} @ {}", arrsupplier);
        this.setDirty.run();
    }

    public boolean release(BlockPos blockPos) {
        PoiRecord poiRecord = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(blockPos));
        if (poiRecord == null) {
            throw Util.pauseInIde(new IllegalStateException("POI never registered at " + blockPos));
        }
        boolean bl = poiRecord.releaseTicket();
        this.setDirty.run();
        return bl;
    }

    public boolean exists(BlockPos blockPos, Predicate<PoiType> predicate) {
        short s = SectionPos.sectionRelativePos(blockPos);
        PoiRecord poiRecord = (PoiRecord)this.records.get(s);
        return poiRecord != null && predicate.test(poiRecord.getPoiType());
    }

    public Optional<PoiType> getType(BlockPos blockPos) {
        short s = SectionPos.sectionRelativePos(blockPos);
        PoiRecord poiRecord = (PoiRecord)this.records.get(s);
        return poiRecord != null ? Optional.of(poiRecord.getPoiType()) : Optional.empty();
    }

    public void refresh(Consumer<BiConsumer<BlockPos, PoiType>> consumer) {
        if (!this.isValid) {
            Short2ObjectOpenHashMap short2ObjectOpenHashMap = new Short2ObjectOpenHashMap(this.records);
            this.clear();
            consumer.accept((arg_0, arg_1) -> this.lambda$refresh$10((Short2ObjectMap)short2ObjectOpenHashMap, arg_0, arg_1));
            this.isValid = true;
            this.setDirty.run();
        }
    }

    private void clear() {
        this.records.clear();
        this.byType.clear();
    }

    boolean isValid() {
        return this.isValid;
    }

    private /* synthetic */ void lambda$refresh$10(Short2ObjectMap short2ObjectMap, BlockPos blockPos, PoiType poiType) {
        short s = SectionPos.sectionRelativePos(blockPos);
        PoiRecord poiRecord = (PoiRecord)short2ObjectMap.computeIfAbsent(s, n -> new PoiRecord(blockPos, poiType, this.setDirty));
        this.add(poiRecord);
    }
}


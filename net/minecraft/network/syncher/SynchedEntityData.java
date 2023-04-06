/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network.syncher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SynchedEntityData {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends Entity>, Integer> ENTITY_ID_POOL = Maps.newHashMap();
    private final Entity entity;
    private final Map<Integer, DataItem<?>> itemsById = Maps.newHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isEmpty = true;
    private boolean isDirty;

    public SynchedEntityData(Entity entity) {
        this.entity = entity;
    }

    public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> class_, EntityDataSerializer<T> entityDataSerializer) {
        int n;
        if (LOGGER.isDebugEnabled()) {
            try {
                Class<?> class_2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
                if (!class_2.equals(class_)) {
                    LOGGER.debug("defineId called for: {} from {}", class_, class_2, (Object)new RuntimeException());
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        if (ENTITY_ID_POOL.containsKey(class_)) {
            n = ENTITY_ID_POOL.get(class_) + 1;
        } else {
            int n2 = 0;
            Class<? extends Entity> class_3 = class_;
            while (class_3 != Entity.class) {
                if (!ENTITY_ID_POOL.containsKey(class_3 = class_3.getSuperclass())) continue;
                n2 = ENTITY_ID_POOL.get(class_3) + 1;
                break;
            }
            n = n2;
        }
        if (n > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + n + "! (Max is " + 254 + ")");
        }
        ENTITY_ID_POOL.put(class_, n);
        return entityDataSerializer.createAccessor(n);
    }

    public <T> void define(EntityDataAccessor<T> entityDataAccessor, T t) {
        int n = entityDataAccessor.getId();
        if (n > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + n + "! (Max is " + 254 + ")");
        }
        if (this.itemsById.containsKey(n)) {
            throw new IllegalArgumentException("Duplicate id value for " + n + "!");
        }
        if (EntityDataSerializers.getSerializedId(entityDataAccessor.getSerializer()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + entityDataAccessor.getSerializer() + " for " + n + "!");
        }
        this.createDataItem(entityDataAccessor, t);
    }

    private <T> void createDataItem(EntityDataAccessor<T> entityDataAccessor, T t) {
        DataItem<T> dataItem = new DataItem<T>(entityDataAccessor, t);
        this.lock.writeLock().lock();
        this.itemsById.put(entityDataAccessor.getId(), dataItem);
        this.isEmpty = false;
        this.lock.writeLock().unlock();
    }

    private <T> DataItem<T> getItem(EntityDataAccessor<T> entityDataAccessor) {
        DataItem<?> dataItem;
        this.lock.readLock().lock();
        try {
            dataItem = this.itemsById.get(entityDataAccessor.getId());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Getting synched entity data");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Synched entity data");
            crashReportCategory.setDetail("Data ID", entityDataAccessor);
            throw new ReportedException(crashReport);
        }
        finally {
            this.lock.readLock().unlock();
        }
        return dataItem;
    }

    public <T> T get(EntityDataAccessor<T> entityDataAccessor) {
        return this.getItem(entityDataAccessor).getValue();
    }

    public <T> void set(EntityDataAccessor<T> entityDataAccessor, T t) {
        DataItem<T> dataItem = this.getItem(entityDataAccessor);
        if (ObjectUtils.notEqual(t, dataItem.getValue())) {
            dataItem.setValue(t);
            this.entity.onSyncedDataUpdated(entityDataAccessor);
            dataItem.setDirty(true);
            this.isDirty = true;
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public static void pack(List<DataItem<?>> list, FriendlyByteBuf friendlyByteBuf) throws IOException {
        if (list != null) {
            int n = list.size();
            for (int i = 0; i < n; ++i) {
                SynchedEntityData.writeDataItem(friendlyByteBuf, list.get(i));
            }
        }
        friendlyByteBuf.writeByte(255);
    }

    @Nullable
    public List<DataItem<?>> packDirty() {
        ArrayList arrayList = null;
        if (this.isDirty) {
            this.lock.readLock().lock();
            for (DataItem<?> dataItem : this.itemsById.values()) {
                if (!dataItem.isDirty()) continue;
                dataItem.setDirty(false);
                if (arrayList == null) {
                    arrayList = Lists.newArrayList();
                }
                arrayList.add(dataItem.copy());
            }
            this.lock.readLock().unlock();
        }
        this.isDirty = false;
        return arrayList;
    }

    @Nullable
    public List<DataItem<?>> getAll() {
        ArrayList arrayList = null;
        this.lock.readLock().lock();
        for (DataItem<?> dataItem : this.itemsById.values()) {
            if (arrayList == null) {
                arrayList = Lists.newArrayList();
            }
            arrayList.add(dataItem.copy());
        }
        this.lock.readLock().unlock();
        return arrayList;
    }

    private static <T> void writeDataItem(FriendlyByteBuf friendlyByteBuf, DataItem<T> dataItem) throws IOException {
        EntityDataAccessor<T> entityDataAccessor = dataItem.getAccessor();
        int n = EntityDataSerializers.getSerializedId(entityDataAccessor.getSerializer());
        if (n < 0) {
            throw new EncoderException("Unknown serializer type " + entityDataAccessor.getSerializer());
        }
        friendlyByteBuf.writeByte(entityDataAccessor.getId());
        friendlyByteBuf.writeVarInt(n);
        entityDataAccessor.getSerializer().write(friendlyByteBuf, dataItem.getValue());
    }

    @Nullable
    public static List<DataItem<?>> unpack(FriendlyByteBuf friendlyByteBuf) throws IOException {
        short s;
        ArrayList arrayList = null;
        while ((s = friendlyByteBuf.readUnsignedByte()) != 255) {
            int n;
            EntityDataSerializer<?> entityDataSerializer;
            if (arrayList == null) {
                arrayList = Lists.newArrayList();
            }
            if ((entityDataSerializer = EntityDataSerializers.getSerializer(n = friendlyByteBuf.readVarInt())) == null) {
                throw new DecoderException("Unknown serializer type " + n);
            }
            arrayList.add(SynchedEntityData.genericHelper(friendlyByteBuf, s, entityDataSerializer));
        }
        return arrayList;
    }

    private static <T> DataItem<T> genericHelper(FriendlyByteBuf friendlyByteBuf, int n, EntityDataSerializer<T> entityDataSerializer) {
        return new DataItem<T>(entityDataSerializer.createAccessor(n), entityDataSerializer.read(friendlyByteBuf));
    }

    public void assignValues(List<DataItem<?>> list) {
        this.lock.writeLock().lock();
        for (DataItem<?> dataItem : list) {
            DataItem<?> dataItem2 = this.itemsById.get(dataItem.getAccessor().getId());
            if (dataItem2 == null) continue;
            this.assignValue(dataItem2, dataItem);
            this.entity.onSyncedDataUpdated(dataItem.getAccessor());
        }
        this.lock.writeLock().unlock();
        this.isDirty = true;
    }

    private <T> void assignValue(DataItem<T> dataItem, DataItem<?> dataItem2) {
        if (!Objects.equals(dataItem2.accessor.getSerializer(), dataItem.accessor.getSerializer())) {
            throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", dataItem.accessor.getId(), this.entity, dataItem.value, dataItem.value.getClass(), dataItem2.value, dataItem2.value.getClass()));
        }
        dataItem.setValue(dataItem2.getValue());
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public void clearDirty() {
        this.isDirty = false;
        this.lock.readLock().lock();
        for (DataItem<?> dataItem : this.itemsById.values()) {
            dataItem.setDirty(false);
        }
        this.lock.readLock().unlock();
    }

    public static class DataItem<T> {
        private final EntityDataAccessor<T> accessor;
        private T value;
        private boolean dirty;

        public DataItem(EntityDataAccessor<T> entityDataAccessor, T t) {
            this.accessor = entityDataAccessor;
            this.value = t;
            this.dirty = true;
        }

        public EntityDataAccessor<T> getAccessor() {
            return this.accessor;
        }

        public void setValue(T t) {
            this.value = t;
        }

        public T getValue() {
            return this.value;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean bl) {
            this.dirty = bl;
        }

        public DataItem<T> copy() {
            return new DataItem<T>(this.accessor, this.accessor.getSerializer().copy(this.value));
        }
    }

}


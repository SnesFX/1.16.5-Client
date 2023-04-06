/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class CommandStorage {
    private final Map<String, Container> namespaces = Maps.newHashMap();
    private final DimensionDataStorage storage;

    public CommandStorage(DimensionDataStorage dimensionDataStorage) {
        this.storage = dimensionDataStorage;
    }

    private Container newStorage(String string, String string2) {
        Container container = new Container(string2);
        this.namespaces.put(string, container);
        return container;
    }

    public CompoundTag get(ResourceLocation resourceLocation) {
        String string;
        String string2 = resourceLocation.getNamespace();
        Container container = this.storage.get(() -> this.lambda$get$0(string2, string = CommandStorage.createId(string2)), string);
        return container != null ? container.get(resourceLocation.getPath()) : new CompoundTag();
    }

    public void set(ResourceLocation resourceLocation, CompoundTag compoundTag) {
        String string = resourceLocation.getNamespace();
        String string2 = CommandStorage.createId(string);
        this.storage.computeIfAbsent(() -> this.newStorage(string, string2), string2).put(resourceLocation.getPath(), compoundTag);
    }

    public Stream<ResourceLocation> keys() {
        return this.namespaces.entrySet().stream().flatMap(entry -> ((Container)entry.getValue()).getKeys((String)entry.getKey()));
    }

    private static String createId(String string) {
        return "command_storage_" + string;
    }

    private /* synthetic */ Container lambda$get$0(String string, String string2) {
        return this.newStorage(string, string2);
    }

    static class Container
    extends SavedData {
        private final Map<String, CompoundTag> storage = Maps.newHashMap();

        public Container(String string) {
            super(string);
        }

        @Override
        public void load(CompoundTag compoundTag) {
            CompoundTag compoundTag2 = compoundTag.getCompound("contents");
            for (String string : compoundTag2.getAllKeys()) {
                this.storage.put(string, compoundTag2.getCompound(string));
            }
        }

        @Override
        public CompoundTag save(CompoundTag compoundTag) {
            CompoundTag compoundTag3 = new CompoundTag();
            this.storage.forEach((string, compoundTag2) -> compoundTag3.put((String)string, compoundTag2.copy()));
            compoundTag.put("contents", compoundTag3);
            return compoundTag;
        }

        public CompoundTag get(String string) {
            CompoundTag compoundTag = this.storage.get(string);
            return compoundTag != null ? compoundTag : new CompoundTag();
        }

        public void put(String string, CompoundTag compoundTag) {
            if (compoundTag.isEmpty()) {
                this.storage.remove(string);
            } else {
                this.storage.put(string, compoundTag);
            }
            this.setDirty();
        }

        public Stream<ResourceLocation> getKeys(String string) {
            return this.storage.keySet().stream().map(string2 -> new ResourceLocation(string, (String)string2));
        }
    }

}


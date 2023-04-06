/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;

public interface Tag<T> {
    public static <T> Codec<Tag<T>> codec(Supplier<TagCollection<T>> supplier) {
        return ResourceLocation.CODEC.flatXmap(resourceLocation -> Optional.ofNullable(((TagCollection)supplier.get()).getTag((ResourceLocation)resourceLocation)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown tag: " + resourceLocation))), tag -> Optional.ofNullable(((TagCollection)supplier.get()).getId(tag)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown tag: " + tag))));
    }

    public boolean contains(T var1);

    public List<T> getValues();

    default public T getRandomElement(Random random) {
        List<T> list = this.getValues();
        return list.get(random.nextInt(list.size()));
    }

    public static <T> Tag<T> fromSet(Set<T> set) {
        return SetTag.create(set);
    }

    public static interface Named<T>
    extends Tag<T> {
        public ResourceLocation getName();
    }

    public static class OptionalTagEntry
    implements Entry {
        private final ResourceLocation id;

        public OptionalTagEntry(ResourceLocation resourceLocation) {
            this.id = resourceLocation;
        }

        @Override
        public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
            Tag<T> tag = function.apply(this.id);
            if (tag != null) {
                tag.getValues().forEach(consumer);
            }
            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonArray) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", "#" + this.id);
            jsonObject.addProperty("required", Boolean.valueOf(false));
            jsonArray.add((JsonElement)jsonObject);
        }

        public String toString() {
            return "#" + this.id + "?";
        }
    }

    public static class TagEntry
    implements Entry {
        private final ResourceLocation id;

        public TagEntry(ResourceLocation resourceLocation) {
            this.id = resourceLocation;
        }

        @Override
        public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
            Tag<T> tag = function.apply(this.id);
            if (tag == null) {
                return false;
            }
            tag.getValues().forEach(consumer);
            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonArray) {
            jsonArray.add("#" + this.id);
        }

        public String toString() {
            return "#" + this.id;
        }
    }

    public static class OptionalElementEntry
    implements Entry {
        private final ResourceLocation id;

        public OptionalElementEntry(ResourceLocation resourceLocation) {
            this.id = resourceLocation;
        }

        @Override
        public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
            T t = function2.apply(this.id);
            if (t != null) {
                consumer.accept(t);
            }
            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonArray) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", this.id.toString());
            jsonObject.addProperty("required", Boolean.valueOf(false));
            jsonArray.add((JsonElement)jsonObject);
        }

        public String toString() {
            return this.id.toString() + "?";
        }
    }

    public static class ElementEntry
    implements Entry {
        private final ResourceLocation id;

        public ElementEntry(ResourceLocation resourceLocation) {
            this.id = resourceLocation;
        }

        @Override
        public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
            T t = function2.apply(this.id);
            if (t == null) {
                return false;
            }
            consumer.accept(t);
            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonArray) {
            jsonArray.add(this.id.toString());
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public static interface Entry {
        public <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3);

        public void serializeTo(JsonArray var1);
    }

    public static class Builder {
        private final List<BuilderEntry> entries = Lists.newArrayList();

        public static Builder tag() {
            return new Builder();
        }

        public Builder add(BuilderEntry builderEntry) {
            this.entries.add(builderEntry);
            return this;
        }

        public Builder add(Entry entry, String string) {
            return this.add(new BuilderEntry(entry, string));
        }

        public Builder addElement(ResourceLocation resourceLocation, String string) {
            return this.add(new ElementEntry(resourceLocation), string);
        }

        public Builder addTag(ResourceLocation resourceLocation, String string) {
            return this.add(new TagEntry(resourceLocation), string);
        }

        public <T> Optional<Tag<T>> build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (BuilderEntry builderEntry : this.entries) {
                if (builderEntry.getEntry().build(function, function2, ((ImmutableSet.Builder)builder)::add)) continue;
                return Optional.empty();
            }
            return Optional.of(Tag.fromSet(builder.build()));
        }

        public Stream<BuilderEntry> getEntries() {
            return this.entries.stream();
        }

        public <T> Stream<BuilderEntry> getUnresolvedEntries(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2) {
            return this.getEntries().filter(builderEntry -> !builderEntry.getEntry().build(function, function2, object -> {}));
        }

        public Builder addFromJson(JsonObject jsonObject, String string) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "values");
            ArrayList arrayList = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                arrayList.add(Builder.parseEntry(jsonElement));
            }
            if (GsonHelper.getAsBoolean(jsonObject, "replace", false)) {
                this.entries.clear();
            }
            arrayList.forEach(entry -> this.entries.add(new BuilderEntry((Entry)entry, string)));
            return this;
        }

        private static Entry parseEntry(JsonElement jsonElement) {
            Object object;
            boolean bl;
            String string;
            if (jsonElement.isJsonObject()) {
                object = jsonElement.getAsJsonObject();
                string = GsonHelper.getAsString(object, "id");
                bl = GsonHelper.getAsBoolean(object, "required", true);
            } else {
                string = GsonHelper.convertToString(jsonElement, "id");
                bl = true;
            }
            if (string.startsWith("#")) {
                object = new ResourceLocation(string.substring(1));
                return bl ? new TagEntry((ResourceLocation)object) : new OptionalTagEntry((ResourceLocation)object);
            }
            object = new ResourceLocation(string);
            return bl ? new ElementEntry((ResourceLocation)object) : new OptionalElementEntry((ResourceLocation)object);
        }

        public JsonObject serializeToJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (BuilderEntry builderEntry : this.entries) {
                builderEntry.getEntry().serializeTo(jsonArray);
            }
            jsonObject.addProperty("replace", Boolean.valueOf(false));
            jsonObject.add("values", (JsonElement)jsonArray);
            return jsonObject;
        }
    }

    public static class BuilderEntry {
        private final Entry entry;
        private final String source;

        private BuilderEntry(Entry entry, String string) {
            this.entry = entry;
            this.source = string;
        }

        public Entry getEntry() {
            return this.entry;
        }

        public String toString() {
            return this.entry.toString() + " (from " + this.source + ")";
        }
    }

}


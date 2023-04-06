/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNbtFunction
extends LootItemConditionalFunction {
    private final DataSource source;
    private final List<CopyOperation> operations;
    private static final Function<Entity, Tag> ENTITY_GETTER = NbtPredicate::getEntityTagToCompare;
    private static final Function<BlockEntity, Tag> BLOCK_ENTITY_GETTER = blockEntity -> blockEntity.save(new CompoundTag());

    private CopyNbtFunction(LootItemCondition[] arrlootItemCondition, DataSource dataSource, List<CopyOperation> list) {
        super(arrlootItemCondition);
        this.source = dataSource;
        this.operations = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NBT;
    }

    private static NbtPathArgument.NbtPath compileNbtPath(String string) {
        try {
            return new NbtPathArgument().parse(new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException("Failed to parse path " + string, commandSyntaxException);
        }
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Tag tag = this.source.getter.apply(lootContext);
        if (tag != null) {
            this.operations.forEach(copyOperation -> copyOperation.apply(itemStack::getOrCreateTag, tag));
        }
        return itemStack;
    }

    public static Builder copyData(DataSource dataSource) {
        return new Builder(dataSource);
    }

    static /* synthetic */ Function access$600() {
        return ENTITY_GETTER;
    }

    static /* synthetic */ Function access$700() {
        return BLOCK_ENTITY_GETTER;
    }

    static /* synthetic */ DataSource access$800(CopyNbtFunction copyNbtFunction) {
        return copyNbtFunction.source;
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyNbtFunction> {
        @Override
        public void serialize(JsonObject jsonObject, CopyNbtFunction copyNbtFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, copyNbtFunction, jsonSerializationContext);
            jsonObject.addProperty("source", CopyNbtFunction.access$800((CopyNbtFunction)copyNbtFunction).name);
            JsonArray jsonArray = new JsonArray();
            copyNbtFunction.operations.stream().map(CopyOperation::toJson).forEach(((JsonArray)jsonArray)::add);
            jsonObject.add("ops", (JsonElement)jsonArray);
        }

        @Override
        public CopyNbtFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            DataSource dataSource = DataSource.getByName(GsonHelper.getAsString(jsonObject, "source"));
            ArrayList arrayList = Lists.newArrayList();
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "ops");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "op");
                arrayList.add(CopyOperation.fromJson(jsonObject2));
            }
            return new CopyNbtFunction(arrlootItemCondition, dataSource, arrayList);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static enum DataSource {
        THIS("this", LootContextParams.THIS_ENTITY, CopyNbtFunction.access$600()),
        KILLER("killer", LootContextParams.KILLER_ENTITY, CopyNbtFunction.access$600()),
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER, CopyNbtFunction.access$600()),
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY, CopyNbtFunction.access$700());
        
        public final String name;
        public final LootContextParam<?> param;
        public final Function<LootContext, Tag> getter;

        private <T> DataSource(String string2, LootContextParam<T> lootContextParam, Function<? super T, Tag> function) {
            this.name = string2;
            this.param = lootContextParam;
            this.getter = lootContext -> {
                Object t = lootContext.getParamOrNull(lootContextParam);
                return t != null ? (Tag)function.apply((T)t) : null;
            };
        }

        public static DataSource getByName(String string) {
            for (DataSource dataSource : DataSource.values()) {
                if (!dataSource.name.equals(string)) continue;
                return dataSource;
            }
            throw new IllegalArgumentException("Invalid tag source " + string);
        }
    }

    public static enum MergeStrategy {
        REPLACE("replace"){

            @Override
            public void merge(Tag tag, NbtPathArgument.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                nbtPath.set(tag, ((Tag)Iterables.getLast(list))::copy);
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(Tag tag2, NbtPathArgument.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = nbtPath.getOrCreate(tag2, ListTag::new);
                list2.forEach(tag -> {
                    if (tag instanceof ListTag) {
                        list.forEach(tag2 -> ((ListTag)tag).add(tag2.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(Tag tag2, NbtPathArgument.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = nbtPath.getOrCreate(tag2, CompoundTag::new);
                list2.forEach(tag -> {
                    if (tag instanceof CompoundTag) {
                        list.forEach(tag2 -> {
                            if (tag2 instanceof CompoundTag) {
                                ((CompoundTag)tag).merge((CompoundTag)tag2);
                            }
                        });
                    }
                });
            }
        };
        
        private final String name;

        public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        private MergeStrategy(String string2) {
            this.name = string2;
        }

        public static MergeStrategy getByName(String string) {
            for (MergeStrategy mergeStrategy : MergeStrategy.values()) {
                if (!mergeStrategy.name.equals(string)) continue;
                return mergeStrategy;
            }
            throw new IllegalArgumentException("Invalid merge strategy" + string);
        }

    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final DataSource source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        private Builder(DataSource dataSource) {
            this.source = dataSource;
        }

        public Builder copy(String string, String string2, MergeStrategy mergeStrategy) {
            this.ops.add(new CopyOperation(string, string2, mergeStrategy));
            return this;
        }

        public Builder copy(String string, String string2) {
            return this.copy(string, string2, MergeStrategy.REPLACE);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

    static class CopyOperation {
        private final String sourcePathText;
        private final NbtPathArgument.NbtPath sourcePath;
        private final String targetPathText;
        private final NbtPathArgument.NbtPath targetPath;
        private final MergeStrategy op;

        private CopyOperation(String string, String string2, MergeStrategy mergeStrategy) {
            this.sourcePathText = string;
            this.sourcePath = CopyNbtFunction.compileNbtPath(string);
            this.targetPathText = string2;
            this.targetPath = CopyNbtFunction.compileNbtPath(string2);
            this.op = mergeStrategy;
        }

        public void apply(Supplier<Tag> supplier, Tag tag) {
            try {
                List<Tag> list = this.sourcePath.get(tag);
                if (!list.isEmpty()) {
                    this.op.merge(supplier.get(), this.targetPath, list);
                }
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("source", this.sourcePathText);
            jsonObject.addProperty("target", this.targetPathText);
            jsonObject.addProperty("op", this.op.name);
            return jsonObject;
        }

        public static CopyOperation fromJson(JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "source");
            String string2 = GsonHelper.getAsString(jsonObject, "target");
            MergeStrategy mergeStrategy = MergeStrategy.getByName(GsonHelper.getAsString(jsonObject, "op"));
            return new CopyOperation(string, string2, mergeStrategy);
        }
    }

}


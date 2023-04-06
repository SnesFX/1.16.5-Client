/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate {
    public static final BlockPredicate ANY = new BlockPredicate(null, null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);
    @Nullable
    private final Tag<Block> tag;
    @Nullable
    private final Block block;
    private final StatePropertiesPredicate properties;
    private final NbtPredicate nbt;

    public BlockPredicate(@Nullable Tag<Block> tag, @Nullable Block block, StatePropertiesPredicate statePropertiesPredicate, NbtPredicate nbtPredicate) {
        this.tag = tag;
        this.block = block;
        this.properties = statePropertiesPredicate;
        this.nbt = nbtPredicate;
    }

    public boolean matches(ServerLevel serverLevel, BlockPos blockPos) {
        BlockEntity blockEntity;
        if (this == ANY) {
            return true;
        }
        if (!serverLevel.isLoaded(blockPos)) {
            return false;
        }
        BlockState blockState = serverLevel.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (this.tag != null && !this.tag.contains(block)) {
            return false;
        }
        if (this.block != null && block != this.block) {
            return false;
        }
        if (!this.properties.matches(blockState)) {
            return false;
        }
        return this.nbt == NbtPredicate.ANY || (blockEntity = serverLevel.getBlockEntity(blockPos)) != null && this.nbt.matches(blockEntity.save(new CompoundTag()));
    }

    public static BlockPredicate fromJson(@Nullable JsonElement jsonElement) {
        Tag<Block> tag;
        Object object;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "block");
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        Block block = null;
        if (jsonObject.has("block")) {
            tag = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            block = Registry.BLOCK.get((ResourceLocation)((Object)tag));
        }
        tag = null;
        if (jsonObject.has("tag")) {
            object = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
            tag = SerializationTags.getInstance().getBlocks().getTag((ResourceLocation)object);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown block tag '" + object + "'");
            }
        }
        object = StatePropertiesPredicate.fromJson(jsonObject.get("state"));
        return new BlockPredicate(tag, block, (StatePropertiesPredicate)object, nbtPredicate);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.block != null) {
            jsonObject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", SerializationTags.getInstance().getBlocks().getIdOrThrow(this.tag).toString());
        }
        jsonObject.add("nbt", this.nbt.serializeToJson());
        jsonObject.add("state", this.properties.serializeToJson());
        return jsonObject;
    }

    public static class Builder {
        @Nullable
        private Block block;
        @Nullable
        private Tag<Block> blocks;
        private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block block) {
            this.block = block;
            return this;
        }

        public Builder of(Tag<Block> tag) {
            this.blocks = tag;
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate statePropertiesPredicate) {
            this.properties = statePropertiesPredicate;
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.blocks, this.block, this.properties, this.nbt);
        }
    }

}


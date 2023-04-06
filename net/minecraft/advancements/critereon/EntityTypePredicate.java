/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> entityType) {
            return true;
        }

        @Override
        public JsonElement serializeToJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ");

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement serializeToJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        String string = GsonHelper.convertToString(jsonElement, "type");
        if (string.startsWith("#")) {
            ResourceLocation resourceLocation = new ResourceLocation(string.substring(1));
            return new TagPredicate(SerializationTags.getInstance().getEntityTypes().getTagOrEmpty(resourceLocation));
        }
        ResourceLocation resourceLocation = new ResourceLocation(string);
        EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(resourceLocation).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + resourceLocation + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.keySet())));
        return new TypePredicate(entityType);
    }

    public static EntityTypePredicate of(EntityType<?> entityType) {
        return new TypePredicate(entityType);
    }

    public static EntityTypePredicate of(Tag<EntityType<?>> tag) {
        return new TagPredicate(tag);
    }

    static class TagPredicate
    extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;

        public TagPredicate(Tag<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean matches(EntityType<?> entityType) {
            return this.tag.contains(entityType);
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive("#" + SerializationTags.getInstance().getEntityTypes().getIdOrThrow(this.tag));
        }
    }

    static class TypePredicate
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public TypePredicate(EntityType<?> entityType) {
            this.type = entityType;
        }

        @Override
        public boolean matches(EntityType<?> entityType) {
            return this.type == entityType;
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
        }
    }

}


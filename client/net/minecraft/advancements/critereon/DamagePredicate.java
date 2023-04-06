/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamagePredicate {
    public static final DamagePredicate ANY = Builder.damageInstance().build();
    private final MinMaxBounds.Floats dealtDamage;
    private final MinMaxBounds.Floats takenDamage;
    private final EntityPredicate sourceEntity;
    private final Boolean blocked;
    private final DamageSourcePredicate type;

    public DamagePredicate() {
        this.dealtDamage = MinMaxBounds.Floats.ANY;
        this.takenDamage = MinMaxBounds.Floats.ANY;
        this.sourceEntity = EntityPredicate.ANY;
        this.blocked = null;
        this.type = DamageSourcePredicate.ANY;
    }

    public DamagePredicate(MinMaxBounds.Floats floats, MinMaxBounds.Floats floats2, EntityPredicate entityPredicate, @Nullable Boolean bl, DamageSourcePredicate damageSourcePredicate) {
        this.dealtDamage = floats;
        this.takenDamage = floats2;
        this.sourceEntity = entityPredicate;
        this.blocked = bl;
        this.type = damageSourcePredicate;
    }

    public boolean matches(ServerPlayer serverPlayer, DamageSource damageSource, float f, float f2, boolean bl) {
        if (this == ANY) {
            return true;
        }
        if (!this.dealtDamage.matches(f)) {
            return false;
        }
        if (!this.takenDamage.matches(f2)) {
            return false;
        }
        if (!this.sourceEntity.matches(serverPlayer, damageSource.getEntity())) {
            return false;
        }
        if (this.blocked != null && this.blocked != bl) {
            return false;
        }
        return this.type.matches(serverPlayer, damageSource);
    }

    public static DamagePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "damage");
        MinMaxBounds.Floats floats = MinMaxBounds.Floats.fromJson(jsonObject.get("dealt"));
        MinMaxBounds.Floats floats2 = MinMaxBounds.Floats.fromJson(jsonObject.get("taken"));
        Boolean bl = jsonObject.has("blocked") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "blocked")) : null;
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("type"));
        return new DamagePredicate(floats, floats2, entityPredicate, bl, damageSourcePredicate);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("dealt", this.dealtDamage.serializeToJson());
        jsonObject.add("taken", this.takenDamage.serializeToJson());
        jsonObject.add("source_entity", this.sourceEntity.serializeToJson());
        jsonObject.add("type", this.type.serializeToJson());
        if (this.blocked != null) {
            jsonObject.addProperty("blocked", this.blocked);
        }
        return jsonObject;
    }

    public static class Builder {
        private MinMaxBounds.Floats dealtDamage = MinMaxBounds.Floats.ANY;
        private MinMaxBounds.Floats takenDamage = MinMaxBounds.Floats.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;
        private Boolean blocked;
        private DamageSourcePredicate type = DamageSourcePredicate.ANY;

        public static Builder damageInstance() {
            return new Builder();
        }

        public Builder blocked(Boolean bl) {
            this.blocked = bl;
            return this;
        }

        public Builder type(DamageSourcePredicate.Builder builder) {
            this.type = builder.build();
            return this;
        }

        public DamagePredicate build() {
            return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
        }
    }

}


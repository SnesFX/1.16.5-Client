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
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate ANY = Builder.damageType().build();
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(@Nullable Boolean bl, @Nullable Boolean bl2, @Nullable Boolean bl3, @Nullable Boolean bl4, @Nullable Boolean bl5, @Nullable Boolean bl6, @Nullable Boolean bl7, @Nullable Boolean bl8, EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
        this.isProjectile = bl;
        this.isExplosion = bl2;
        this.bypassesArmor = bl3;
        this.bypassesInvulnerability = bl4;
        this.bypassesMagic = bl5;
        this.isFire = bl6;
        this.isMagic = bl7;
        this.isLightning = bl8;
        this.directEntity = entityPredicate;
        this.sourceEntity = entityPredicate2;
    }

    public boolean matches(ServerPlayer serverPlayer, DamageSource damageSource) {
        return this.matches(serverPlayer.getLevel(), serverPlayer.position(), damageSource);
    }

    public boolean matches(ServerLevel serverLevel, Vec3 vec3, DamageSource damageSource) {
        if (this == ANY) {
            return true;
        }
        if (this.isProjectile != null && this.isProjectile.booleanValue() != damageSource.isProjectile()) {
            return false;
        }
        if (this.isExplosion != null && this.isExplosion.booleanValue() != damageSource.isExplosion()) {
            return false;
        }
        if (this.bypassesArmor != null && this.bypassesArmor.booleanValue() != damageSource.isBypassArmor()) {
            return false;
        }
        if (this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != damageSource.isBypassInvul()) {
            return false;
        }
        if (this.bypassesMagic != null && this.bypassesMagic.booleanValue() != damageSource.isBypassMagic()) {
            return false;
        }
        if (this.isFire != null && this.isFire.booleanValue() != damageSource.isFire()) {
            return false;
        }
        if (this.isMagic != null && this.isMagic.booleanValue() != damageSource.isMagic()) {
            return false;
        }
        if (this.isLightning != null && this.isLightning != (damageSource == DamageSource.LIGHTNING_BOLT)) {
            return false;
        }
        if (!this.directEntity.matches(serverLevel, vec3, damageSource.getDirectEntity())) {
            return false;
        }
        return this.sourceEntity.matches(serverLevel, vec3, damageSource.getEntity());
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "damage type");
        Boolean bl = DamageSourcePredicate.getOptionalBoolean(jsonObject, "is_projectile");
        Boolean bl2 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "is_explosion");
        Boolean bl3 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "bypasses_armor");
        Boolean bl4 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "bypasses_invulnerability");
        Boolean bl5 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "bypasses_magic");
        Boolean bl6 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "is_fire");
        Boolean bl7 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "is_magic");
        Boolean bl8 = DamageSourcePredicate.getOptionalBoolean(jsonObject, "is_lightning");
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("direct_entity"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        return new DamageSourcePredicate(bl, bl2, bl3, bl4, bl5, bl6, bl7, bl8, entityPredicate, entityPredicate2);
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, string)) : null;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.addOptionally(jsonObject, "is_projectile", this.isProjectile);
        this.addOptionally(jsonObject, "is_explosion", this.isExplosion);
        this.addOptionally(jsonObject, "bypasses_armor", this.bypassesArmor);
        this.addOptionally(jsonObject, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addOptionally(jsonObject, "bypasses_magic", this.bypassesMagic);
        this.addOptionally(jsonObject, "is_fire", this.isFire);
        this.addOptionally(jsonObject, "is_magic", this.isMagic);
        this.addOptionally(jsonObject, "is_lightning", this.isLightning);
        jsonObject.add("direct_entity", this.directEntity.serializeToJson());
        jsonObject.add("source_entity", this.sourceEntity.serializeToJson());
        return jsonObject;
    }

    private void addOptionally(JsonObject jsonObject, String string, @Nullable Boolean bl) {
        if (bl != null) {
            jsonObject.addProperty(string, bl);
        }
    }

    public static class Builder {
        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private EntityPredicate directEntity = EntityPredicate.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;

        public static Builder damageType() {
            return new Builder();
        }

        public Builder isProjectile(Boolean bl) {
            this.isProjectile = bl;
            return this;
        }

        public Builder isLightning(Boolean bl) {
            this.isLightning = bl;
            return this;
        }

        public Builder direct(EntityPredicate.Builder builder) {
            this.directEntity = builder.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }

}


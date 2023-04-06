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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityFlagsPredicate {
    public static final EntityFlagsPredicate ANY = new Builder().build();
    @Nullable
    private final Boolean isOnFire;
    @Nullable
    private final Boolean isCrouching;
    @Nullable
    private final Boolean isSprinting;
    @Nullable
    private final Boolean isSwimming;
    @Nullable
    private final Boolean isBaby;

    public EntityFlagsPredicate(@Nullable Boolean bl, @Nullable Boolean bl2, @Nullable Boolean bl3, @Nullable Boolean bl4, @Nullable Boolean bl5) {
        this.isOnFire = bl;
        this.isCrouching = bl2;
        this.isSprinting = bl3;
        this.isSwimming = bl4;
        this.isBaby = bl5;
    }

    public boolean matches(Entity entity) {
        if (this.isOnFire != null && entity.isOnFire() != this.isOnFire.booleanValue()) {
            return false;
        }
        if (this.isCrouching != null && entity.isCrouching() != this.isCrouching.booleanValue()) {
            return false;
        }
        if (this.isSprinting != null && entity.isSprinting() != this.isSprinting.booleanValue()) {
            return false;
        }
        if (this.isSwimming != null && entity.isSwimming() != this.isSwimming.booleanValue()) {
            return false;
        }
        return this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isBaby() == this.isBaby.booleanValue();
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, string)) : null;
    }

    public static EntityFlagsPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "entity flags");
        Boolean bl = EntityFlagsPredicate.getOptionalBoolean(jsonObject, "is_on_fire");
        Boolean bl2 = EntityFlagsPredicate.getOptionalBoolean(jsonObject, "is_sneaking");
        Boolean bl3 = EntityFlagsPredicate.getOptionalBoolean(jsonObject, "is_sprinting");
        Boolean bl4 = EntityFlagsPredicate.getOptionalBoolean(jsonObject, "is_swimming");
        Boolean bl5 = EntityFlagsPredicate.getOptionalBoolean(jsonObject, "is_baby");
        return new EntityFlagsPredicate(bl, bl2, bl3, bl4, bl5);
    }

    private void addOptionalBoolean(JsonObject jsonObject, String string, @Nullable Boolean bl) {
        if (bl != null) {
            jsonObject.addProperty(string, bl);
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.addOptionalBoolean(jsonObject, "is_on_fire", this.isOnFire);
        this.addOptionalBoolean(jsonObject, "is_sneaking", this.isCrouching);
        this.addOptionalBoolean(jsonObject, "is_sprinting", this.isSprinting);
        this.addOptionalBoolean(jsonObject, "is_swimming", this.isSwimming);
        this.addOptionalBoolean(jsonObject, "is_baby", this.isBaby);
        return jsonObject;
    }

    public static class Builder {
        @Nullable
        private Boolean isOnFire;
        @Nullable
        private Boolean isCrouching;
        @Nullable
        private Boolean isSprinting;
        @Nullable
        private Boolean isSwimming;
        @Nullable
        private Boolean isBaby;

        public static Builder flags() {
            return new Builder();
        }

        public Builder setOnFire(@Nullable Boolean bl) {
            this.isOnFire = bl;
            return this;
        }

        public Builder setIsBaby(@Nullable Boolean bl) {
            this.isBaby = bl;
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }

}


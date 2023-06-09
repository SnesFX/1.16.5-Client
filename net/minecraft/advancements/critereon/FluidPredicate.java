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
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidPredicate {
    public static final FluidPredicate ANY = new FluidPredicate(null, null, StatePropertiesPredicate.ANY);
    @Nullable
    private final Tag<Fluid> tag;
    @Nullable
    private final Fluid fluid;
    private final StatePropertiesPredicate properties;

    public FluidPredicate(@Nullable Tag<Fluid> tag, @Nullable Fluid fluid, StatePropertiesPredicate statePropertiesPredicate) {
        this.tag = tag;
        this.fluid = fluid;
        this.properties = statePropertiesPredicate;
    }

    public boolean matches(ServerLevel serverLevel, BlockPos blockPos) {
        if (this == ANY) {
            return true;
        }
        if (!serverLevel.isLoaded(blockPos)) {
            return false;
        }
        FluidState fluidState = serverLevel.getFluidState(blockPos);
        Fluid fluid = fluidState.getType();
        if (this.tag != null && !this.tag.contains(fluid)) {
            return false;
        }
        if (this.fluid != null && fluid != this.fluid) {
            return false;
        }
        return this.properties.matches(fluidState);
    }

    public static FluidPredicate fromJson(@Nullable JsonElement jsonElement) {
        Object object;
        Tag<Fluid> tag;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "fluid");
        Fluid fluid = null;
        if (jsonObject.has("fluid")) {
            tag = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
            fluid = Registry.FLUID.get((ResourceLocation)((Object)tag));
        }
        tag = null;
        if (jsonObject.has("tag")) {
            object = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
            tag = SerializationTags.getInstance().getFluids().getTag((ResourceLocation)object);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown fluid tag '" + object + "'");
            }
        }
        object = StatePropertiesPredicate.fromJson(jsonObject.get("state"));
        return new FluidPredicate(tag, fluid, (StatePropertiesPredicate)object);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.fluid != null) {
            jsonObject.addProperty("fluid", Registry.FLUID.getKey(this.fluid).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", SerializationTags.getInstance().getFluids().getIdOrThrow(this.tag).toString());
        }
        jsonObject.add("state", this.properties.serializeToJson());
        return jsonObject;
    }
}


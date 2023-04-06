/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 */
package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.Function;
import net.minecraft.data.models.blockstates.VariantProperty;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
    public static final VariantProperty<Rotation> X_ROT = new VariantProperty<Rotation>("x", rotation -> new JsonPrimitive((Number)rotation.value));
    public static final VariantProperty<Rotation> Y_ROT = new VariantProperty<Rotation>("y", rotation -> new JsonPrimitive((Number)rotation.value));
    public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty<ResourceLocation>("model", resourceLocation -> new JsonPrimitive(resourceLocation.toString()));
    public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty<Boolean>("uvlock", JsonPrimitive::new);
    public static final VariantProperty<Integer> WEIGHT = new VariantProperty<Integer>("weight", JsonPrimitive::new);

    public static enum Rotation {
        R0(0),
        R90(90),
        R180(180),
        R270(270);
        
        private final int value;

        private Rotation(int n2) {
            this.value = n2;
        }
    }

}


/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.model;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public class ModelResourceLocation
extends ResourceLocation {
    private final String variant;

    protected ModelResourceLocation(String[] arrstring) {
        super(arrstring);
        this.variant = arrstring[2].toLowerCase(Locale.ROOT);
    }

    public ModelResourceLocation(String string) {
        this(ModelResourceLocation.decompose(string));
    }

    public ModelResourceLocation(ResourceLocation resourceLocation, String string) {
        this(resourceLocation.toString(), string);
    }

    public ModelResourceLocation(String string, String string2) {
        this(ModelResourceLocation.decompose(string + '#' + string2));
    }

    protected static String[] decompose(String string) {
        String[] arrstring = new String[]{null, string, ""};
        int n = string.indexOf(35);
        String string2 = string;
        if (n >= 0) {
            arrstring[2] = string.substring(n + 1, string.length());
            if (n > 1) {
                string2 = string.substring(0, n);
            }
        }
        System.arraycopy(ResourceLocation.decompose(string2, ':'), 0, arrstring, 0, 2);
        return arrstring;
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ModelResourceLocation && super.equals(object)) {
            ModelResourceLocation modelResourceLocation = (ModelResourceLocation)object;
            return this.variant.equals(modelResourceLocation.variant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + '#' + this.variant;
    }
}


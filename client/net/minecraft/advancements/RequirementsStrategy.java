/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
    public static final RequirementsStrategy AND = collection -> {
        String[][] arrstring = new String[collection.size()][];
        int n = 0;
        for (String string : collection) {
            arrstring[n++] = new String[]{string};
        }
        return arrstring;
    };
    public static final RequirementsStrategy OR = collection -> new String[][]{collection.toArray(new String[0])};

    public String[][] createRequirements(Collection<String> var1);
}


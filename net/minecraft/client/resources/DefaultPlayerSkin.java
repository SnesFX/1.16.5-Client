/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class DefaultPlayerSkin {
    private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

    public static ResourceLocation getDefaultSkin() {
        return STEVE_SKIN_LOCATION;
    }

    public static ResourceLocation getDefaultSkin(UUID uUID) {
        if (DefaultPlayerSkin.isAlexDefault(uUID)) {
            return ALEX_SKIN_LOCATION;
        }
        return STEVE_SKIN_LOCATION;
    }

    public static String getSkinModelName(UUID uUID) {
        if (DefaultPlayerSkin.isAlexDefault(uUID)) {
            return "slim";
        }
        return "default";
    }

    private static boolean isAlexDefault(UUID uUID) {
        return (uUID.hashCode() & 1) == 1;
    }
}


/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GrassColor;

public class GrassColorReloadListener
extends SimplePreparableReloadListener<int[]> {
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/colormap/grass.png");

    @Override
    protected int[] prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        try {
            return LegacyStuffWrapper.getPixels(resourceManager, LOCATION);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load grass color texture", iOException);
        }
    }

    @Override
    protected void apply(int[] arrn, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        GrassColor.init(arrn);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }
}


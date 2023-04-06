/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LazyArea area;

    public Layer(AreaFactory<LazyArea> areaFactory) {
        this.area = areaFactory.make();
    }

    public Biome get(Registry<Biome> registry, int n, int n2) {
        int n3 = this.area.get(n, n2);
        ResourceKey<Biome> resourceKey = Biomes.byId(n3);
        if (resourceKey == null) {
            throw new IllegalStateException("Unknown biome id emitted by layers: " + n3);
        }
        Biome biome = registry.get(resourceKey);
        if (biome == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw Util.pauseInIde(new IllegalStateException("Unknown biome id: " + n3));
            }
            LOGGER.warn("Unknown biome id: ", (Object)n3);
            return registry.get(Biomes.byId(0));
        }
        return biome;
    }
}


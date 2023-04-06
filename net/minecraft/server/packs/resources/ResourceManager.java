/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;

public interface ResourceManager {
    public Set<String> getNamespaces();

    public Resource getResource(ResourceLocation var1) throws IOException;

    public boolean hasResource(ResourceLocation var1);

    public List<Resource> getResources(ResourceLocation var1) throws IOException;

    public Collection<ResourceLocation> listResources(String var1, Predicate<String> var2);

    public Stream<PackResources> listPacks();

    public static enum Empty implements ResourceManager
    {
        INSTANCE;
        

        @Override
        public Set<String> getNamespaces() {
            return ImmutableSet.of();
        }

        @Override
        public Resource getResource(ResourceLocation resourceLocation) throws IOException {
            throw new FileNotFoundException(resourceLocation.toString());
        }

        @Override
        public boolean hasResource(ResourceLocation resourceLocation) {
            return false;
        }

        @Override
        public List<Resource> getResources(ResourceLocation resourceLocation) {
            return ImmutableList.of();
        }

        @Override
        public Collection<ResourceLocation> listResources(String string, Predicate<String> predicate) {
            return ImmutableSet.of();
        }

        @Override
        public Stream<PackResources> listPacks() {
            return Stream.of(new PackResources[0]);
        }
    }

}


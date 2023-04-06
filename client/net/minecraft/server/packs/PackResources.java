/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface PackResources
extends AutoCloseable {
    public InputStream getRootResource(String var1) throws IOException;

    public InputStream getResource(PackType var1, ResourceLocation var2) throws IOException;

    public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5);

    public boolean hasResource(PackType var1, ResourceLocation var2);

    public Set<String> getNamespaces(PackType var1);

    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException;

    public String getName();

    @Override
    public void close();
}


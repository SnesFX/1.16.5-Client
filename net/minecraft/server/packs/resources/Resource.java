/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface Resource
extends Closeable {
    public ResourceLocation getLocation();

    public InputStream getInputStream();

    @Nullable
    public <T> T getMetadata(MetadataSectionSerializer<T> var1);

    public String getSourceName();
}


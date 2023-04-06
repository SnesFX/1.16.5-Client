/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

public class SimpleResource
implements Resource {
    private final String sourceName;
    private final ResourceLocation location;
    private final InputStream resourceStream;
    private final InputStream metadataStream;
    private boolean triedMetadata;
    private JsonObject metadata;

    public SimpleResource(String string, ResourceLocation resourceLocation, InputStream inputStream, @Nullable InputStream inputStream2) {
        this.sourceName = string;
        this.location = resourceLocation;
        this.resourceStream = inputStream;
        this.metadataStream = inputStream2;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.location;
    }

    @Override
    public InputStream getInputStream() {
        return this.resourceStream;
    }

    public boolean hasMetadata() {
        return this.metadataStream != null;
    }

    @Nullable
    @Override
    public <T> T getMetadata(MetadataSectionSerializer<T> metadataSectionSerializer) {
        Object object;
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            object = null;
            try {
                object = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
                this.metadata = GsonHelper.parse((Reader)object);
            }
            finally {
                IOUtils.closeQuietly((Reader)object);
            }
        }
        if (this.metadata == null) {
            return null;
        }
        object = metadataSectionSerializer.getMetadataSectionName();
        return this.metadata.has((String)object) ? (T)metadataSectionSerializer.fromJson(GsonHelper.getAsJsonObject(this.metadata, (String)object)) : null;
    }

    @Override
    public String getSourceName() {
        return this.sourceName;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleResource)) {
            return false;
        }
        SimpleResource simpleResource = (SimpleResource)object;
        if (this.location != null ? !this.location.equals(simpleResource.location) : simpleResource.location != null) {
            return false;
        }
        return !(this.sourceName != null ? !this.sourceName.equals(simpleResource.sourceName) : simpleResource.sourceName != null);
    }

    public int hashCode() {
        int n = this.sourceName != null ? this.sourceName.hashCode() : 0;
        n = 31 * n + (this.location != null ? this.location.hashCode() : 0);
        return n;
    }

    @Override
    public void close() throws IOException {
        this.resourceStream.close();
        if (this.metadataStream != null) {
            this.metadataStream.close();
        }
    }
}


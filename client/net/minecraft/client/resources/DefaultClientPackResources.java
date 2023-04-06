/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;

public class DefaultClientPackResources
extends VanillaPackResources {
    private final AssetIndex assetIndex;

    public DefaultClientPackResources(AssetIndex assetIndex) {
        super("minecraft", "realms");
        this.assetIndex = assetIndex;
    }

    @Nullable
    @Override
    protected InputStream getResourceAsStream(PackType packType, ResourceLocation resourceLocation) {
        File file;
        if (packType == PackType.CLIENT_RESOURCES && (file = this.assetIndex.getFile(resourceLocation)) != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getResourceAsStream(packType, resourceLocation);
    }

    @Override
    public boolean hasResource(PackType packType, ResourceLocation resourceLocation) {
        File file;
        if (packType == PackType.CLIENT_RESOURCES && (file = this.assetIndex.getFile(resourceLocation)) != null && file.exists()) {
            return true;
        }
        return super.hasResource(packType, resourceLocation);
    }

    @Nullable
    @Override
    protected InputStream getResourceAsStream(String string) {
        File file = this.assetIndex.getRootFile(string);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getResourceAsStream(string);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String string, String string2, int n, Predicate<String> predicate) {
        Collection<ResourceLocation> collection = super.getResources(packType, string, string2, n, predicate);
        collection.addAll(this.assetIndex.getFiles(string2, string, n, predicate));
        return collection;
    }
}


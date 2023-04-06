/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.metadata.pack;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;

public class PackMetadataSection {
    public static final PackMetadataSectionSerializer SERIALIZER = new PackMetadataSectionSerializer();
    private final Component description;
    private final int packFormat;

    public PackMetadataSection(Component component, int n) {
        this.description = component;
        this.packFormat = n;
    }

    public Component getDescription() {
        return this.description;
    }

    public int getPackFormat() {
        return this.packFormat;
    }
}


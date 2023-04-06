/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import javax.annotation.Nullable;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderBuilder {
    @Nullable
    public GlyphProvider create(ResourceManager var1);
}


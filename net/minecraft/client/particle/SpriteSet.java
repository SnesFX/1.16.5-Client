/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteSet {
    public TextureAtlasSprite get(int var1, int var2);

    public TextureAtlasSprite get(Random var1);
}


/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.util.Mth;

public class PanoramaRenderer {
    private final Minecraft minecraft;
    private final CubeMap cubeMap;
    private float time;

    public PanoramaRenderer(CubeMap cubeMap) {
        this.cubeMap = cubeMap;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(float f, float f2) {
        this.time += f;
        this.cubeMap.render(this.minecraft, Mth.sin(this.time * 0.001f) * 5.0f + 25.0f, -this.time * 0.1f, f2);
    }
}


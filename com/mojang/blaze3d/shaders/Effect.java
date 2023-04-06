/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.shaders.Program;

public interface Effect {
    public int getId();

    public void markDirty();

    public Program getVertexProgram();

    public Program getFragmentProgram();
}


/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.gametest.framework;

import net.minecraft.gametest.framework.GameTestInfo;

public interface GameTestListener {
    public void testStructureLoaded(GameTestInfo var1);

    public void testFailed(GameTestInfo var1);
}


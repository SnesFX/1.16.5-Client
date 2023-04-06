/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 */
package net.minecraft.client.renderer.blockentity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BrightnessCombiner<S extends BlockEntity>
implements DoubleBlockCombiner.Combiner<S, Int2IntFunction> {
    @Override
    public Int2IntFunction acceptDouble(S s, S s2) {
        return n -> {
            int n2 = LevelRenderer.getLightColor(s.getLevel(), s.getBlockPos());
            int n3 = LevelRenderer.getLightColor(s2.getLevel(), s2.getBlockPos());
            int n4 = LightTexture.block(n2);
            int n5 = LightTexture.block(n3);
            int n6 = LightTexture.sky(n2);
            int n7 = LightTexture.sky(n3);
            return LightTexture.pack(Math.max(n4, n5), Math.max(n6, n7));
        };
    }

    @Override
    public Int2IntFunction acceptSingle(S s) {
        return n -> n;
    }

    @Override
    public Int2IntFunction acceptNone() {
        return n -> n;
    }

    @Override
    public /* synthetic */ Object acceptNone() {
        return this.acceptNone();
    }
}


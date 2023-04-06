/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;

public interface BlockStateGenerator
extends Supplier<JsonElement> {
    public Block getBlock();
}


/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.PoiTypeRename;

public class BeehivePoiRenameFix
extends PoiTypeRename {
    public BeehivePoiRenameFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected String rename(String string) {
        return string.equals("minecraft:bee_hive") ? "minecraft:beehive" : string;
    }
}


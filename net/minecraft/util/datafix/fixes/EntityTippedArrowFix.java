/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;

public class EntityTippedArrowFix
extends SimplestEntityRenameFix {
    public EntityTippedArrowFix(Schema schema, boolean bl) {
        super("EntityTippedArrowFix", schema, bl);
    }

    @Override
    protected String rename(String string) {
        return Objects.equals(string, "TippedArrow") ? "Arrow" : string;
    }
}


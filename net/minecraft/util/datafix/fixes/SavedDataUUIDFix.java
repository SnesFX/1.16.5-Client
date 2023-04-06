/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.Logger;

public class SavedDataUUIDFix
extends AbstractUUIDFix {
    public SavedDataUUIDFix(Schema schema) {
        super(schema, References.SAVED_DATA);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> typed2.updateTyped(typed2.getType().findField("data"), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("Raids", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> dynamic.update("HeroesOfTheVillage", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> SavedDataUUIDFix.createUUIDFromLongs(dynamic, "UUIDMost", "UUIDLeast").orElseGet(() -> {
            LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
            return dynamic;
        }))))))))));
    }
}


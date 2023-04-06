/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractUUIDFix
extends DataFix {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected DSL.TypeReference typeReference;

    public AbstractUUIDFix(Schema schema, DSL.TypeReference typeReference) {
        super(schema, false);
        this.typeReference = typeReference;
    }

    protected Typed<?> updateNamedChoice(Typed<?> typed2, String string, Function<Dynamic<?>, Dynamic<?>> function) {
        Type type = this.getInputSchema().getChoiceType(this.typeReference, string);
        Type type2 = this.getOutputSchema().getChoiceType(this.typeReference, string);
        return typed2.updateTyped(DSL.namedChoice((String)string, (Type)type), type2, typed -> typed.update(DSL.remainderFinder(), function));
    }

    protected static Optional<Dynamic<?>> replaceUUIDString(Dynamic<?> dynamic, String string, String string2) {
        return AbstractUUIDFix.createUUIDFromString(dynamic, string).map(dynamic2 -> dynamic.remove(string).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> replaceUUIDMLTag(Dynamic<?> dynamic, String string, String string2) {
        return dynamic.get(string).result().flatMap(AbstractUUIDFix::createUUIDFromML).map(dynamic2 -> dynamic.remove(string).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> replaceUUIDLeastMost(Dynamic<?> dynamic, String string, String string2) {
        String string3 = string + "Most";
        String string4 = string + "Least";
        return AbstractUUIDFix.createUUIDFromLongs(dynamic, string3, string4).map(dynamic2 -> dynamic.remove(string3).remove(string4).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> createUUIDFromString(Dynamic<?> dynamic, String string) {
        return dynamic.get(string).result().flatMap(dynamic2 -> {
            String string = dynamic2.asString(null);
            if (string != null) {
                try {
                    UUID uUID = UUID.fromString(string);
                    return AbstractUUIDFix.createUUIDTag(dynamic, uUID.getMostSignificantBits(), uUID.getLeastSignificantBits());
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            return Optional.empty();
        });
    }

    protected static Optional<Dynamic<?>> createUUIDFromML(Dynamic<?> dynamic) {
        return AbstractUUIDFix.createUUIDFromLongs(dynamic, "M", "L");
    }

    protected static Optional<Dynamic<?>> createUUIDFromLongs(Dynamic<?> dynamic, String string, String string2) {
        long l = dynamic.get(string).asLong(0L);
        long l2 = dynamic.get(string2).asLong(0L);
        if (l == 0L || l2 == 0L) {
            return Optional.empty();
        }
        return AbstractUUIDFix.createUUIDTag(dynamic, l, l2);
    }

    protected static Optional<Dynamic<?>> createUUIDTag(Dynamic<?> dynamic, long l, long l2) {
        return Optional.of(dynamic.createIntList(Arrays.stream(new int[]{(int)(l >> 32), (int)l, (int)(l2 >> 32), (int)l2})));
    }
}


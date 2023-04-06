/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.util.datafix.fixes.References;

public class ChunkBiomeFix
extends DataFix {
    public ChunkBiomeFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        return this.fixTypeEverywhereTyped("Leaves fix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            int n;
            Optional optional = dynamic.get("Biomes").asIntStreamOpt().result();
            if (!optional.isPresent()) {
                return dynamic;
            }
            int[] arrn = ((IntStream)optional.get()).toArray();
            int[] arrn2 = new int[1024];
            for (n = 0; n < 4; ++n) {
                for (int i = 0; i < 4; ++i) {
                    int n2 = (n << 2) + 2;
                    int n3 = (i << 2) + 2;
                    int n4 = n2 << 4 | n3;
                    arrn2[n << 2 | i] = n4 < arrn.length ? arrn[n4] : -1;
                }
            }
            for (n = 1; n < 64; ++n) {
                System.arraycopy(arrn2, 0, arrn2, n * 16, 16);
            }
            return dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(arrn2)));
        })));
    }
}


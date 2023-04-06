/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;

public class BitStorageAlignFix
extends DataFix {
    public BitStorageAlignFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        Type type2 = type.findFieldType("Level");
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", (Type)type2);
        OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
        Type type3 = ((List.ListType)opticFinder2.type()).getElement();
        OpticFinder opticFinder3 = DSL.typeFinder((Type)type3);
        Type type4 = DSL.named((String)References.BLOCK_STATE.typeName(), (Type)DSL.remainderType());
        OpticFinder opticFinder4 = DSL.fieldFinder((String)"Palette", (Type)DSL.list((Type)type4));
        return this.fixTypeEverywhereTyped("BitStorageAlignFix", type, this.getOutputSchema().getType(References.CHUNK), typed2 -> typed2.updateTyped(opticFinder, typed -> this.updateHeightmaps(BitStorageAlignFix.updateSections(opticFinder2, opticFinder3, opticFinder4, typed))));
    }

    private Typed<?> updateHeightmaps(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("Heightmaps", dynamic2 -> dynamic2.updateMapValues(pair -> pair.mapSecond(dynamic2 -> BitStorageAlignFix.updateBitStorage(dynamic, dynamic2, 256, 9)))));
    }

    private static Typed<?> updateSections(OpticFinder<?> opticFinder, OpticFinder<?> opticFinder2, OpticFinder<List<Pair<String, Dynamic<?>>>> opticFinder3, Typed<?> typed) {
        return typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> {
            int n = typed.getOptional(opticFinder3).map(list -> Math.max(4, DataFixUtils.ceillog2((int)list.size()))).orElse(0);
            if (n == 0 || Mth.isPowerOfTwo(n)) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("BlockStates", dynamic2 -> BitStorageAlignFix.updateBitStorage(dynamic, dynamic2, 4096, n)));
        }));
    }

    private static Dynamic<?> updateBitStorage(Dynamic<?> dynamic, Dynamic<?> dynamic2, int n, int n2) {
        long[] arrl = dynamic2.asLongStream().toArray();
        long[] arrl2 = BitStorageAlignFix.addPadding(n, n2, arrl);
        return dynamic.createLongList(LongStream.of(arrl2));
    }

    public static long[] addPadding(int n, int n2, long[] arrl) {
        int n3 = arrl.length;
        if (n3 == 0) {
            return arrl;
        }
        long l = (1L << n2) - 1L;
        int n4 = 64 / n2;
        int n5 = (n + n4 - 1) / n4;
        long[] arrl2 = new long[n5];
        int n6 = 0;
        int n7 = 0;
        long l2 = 0L;
        int n8 = 0;
        long l3 = arrl[0];
        long l4 = n3 > 1 ? arrl[1] : 0L;
        for (int i = 0; i < n; ++i) {
            int n9;
            long l5;
            int n10 = i * n2;
            int n11 = n10 >> 6;
            int n12 = (i + 1) * n2 - 1 >> 6;
            int n13 = n10 ^ n11 << 6;
            if (n11 != n8) {
                l3 = l4;
                l4 = n11 + 1 < n3 ? arrl[n11 + 1] : 0L;
                n8 = n11;
            }
            if (n11 == n12) {
                l5 = l3 >>> n13 & l;
            } else {
                n9 = 64 - n13;
                l5 = (l3 >>> n13 | l4 << n9) & l;
            }
            n9 = n7 + n2;
            if (n9 >= 64) {
                arrl2[n6++] = l2;
                l2 = l5;
                n7 = n2;
                continue;
            }
            l2 |= l5 << n7;
            n7 = n9;
        }
        if (l2 != 0L) {
            arrl2[n6] = l2;
        }
        return arrl2;
    }
}


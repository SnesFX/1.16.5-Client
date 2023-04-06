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
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class ChunkToProtochunkFix
extends DataFix {
    public ChunkToProtochunkFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        Type type2 = this.getOutputSchema().getType(References.CHUNK);
        Type type3 = type.findFieldType("Level");
        Type type4 = type2.findFieldType("Level");
        Type type5 = type3.findFieldType("TileTicks");
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", (Type)type3);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"TileTicks", (Type)type5);
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(References.CHUNK), typed -> typed.updateTyped(opticFinder, type4, typed2 -> {
            Dynamic dynamic2;
            Optional optional = typed2.getOptionalTyped(opticFinder2).flatMap(typed -> typed.write().result()).flatMap(dynamic -> dynamic.asStreamOpt().result());
            Dynamic dynamic3 = (Dynamic)typed2.get(DSL.remainderFinder());
            boolean bl = dynamic3.get("TerrainPopulated").asBoolean(false) && (!dynamic3.get("LightPopulated").asNumber().result().isPresent() || dynamic3.get("LightPopulated").asBoolean(false));
            dynamic3 = dynamic3.set("Status", dynamic3.createString(bl ? "mobs_spawned" : "empty"));
            dynamic3 = dynamic3.set("hasLegacyStructureData", dynamic3.createBoolean(true));
            if (bl) {
                Object object;
                Object object2;
                Optional optional2 = dynamic3.get("Biomes").asByteBufferOpt().result();
                if (optional2.isPresent()) {
                    object2 = (ByteBuffer)optional2.get();
                    object = new int[256];
                    for (int i = 0; i < ((int[])object).length; ++i) {
                        if (i >= ((Buffer)object2).capacity()) continue;
                        object[i] = ((ByteBuffer)object2).get(i) & 0xFF;
                    }
                    dynamic3 = dynamic3.set("Biomes", dynamic3.createIntList(Arrays.stream((int[])object)));
                }
                object2 = dynamic3;
                object = IntStream.range(0, 16).mapToObj(n -> new ShortArrayList()).collect(Collectors.toList());
                if (optional.isPresent()) {
                    ((Stream)optional.get()).forEach(arg_0 -> ChunkToProtochunkFix.lambda$null$3((List)object, arg_0));
                    dynamic3 = dynamic3.set("ToBeTicked", dynamic3.createList(object.stream().map(arg_0 -> ChunkToProtochunkFix.lambda$null$4((Dynamic)object2, arg_0))));
                }
                dynamic2 = (Dynamic)DataFixUtils.orElse((Optional)typed2.set(DSL.remainderFinder(), (Object)dynamic3).write().result(), (Object)dynamic3);
            } else {
                dynamic2 = dynamic3;
            }
            return (Typed)((Pair)type4.readTyped(dynamic2).result().orElseThrow(() -> new IllegalStateException("Could not read the new chunk"))).getFirst();
        })), (TypeRewriteRule)this.writeAndRead("Structure biome inject", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE)));
    }

    private static short packOffsetCoordinates(int n, int n2, int n3) {
        return (short)(n & 0xF | (n2 & 0xF) << 4 | (n3 & 0xF) << 8);
    }

    private static /* synthetic */ Dynamic lambda$null$4(Dynamic dynamic, ShortList shortList) {
        return dynamic.createList(shortList.stream().map(((Dynamic)dynamic)::createShort));
    }

    private static /* synthetic */ void lambda$null$3(List list, Dynamic dynamic) {
        int n = dynamic.get("x").asInt(0);
        int n2 = dynamic.get("y").asInt(0);
        int n3 = dynamic.get("z").asInt(0);
        short s = ChunkToProtochunkFix.packOffsetCoordinates(n, n2, n3);
        ((ShortList)list.get(n2 >> 4)).add(s);
    }
}

